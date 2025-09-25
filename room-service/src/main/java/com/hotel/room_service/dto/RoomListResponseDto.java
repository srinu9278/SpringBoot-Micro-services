package com.hotel.room_service.dto;

import com.hotel.room_service.enums.RoomStatus;
import com.hotel.room_service.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RoomListResponseDto {
    
    private Long id;
    private String roomNumber;
    private Long hotelId;
    private RoomType roomType;
    private Integer floor;
    private Integer maxOccupancy;
    private String bedType;
    private BigDecimal pricePerNight;
    private RoomStatus status;
    private Boolean isSmokingAllowed;
    private Boolean hasBalcony;
    private Boolean hasSeaView;
    private Boolean hasCityView;
    private Boolean isAccessible;
}
