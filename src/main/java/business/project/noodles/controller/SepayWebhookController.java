package business.project.noodles.controller;

import business.project.noodles.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
@Slf4j
public class SepayWebhookController {

    private final PaymentService paymentService;

    /**
     * Webhook endpoint nhận notification từ Sepay Format từ Sepay: { "id":
     * "123456", "gateway": "VCB", "transactionDate": "2024-01-15 14:30:00",
     * "accountNumber": "1234567890", "subAccount": null, "transferType": "in",
     * "transferAmount": 50000, "accumulated": 1000000, "code": null, "content":
     * "ORDER123 Thanh toan don hang", "description": "..." }
     */
    @PostMapping("/sepay")
    public ResponseEntity<?> handleSepayWebhook(@RequestBody Map<String, Object> payload) {
        try {
            log.info("📩 Received Sepay webhook: {}", payload);

            // Parse thông tin từ webhook
            String content = (String) payload.get("content"); // VD: "ORDER123 Thanh toan don hang"
            Object amountObj = payload.get("transferAmount");
            String gateway = (String) payload.get("gateway");
            String transactionId = payload.get("id") != null ? payload.get("id").toString() : null;

            // Convert amount sang Double
            Double amount = null;
            if (amountObj instanceof Number) {
                amount = ((Number) amountObj).doubleValue();
            }

            log.info("💰 Payment details - Content: {}, Amount: {}, Gateway: {}, Transaction: {}",
                    content, amount, gateway, transactionId);

            // Extract order ID từ content (format: "ORDER123" hoặc "123")
            if (content != null && !content.isBlank()) {
                String orderId = extractOrderId(content);

                if (orderId != null) {
                    log.info("🎯 Processing payment for order ID: {}", orderId);

                    // Xử lý thanh toán qua PaymentService
                    boolean success = paymentService.processPaymentSuccess(orderId, amount, transactionId);

                    if (success) {
                        log.info("✅ Payment processed successfully for order: {}", orderId);
                        return ResponseEntity.ok(Map.of(
                                "success", true,
                                "message", "Payment processed successfully",
                                "order_id", orderId
                        ));
                    } else {
                        log.error("❌ Payment processing failed for order: {}", orderId);
                        return ResponseEntity.ok(Map.of(
                                "success", false,
                                "message", "Payment processing failed"
                        ));
                    }
                } else {
                    log.warn("⚠️ Could not extract order ID from content: {}", content);
                }
            }

            log.warn("⚠️ Invalid webhook data - missing or invalid content");
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Invalid webhook data - could not extract order ID"
            ));

        } catch (Exception e) {
            log.error("❌ Error processing Sepay webhook: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Internal server error",
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Extract order ID từ content của Sepay Hỗ trợ các format: - "ORDER123" →
     * "123" - "123 Thanh toan" → "123" - "DH123" → "123"
     */
    private String extractOrderId(String content) {
        if (content == null || content.isBlank()) {
            return null;
        }

        // Loại bỏ khoảng trắng thừa
        content = content.trim();

        // Tìm số đầu tiên trong chuỗi
        String[] words = content.split("\\s+");
        for (String word : words) {
            // Loại bỏ chữ cái, chỉ giữ số
            String numbers = word.replaceAll("[^0-9]", "");
            if (!numbers.isEmpty()) {
                return numbers;
            }
        }

        return null;
    }

    /**
     * Endpoint test để simulate payment success từ Sepay Dùng để test khi chưa
     * có webhook thật
     *
     * Test command: curl -X POST
     * http://localhost:8080/api/webhook/test-payment/123
     */
    @PostMapping("/test-payment/{orderId}")
    public ResponseEntity<?> testPayment(@PathVariable String orderId) {
        log.info("🧪 Testing payment notification for order: {}", orderId);

        try {
            paymentService.notifyPaymentSuccess(orderId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Test payment notification sent successfully",
                    "order_id", orderId,
                    "websocket_topic", "/topic/payment." + orderId
            ));
        } catch (Exception e) {
            log.error("❌ Error sending test notification: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Failed to send test notification",
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Endpoint test để simulate full payment flow Test command: curl -X POST
     * http://localhost:8080/api/webhook/test-sepay \ -H "Content-Type:
     * application/json" \ -d
     * '{"content":"ORDER123","transferAmount":50000,"gateway":"VCB","id":"TX123456"}'
     */
    @PostMapping("/test-sepay")
    public ResponseEntity<?> testSepayWebhook(@RequestBody(required = false) Map<String, Object> payload) {
        if (payload == null) {
            payload = Map.of(
                    "content", "ORDER123 Thanh toan don hang",
                    "transferAmount", 50000,
                    "gateway", "VCB",
                    "id", "TEST_TX_" + System.currentTimeMillis()
            );
        }

        log.info("🧪 Testing Sepay webhook with payload: {}", payload);
        return handleSepayWebhook(payload);
    }
}
