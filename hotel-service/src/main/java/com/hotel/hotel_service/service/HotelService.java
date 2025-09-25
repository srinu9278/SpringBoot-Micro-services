package com.hotel.hotel_service.service;

import com.hotel.hotel_service.dto.HotelListResponseDto;
import com.hotel.hotel_service.dto.HotelRequestDto;
import com.hotel.hotel_service.dto.HotelResponseDto;
import com.hotel.hotel_service.dto.UpdateHotelRequest;

import java.util.List;
import java.util.Map;

public interface HotelService {
    
    HotelResponseDto createHotel(HotelRequestDto hotelRequestDto);
    
    HotelResponseDto getHotelById(Long id);
    
    List<HotelListResponseDto> getAllHotels();
    
    List<HotelListResponseDto> getHotelsByCity(String city);
    
    List<HotelListResponseDto> getHotelsByCountry(String country);
    
    List<HotelListResponseDto> getHotelsByStarRating(Integer starRating);
    
    List<HotelListResponseDto> getHotelsByRating(Double minRating);
    
    List<HotelListResponseDto> searchHotelsByName(String name);
    
    HotelResponseDto updateHotel(Long id, UpdateHotelRequest updateRequest);
    
    Map<String, String> deleteHotel(Long id);
    
    Map<String, String> updateHotelStatus(Long id, String status);
    
    Long getTotalActiveHotels();
}
