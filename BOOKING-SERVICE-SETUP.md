# Booking Service Setup Guide

## Overview
The Booking Service is now fully integrated with the Config Service and follows the same patterns as other services in the microservice architecture.

## Prerequisites
1. **Service Discovery (Eureka)** - Running on port 8761
2. **Config Service** - Running on port 8888
3. **PostgreSQL Database** - Running on port 5432
4. **Database**: `booking_db` (create this database)

## Database Setup
```sql
-- Create the booking database
CREATE DATABASE booking_db;

-- Connect to booking_db and create the bookings table
\c booking_db;

-- The table will be created automatically by Hibernate
-- But you can also create it manually if needed:
CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    booking_reference VARCHAR(255) UNIQUE NOT NULL,
    hotel_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    guest_name VARCHAR(100) NOT NULL,
    guest_email VARCHAR(100) NOT NULL,
    guest_phone VARCHAR(20),
    check_in_date TIMESTAMP NOT NULL,
    check_out_date TIMESTAMP NOT NULL,
    number_of_guests INTEGER NOT NULL,
    number_of_rooms INTEGER NOT NULL DEFAULT 1,
    total_nights INTEGER,
    room_price_per_night DECIMAL(10,2),
    total_amount DECIMAL(10,2),
    tax_amount DECIMAL(10,2) DEFAULT 0,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    final_amount DECIMAL(10,2),
    status VARCHAR(20) NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    special_requests TEXT,
    cancellation_reason TEXT,
    cancelled_at TIMESTAMP,
    cancelled_by BIGINT,
    confirmation_sent BOOLEAN DEFAULT FALSE,
    confirmation_sent_at TIMESTAMP,
    reminder_sent BOOLEAN DEFAULT FALSE,
    reminder_sent_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## Configuration
The Booking Service gets its configuration from the Config Service:

### Config Service Configuration (`config-service/src/main/resources/config/booking-service.yml`)
- **Port**: 9004
- **Database**: `booking_db`
- **Eureka Integration**: Enabled
- **Actuator Endpoints**: Enabled
- **Logging**: Configured for debugging

### Local Configuration (`booking-service/src/main/resources/application.yaml`)
- **Config Service Integration**: Enabled
- **Fail Fast**: Enabled (will fail if config service is not available)
- **Local Fallback**: Database configuration for local development

## Starting the Service

### Option 1: Using Batch Script
```bash
# Run the startup script
start-booking-service.bat
```

### Option 2: Manual Start
```bash
# Navigate to booking-service directory
cd booking-service

# Start the service
mvn spring-boot:run
```

### Option 3: With Config Service
```bash
# Start Config Service first
cd config-service
mvn spring-boot:run

# Then start Booking Service
cd ../booking-service
mvn spring-boot:run
```

## Service Endpoints

### Base URL
- **Direct Access**: `http://localhost:9004`
- **Via API Gateway**: `http://localhost:8080/api/v2/booking`

### Key Endpoints
- `POST /api/v2/booking/create` - Create new booking
- `GET /api/v2/booking/{id}` - Get booking by ID
- `PUT /api/v2/booking/update/{id}` - Update booking
- `DELETE /api/v2/booking/delete/{id}` - Delete booking
- `POST /api/v2/booking/search/availability` - Search availability
- `GET /api/v2/booking/statistics/overall` - Get statistics

## Testing the Service

### 1. Health Check
```bash
curl http://localhost:9004/actuator/health
```

### 2. Create a Booking
```bash
curl -X POST http://localhost:9004/api/v2/booking/create \
  -H "Content-Type: application/json" \
  -d '{
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
    "specialRequests": "Late check-in requested"
  }'
```

### 3. Get All Bookings
```bash
curl http://localhost:9004/api/v2/booking/all
```

## Service Discovery
The Booking Service will automatically register with Eureka Server and be discoverable by other services.

## Monitoring
- **Actuator Endpoints**: `http://localhost:9004/actuator`
- **Health Check**: `http://localhost:9004/actuator/health`
- **Metrics**: `http://localhost:9004/actuator/metrics`
- **Prometheus**: `http://localhost:9004/actuator/prometheus`

## Troubleshooting

### Common Issues
1. **Config Service Not Available**: Ensure Config Service is running on port 8888
2. **Database Connection**: Ensure PostgreSQL is running and `booking_db` exists
3. **Eureka Registration**: Ensure Eureka Server is running on port 8761
4. **Port Conflicts**: Ensure port 9004 is available

### Logs
Check the console output for detailed logs. The service logs at DEBUG level for troubleshooting.

## Integration with Other Services
The Booking Service integrates with:
- **User Service**: For user validation
- **Hotel Service**: For hotel information
- **Room Service**: For room availability
- **Payment Service**: For payment processing (future)
- **Notification Service**: For booking notifications (future)

