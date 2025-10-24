package business.project.noodles.websocket;

import business.project.noodles.entity.Orders;
import business.project.noodles.repository.OrdersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckTransferWebSocketHandler extends TextWebSocketHandler {

    private final OrdersRepository orderRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("✅ Client connected to /ws/checkTransfer (sessionId={})", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            log.info("📩 Received message: {}", message.getPayload());

            // Parse message từ client: {"order_id": "123"}
            Map<String, Object> data = objectMapper.readValue(message.getPayload(), Map.class);
            String orderId = (String) data.get("order_id");

            if (orderId == null || orderId.isBlank()) {
                log.error("⚠️ Missing order_id in message");
                session.sendMessage(new TextMessage(
                        objectMapper.writeValueAsString(Map.of(
                                "result", false,
                                "detail", "Missing order_id"
                        ))
                ));
                session.close(CloseStatus.BAD_DATA);
                return;
            }

            log.info("🔍 Start checking transfer status for order: {}", orderId);

            // Bắt đầu polling kiểm tra trạng thái thanh toán mỗi 5 giây
            ScheduledFuture<?> pollingTask = scheduler.scheduleAtFixedRate(() -> {
                try {
                    if (!session.isOpen()) {
                        log.info("🔌 Session closed, stop polling for order: {}", orderId);
                        return;
                    }

                    // Kiểm tra trạng thái order trong database
                    TransferState state = checkTransferState(orderId);

                    // Gửi kết quả về client
                    String response = objectMapper.writeValueAsString(Map.of(
                            "result", state.isPaid(),
                            "detail", state.getDetail()
                    ));

                    session.sendMessage(new TextMessage(response));
                    log.info("📤 Sent status to client - Order: {}, Paid: {}, Detail: {}",
                            orderId, state.isPaid(), state.getDetail());

                    // Nếu đã thanh toán xong (result=true, detail=""), đóng kết nối
                    if (state.isPaid() && state.getDetail().isEmpty()) {
                        log.info("✅ Payment completed for order: {}, closing connection", orderId);
                        session.close(CloseStatus.NORMAL);
                    }

                } catch (Exception e) {
                    log.error("❌ Error during polling for order {}: {}", orderId, e.getMessage(), e);
                    try {
                        session.sendMessage(new TextMessage(
                                objectMapper.writeValueAsString(Map.of(
                                        "result", false,
                                        "detail", "Error checking payment status"
                                ))
                        ));
                    } catch (Exception ex) {
                        log.error("Failed to send error message", ex);
                    }
                }
            }, 0, 5, TimeUnit.SECONDS); // Chạy ngay lập tức, sau đó mỗi 5 giây

            // Lưu task vào session để có thể cancel khi đóng kết nối
            session.getAttributes().put("pollingTask", pollingTask);

        } catch (Exception e) {
            log.error("⚠️ Error handling WebSocket message: {}", e.getMessage(), e);
            session.sendMessage(new TextMessage(
                    objectMapper.writeValueAsString(Map.of(
                            "result", false,
                            "detail", "Invalid message format"
                    ))
            ));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("❌ Client disconnected (sessionId={}, status={})", session.getId(), status);

        // Cancel polling task khi đóng kết nối
        Object task = session.getAttributes().get("pollingTask");
        if (task instanceof ScheduledFuture) {
            ((ScheduledFuture<?>) task).cancel(true);
            log.info("🛑 Cancelled polling task for session: {}", session.getId());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("⚠️ WebSocket error (sessionId={}): {}", session.getId(), exception.getMessage());

        // Cancel polling task
        Object task = session.getAttributes().get("pollingTask");
        if (task instanceof ScheduledFuture) {
            ((ScheduledFuture<?>) task).cancel(true);
        }
    }

    /**
     * Kiểm tra trạng thái thanh toán của order Giống với getTransferState()
     * trong Python
     */
    private TransferState checkTransferState(String orderId) {
        try {
            Optional<Orders> orderOpt = orderRepository.findById(Long.parseLong(orderId));

            if (orderOpt.isEmpty()) {
                return new TransferState(false, "Order not found");
            }

            Orders order = orderOpt.get();
            Orders.OrderStatus status = order.getOrder_status();

            // Kiểm tra trạng thái
            if ("COMPLETED".equals(status)) {
                // Thanh toán thành công
                return new TransferState(true, "");
            } else if ("CANCELLED".equals(status)) {
                // Đơn hàng đã hủy
                return new TransferState(false, "Order cancelled");
            } else {
                // Đang chờ thanh toán
                return new TransferState(false, "Waiting for payment");
            }

        } catch (NumberFormatException e) {
            log.error("Invalid order ID format: {}", orderId);
            return new TransferState(false, "Invalid order ID");
        } catch (Exception e) {
            log.error("Error checking transfer state: {}", e.getMessage());
            return new TransferState(false, "Error checking status");
        }
    }

    /**
     * Class đại diện cho trạng thái thanh toán
     */
    private static class TransferState {

        private final boolean paid;
        private final String detail;

        public TransferState(boolean paid, String detail) {
            this.paid = paid;
            this.detail = detail;
        }

        public boolean isPaid() {
            return paid;
        }

        public String getDetail() {
            return detail;
        }
    }
}
