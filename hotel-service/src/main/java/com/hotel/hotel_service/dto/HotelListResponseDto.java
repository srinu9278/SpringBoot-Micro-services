package com.hotel.hotel_service.dto;

import com.hotel.hotel_service.model.Hotel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class HotelListResponseDto {
    
    private Long id;
    private String name;
    private String city;
    private String country;
    private Double rating;
    private Integer starRating;
    private Hotel.HotelStatus status;
}
