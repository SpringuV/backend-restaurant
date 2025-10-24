# Payment Flow Test Guide

## 📋 Flow hoàn chỉnh khi nhận webhook từ Sepay:

### 1️⃣ **Sepay gửi webhook** → `POST /api/webhook/sepay`
```json
{
  "id": "TX123456",
  "gateway": "BIDV",
  "transferAmount": 50000,
  "content": "ORDER123 Thanh toan don hang"
}
```

### 2️⃣ **Backend xử lý** (SepayWebhookController → PaymentService):

#### ✅ **Cập nhật Order**:
- `order_status` = `COMPLETED`

#### ✅ **Cập nhật Invoice**:
- `payment_status` = `PAID`
- `payment_method` = `BANKING`

#### ✅ **Gửi WebSocket**:
- Topic: `/topic/payment.{orderId}`
- Message: `{"status": "PAID", "order_id": "123", ...}`

### 3️⃣ **Frontend nhận notification**:
- Polling WebSocket tại `/ws/checkTransfer` nhận được `{result: true, detail: ""}`
- Hiển thị "Thanh toán thành công!"
- Auto redirect về `/booking`

---

## 🧪 Test Commands:

### Test 1: Simulate Sepay webhook
```bash
curl -X POST http://localhost:8080/api/webhook/sepay \
  -H "Content-Type: application/json" \
  -d '{
    "id": "TEST_TX_123",
    "gateway": "BIDV",
    "transferAmount": 50000,
    "content": "ORDER1 Thanh toan"
  }'
```

### Test 2: Quick test payment notification
```bash
curl -X POST http://localhost:8080/api/webhook/test-payment/1
```

### Test 3: Test full Sepay flow
```bash
curl -X POST http://localhost:8080/api/webhook/test-sepay \
  -H "Content-Type: application/json" \
  -d '{
    "content": "ORDER1",
    "transferAmount": 50000,
    "gateway": "BIDV"
  }'
```

---

## 🔍 Kiểm tra kết quả:

### Check Order status:
```sql
SELECT id_order, order_status, total_amount 
FROM orders 
WHERE id_order = 1;
```

### Check Invoice payment:
```sql
SELECT i.id_invoice, i.payment_status, i.payment_method, o.id_order
FROM invoices i
JOIN orders o ON i.id_order = o.id_order
WHERE o.id_order = 1;
```

Expected:
- `order_status` = `COMPLETED`
- `payment_status` = `PAID`
- `payment_method` = `BANKING`

---

## 📊 Flow Diagram:

```
Sepay Bank Transfer
        ↓
  Webhook POST /api/webhook/sepay
        ↓
  SepayWebhookController
        ↓
  PaymentService.processPaymentSuccess()
        ↓
    ┌───┴───┐
    ↓       ↓
 Orders  Invoice
 status  payment_status = PAID
 = COMPLETED  payment_method = BANKING
    ↓       ↓
    └───┬───┘
        ↓
  WebSocket Notification
  /topic/payment.{orderId}
        ↓
  Frontend Payment Page
  (Polling /ws/checkTransfer)
        ↓
  Show Success Message
        ↓
  Redirect to /booking
```

---

## ✅ Changes Made:

1. **PaymentService.java**:
   - ➕ Added `InvoiceRepository` injection
   - ➕ Added `updateInvoicePaymentStatus()` method
   - ✏️ Updated `processPaymentSuccess()` to call invoice update
   - ✅ Sets `payment_status` = `PAID`
   - ✅ Sets `payment_method` = `BANKING`

2. **Flow Updated**:
   - Step 4: Update Order status
   - Step 5: **Update Invoice payment** (NEW! ✨)
   - Step 6: Send WebSocket notification
   - Step 7: Additional processing (TODO)

---

## 🎯 Next Steps:

- [ ] Add `transaction_id` field to Invoice entity (optional)
- [ ] Add `payment_time` field to track when payment completed
- [ ] Create payment history table for audit trail
- [ ] Send email/SMS notification
- [ ] Update table status to AVAILABLE after payment
