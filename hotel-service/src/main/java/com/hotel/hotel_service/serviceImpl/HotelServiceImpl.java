package com.hotel.hotel_service.serviceImpl;

import com.hotel.hotel_service.client.NotificationClient;
import com.hotel.hotel_service.dto.HotelListResponseDto;
import com.hotel.hotel_service.dto.HotelRequestDto;
import com.hotel.hotel_service.dto.HotelResponseDto;
import com.hotel.hotel_service.dto.UpdateHotelRequest;
import com.hotel.hotel_service.enums.ErrorCode;
import com.hotel.hotel_service.exception.CustomException;
import com.hotel.hotel_service.model.Hotel;
import com.hotel.hotel_service.repository.HotelRepository;
import com.hotel.hotel_service.service.HotelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    private HotelRepository hotelRepository;
    
    @Autowired
    private NotificationClient notificationClient;

    @Override
    public HotelResponseDto createHotel(HotelRequestDto hotelRequestDto) {
        log.info("Creating new hotel: {}", hotelRequestDto.getName());
        
        // Check if hotel already exists with same name and address
        Optional<Hotel> existingHotel = hotelRepository.findByNameAndAddress(
            hotelRequestDto.getName(), 
            hotelRequestDto.getAddress()
        );
        
        if (existingHotel.isPresent()) {
            throw new CustomException(
                ErrorCode.HOTEL_ALREADY_EXISTS,
                "Hotel already exists with name: " + hotelRequestDto.getName() + 
                " and address: " + hotelRequestDto.getAddress()
            );
        }
        
        // No image handling for now
        
        Hotel hotel = Hotel.builder()
            .name(hotelRequestDto.getName())
            .description(hotelRequestDto.getDescription())
            .address(hotelRequestDto.getAddress())
            .city(hotelRequestDto.getCity())
            .state(hotelRequestDto.getState())
            .country(hotelRequestDto.getCountry())
            .postalCode(hotelRequestDto.getPostalCode())
            .phone(hotelRequestDto.getPhone())
            .email(hotelRequestDto.getEmail())
            .website(hotelRequestDto.getWebsite())
            .rating(hotelRequestDto.getRating())
            .starRating(hotelRequestDto.getStarRating())
            .totalRooms(hotelRequestDto.getTotalRooms())
            .amenities(hotelRequestDto.getAmenities())
            .images("[]") // No images for now
            .status(hotelRequestDto.getStatus())
            .build();
        
        Hotel savedHotel = hotelRepository.save(hotel);
        log.info("Hotel created successfully with ID: {}", savedHotel.getId());
        
        // Send notification to Slack
        try {
            Map<String, Object> hotelData = new HashMap<>();
            hotelData.put("hotelId", savedHotel.getId());
            hotelData.put("hotelName", savedHotel.getName());
            hotelData.put("city", savedHotel.getCity());
            hotelData.put("country", savedHotel.getCountry());
            hotelData.put("starRating", savedHotel.getStarRating());
            hotelData.put("status", savedHotel.getStatus());
            
            notificationClient.handleSystemEvent("HOTEL_CREATED", hotelData);
            log.info("Hotel creation notification sent successfully");
        } catch (Exception e) {
            log.error("Failed to send hotel creation notification: {}", e.getMessage());
            // Don't fail the hotel creation if notification fails
        }
        
        return convertToResponseDto(savedHotel);
    }

    @Override
    public HotelResponseDto getHotelById(Long id) {
        log.info("Fetching hotel by ID: {}", id);
        
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.HOTEL_NOT_FOUND, "Hotel not found with ID: " + id));
        
        return convertToResponseDto(hotel);
    }

    @Override
    public List<HotelListResponseDto> getAllHotels() {
        log.info("Fetching all hotels");
        
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<HotelListResponseDto> getHotelsByCity(String city) {
        log.info("Fetching hotels by city: {}", city);
        
        List<Hotel> hotels = hotelRepository.findActiveHotelsByCity(city);
        return hotels.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<HotelListResponseDto> getHotelsByCountry(String country) {
        log.info("Fetching hotels by country: {}", country);
        
        List<Hotel> hotels = hotelRepository.findActiveHotelsByCountry(country);
        return hotels.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<HotelListResponseDto> getHotelsByStarRating(Integer starRating) {
        log.info("Fetching hotels by star rating: {}", starRating);
        
        List<Hotel> hotels = hotelRepository.findByStarRating(starRating);
        return hotels.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<HotelListResponseDto> getHotelsByRating(Double minRating) {
        log.info("Fetching hotels by minimum rating: {}", minRating);
        
        List<Hotel> hotels = hotelRepository.findByRatingGreaterThanEqual(minRating);
        return hotels.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<HotelListResponseDto> searchHotelsByName(String name) {
        log.info("Searching hotels by name: {}", name);
        
        List<Hotel> hotels = hotelRepository.findByNameContaining(name);
        return hotels.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
   public HotelResponseDto updateHotel(Long id, UpdateHotelRequest updateRequest) {
        log.info("Updating hotel with ID: {}", id);
        
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.HOTEL_NOT_FOUND, "Hotel not found with ID: " + id));
        
        // Update fields if provided using builder pattern
        Hotel.HotelBuilder hotelBuilder = hotel.toBuilder();
        
        if (updateRequest.getName() != null) hotelBuilder.name(updateRequest.getName());
        if (updateRequest.getDescription() != null) hotelBuilder.description(updateRequest.getDescription());
        if (updateRequest.getAddress() != null) hotelBuilder.address(updateRequest.getAddress());
        if (updateRequest.getCity() != null) hotelBuilder.city(updateRequest.getCity());
        if (updateRequest.getState() != null) hotelBuilder.state(updateRequest.getState());
        if (updateRequest.getCountry() != null) hotelBuilder.country(updateRequest.getCountry());
        if (updateRequest.getPostalCode() != null) hotelBuilder.postalCode(updateRequest.getPostalCode());
        if (updateRequest.getPhone() != null) hotelBuilder.phone(updateRequest.getPhone());
        if (updateRequest.getEmail() != null) hotelBuilder.email(updateRequest.getEmail());
        if (updateRequest.getWebsite() != null) hotelBuilder.website(updateRequest.getWebsite());
        if (updateRequest.getRating() != null) hotelBuilder.rating(updateRequest.getRating());
        if (updateRequest.getStarRating() != null) hotelBuilder.starRating(updateRequest.getStarRating());
        if (updateRequest.getTotalRooms() != null) hotelBuilder.totalRooms(updateRequest.getTotalRooms());
        if (updateRequest.getAmenities() != null) hotelBuilder.amenities(updateRequest.getAmenities());
        // No image handling for now
        if (updateRequest.getStatus() != null) hotelBuilder.status(updateRequest.getStatus());
        
        Hotel updatedHotel = hotelBuilder.build();
        updatedHotel = hotelRepository.save(updatedHotel);
        log.info("Hotel updated successfully with ID: {}", updatedHotel.getId());
        
        // Send notification to Slack
        try {
            Map<String, Object> hotelData = new HashMap<>();
            hotelData.put("hotelId", updatedHotel.getId());
            hotelData.put("hotelName", updatedHotel.getName());
            hotelData.put("city", updatedHotel.getCity());
            hotelData.put("country", updatedHotel.getCountry());
            hotelData.put("starRating", updatedHotel.getStarRating());
            hotelData.put("status", updatedHotel.getStatus());
            
            notificationClient.handleSystemEvent("HOTEL_UPDATED", hotelData);
            log.info("Hotel update notification sent successfully");
        } catch (Exception e) {
            log.error("Failed to send hotel update notification: {}", e.getMessage());
            // Don't fail the hotel update if notification fails
        }
        
        return convertToResponseDto(updatedHotel);
    }

    @Override
    public Map<String, String> deleteHotel(Long id) {
        log.info("Deleting hotel with ID: {}", id);
        
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.HOTEL_NOT_FOUND, "Hotel not found with ID: " + id));
        
        // Send notification to Slack before deletion
        try {
            Map<String, Object> hotelData = new HashMap<>();
            hotelData.put("hotelId", hotel.getId());
            hotelData.put("hotelName", hotel.getName());
            hotelData.put("city", hotel.getCity());
            hotelData.put("country", hotel.getCountry());
            hotelData.put("starRating", hotel.getStarRating());
            hotelData.put("status", hotel.getStatus());
            
            notificationClient.handleSystemEvent("HOTEL_DELETED", hotelData);
            log.info("Hotel deletion notification sent successfully");
        } catch (Exception e) {
            log.error("Failed to send hotel deletion notification: {}", e.getMessage());
            // Don't fail the hotel deletion if notification fails
        }
        
        hotelRepository.delete(hotel);
        log.info("Hotel deleted successfully with ID: {}", id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hotel deleted successfully");
        response.put("hotelId", id.toString());
        return response;
    }

    @Override
    public Map<String, String> updateHotelStatus(Long id, String status) {
        log.info("Updating hotel status for ID: {} to {}", id, status);
        
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.HOTEL_NOT_FOUND, "Hotel not found with ID: " + id));
        
        try {
            Hotel.HotelStatus newStatus = Hotel.HotelStatus.valueOf(status.toUpperCase());
            hotel.setStatus(newStatus);
            hotelRepository.save(hotel);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Hotel status updated successfully");
            response.put("hotelId", id.toString());
            response.put("newStatus", status);
            return response;
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_HOTEL_STATUS, "Invalid status: " + status + ". Valid statuses are: ACTIVE, INACTIVE, MAINTENANCE, SUSPENDED");
        }
    }

    @Override
    public Long getTotalActiveHotels() {
        log.info("Getting total active hotels count");
        return hotelRepository.countActiveHotels();
    }

    private HotelResponseDto convertToResponseDto(Hotel hotel) {
        return HotelResponseDto.builder()
            .id(hotel.getId())
            .name(hotel.getName())
            .description(hotel.getDescription())
            .address(hotel.getAddress())
            .city(hotel.getCity())
            .state(hotel.getState())
            .country(hotel.getCountry())
            .postalCode(hotel.getPostalCode())
            .phone(hotel.getPhone())
            .email(hotel.getEmail())
            .website(hotel.getWebsite())
            .rating(hotel.getRating())
            .starRating(hotel.getStarRating())
            .totalRooms(hotel.getTotalRooms())
            .amenities(hotel.getAmenities())
            .images(hotel.getImages())
            .status(hotel.getStatus())
            .createdAt(hotel.getCreatedAt())
            .updatedAt(hotel.getUpdatedAt())
            .build();
    }

    private HotelListResponseDto convertToListResponseDto(Hotel hotel) {
        return HotelListResponseDto.builder()
            .id(hotel.getId())
            .name(hotel.getName())
            .city(hotel.getCity())
            .country(hotel.getCountry())
            .rating(hotel.getRating())
            .starRating(hotel.getStarRating())
            .status(hotel.getStatus())
            .build();
    }
    
}
