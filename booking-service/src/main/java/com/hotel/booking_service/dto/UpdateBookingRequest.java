package com.hotel.booking_service.dto;

import com.hotel.booking_service.enums.BookingStatus;
import com.hotel.booking_service.enums.PaymentStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UpdateBookingRequest {
    
    @Positive(message = "Hotel ID must be positive")
    private Long hotelId;
    
    @Positive(message = "Room ID must be positive")
    private Long roomId;
    
    @Positive(message = "User ID must be positive")
    private Long userId;
    
    @Size(min = 2, max = 100, message = "Guest name must be between 2 and 100 characters")
    private String guestName;
    
    @Email(message = "Guest email must be valid")
    @Size(max = 100, message = "Guest email cannot exceed 100 characters")
    private String guestEmail;
    
    @Size(max = 20, message = "Guest phone cannot exceed 20 characters")
    private String guestPhone;
    
    @Future(message = "Check-in date must be in the future")
    private LocalDateTime checkInDate;
    
    @Future(message = "Check-out date must be in the future")
    private LocalDateTime checkOutDate;
    
    @Min(value = 1, message = "Number of guests must be at least 1")
    @Max(value = 10, message = "Number of guests cannot exceed 10")
    private Integer numberOfGuests;
    
    @Min(value = 1, message = "Number of rooms must be at least 1")
    @Max(value = 5, message = "Number of rooms cannot exceed 5")
    private Integer numberOfRooms;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Room price per night must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Room price per night must have at most 8 integer digits and 2 decimal places")
    private BigDecimal roomPricePerNight;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Tax amount cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Tax amount must have at most 8 integer digits and 2 decimal places")
    private BigDecimal taxAmount;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Discount amount cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Discount amount must have at most 8 integer digits and 2 decimal places")
    private BigDecimal discountAmount;
    
    private BookingStatus status;
    
    private PaymentStatus paymentStatus;
    
    @Size(max = 500, message = "Special requests cannot exceed 500 characters")
    private String specialRequests;
    
    @Size(max = 500, message = "Cancellation reason cannot exceed 500 characters")
    private String cancellationReason;
}


