# Payment Service - Sample Payloads

## 1. Create Payment (POST /api/v2/payment/create)

### Basic Payment
```json
{
  "bookingId": 1,
  "userId": 1,
  "hotelId": 1,
  "amount": 150.00,
  "currency": "USD",
  "paymentMethod": "CREDIT_CARD",
  "transactionType": "PAYMENT",
  "gatewayName": "STRIPE",
  "cardLastFour": "4242",
  "cardBrand": "Visa",
  "metadata": "{\"booking_reference\": \"BK001\", \"room_type\": \"Deluxe\"}",
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
}
```

### PayPal Payment
```json
{
  "bookingId": 2,
  "userId": 2,
  "hotelId": 1,
  "amount": 200.50,
  "currency": "USD",
  "paymentMethod": "PAYPAL",
  "transactionType": "PAYMENT",
  "gatewayName": "PAYPAL",
  "metadata": "{\"paypal_order_id\": \"PAY-123456789\"}",
  "ipAddress": "192.168.1.101",
  "userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36"
}
```

### Bank Transfer Payment
```json
{
  "bookingId": 3,
  "userId": 3,
  "hotelId": 2,
  "amount": 500.00,
  "currency": "EUR",
  "paymentMethod": "BANK_TRANSFER",
  "transactionType": "PAYMENT",
  "gatewayName": "BANK_TRANSFER",
  "metadata": "{\"bank_name\": \"Chase Bank\", \"account_last_four\": \"1234\"}",
  "ipAddress": "192.168.1.102",
  "userAgent": "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36"
}
```

## 2. Process Payment (POST /api/v2/payment/process/{id})

### Process Payment
```
POST /api/v2/payment/process/1
```

## 3. Cancel Payment (POST /api/v2/payment/cancel/{id})

### Cancel Payment
```
POST /api/v2/payment/cancel/1?reason=Customer%20requested%20cancellation
```

## 4. Update Payment Status (PUT /api/v2/payment/status/{id})

### Update Status
```
PUT /api/v2/payment/status/1?status=COMPLETED
```

## 5. Process Refund (POST /api/v2/payment/refund)

### Full Refund
```json
{
  "paymentId": 1,
  "refundAmount": 150.00,
  "refundReason": "Customer requested full refund due to cancellation",
  "refundReference": "REF_001",
  "notes": "Refund processed within 24 hours as per policy"
}
```

### Partial Refund
```json
{
  "paymentId": 2,
  "refundAmount": 50.00,
  "refundReason": "Partial refund for service issues",
  "refundReference": "REF_002",
  "notes": "Refund for one night due to maintenance issues"
}
```

## 6. Search Payments (GET /api/v2/payment/search)

### Search by User
```
GET /api/v2/payment/search?userId=1&page=0&size=10&sortBy=createdAt&sortDir=desc
```

### Search by Hotel
```
GET /api/v2/payment/search?hotelId=1&status=COMPLETED&page=0&size=10
```

### Search by Date Range
```
GET /api/v2/payment/search?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59&page=0&size=10
```

### Search by Amount Range
```
GET /api/v2/payment/search?minAmount=100.00&maxAmount=500.00&page=0&size=10
```

### Search by Payment Method
```
GET /api/v2/payment/search?paymentMethod=CREDIT_CARD&status=COMPLETED&page=0&size=10
```

### Search by Transaction Type
```
GET /api/v2/payment/search?transactionType=PAYMENT&page=0&size=10
```

## 7. Get Payments by User (GET /api/v2/payment/user/{userId})

### All User Payments
```
GET /api/v2/payment/user/1
```

### User Payments by Status
```
GET /api/v2/payment/user/1/status/COMPLETED
```

### User Payments with Pagination
```
GET /api/v2/payment/user/1/paginated?page=0&size=10&sortBy=createdAt&sortDir=desc
```

## 8. Get Payments by Hotel (GET /api/v2/payment/hotel/{hotelId})

### All Hotel Payments
```
GET /api/v2/payment/hotel/1
```

### Hotel Payments by Status
```
GET /api/v2/payment/hotel/1/status/COMPLETED
```

### Hotel Payments with Pagination
```
GET /api/v2/payment/hotel/1/paginated?page=0&size=10&sortBy=createdAt&sortDir=desc
```

## 9. Get Payments by Booking (GET /api/v2/payment/booking/{bookingId})

### All Booking Payments
```
GET /api/v2/payment/booking/1
```

### Booking Payments by Status
```
GET /api/v2/payment/booking/1/status/COMPLETED
```

## 10. Get Payments by Status (GET /api/v2/payment/status/{status})

### All Payments by Status
```
GET /api/v2/payment/status/COMPLETED
```

### Payments by Status with Pagination
```
GET /api/v2/payment/status/COMPLETED/paginated?page=0&size=10&sortBy=createdAt&sortDir=desc
```

## 11. Get Payments by Payment Method (GET /api/v2/payment/method/{paymentMethod})

### All Payments by Method
```
GET /api/v2/payment/method/CREDIT_CARD
```

### Payments by Method and Status
```
GET /api/v2/payment/method/CREDIT_CARD/status/COMPLETED
```

## 12. Get Payments by Transaction Type (GET /api/v2/payment/transaction-type/{transactionType})

### All Payments by Type
```
GET /api/v2/payment/transaction-type/PAYMENT
```

### Payments by Type and Status
```
GET /api/v2/payment/transaction-type/PAYMENT/status/COMPLETED
```

## 13. Get Payments by Date Range (GET /api/v2/payment/date-range)

### Payments by Date Range
```
GET /api/v2/payment/date-range?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59
```

### Payments by Date Range and Status
```
GET /api/v2/payment/date-range/status/COMPLETED?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59
```

