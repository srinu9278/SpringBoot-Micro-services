package com.hotel.room_service.serviceImpl;

import com.hotel.room_service.client.NotificationClient;
import com.hotel.room_service.dto.RoomListResponseDto;
import com.hotel.room_service.dto.RoomRequestDto;
import com.hotel.room_service.dto.RoomResponseDto;
import com.hotel.room_service.dto.UpdateRoomRequest;
import com.hotel.room_service.enums.ErrorCode;
import com.hotel.room_service.enums.RoomStatus;
import com.hotel.room_service.enums.RoomType;
import com.hotel.room_service.exception.CustomException;
import com.hotel.room_service.model.Room;
import com.hotel.room_service.repository.RoomRepository;
import com.hotel.room_service.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private NotificationClient notificationClient;

    @Override
    @Transactional
    public RoomResponseDto createRoom(RoomRequestDto roomRequestDto) {
        log.info("Creating new room: {} for hotel: {}", roomRequestDto.getRoomNumber(), roomRequestDto.getHotelId());
        
        // Check if room already exists with same room number in the hotel
        if (roomRepository.existsByRoomNumberAndHotelId(roomRequestDto.getRoomNumber(), roomRequestDto.getHotelId())) {
            throw new CustomException(
                ErrorCode.ROOM_ALREADY_EXISTS,
                "Room already exists with number: " + roomRequestDto.getRoomNumber() + 
                " in hotel: " + roomRequestDto.getHotelId()
            );
        }
        Room room = Room.builder()
            .roomNumber(roomRequestDto.getRoomNumber())
            .hotelId(roomRequestDto.getHotelId())
            .roomType(roomRequestDto.getRoomType())
            .floor(roomRequestDto.getFloor())
            .maxOccupancy(roomRequestDto.getMaxOccupancy())
            .bedType(roomRequestDto.getBedType())
            .roomSizeSqft(roomRequestDto.getRoomSizeSqft())
            .pricePerNight(roomRequestDto.getPricePerNight())
            .amenities(roomRequestDto.getAmenities())
            .status(roomRequestDto.getStatus())
            .isSmokingAllowed(roomRequestDto.getIsSmokingAllowed())
            .hasBalcony(roomRequestDto.getHasBalcony())
            .hasSeaView(roomRequestDto.getHasSeaView())
            .hasCityView(roomRequestDto.getHasCityView())
            .isAccessible(roomRequestDto.getIsAccessible())
            .description(roomRequestDto.getDescription())
            .build();
        
        Room savedRoom = roomRepository.save(room);
        log.info("Room created successfully with ID: {}", savedRoom.getId());
        
        // Send notification to Slack
        try {
            Map<String, Object> roomData = new HashMap<>();
            roomData.put("roomId", savedRoom.getId());
            roomData.put("roomNumber", savedRoom.getRoomNumber());
            roomData.put("hotelId", savedRoom.getHotelId());
            roomData.put("roomType", savedRoom.getRoomType());
            roomData.put("floor", savedRoom.getFloor());
            roomData.put("maxOccupancy", savedRoom.getMaxOccupancy());
            roomData.put("pricePerNight", savedRoom.getPricePerNight());
            roomData.put("status", savedRoom.getStatus());
            roomData.put("amenities", savedRoom.getAmenities());
            
            notificationClient.handleRoomEvent("ROOM_CREATED", roomData);
            log.info("Room creation notification sent successfully");
        } catch (Exception e) {
            log.error("Failed to send room creation notification: {}", e.getMessage());
            // Don't fail the room creation if notification fails
        }
        
        return convertToResponseDto(savedRoom);
    }

    @Override
    public RoomResponseDto getRoomById(Long id) {
        log.info("Fetching room by ID: {}", id);
        
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND, "Room not found with ID: " + id));
        
        return convertToResponseDto(room);
    }

    @Override
    public List<RoomListResponseDto> getAllRooms() {
        log.info("Fetching all rooms");
        
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoomResponseDto updateRoom(Long id, UpdateRoomRequest updateRequest) {
        log.info("Updating room with ID: {}", id);
        
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND, "Room not found with ID: " + id));
        
        // Check if room number is being changed and if it conflicts
        if (updateRequest.getRoomNumber() != null && !updateRequest.getRoomNumber().equals(room.getRoomNumber())) {
            if (roomRepository.existsByRoomNumberAndHotelId(updateRequest.getRoomNumber(), 
                    updateRequest.getHotelId() != null ? updateRequest.getHotelId() : room.getHotelId())) {
                throw new CustomException(
                    ErrorCode.ROOM_NUMBER_ALREADY_EXISTS,
                    "Room number already exists: " + updateRequest.getRoomNumber()
                );
            }
        }
        
        // Update fields if provided using builder pattern
        Room.RoomBuilder roomBuilder = room.toBuilder();
        
        if (updateRequest.getRoomNumber() != null) roomBuilder.roomNumber(updateRequest.getRoomNumber());
        if (updateRequest.getHotelId() != null) roomBuilder.hotelId(updateRequest.getHotelId());
        if (updateRequest.getRoomType() != null) roomBuilder.roomType(updateRequest.getRoomType());
        if (updateRequest.getFloor() != null) roomBuilder.floor(updateRequest.getFloor());
        if (updateRequest.getMaxOccupancy() != null) roomBuilder.maxOccupancy(updateRequest.getMaxOccupancy());
        if (updateRequest.getBedType() != null) roomBuilder.bedType(updateRequest.getBedType());
        if (updateRequest.getRoomSizeSqft() != null) roomBuilder.roomSizeSqft(updateRequest.getRoomSizeSqft());
        if (updateRequest.getPricePerNight() != null) roomBuilder.pricePerNight(updateRequest.getPricePerNight());
        if (updateRequest.getAmenities() != null) roomBuilder.amenities(updateRequest.getAmenities());
        if (updateRequest.getStatus() != null) roomBuilder.status(updateRequest.getStatus());
        if (updateRequest.getIsSmokingAllowed() != null) roomBuilder.isSmokingAllowed(updateRequest.getIsSmokingAllowed());
        if (updateRequest.getHasBalcony() != null) roomBuilder.hasBalcony(updateRequest.getHasBalcony());
        if (updateRequest.getHasSeaView() != null) roomBuilder.hasSeaView(updateRequest.getHasSeaView());
        if (updateRequest.getHasCityView() != null) roomBuilder.hasCityView(updateRequest.getHasCityView());
        if (updateRequest.getIsAccessible() != null) roomBuilder.isAccessible(updateRequest.getIsAccessible());
        if (updateRequest.getDescription() != null) roomBuilder.description(updateRequest.getDescription());
        
        Room updatedRoom = roomBuilder.build();
        updatedRoom = roomRepository.save(updatedRoom);
        log.info("Room updated successfully with ID: {}", updatedRoom.getId());
        
        return convertToResponseDto(updatedRoom);
    }

    @Override
    @Transactional
    public Map<String, String> deleteRoom(Long id) {
        log.info("Deleting room with ID: {}", id);
        
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND, "Room not found with ID: " + id));
        
        roomRepository.delete(room);
        log.info("Room deleted successfully with ID: {}", id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Room deleted successfully");
        response.put("roomId", id.toString());
        return response;
    }

    @Override
    public List<RoomListResponseDto> getRoomsByHotelId(Long hotelId) {
        log.info("Fetching rooms by hotel ID: {}", hotelId);
        
        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        return rooms.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomListResponseDto> getAvailableRoomsByHotelId(Long hotelId) {
        log.info("Fetching available rooms by hotel ID: {}", hotelId);
        
        List<Room> rooms = roomRepository.findAvailableRoomsByHotelId(hotelId);
        return rooms.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomListResponseDto> getRoomsByHotelIdAndStatus(Long hotelId, String status) {
        log.info("Fetching rooms by hotel ID: {} and status: {}", hotelId, status);
        
        try {
            RoomStatus roomStatus = RoomStatus.valueOf(status.toUpperCase());
            List<Room> rooms = roomRepository.findByHotelIdAndStatus(hotelId, roomStatus);
            return rooms.stream()
                    .map(this::convertToListResponseDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_ROOM_STATUS, "Invalid status: " + status);
        }
    }

    @Override
    public long getRoomCountByHotelId(Long hotelId) {
        log.info("Getting room count for hotel ID: {}", hotelId);
        return roomRepository.countByHotelId(hotelId);
    }

    @Override
    public long getAvailableRoomCountByHotelId(Long hotelId) {
        log.info("Getting available room count for hotel ID: {}", hotelId);
        return roomRepository.countAvailableRoomsByHotelId(hotelId);
    }

    @Override
    public List<RoomListResponseDto> getRoomsByRoomType(String roomType) {
        log.info("Fetching rooms by room type: {}", roomType);
        
        try {
            RoomType type = RoomType.valueOf(roomType.toUpperCase());
            List<Room> rooms = roomRepository.findByRoomType(type);
            return rooms.stream()
                    .map(this::convertToListResponseDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_ROOM_TYPE, "Invalid room type: " + roomType);
        }
    }

    @Override
    public List<RoomListResponseDto> getRoomsByHotelIdAndRoomType(Long hotelId, String roomType) {
        log.info("Fetching rooms by hotel ID: {} and room type: {}", hotelId, roomType);
        
        try {
            RoomType type = RoomType.valueOf(roomType.toUpperCase());
            List<Room> rooms = roomRepository.findByHotelIdAndRoomType(hotelId, type);
            return rooms.stream()
                    .map(this::convertToListResponseDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_ROOM_TYPE, "Invalid room type: " + roomType);
        }
    }

    @Override
    public List<RoomListResponseDto> getAvailableRoomsByHotelIdAndRoomType(Long hotelId, String roomType) {
        log.info("Fetching available rooms by hotel ID: {} and room type: {}", hotelId, roomType);
        
        try {
            RoomType type = RoomType.valueOf(roomType.toUpperCase());
            List<Room> rooms = roomRepository.findAvailableRoomsByHotelIdAndRoomType(hotelId, type);
            return rooms.stream()
                    .map(this::convertToListResponseDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_ROOM_TYPE, "Invalid room type: " + roomType);
        }
    }

    @Override
    public List<RoomListResponseDto> getRoomsByStatus(String status) {
        log.info("Fetching rooms by status: {}", status);
        
        try {
            RoomStatus roomStatus = RoomStatus.valueOf(status.toUpperCase());
            List<Room> rooms = roomRepository.findByStatus(roomStatus);
            return rooms.stream()
                    .map(this::convertToListResponseDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_ROOM_STATUS, "Invalid status: " + status);
        }
    }

    @Override
    @Transactional
    public Map<String, String> updateRoomStatus(Long id, String status) {
        log.info("Updating room status for ID: {} to {}", id, status);
        
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND, "Room not found with ID: " + id));
        
        try {
            RoomStatus newStatus = RoomStatus.valueOf(status.toUpperCase());
            room.setStatus(newStatus);
            roomRepository.save(room);
            
            // Send notification to Slack
            try {
                Map<String, Object> roomData = new HashMap<>();
                roomData.put("roomId", room.getId());
                roomData.put("roomNumber", room.getRoomNumber());
                roomData.put("hotelId", room.getHotelId());
                roomData.put("roomType", room.getRoomType());
                roomData.put("oldStatus", room.getStatus());
                roomData.put("newStatus", newStatus);
                
                notificationClient.handleRoomEvent("ROOM_STATUS_UPDATED", roomData);
                log.info("Room status update notification sent successfully");
            } catch (Exception e) {
                log.error("Failed to send room status update notification: {}", e.getMessage());
                // Don't fail the room status update if notification fails
            }
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Room status updated successfully");
            response.put("roomId", id.toString());
            response.put("newStatus", status);
            return response;
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_ROOM_STATUS, "Invalid status: " + status);
        }
    }

    @Override
    public List<RoomListResponseDto> getRoomsByHotelIdAndFloor(Long hotelId, Integer floor) {
        log.info("Fetching rooms by hotel ID: {} and floor: {}", hotelId, floor);
        
        List<Room> rooms = roomRepository.findByHotelIdAndFloor(hotelId, floor);
        return rooms.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomListResponseDto> searchRoomsByRoomNumber(String roomNumber) {
        log.info("Searching rooms by room number: {}", roomNumber);
        
        List<Room> rooms = roomRepository.findByRoomNumberContaining(roomNumber);
        return rooms.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomListResponseDto> getRoomsByMaxOccupancy(Integer maxOccupancy) {
        log.info("Fetching rooms by max occupancy: {}", maxOccupancy);
        
        List<Room> rooms = roomRepository.findByMaxOccupancy(maxOccupancy);
        return rooms.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomListResponseDto> getRoomsByHotelIdAndMaxOccupancy(Long hotelId, Integer maxOccupancy) {
        log.info("Fetching rooms by hotel ID: {} and max occupancy: {}", hotelId, maxOccupancy);
        
        List<Room> rooms = roomRepository.findByHotelIdAndMaxOccupancy(hotelId, maxOccupancy);
        return rooms.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomListResponseDto> getAvailableRoomsByHotelIdAndMinOccupancy(Long hotelId, Integer minOccupancy) {
        log.info("Fetching available rooms by hotel ID: {} and min occupancy: {}", hotelId, minOccupancy);
        
        List<Room> rooms = roomRepository.findAvailableRoomsByHotelIdAndMinOccupancy(hotelId, minOccupancy);
        return rooms.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomListResponseDto> getRoomsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Fetching rooms by price range: {} - {}", minPrice, maxPrice);
        
        List<Room> rooms = roomRepository.findByPricePerNightBetween(minPrice, maxPrice);
        return rooms.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomListResponseDto> getRoomsByHotelIdAndPriceRange(Long hotelId, BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Fetching rooms by hotel ID: {} and price range: {} - {}", hotelId, minPrice, maxPrice);
        
        List<Room> rooms = roomRepository.findByHotelIdAndPricePerNightBetween(hotelId, minPrice, maxPrice);
        return rooms.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomListResponseDto> getRoomsByAmenities(String amenity) {
        log.info("Fetching rooms by amenities containing: {}", amenity);
        
        List<Room> rooms = roomRepository.findByAmenitiesContaining(amenity);
        return rooms.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomListResponseDto> getRoomsByHotelIdAndAmenities(Long hotelId, String amenity) {
        log.info("Fetching rooms by hotel ID: {} and amenities containing: {}", hotelId, amenity);
        
        List<Room> rooms = roomRepository.findByHotelIdAndAmenitiesContaining(hotelId, amenity);
        return rooms.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomListResponseDto> getRoomsWithSeaView() {
        log.info("Fetching rooms with sea view");
        
        List<Room> rooms = roomRepository.findByHasSeaViewTrue();
        return rooms.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomListResponseDto> getRoomsWithBalcony() {
        log.info("Fetching rooms with balcony");
        
        List<Room> rooms = roomRepository.findByHasBalconyTrue();
        return rooms.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomListResponseDto> getAccessibleRooms() {
        log.info("Fetching accessible rooms");
        
        List<Room> rooms = roomRepository.findByIsAccessibleTrue();
        return rooms.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomListResponseDto> getSmokingAllowedRooms() {
        log.info("Fetching smoking allowed rooms");
        
        List<Room> rooms = roomRepository.findByIsSmokingAllowedTrue();
        return rooms.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomListResponseDto> getRoomsByHotelIdWithSeaView(Long hotelId) {
        log.info("Fetching rooms with sea view by hotel ID: {}", hotelId);
        
        List<Room> rooms = roomRepository.findByHotelIdAndHasSeaViewTrue(hotelId);
        return rooms.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomListResponseDto> getRoomsByHotelIdWithBalcony(Long hotelId) {
        log.info("Fetching rooms with balcony by hotel ID: {}", hotelId);
        
        List<Room> rooms = roomRepository.findByHotelIdAndHasBalconyTrue(hotelId);
        return rooms.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomListResponseDto> getAccessibleRoomsByHotelId(Long hotelId) {
        log.info("Fetching accessible rooms by hotel ID: {}", hotelId);
        
        List<Room> rooms = roomRepository.findByHotelIdAndIsAccessibleTrue(hotelId);
        return rooms.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isRoomAvailable(Long roomId) {
        log.info("Checking availability for room ID: {}", roomId);
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND, "Room not found with ID: " + roomId));
        
        return room.getStatus() == RoomStatus.AVAILABLE;
    }

    @Override
    public boolean isRoomAvailableByRoomNumberAndHotelId(String roomNumber, Long hotelId) {
        log.info("Checking availability for room number: {} in hotel: {}", roomNumber, hotelId);
        
        Room room = roomRepository.findByRoomNumberAndHotelId(roomNumber, hotelId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND, 
                    "Room not found with number: " + roomNumber + " in hotel: " + hotelId));
        
        return room.getStatus() == RoomStatus.AVAILABLE;
    }

    @Override
    public List<RoomListResponseDto> checkRoomAvailability(Long hotelId, String roomType, Integer minOccupancy, BigDecimal maxPrice) {
        log.info("Checking room availability for hotel: {}, type: {}, occupancy: {}, max price: {}", 
                hotelId, roomType, minOccupancy, maxPrice);
        
        List<Room> rooms = roomRepository.findAvailableRoomsByHotelId(hotelId);
        
        return rooms.stream()
                .filter(room -> roomType == null || room.getRoomType().name().equalsIgnoreCase(roomType))
                .filter(room -> minOccupancy == null || room.getMaxOccupancy() >= minOccupancy)
                .filter(room -> maxPrice == null || room.getPricePerNight().compareTo(maxPrice) <= 0)
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getRoomStatisticsByHotelId(Long hotelId) {
        log.info("Getting room statistics for hotel ID: {}", hotelId);
        
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalRooms", roomRepository.countByHotelId(hotelId));
        stats.put("availableRooms", roomRepository.countAvailableRoomsByHotelId(hotelId));
        stats.put("occupiedRooms", roomRepository.countByHotelIdAndStatus(hotelId, RoomStatus.OCCUPIED));
        stats.put("maintenanceRooms", roomRepository.countByHotelIdAndStatus(hotelId, RoomStatus.MAINTENANCE));
        stats.put("outOfOrderRooms", roomRepository.countByHotelIdAndStatus(hotelId, RoomStatus.OUT_OF_ORDER));
        
        return stats;
    }

    @Override
    public Map<String, Long> getRoomStatisticsByStatus(String status) {
        log.info("Getting room statistics for status: {}", status);
        
        try {
            RoomStatus roomStatus = RoomStatus.valueOf(status.toUpperCase());
            Map<String, Long> stats = new HashMap<>();
            stats.put("totalRooms", roomRepository.countByStatus(roomStatus));
            return stats;
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_ROOM_STATUS, "Invalid status: " + status);
        }
    }

    @Override
    public Map<String, Long> getOverallRoomStatistics() {
        log.info("Getting overall room statistics");
        
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalRooms", roomRepository.count());
        stats.put("availableRooms", roomRepository.countByStatus(RoomStatus.AVAILABLE));
        stats.put("occupiedRooms", roomRepository.countByStatus(RoomStatus.OCCUPIED));
        stats.put("maintenanceRooms", roomRepository.countByStatus(RoomStatus.MAINTENANCE));
        stats.put("outOfOrderRooms", roomRepository.countByStatus(RoomStatus.OUT_OF_ORDER));
        stats.put("cleaningRooms", roomRepository.countByStatus(RoomStatus.CLEANING));
        stats.put("reservedRooms", roomRepository.countByStatus(RoomStatus.RESERVED));
        
        return stats;
    }

    private RoomResponseDto convertToResponseDto(Room room) {
        return RoomResponseDto.builder()
            .id(room.getId())
            .roomNumber(room.getRoomNumber())
            .hotelId(room.getHotelId())
            .roomType(room.getRoomType())
            .floor(room.getFloor())
            .maxOccupancy(room.getMaxOccupancy())
            .bedType(room.getBedType())
            .roomSizeSqft(room.getRoomSizeSqft())
            .pricePerNight(room.getPricePerNight())
            .amenities(room.getAmenities())
            .status(room.getStatus())
            .isSmokingAllowed(room.getIsSmokingAllowed())
            .hasBalcony(room.getHasBalcony())
            .hasSeaView(room.getHasSeaView())
            .hasCityView(room.getHasCityView())
            .isAccessible(room.getIsAccessible())
            .description(room.getDescription())
            .createdAt(room.getCreatedAt())
            .updatedAt(room.getUpdatedAt())
            .build();
    }

    private RoomListResponseDto convertToListResponseDto(Room room) {
        return RoomListResponseDto.builder()
            .id(room.getId())
            .roomNumber(room.getRoomNumber())
            .hotelId(room.getHotelId())
            .roomType(room.getRoomType())
            .floor(room.getFloor())
            .maxOccupancy(room.getMaxOccupancy())
            .bedType(room.getBedType())
            .pricePerNight(room.getPricePerNight())
            .status(room.getStatus())
            .isSmokingAllowed(room.getIsSmokingAllowed())
            .hasBalcony(room.getHasBalcony())
            .hasSeaView(room.getHasSeaView())
            .hasCityView(room.getHasCityView())
            .isAccessible(room.getIsAccessible())
            .build();
    }
}
