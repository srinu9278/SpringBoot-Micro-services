package com.hotel.hotel_service.dto;

import com.hotel.hotel_service.model.Hotel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class HotelRequestDto {
    
    @NotBlank(message = "Hotel name is required")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @NotBlank(message = "City is required")
    private String city;
    
    private String state;
    
    @NotBlank(message = "Country is required")
    private String country;
    
    private String postalCode;
    
    private String phone;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String website;
    
    @Positive(message = "Rating must be positive")
    private Double rating;
    
    @Positive(message = "Star rating must be positive")
    private Integer starRating;
    
    @Positive(message = "Total rooms must be positive")
    private Integer totalRooms;
    
    private String amenities;
    
    @NotNull(message = "Status is required")
    private Hotel.HotelStatus status;
}
