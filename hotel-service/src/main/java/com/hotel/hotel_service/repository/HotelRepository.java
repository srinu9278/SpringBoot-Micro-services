package com.hotel.hotel_service.repository;

import com.hotel.hotel_service.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    
    List<Hotel> findByStatus(Hotel.HotelStatus status);
    
    List<Hotel> findByCity(String city);
    
    List<Hotel> findByCountry(String country);
    
    List<Hotel> findByStarRating(Integer starRating);
    
    @Query("SELECT h FROM Hotel h WHERE h.rating >= :minRating")
    List<Hotel> findByRatingGreaterThanEqual(@Param("minRating") Double minRating);
    
    @Query("SELECT h FROM Hotel h WHERE h.name LIKE %:name%")
    List<Hotel> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT h FROM Hotel h WHERE h.city = :city AND h.status = 'ACTIVE'")
    List<Hotel> findActiveHotelsByCity(@Param("city") String city);
    
    @Query("SELECT h FROM Hotel h WHERE h.country = :country AND h.status = 'ACTIVE'")
    List<Hotel> findActiveHotelsByCountry(@Param("country") String country);
    
    Optional<Hotel> findByNameAndAddress(String name, String address);
    
    @Query("SELECT COUNT(h) FROM Hotel h WHERE h.status = 'ACTIVE'")
    Long countActiveHotels();
}
