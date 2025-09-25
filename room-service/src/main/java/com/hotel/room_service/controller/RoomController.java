package com.hotel.room_service.controller;

import com.hotel.room_service.dto.RoomListResponseDto;
import com.hotel.room_service.dto.RoomRequestDto;
import com.hotel.room_service.dto.RoomResponseDto;
import com.hotel.room_service.dto.UpdateRoomRequest;
import com.hotel.room_service.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/room")
@CrossOrigin("http://localhost:5173/")
public class RoomController {
    
    @Autowired
    private RoomService roomService;

    // Basic CRUD operations
    @PostMapping(value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoomResponseDto> createRoom(
            @RequestBody @Validated RoomRequestDto roomRequestDto) {
        return ResponseEntity.ok(roomService.createRoom(roomRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDto> getRoomById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<RoomListResponseDto>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @PutMapping(value = "/update/{id}",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoomResponseDto> updateRoom(@PathVariable("id") Long id, 
                                                     @RequestBody @Validated UpdateRoomRequest updateRequest) {
        return ResponseEntity.ok(roomService.updateRoom(id, updateRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRoom(@PathVariable("id") Long id) {
        return ResponseEntity.ok(roomService.deleteRoom(id));
    }

    // Room management by hotel
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RoomListResponseDto>> getRoomsByHotelId(@PathVariable("hotelId") Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomsByHotelId(hotelId));
    }

    @GetMapping("/hotel/{hotelId}/available")
    public ResponseEntity<List<RoomListResponseDto>> getAvailableRoomsByHotelId(@PathVariable("hotelId") Long hotelId) {
        return ResponseEntity.ok(roomService.getAvailableRoomsByHotelId(hotelId));
    }

    @GetMapping("/hotel/{hotelId}/status/{status}")
    public ResponseEntity<List<RoomListResponseDto>> getRoomsByHotelIdAndStatus(
            @PathVariable("hotelId") Long hotelId, 
            @PathVariable("status") String status) {
        return ResponseEntity.ok(roomService.getRoomsByHotelIdAndStatus(hotelId, status));
    }

    @GetMapping("/hotel/{hotelId}/count")
    public ResponseEntity<Map<String, Long>> getRoomCountByHotelId(@PathVariable("hotelId") Long hotelId) {
        long count = roomService.getRoomCountByHotelId(hotelId);
        Map<String, Long> response = Map.of("totalRooms", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hotel/{hotelId}/available/count")
    public ResponseEntity<Map<String, Long>> getAvailableRoomCountByHotelId(@PathVariable("hotelId") Long hotelId) {
        long count = roomService.getAvailableRoomCountByHotelId(hotelId);
        Map<String, Long> response = Map.of("availableRooms", count);
        return ResponseEntity.ok(response);
    }

    // Room type management
    @GetMapping("/type/{roomType}")
    public ResponseEntity<List<RoomListResponseDto>> getRoomsByRoomType(@PathVariable("roomType") String roomType) {
        return ResponseEntity.ok(roomService.getRoomsByRoomType(roomType));
    }

    @GetMapping("/hotel/{hotelId}/type/{roomType}")
    public ResponseEntity<List<RoomListResponseDto>> getRoomsByHotelIdAndRoomType(
            @PathVariable("hotelId") Long hotelId, 
            @PathVariable("roomType") String roomType) {
        return ResponseEntity.ok(roomService.getRoomsByHotelIdAndRoomType(hotelId, roomType));
    }

    @GetMapping("/hotel/{hotelId}/type/{roomType}/available")
    public ResponseEntity<List<RoomListResponseDto>> getAvailableRoomsByHotelIdAndRoomType(
            @PathVariable("hotelId") Long hotelId, 
            @PathVariable("roomType") String roomType) {
        return ResponseEntity.ok(roomService.getAvailableRoomsByHotelIdAndRoomType(hotelId, roomType));
    }

    // Room status management
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RoomListResponseDto>> getRoomsByStatus(@PathVariable("status") String status) {
        return ResponseEntity.ok(roomService.getRoomsByStatus(status));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> updateRoomStatus(@PathVariable("id") Long id, 
                                                               @RequestParam("status") String status) {
        return ResponseEntity.ok(roomService.updateRoomStatus(id, status));
    }

    @GetMapping("/hotel/{hotelId}/floor/{floor}")
    public ResponseEntity<List<RoomListResponseDto>> getRoomsByHotelIdAndFloor(
            @PathVariable("hotelId") Long hotelId, 
            @PathVariable("floor") Integer floor) {
        return ResponseEntity.ok(roomService.getRoomsByHotelIdAndFloor(hotelId, floor));
    }

    // Room search and filtering
    @GetMapping("/search")
    public ResponseEntity<List<RoomListResponseDto>> searchRoomsByRoomNumber(@RequestParam("roomNumber") String roomNumber) {
        return ResponseEntity.ok(roomService.searchRoomsByRoomNumber(roomNumber));
    }

    @GetMapping("/occupancy/{maxOccupancy}")
    public ResponseEntity<List<RoomListResponseDto>> getRoomsByMaxOccupancy(@PathVariable("maxOccupancy") Integer maxOccupancy) {
        return ResponseEntity.ok(roomService.getRoomsByMaxOccupancy(maxOccupancy));
    }

    @GetMapping("/hotel/{hotelId}/occupancy/{maxOccupancy}")
    public ResponseEntity<List<RoomListResponseDto>> getRoomsByHotelIdAndMaxOccupancy(
            @PathVariable("hotelId") Long hotelId, 
            @PathVariable("maxOccupancy") Integer maxOccupancy) {
        return ResponseEntity.ok(roomService.getRoomsByHotelIdAndMaxOccupancy(hotelId, maxOccupancy));
    }

    @GetMapping("/hotel/{hotelId}/min-occupancy/{minOccupancy}")
    public ResponseEntity<List<RoomListResponseDto>> getAvailableRoomsByHotelIdAndMinOccupancy(
            @PathVariable("hotelId") Long hotelId, 
            @PathVariable("minOccupancy") Integer minOccupancy) {
        return ResponseEntity.ok(roomService.getAvailableRoomsByHotelIdAndMinOccupancy(hotelId, minOccupancy));
    }

    // Price-based filtering
    @GetMapping("/price-range")
    public ResponseEntity<List<RoomListResponseDto>> getRoomsByPriceRange(
            @RequestParam("minPrice") BigDecimal minPrice, 
            @RequestParam("maxPrice") BigDecimal maxPrice) {
        return ResponseEntity.ok(roomService.getRoomsByPriceRange(minPrice, maxPrice));
    }

    @GetMapping("/hotel/{hotelId}/price-range")
    public ResponseEntity<List<RoomListResponseDto>> getRoomsByHotelIdAndPriceRange(
            @PathVariable("hotelId") Long hotelId,
            @RequestParam("minPrice") BigDecimal minPrice, 
            @RequestParam("maxPrice") BigDecimal maxPrice) {
        return ResponseEntity.ok(roomService.getRoomsByHotelIdAndPriceRange(hotelId, minPrice, maxPrice));
    }

    // Amenity-based filtering
    @GetMapping("/amenities")
    public ResponseEntity<List<RoomListResponseDto>> getRoomsByAmenities(@RequestParam("amenity") String amenity) {
        return ResponseEntity.ok(roomService.getRoomsByAmenities(amenity));
    }

    @GetMapping("/hotel/{hotelId}/amenities")
    public ResponseEntity<List<RoomListResponseDto>> getRoomsByHotelIdAndAmenities(
            @PathVariable("hotelId") Long hotelId, 
            @RequestParam("amenity") String amenity) {
        return ResponseEntity.ok(roomService.getRoomsByHotelIdAndAmenities(hotelId, amenity));
    }

    // Special room features
    @GetMapping("/sea-view")
    public ResponseEntity<List<RoomListResponseDto>> getRoomsWithSeaView() {
        return ResponseEntity.ok(roomService.getRoomsWithSeaView());
    }

    @GetMapping("/balcony")
    public ResponseEntity<List<RoomListResponseDto>> getRoomsWithBalcony() {
        return ResponseEntity.ok(roomService.getRoomsWithBalcony());
    }

    @GetMapping("/accessible")
    public ResponseEntity<List<RoomListResponseDto>> getAccessibleRooms() {
        return ResponseEntity.ok(roomService.getAccessibleRooms());
    }

    @GetMapping("/smoking-allowed")
    public ResponseEntity<List<RoomListResponseDto>> getSmokingAllowedRooms() {
        return ResponseEntity.ok(roomService.getSmokingAllowedRooms());
    }

    @GetMapping("/hotel/{hotelId}/sea-view")
    public ResponseEntity<List<RoomListResponseDto>> getRoomsByHotelIdWithSeaView(@PathVariable("hotelId") Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomsByHotelIdWithSeaView(hotelId));
    }

    @GetMapping("/hotel/{hotelId}/balcony")
    public ResponseEntity<List<RoomListResponseDto>> getRoomsByHotelIdWithBalcony(@PathVariable("hotelId") Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomsByHotelIdWithBalcony(hotelId));
    }

    @GetMapping("/hotel/{hotelId}/accessible")
    public ResponseEntity<List<RoomListResponseDto>> getAccessibleRoomsByHotelId(@PathVariable("hotelId") Long hotelId) {
        return ResponseEntity.ok(roomService.getAccessibleRoomsByHotelId(hotelId));
    }

    // Availability checking
    @GetMapping("/{id}/available")
    public ResponseEntity<Map<String, Boolean>> isRoomAvailable(@PathVariable("id") Long id) {
        boolean available = roomService.isRoomAvailable(id);
        Map<String, Boolean> response = Map.of("available", available);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/availability")
    public ResponseEntity<Map<String, Boolean>> isRoomAvailableByRoomNumberAndHotelId(
            @RequestParam("roomNumber") String roomNumber, 
            @RequestParam("hotelId") Long hotelId) {
        boolean available = roomService.isRoomAvailableByRoomNumberAndHotelId(roomNumber, hotelId);
        Map<String, Boolean> response = Map.of("available", available);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hotel/{hotelId}/check-availability")
    public ResponseEntity<List<RoomListResponseDto>> checkRoomAvailability(
            @PathVariable("hotelId") Long hotelId,
            @RequestParam(value = "roomType", required = false) String roomType,
            @RequestParam(value = "minOccupancy", required = false) Integer minOccupancy,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice) {
        return ResponseEntity.ok(roomService.checkRoomAvailability(hotelId, roomType, minOccupancy, maxPrice));
    }

    // Room statistics
    @GetMapping("/hotel/{hotelId}/statistics")
    public ResponseEntity<Map<String, Long>> getRoomStatisticsByHotelId(@PathVariable("hotelId") Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomStatisticsByHotelId(hotelId));
    }

    @GetMapping("/status/{status}/statistics")
    public ResponseEntity<Map<String, Long>> getRoomStatisticsByStatus(@PathVariable("status") String status) {
        return ResponseEntity.ok(roomService.getRoomStatisticsByStatus(status));
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getOverallRoomStatistics() {
        return ResponseEntity.ok(roomService.getOverallRoomStatistics());
    }
}