### Processed Payments by Date Range
```
GET /api/v2/payment/processed/date-range?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59
```

## 14. Get Payments by Amount Range (GET /api/v2/payment/amount-range)

### Payments by Amount Range
```
GET /api/v2/payment/amount-range?minAmount=100.00&maxAmount=500.00
```

### Payments by Amount Range and Status
```
GET /api/v2/payment/amount-range/status/COMPLETED?minAmount=100.00&maxAmount=500.00
```

## 15. Get Payments by Gateway (GET /api/v2/payment/gateway/{gatewayName})

### All Payments by Gateway
```
GET /api/v2/payment/gateway/STRIPE
```

### Payments by Gateway and Status
```
GET /api/v2/payment/gateway/STRIPE/status/COMPLETED
```

## 16. Get Refunded Payments (GET /api/v2/payment/refunded)

### All Refunded Payments
```
GET /api/v2/payment/refunded
```

### Refunded Payments by Amount
```
GET /api/v2/payment/refunded/amount/50.00
```

### Refunded Payments by Date Range
```
GET /api/v2/payment/refunded/date-range?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59
```

## 17. Get Payment Statistics (GET /api/v2/payment/statistics)

### Payment Count by Status
```
GET /api/v2/payment/statistics/count/status/COMPLETED
```

### Payment Count by User and Status
```
GET /api/v2/payment/statistics/count/user/1/status/COMPLETED
```

### Payment Count by Hotel and Status
```
GET /api/v2/payment/statistics/count/hotel/1/status/COMPLETED
```

### Total Amount by Status
```
GET /api/v2/payment/statistics/amount/status/COMPLETED
```

### Total Amount by User and Status
```
GET /api/v2/payment/statistics/amount/user/1/status/COMPLETED
```

### Total Amount by Hotel and Status
```
GET /api/v2/payment/statistics/amount/hotel/1/status/COMPLETED
```

### Total Refund Amount
```
GET /api/v2/payment/statistics/refund/total
```

### Total Refund Amount by Hotel
```
GET /api/v2/payment/statistics/refund/hotel/1
```

## 18. Get Recent Payments (GET /api/v2/payment/recent)

### Recent Payments with Pagination
```
GET /api/v2/payment/recent?page=0&size=10&sortBy=createdAt&sortDir=desc
```

## 19. Get Failed Payments (GET /api/v2/payment/failed/since)

### Failed Payments Since Date
```
GET /api/v2/payment/failed/since?since=2024-01-01T00:00:00
```

## 20. Utility Operations (GET /api/v2/payment)

### Check if Payment is Refundable
```
GET /api/v2/payment/refundable/1
```

### Check if Payment is Expired
```
GET /api/v2/payment/expired/1
```

### Generate Payment Reference
```
GET /api/v2/payment/reference/generate
```

### Generate Refund Reference
```
GET /api/v2/payment/refund-reference/generate
```

## Test Data Setup

### Sample Bookings (for testing)
- Booking ID: 1 - User 1, Hotel 1, Amount: $150.00
- Booking ID: 2 - User 2, Hotel 1, Amount: $200.50
- Booking ID: 3 - User 3, Hotel 2, Amount: $500.00

### Sample Users (for testing)
- User ID: 1 - "john.doe@email.com"
- User ID: 2 - "jane.smith@email.com"
- User ID: 3 - "robert.johnson@company.com"

### Sample Hotels (for testing)
- Hotel ID: 1 - "Grand Hotel Downtown"
- Hotel ID: 2 - "Business Center Hotel"

## Payment Status Values
- `PENDING` - Payment initiated but not processed
- `PROCESSING` - Payment being processed by gateway
- `COMPLETED` - Payment successfully processed
- `FAILED` - Payment processing failed
- `CANCELLED` - Payment cancelled by user or system
- `REFUNDED` - Payment fully refunded
- `PARTIALLY_REFUNDED` - Payment partially refunded
- `EXPIRED` - Payment expired without processing

## Payment Method Values
- `CREDIT_CARD` - Credit card payment
- `DEBIT_CARD` - Debit card payment
- `BANK_TRANSFER` - Bank transfer
- `PAYPAL` - PayPal payment
- `STRIPE` - Stripe payment
- `RAZORPAY` - Razorpay payment
- `CASH` - Cash payment
- `WALLET` - Digital wallet
- `UPI` - UPI payment
- `NET_BANKING` - Net banking

## Transaction Type Values
- `PAYMENT` - Regular payment
- `REFUND` - Refund transaction
- `PARTIAL_REFUND` - Partial refund
- `CHARGEBACK` - Chargeback
- `DISPUTE` - Dispute
- `ADJUSTMENT` - Adjustment
- `COMMISSION` - Commission
- `FEE` - Fee

## Notes

1. **Date Format**: Use `YYYY-MM-DDTHH:mm:ss` format for all dates
2. **Amount Format**: Use decimal numbers (e.g., 150.00)
3. **Currency Format**: Use 3-letter currency codes (e.g., USD, EUR, GBP)
4. **Pagination**: Use `page` (0-based) and `size` parameters
5. **Sorting**: Use `sortBy` and `sortDir` (asc/desc) parameters
6. **Status Values**: Use exact enum values (COMPLETED, FAILED, etc.)
7. **Payment Methods**: Use exact enum values (CREDIT_CARD, PAYPAL, etc.)
8. **Transaction Types**: Use exact enum values (PAYMENT, REFUND, etc.)

## Error Responses

The service will return standardized error responses:

```json
{
  "errorCode": "PAYMENT_NOT_FOUND",
  "message": "Payment not found with ID: 999",
  "details": "No payment exists with the provided ID",
  "path": "/api/v2/payment/999",
  "timestamp": "2024-01-15T10:30:00Z"
}
```


