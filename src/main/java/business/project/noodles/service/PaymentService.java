package business.project.noodles.service;

import business.project.noodles.entity.Orders;
import business.project.noodles.repository.OrdersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final SimpMessagingTemplate messagingTemplate;
    private final OrdersRepository orderRepository;

    /**
     * Xử lý khi nhận được thông báo thanh toán thành công từ Sepay webhook
     * @param orderId ID của order
     * @param amount Số tiền đã thanh toán
     * @param transactionId ID giao dịch từ ngân hàng
     * @return true nếu xử lý thành công
     */
    @Transactional
    public boolean processPaymentSuccess(String orderId, Double amount, String transactionId) {
        try {
            log.info("💳 Processing payment for order: {}, amount: {}, transaction: {}",
                    orderId, amount, transactionId);

            // 1. Tìm order trong database
            Optional<Orders> orderOpt = orderRepository.findById(Long.parseLong(orderId));

            if (orderOpt.isEmpty()) {
                log.error("❌ Order not found: {}", orderId);
                notifyPaymentError(orderId, "Không tìm thấy đơn hàng");
                return false;
            }

            Orders order = orderOpt.get();

            // 2. Validate số tiền
            if (!validateAmount(order, amount)) {
                log.error("❌ Amount mismatch for order {}: expected {}, got {}",
                        orderId, order.getTotal_amount(), amount);
                notifyPaymentError(orderId, "Số tiền không khớp");
                return false;
            }

            // 3. Kiểm tra trạng thái order
            if ("COMPLETED".equals(order.getOrder_status())) {
                log.warn("⚠️ Order {} already completed", orderId);
                // Vẫn notify thành công để đóng màn hình payment
                notifyPaymentSuccess(orderId);
                return true;
            }

            if ("CANCELLED".equals(order.getOrder_status())) {
                log.error("❌ Order {} is cancelled", orderId);
                notifyPaymentError(orderId, "Đơn hàng đã bị hủy");
                return false;
            }

            // 4. Cập nhật trạng thái order
            order.setOrder_status(Orders.OrderStatus.COMPLETED);
            // TODO: Lưu thông tin transaction nếu có trường trong entity
            // order.setTransactionId(transactionId);
            // order.setPaymentMethod("BANK_TRANSFER");
            // order.setPaymentTime(Instant.now());

            orderRepository.save(order);
            log.info("✅ Order {} status updated to COMPLETED", orderId);

            // 5. Notify qua WebSocket
            notifyPaymentSuccess(orderId);

            // 6. TODO: Các xử lý bổ sung
            // - Gửi email/SMS thông báo
            // - Cập nhật bàn về trạng thái available
            // - Tạo hóa đơn
            // - Log vào bảng payment_history

            return true;

        } catch (Exception e) {
            log.error("❌ Error processing payment for order {}: {}", orderId, e.getMessage(), e);
            notifyPaymentError(orderId, "Lỗi xử lý thanh toán");
            return false;
        }
    }

    /**
     * Validate số tiền thanh toán
     */
    private boolean validateAmount(Orders order, Double paidAmount) {
        if (order.getTotal_amount() >=0 || paidAmount == null) {
            return false;
        }

        // Cho phép sai lệch 1000đ do làm tròn
        double difference = Math.abs(order.getTotal_amount() - paidAmount);
        return difference < 1000;
    }

    /**
     * Gửi thông báo thanh toán thành công qua WebSocket
     */
    public void notifyPaymentSuccess(String orderId) {
        log.info("🔔 Notifying payment success for order: {}", orderId);

        try {
            messagingTemplate.convertAndSend(
                    "/topic/payment." + orderId,
                    Map.of(
                            "status", "PAID",
                            "order_id", orderId,
                            "message", "Thanh toán thành công",
                            "timestamp", Instant.now().toString()
                    )
            );
            log.info("✅ Payment notification sent successfully for order: {}", orderId);
        } catch (Exception e) {
            log.error("❌ Failed to send payment notification for order {}: {}",
                    orderId, e.getMessage(), e);
        }
    }

    /**
     * Gửi thông báo lỗi thanh toán qua WebSocket
     */
    public void notifyPaymentError(String orderId, String errorMessage) {
        log.warn("⚠️ Notifying payment error for order {}: {}", orderId, errorMessage);

        try {
            messagingTemplate.convertAndSend(
                    "/topic/payment." + orderId,
                    Map.of(
                            "status", "ERROR",
                            "order_id", orderId,
                            "message", errorMessage,
                            "timestamp", Instant.now().toString()
                    )
            );
        } catch (Exception e) {
            log.error("❌ Failed to send error notification for order {}: {}",
                    orderId, e.getMessage(), e);
        }
    }

    /**
     * Gửi thông báo đang xử lý thanh toán
     */
    public void notifyPaymentProcessing(String orderId) {
        log.info("⏳ Notifying payment processing for order: {}", orderId);

        try {
            messagingTemplate.convertAndSend(
                    "/topic/payment." + orderId,
                    Map.of(
                            "status", "PROCESSING",
                            "order_id", orderId,
                            "message", "Đang xử lý thanh toán...",
                            "timestamp", Instant.now().toString()
                    )
            );
        } catch (Exception e) {
            log.error("❌ Failed to send processing notification for order {}: {}",
                    orderId, e.getMessage(), e);
        }
    }

    /**
     * Kiểm tra trạng thái thanh toán của order
     */
    public Map<String, Object> checkPaymentStatus(Long orderId) {
        try {
            Optional<Orders> orderOpt = orderRepository.findById(orderId);

            if (orderOpt.isEmpty()) {
                return Map.of(
                        "success", false,
                        "message", "Order not found"
                );
            }

            Orders order = orderOpt.get();
            boolean isPaid = "COMPLETED".equals(order.getOrder_status());

            return Map.of(
                    "success", true,
                    "order_id", orderId,
                    "status", order.getOrder_status(),
                    "is_paid", isPaid,
                    "total_amount", order.getTotal_amount()
            );

        } catch (Exception e) {
            log.error("❌ Error checking payment status for order {}: {}",
                    orderId, e.getMessage(), e);
            return Map.of(
                    "success", false,
                    "message", "Error checking payment status"
            );
        }
    }

    /**
     * Hủy giao dịch thanh toán (nếu cần)
     */
    @Transactional
    public boolean cancelPayment(Long orderId, String reason) {
        try {
            log.info("🚫 Cancelling payment for order: {}, reason: {}", orderId, reason);

            Optional<Orders> orderOpt = orderRepository.findById(orderId);

            if (orderOpt.isEmpty()) {
                log.error("❌ Order not found: {}", orderId);
                return false;
            }

            Orders order = orderOpt.get();

            // Chỉ cho phép hủy nếu chưa hoàn thành
            if ("COMPLETED".equals(order.getOrder_status())) {
                log.warn("⚠️ Cannot cancel completed order: {}", orderId);
                return false;
            }

            order.setOrder_status(Orders.OrderStatus.CANCELLED);
            // TODO: Lưu lý do hủy nếu có trường
            // order.setCancelReason(reason);

            orderRepository.save(order);
            log.info("✅ Payment cancelled for order: {}", orderId);

            // Notify qua WebSocket
            notifyPaymentError(orderId.toString(), "Đơn hàng đã bị hủy: " + reason);

            return true;

        } catch (Exception e) {
            log.error("❌ Error cancelling payment for order {}: {}",
                    orderId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Lấy thống kê thanh toán theo ngày
     */
    public Map<String, Object> getPaymentStatistics(Instant startDate, Instant endDate) {
        try {
            // TODO: Implement statistics query
            // - Tổng số giao dịch thành công
            // - Tổng doanh thu
            // - Số giao dịch thất bại
            // - Phương thức thanh toán phổ biến

            return Map.of(
                    "success", true,
                    "start_date", startDate.toString(),
                    "end_date", endDate.toString(),
                    "message", "Statistics feature coming soon"
            );

        } catch (Exception e) {
            log.error("❌ Error getting payment statistics: {}", e.getMessage(), e);
            return Map.of(
                    "success", false,
                    "message", "Error getting statistics"
            );
        }
    }
}
