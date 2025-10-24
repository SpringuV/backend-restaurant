# TEST PAYMENT WEBHOOK

## 🧪 Cách test thanh toán:

### Option 1: Test nhanh với curl (Recommended)

```bash
# Test với order_id = 1, amount = 50000
curl -X POST http://localhost:8080/api/webhook/test-sepay ^
  -H "Content-Type: application/json" ^
  -d "{\"content\":\"ORDER1\",\"transferAmount\":50000,\"gateway\":\"BIDV\",\"id\":\"TEST_TX_123\"}"
```

### Option 2: Simulate Sepay webhook thật

```bash
# Giả lập webhook từ Sepay
curl -X POST http://localhost:8080/api/webhook/sepay ^
  -H "Content-Type: application/json" ^
  -d "{\"id\":\"TX123456\",\"gateway\":\"BIDV\",\"transferAmount\":50000,\"content\":\"ORDER1 Thanh toan don hang\",\"transactionDate\":\"2025-10-24 14:30:00\",\"accountNumber\":\"962471907021002\"}"
```

### Option 3: Quick test chỉ gửi WebSocket

```bash
curl -X POST http://localhost:8080/api/webhook/test-payment/1
```

---

## 📋 Các bước để test đầy đủ:

### 1. Kiểm tra Order hiện tại:

```sql
SELECT id_order, order_status, total_amount 
FROM orders 
WHERE id_order = 1;
```

Kết quả:
- `order_status` nên là `PENDING` hoặc `CONFIRMED`
- Lưu lại `total_amount` để test

### 2. Kiểm tra Invoice hiện tại:

```sql
SELECT i.id_invoice, i.payment_status, i.payment_method, o.id_order
FROM invoices i
JOIN orders o ON i.id_order = o.id_order
WHERE o.id_order = 1;
```

Kết quả:
- `payment_status` nên là `PENDING`
- `payment_method` nên là `BANKING`

### 3. Chạy test webhook:

**PowerShell:**
```powershell
# Thay 50000 bằng total_amount của order
$body = @{
    content = "ORDER1"
    transferAmount = 50000
    gateway = "BIDV"
    id = "TEST_TX_" + (Get-Date -Format "yyyyMMddHHmmss")
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/webhook/test-sepay" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body
```

**CMD:**
```cmd
curl -X POST http://localhost:8080/api/webhook/test-sepay ^
  -H "Content-Type: application/json" ^
  -d "{\"content\":\"ORDER1\",\"transferAmount\":50000,\"gateway\":\"BIDV\"}"
```

### 4. Kiểm tra backend logs:

Bạn sẽ thấy logs như sau:

```
📩 Received Sepay webhook: {content=ORDER1, transferAmount=50000, ...}
💰 Payment details - Content: ORDER1, Amount: 50000.0, ...
🎯 Processing payment for order ID: 1
💳 Processing payment for order: 1, amount: 50000.0, ...
💰 Amount validation - Order: 50000.0, Paid: 50000.0, Difference: 0.0, Valid: true
✅ Order 1 status updated to COMPLETED
📄 Updating invoice payment status for order: 1
✅ Invoice payment status updated to PAID for order: 1
🔔 Notifying payment success for order: 1
✅ Payment notification sent successfully for order: 1
✅ Payment processed successfully for order: 1
```

### 5. Kiểm tra frontend:

Trang payment sẽ nhận WebSocket message:

```
📩 Received WebSocket message: {"result":true,"detail":""}
📊 Parsed data: {result: true, detail: ''}
🔍 Result: true | Detail: 
🎉 Payment confirmed!
✅ Order status → COMPLETED
✅ Invoice payment_status → PAID
✅ Invoice payment_method → BANKING
🔄 Redirecting to /booking...
```

### 6. Verify database:

```sql
-- Check Order
SELECT id_order, order_status, total_amount 
FROM orders 
WHERE id_order = 1;
-- Expected: order_status = 'COMPLETED'

-- Check Invoice
SELECT i.id_invoice, i.payment_status, i.payment_method, o.id_order
FROM invoices i
JOIN orders o ON i.id_order = o.id_order
WHERE o.id_order = 1;
-- Expected: payment_status = 'PAID', payment_method = 'BANKING'
```

---

## 🐛 Troubleshooting:

### Issue 1: Amount validation failed
**Symptom:**
```
❌ Amount mismatch for order 1: expected 50000.0, got 49000.0
```

**Solution:** Đảm bảo `transferAmount` khớp với `total_amount` của order (cho phép sai lệch < 1000đ)

### Issue 2: Order not found
**Symptom:**
```
❌ Order not found: 1
```

**Solution:** Kiểm tra order có tồn tại trong DB với query:
```sql
SELECT * FROM orders WHERE id_order = 1;
```

### Issue 3: Invoice not found
**Symptom:**
```
⚠️ Invoice not found for order: 1
```

**Solution:** Tạo invoice trước khi test payment:
```sql
INSERT INTO invoices (id_invoice, id_order, payment_method, payment_status)
VALUES (UUID(), 1, 'BANKING', 'PENDING');
```

### Issue 4: Frontend không nhận WebSocket
**Symptom:** Frontend log `Waiting for payment` mãi

**Possible causes:**
1. Backend chưa chạy → Start `./gradlew bootRun`
2. WebSocket URL sai → Check `NEXT_PUBLIC_WS_BACKEND` trong `.env`
3. Order ID sai → Check URL params `?id_order=1&total=50000`

---

## 🎯 Expected Flow:

```
1. User mở trang payment với ?id_order=1&total=50000
   ↓
2. Frontend connect WebSocket ws://localhost:8080/ws/checkTransfer
   ↓
3. Frontend send: {"order_id": "1"}
   ↓
4. Backend polling mỗi 5s, check order status
   ↓
5. Backend send: {"result": false, "detail": "Waiting for payment"}
   ↓
6. User chuyển khoản qua Sepay (hoặc test bằng curl)
   ↓
7. Sepay gửi webhook → Backend
   ↓
8. Backend cập nhật:
   - Orders.order_status = COMPLETED
   - Invoice.payment_status = PAID
   - Invoice.payment_method = BANKING
   ↓
9. Backend polling lần tiếp theo detect COMPLETED
   ↓
10. Backend send: {"result": true, "detail": ""}
   ↓
11. Frontend hiển thị "Thanh toán thành công!" → Redirect
```

---

## 🔧 Bug đã fix:

### Before (Line 106 - WRONG):
```java
if (order.getTotal_amount() >=0 || paidAmount == null) {
    return false;  // ❌ Luôn return false vì total_amount >= 0
}
```

### After (CORRECT):
```java
if (paidAmount == null || paidAmount <= 0) {
    return false;  // ✅ Chỉ reject khi amount invalid
}
if (order.getTotal_amount() <= 0) {
    return false;  // ✅ Chỉ reject khi order amount invalid
}
```

---

## ✅ Ready to test!

1. Start backend: `./gradlew bootRun`
2. Start frontend: `npm run dev`
3. Mở trang: `http://localhost:3000/payment?id_order=1&total=50000`
4. Run test webhook (PowerShell):
   ```powershell
   curl -X POST http://localhost:8080/api/webhook/test-sepay `
     -H "Content-Type: application/json" `
     -d '{"content":"ORDER1","transferAmount":50000,"gateway":"BIDV"}'
   ```
5. Xem frontend tự động cập nhật! 🎉
