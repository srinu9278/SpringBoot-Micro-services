package com.hotel.room_service.repository;

import com.hotel.room_service.enums.RoomStatus;
import com.hotel.room_service.enums.RoomType;
import com.hotel.room_service.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    // Find rooms by hotel ID
    List<Room> findByHotelId(Long hotelId);
    
    // Find rooms by hotel ID and status
    List<Room> findByHotelIdAndStatus(Long hotelId, RoomStatus status);
    
    // Find rooms by room type
    List<Room> findByRoomType(RoomType roomType);
    
    // Find rooms by hotel ID and room type
    List<Room> findByHotelIdAndRoomType(Long hotelId, RoomType roomType);
    
    // Find rooms by status
    List<Room> findByStatus(RoomStatus status);
    
    // Find rooms by floor
    List<Room> findByFloor(Integer floor);
    
    // Find rooms by hotel ID and floor
    List<Room> findByHotelIdAndFloor(Long hotelId, Integer floor);
    
    // Find rooms by max occupancy
    List<Room> findByMaxOccupancy(Integer maxOccupancy);
    
    // Find rooms by hotel ID and max occupancy
    List<Room> findByHotelIdAndMaxOccupancy(Long hotelId, Integer maxOccupancy);
    
    // Find rooms by price range
    List<Room> findByPricePerNightBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Find rooms by hotel ID and price range
    List<Room> findByHotelIdAndPricePerNightBetween(Long hotelId, BigDecimal minPrice, BigDecimal maxPrice);
    
    // Find available rooms by hotel ID
    @Query("SELECT r FROM Room r WHERE r.hotelId = :hotelId AND r.status = 'AVAILABLE'")
    List<Room> findAvailableRoomsByHotelId(@Param("hotelId") Long hotelId);
    
    // Find available rooms by hotel ID and room type
    @Query("SELECT r FROM Room r WHERE r.hotelId = :hotelId AND r.roomType = :roomType AND r.status = 'AVAILABLE'")
    List<Room> findAvailableRoomsByHotelIdAndRoomType(@Param("hotelId") Long hotelId, @Param("roomType") RoomType roomType);
    
    // Find available rooms by hotel ID and max occupancy
    @Query("SELECT r FROM Room r WHERE r.hotelId = :hotelId AND r.maxOccupancy >= :minOccupancy AND r.status = 'AVAILABLE'")
    List<Room> findAvailableRoomsByHotelIdAndMinOccupancy(@Param("hotelId") Long hotelId, @Param("minOccupancy") Integer minOccupancy);
    
    // Find rooms by amenities (contains)
    @Query("SELECT r FROM Room r WHERE r.amenities LIKE %:amenity%")
    List<Room> findByAmenitiesContaining(@Param("amenity") String amenity);
    
    // Find rooms by hotel ID and amenities
    @Query("SELECT r FROM Room r WHERE r.hotelId = :hotelId AND r.amenities LIKE %:amenity%")
    List<Room> findByHotelIdAndAmenitiesContaining(@Param("hotelId") Long hotelId, @Param("amenity") String amenity);
    
    // Find rooms with sea view
    List<Room> findByHasSeaViewTrue();
    
    // Find rooms with balcony
    List<Room> findByHasBalconyTrue();
    
    // Find accessible rooms
    List<Room> findByIsAccessibleTrue();
    
    // Find smoking allowed rooms
    List<Room> findByIsSmokingAllowedTrue();
    
    // Find rooms by hotel ID with sea view
    List<Room> findByHotelIdAndHasSeaViewTrue(Long hotelId);
    
    // Find rooms by hotel ID with balcony
    List<Room> findByHotelIdAndHasBalconyTrue(Long hotelId);
    
    // Find accessible rooms by hotel ID
    List<Room> findByHotelIdAndIsAccessibleTrue(Long hotelId);
    
    // Count rooms by hotel ID
    long countByHotelId(Long hotelId);
    
    // Count rooms by hotel ID and status
    long countByHotelIdAndStatus(Long hotelId, RoomStatus status);
    
    // Count rooms by status
    long countByStatus(RoomStatus status);
    
    // Count available rooms by hotel ID
    @Query("SELECT COUNT(r) FROM Room r WHERE r.hotelId = :hotelId AND r.status = 'AVAILABLE'")
    long countAvailableRoomsByHotelId(@Param("hotelId") Long hotelId);
    
    // Check if room number exists in hotel
    boolean existsByRoomNumberAndHotelId(String roomNumber, Long hotelId);
    
    // Find room by room number and hotel ID
    Optional<Room> findByRoomNumberAndHotelId(String roomNumber, Long hotelId);
    
    // Find rooms by room number containing
    List<Room> findByRoomNumberContaining(String roomNumber);
    
    // Find rooms by hotel ID ordered by room number
    List<Room> findByHotelIdOrderByRoomNumber(Long hotelId);
    
    // Find rooms by hotel ID and status ordered by room number
    List<Room> findByHotelIdAndStatusOrderByRoomNumber(Long hotelId, RoomStatus status);
    
    // Find rooms by hotel ID ordered by price
    List<Room> findByHotelIdOrderByPricePerNight(Long hotelId);
    
    // Find rooms by hotel ID and status ordered by price
    List<Room> findByHotelIdAndStatusOrderByPricePerNight(Long hotelId, RoomStatus status);
}
