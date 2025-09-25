package com.hotel.hotel_service.dto;

import com.hotel.hotel_service.model.Hotel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class HotelResponseDto {
    
    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String phone;
    private String email;
    private String website;
    private Double rating;
    private Integer starRating;
    private Integer totalRooms;
    private String amenities;
    private String images;
    private Hotel.HotelStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
