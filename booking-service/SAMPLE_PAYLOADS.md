# Booking Service - Sample Payloads

## 1. Create Booking (POST /api/v2/booking/create)

```json
{
  "hotelId": 1,
  "roomId": 101,
  "userId": 1,
  "guestName": "John Doe",
  "guestEmail": "john.doe@example.com",
  "guestPhone": "+1234567890",
  "checkInDate": "2024-02-15T14:00:00",
  "checkOutDate": "2024-02-18T11:00:00",
  "numberOfGuests": 2,
  "numberOfRooms": 1,
  "roomPricePerNight": 150.00,
  "taxAmount": 15.00,
  "discountAmount": 10.00,
  "status": "PENDING",
  "paymentStatus": "PENDING",
  "specialRequests": "Late check-in requested, ground floor room preferred"
}
```

## 2. Update Booking (PUT /api/v2/booking/update/{id})

```json
{
  "guestName": "John Smith",
  "guestPhone": "+1987654321",
  "checkInDate": "2024-02-16T15:00:00",
  "checkOutDate": "2024-02-19T12:00:00",
  "numberOfGuests": 3,
  "specialRequests": "Updated: Early check-in preferred, high floor room"
}
```

## 3. Availability Search (POST /api/v2/booking/search/availability)

```json
{
  "hotelId": 1,
  "checkInDate": "2024-02-15T14:00:00",
  "checkOutDate": "2024-02-18T11:00:00",
  "numberOfGuests": 2,
  "numberOfRooms": 1,
  "roomType": "DOUBLE",
  "maxPricePerNight": 200.00,
  "hasSeaView": true,
  "hasBalcony": false,
  "isAccessible": true
}
```

## 4. Bulk Status Update (PUT /api/v2/booking/bulk/status)

```json
{
  "bookingIds": [1, 2, 3, 4, 5],
  "status": "CONFIRMED"
}
```

## 5. Bulk Payment Status Update (PUT /api/v2/booking/bulk/payment-status)

```json
{
  "bookingIds": [1, 2, 3],
  "paymentStatus": "PAID"
}
```

## 6. Bulk Cancellation (PUT /api/v2/booking/bulk/cancel)

```json
{
  "bookingIds": [1, 2, 3],
  "cancellationReason": "Guest requested cancellation due to travel restrictions",
  "cancelledBy": 1
}
```

## 7. Filter Bookings (POST /api/v2/booking/filter)

```json
{
  "hotelId": 1,
  "status": "CONFIRMED",
  "paymentStatus": "PAID",
  "checkInDate": "2024-02-01T00:00:00",
  "checkOutDate": "2024-02-28T23:59:59"
}
```

## Key Endpoints Summary

### Basic CRUD
- `POST /api/v2/booking/create` - Create new booking
- `GET /api/v2/booking/{id}` - Get booking by ID
- `GET /api/v2/booking/reference/{reference}` - Get booking by reference
- `PUT /api/v2/booking/update/{id}` - Update booking
- `DELETE /api/v2/booking/delete/{id}` - Delete booking

### Status Management
- `PUT /api/v2/booking/{id}/status/{status}` - Update booking status
- `PUT /api/v2/booking/{id}/confirm` - Confirm booking
- `PUT /api/v2/booking/{id}/cancel` - Cancel booking
- `PUT /api/v2/booking/{id}/check-in` - Check-in booking
- `PUT /api/v2/booking/{id}/check-out` - Check-out booking

### Search & Filter
- `POST /api/v2/booking/search/availability` - Search room availability
- `GET /api/v2/booking/room/{roomId}/availability` - Check room availability
- `GET /api/v2/booking/hotel/{hotelId}/available-rooms` - Get available rooms
- `POST /api/v2/booking/search` - Search bookings by query
- `POST /api/v2/booking/filter` - Filter bookings

### Statistics & Analytics
- `GET /api/v2/booking/statistics/overall` - Overall booking statistics
- `GET /api/v2/booking/hotel/{hotelId}/statistics` - Hotel booking statistics
- `GET /api/v2/booking/user/{userId}/statistics` - User booking statistics
- `GET /api/v2/booking/revenue/overall` - Overall revenue statistics
- `GET /api/v2/booking/hotel/{hotelId}/revenue` - Hotel revenue statistics

### Bulk Operations
- `PUT /api/v2/booking/bulk/status` - Bulk status update
- `PUT /api/v2/booking/bulk/payment-status` - Bulk payment status update
- `PUT /api/v2/booking/bulk/cancel` - Bulk cancellation


