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
public class BookingRequestDto {
    
    @NotNull(message = "Hotel ID is required")
    @Positive(message = "Hotel ID must be positive")
    private Long hotelId;
    
    @NotNull(message = "Room ID is required")
    @Positive(message = "Room ID must be positive")
    private Long roomId;
    
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;
    
    @NotBlank(message = "Guest name is required")
    @Size(min = 2, max = 100, message = "Guest name must be between 2 and 100 characters")
    private String guestName;
    
    @NotBlank(message = "Guest email is required")
    @Email(message = "Guest email must be valid")
    @Size(max = 100, message = "Guest email cannot exceed 100 characters")
    private String guestEmail;
    
    @Size(max = 20, message = "Guest phone cannot exceed 20 characters")
    private String guestPhone;
    
    @NotNull(message = "Check-in date is required")
    @Future(message = "Check-in date must be in the future")
    private LocalDateTime checkInDate;
    
    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDateTime checkOutDate;
    
    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "Number of guests must be at least 1")
    @Max(value = 10, message = "Number of guests cannot exceed 10")
    private Integer numberOfGuests;
    
    @Min(value = 1, message = "Number of rooms must be at least 1")
    @Max(value = 5, message = "Number of rooms cannot exceed 5")
    private Integer numberOfRooms = 1;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Room price per night must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Room price per night must have at most 8 integer digits and 2 decimal places")
    private BigDecimal roomPricePerNight;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Tax amount cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Tax amount must have at most 8 integer digits and 2 decimal places")
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Discount amount cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Discount amount must have at most 8 integer digits and 2 decimal places")
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @NotNull(message = "Booking status is required")
    private BookingStatus status = BookingStatus.PENDING;
    
    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Size(max = 500, message = "Special requests cannot exceed 500 characters")
    private String specialRequests;
}


