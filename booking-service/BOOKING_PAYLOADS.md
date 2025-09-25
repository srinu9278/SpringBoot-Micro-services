# Booking Service - Sample Payloads

## 1. Create Booking (POST /api/v2/booking/create)

### Basic Booking
```json
{
  "hotelId": 1,
  "roomId": 101,
  "userId": 1,
  "guestName": "John Doe",
  "guestEmail": "john.doe@email.com",
  "guestPhone": "+1-555-0123",
  "checkInDate": "2024-02-15",
  "checkOutDate": "2024-02-18",
  "numberOfGuests": 2,
  "numberOfRooms": 1,
  "roomPricePerNight": 150.00,
  "specialRequests": "Late check-in requested, ground floor preferred"
}
```

### Family Booking with Multiple Rooms
```json
{
  "hotelId": 1,
  "roomId": 102,
  "userId": 2,
  "guestName": "Jane Smith",
  "guestEmail": "jane.smith@email.com",
  "guestPhone": "+1-555-0456",
  "checkInDate": "2024-03-01",
  "checkOutDate": "2024-03-05",
  "numberOfGuests": 4,
  "numberOfRooms": 2,
  "roomPricePerNight": 200.00,
  "specialRequests": "Connecting rooms preferred, extra bed needed"
}
```

### Business Trip Booking
```json
{
  "hotelId": 2,
  "roomId": 201,
  "userId": 3,
  "guestName": "Robert Johnson",
  "guestEmail": "robert.johnson@company.com",
  "guestPhone": "+1-555-0789",
  "checkInDate": "2024-02-20",
  "checkOutDate": "2024-02-22",
  "numberOfGuests": 1,
  "numberOfRooms": 1,
  "roomPricePerNight": 300.00,
  "specialRequests": "Quiet room, high-speed internet required"
}
```

## 2. Update Booking (PUT /api/v2/booking/update/{id})

### Update Guest Information
```json
{
  "guestName": "John Smith",
  "guestEmail": "john.smith@newemail.com",
  "guestPhone": "+1-555-9999",
  "specialRequests": "Updated: Vegetarian meal options needed"
}
```

### Update Dates
```json
{
  "checkInDate": "2024-02-16",
  "checkOutDate": "2024-02-19",
  "specialRequests": "Extended stay by one day"
}
```

### Update Room Details
```json
{
  "roomId": 103,
  "numberOfGuests": 3,
  "numberOfRooms": 1,
  "roomPricePerNight": 180.00,
  "specialRequests": "Upgraded to larger room"
}
```

## 3. Search Bookings (GET /api/v2/booking/search)

### Search by User
```
GET /api/v2/booking/search?userId=1&page=0&size=10
```

### Search by Hotel
```
GET /api/v2/booking/search?hotelId=1&status=CONFIRMED&page=0&size=10
```

### Search by Date Range
```
GET /api/v2/booking/search?checkInDate=2024-02-01&checkOutDate=2024-02-28&page=0&size=10
```

### Search by Guest Email
```
GET /api/v2/booking/search?guestEmail=john.doe@email.com&page=0&size=10
```

## 4. Availability Search (POST /api/v2/booking/availability)

### Basic Availability Check
```json
{
  "hotelId": 1,
  "checkInDate": "2024-02-15",
  "checkOutDate": "2024-02-18",
  "numberOfGuests": 2,
  "roomType": "DOUBLE"
}
```

### Weekend Availability
```json
{
  "hotelId": 1,
  "checkInDate": "2024-02-17",
  "checkOutDate": "2024-02-19",
  "numberOfGuests": 4,
  "roomType": "SUITE"
}
```

## 5. Update Booking Status (PUT /api/v2/booking/{id}/status)

### Confirm Booking
```json
{
  "status": "CONFIRMED"
}
```

### Check In
```json
{
  "status": "CHECKED_IN"
}
```

### Check Out
```json
{
  "status": "CHECKED_OUT"
}
```

### Cancel Booking
```json
{
  "status": "CANCELLED",
  "cancellationReason": "Change of travel plans"
}
```

## 6. Update Payment Status (PUT /api/v2/booking/{id}/payment)

### Mark as Paid
```json
{
  "paymentStatus": "PAID",
  "finalAmount": 450.00
}
```

### Partial Payment
```json
{
  "paymentStatus": "PARTIALLY_PAID",
  "finalAmount": 225.00
}
```

### Refund
```json
{
  "paymentStatus": "REFUNDED",
  "finalAmount": 0.00
}
```

## 7. Booking Statistics (GET /api/v2/booking/statistics)

### Hotel Statistics
```
GET /api/v2/booking/statistics/hotel/1?startDate=2024-01-01&endDate=2024-12-31
```

### User Statistics
```
GET /api/v2/booking/statistics/user/1?startDate=2024-01-01&endDate=2024-12-31
```

### Revenue Statistics
```
GET /api/v2/booking/statistics/revenue?startDate=2024-01-01&endDate=2024-12-31
```

## 8. Booking History (GET /api/v2/booking/history)

### User Booking History
```
GET /api/v2/booking/history/user/1?page=0&size=10
```

### Hotel Booking History
```
GET /api/v2/booking/history/hotel/1?page=0&size=10
```

## 9. Bulk Operations (POST /api/v2/booking/bulk)

### Bulk Status Update
```json
{
  "bookingIds": [1, 2, 3, 4, 5],
  "status": "CONFIRMED"
}
```

### Bulk Payment Update
```json
{
  "bookingIds": [1, 2, 3],
  "paymentStatus": "PAID"
}
```

## 10. Process Booking Lifecycle (POST /api/v2/booking/process-lifecycle)

### Process Check-ins
```json
{
  "action": "CHECK_IN",
  "bookingIds": [1, 2, 3]
}
```

### Process Check-outs
```json
{
  "action": "CHECK_OUT",
  "bookingIds": [4, 5, 6]
}
```

### Process Cancellations
```json
{
  "action": "CANCEL",
  "bookingIds": [7, 8],
  "reason": "Guest requested cancellation"
}
```

## Test Data Setup

### Sample Hotels (for testing)
- Hotel ID: 1 - "Grand Hotel Downtown"
- Hotel ID: 2 - "Business Center Hotel"
- Hotel ID: 3 - "Resort & Spa"

### Sample Rooms (for testing)
- Room ID: 101 - Hotel 1, Room 101, DOUBLE
- Room ID: 102 - Hotel 1, Room 102, SUITE
- Room ID: 201 - Hotel 2, Room 201, SINGLE
- Room ID: 301 - Hotel 3, Room 301, DELUXE

### Sample Users (for testing)
- User ID: 1 - "john.doe@email.com"
- User ID: 2 - "jane.smith@email.com"
- User ID: 3 - "robert.johnson@company.com"

## Notes

1. **Date Format**: Use `YYYY-MM-DD` format for all dates
2. **Price Format**: Use decimal numbers (e.g., 150.00)
3. **Phone Format**: Include country code and proper formatting
4. **Email Format**: Use valid email addresses
5. **Pagination**: Use `page` (0-based) and `size` parameters
6. **Status Values**: Use exact enum values (CONFIRMED, CHECKED_IN, etc.)
7. **Room Types**: Use exact enum values (SINGLE, DOUBLE, SUITE, etc.)

## Error Responses

The service will return standardized error responses:

```json
{
  "errorCode": "BOOKING_NOT_FOUND",
  "message": "Booking not found with ID: 999",
  "details": "No booking exists with the provided ID",
  "path": "/api/v2/booking/999",
  "timestamp": "2024-01-15T10:30:00Z"
}
```


