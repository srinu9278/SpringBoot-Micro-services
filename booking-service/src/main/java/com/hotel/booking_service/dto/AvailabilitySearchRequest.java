package com.hotel.booking_service.dto;

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
public class AvailabilitySearchRequest {
    
    @NotNull(message = "Hotel ID is required")
    @Positive(message = "Hotel ID must be positive")
    private Long hotelId;
    
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
    
    private String roomType;
    
    private BigDecimal maxPricePerNight;
    
    private Boolean hasSeaView;
    
    private Boolean hasBalcony;
    
    private Boolean isAccessible;
}


