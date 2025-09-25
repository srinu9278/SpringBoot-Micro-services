package com.hotel.room_service.service;

import com.hotel.room_service.dto.RoomListResponseDto;
import com.hotel.room_service.dto.RoomRequestDto;
import com.hotel.room_service.dto.RoomResponseDto;
import com.hotel.room_service.dto.UpdateRoomRequest;
import com.hotel.room_service.model.Room;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface RoomService {
    
    // Basic CRUD operations
    RoomResponseDto createRoom(RoomRequestDto roomRequestDto);
    RoomResponseDto getRoomById(Long id);
    List<RoomListResponseDto> getAllRooms();
    RoomResponseDto updateRoom(Long id, UpdateRoomRequest updateRequest);
    Map<String, String> deleteRoom(Long id);
    
    // Room management by hotel
    List<RoomListResponseDto> getRoomsByHotelId(Long hotelId);
    List<RoomListResponseDto> getAvailableRoomsByHotelId(Long hotelId);
    List<RoomListResponseDto> getRoomsByHotelIdAndStatus(Long hotelId, String status);
    long getRoomCountByHotelId(Long hotelId);
    long getAvailableRoomCountByHotelId(Long hotelId);
    
    // Room type management
    List<RoomListResponseDto> getRoomsByRoomType(String roomType);
    List<RoomListResponseDto> getRoomsByHotelIdAndRoomType(Long hotelId, String roomType);
    List<RoomListResponseDto> getAvailableRoomsByHotelIdAndRoomType(Long hotelId, String roomType);
    
    // Room status management
    List<RoomListResponseDto> getRoomsByStatus(String status);
    Map<String, String> updateRoomStatus(Long id, String status);
    List<RoomListResponseDto> getRoomsByHotelIdAndFloor(Long hotelId, Integer floor);
    
    // Room search and filtering
    List<RoomListResponseDto> searchRoomsByRoomNumber(String roomNumber);
    List<RoomListResponseDto> getRoomsByMaxOccupancy(Integer maxOccupancy);
    List<RoomListResponseDto> getRoomsByHotelIdAndMaxOccupancy(Long hotelId, Integer maxOccupancy);
    List<RoomListResponseDto> getAvailableRoomsByHotelIdAndMinOccupancy(Long hotelId, Integer minOccupancy);
    
    // Price-based filtering
    List<RoomListResponseDto> getRoomsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    List<RoomListResponseDto> getRoomsByHotelIdAndPriceRange(Long hotelId, BigDecimal minPrice, BigDecimal maxPrice);
    
    // Amenity-based filtering
    List<RoomListResponseDto> getRoomsByAmenities(String amenity);
    List<RoomListResponseDto> getRoomsByHotelIdAndAmenities(Long hotelId, String amenity);
    
    // Special room features
    List<RoomListResponseDto> getRoomsWithSeaView();
    List<RoomListResponseDto> getRoomsWithBalcony();
    List<RoomListResponseDto> getAccessibleRooms();
    List<RoomListResponseDto> getSmokingAllowedRooms();
    List<RoomListResponseDto> getRoomsByHotelIdWithSeaView(Long hotelId);
    List<RoomListResponseDto> getRoomsByHotelIdWithBalcony(Long hotelId);
    List<RoomListResponseDto> getAccessibleRoomsByHotelId(Long hotelId);
    
    // Availability checking
    boolean isRoomAvailable(Long roomId);
    boolean isRoomAvailableByRoomNumberAndHotelId(String roomNumber, Long hotelId);
    List<RoomListResponseDto> checkRoomAvailability(Long hotelId, String roomType, Integer minOccupancy, BigDecimal maxPrice);
    
    // Room statistics
    Map<String, Long> getRoomStatisticsByHotelId(Long hotelId);
    Map<String, Long> getRoomStatisticsByStatus(String status);
    Map<String, Long> getOverallRoomStatistics();
}
