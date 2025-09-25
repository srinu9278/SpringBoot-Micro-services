package com.hotel.room_service.dto;

import com.hotel.room_service.enums.RoomStatus;
import com.hotel.room_service.enums.RoomType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UpdateRoomRequest {
    
    @Size(min = 1, max = 10, message = "Room number must be between 1 and 10 characters")
    private String roomNumber;
    
    @Positive(message = "Hotel ID must be positive")
    private Long hotelId;
    
    private RoomType roomType;
    
    @Min(value = 1, message = "Floor must be at least 1")
    @Max(value = 100, message = "Floor cannot exceed 100")
    private Integer floor;
    
    @Min(value = 1, message = "Max occupancy must be at least 1")
    @Max(value = 10, message = "Max occupancy cannot exceed 10")
    private Integer maxOccupancy;
    
    @Size(min = 1, max = 50, message = "Bed type must be between 1 and 50 characters")
    private String bedType;
    
    @Min(value = 50, message = "Room size must be at least 50 sqft")
    @Max(value = 5000, message = "Room size cannot exceed 5000 sqft")
    private Integer roomSizeSqft;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Price per night must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price per night must have at most 8 integer digits and 2 decimal places")
    private BigDecimal pricePerNight;
    
    private String amenities;
    
    private RoomStatus status;
    
    private Boolean isSmokingAllowed;
    
    private Boolean hasBalcony;
    
    private Boolean hasSeaView;
    
    private Boolean hasCityView;
    
    private Boolean isAccessible;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
}
