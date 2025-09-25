package com.hotel.room_service.model;

import com.hotel.room_service.enums.RoomStatus;
import com.hotel.room_service.enums.RoomType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "room_number", nullable = false, unique = true)
    private String roomNumber;
    
    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;
    
    @Column(name = "floor", nullable = false)
    private Integer floor;
    
    @Column(name = "max_occupancy", nullable = false)
    private Integer maxOccupancy;
    
    @Column(name = "bed_type", nullable = false)
    private String bedType;
    
    @Column(name = "room_size_sqft")
    private Integer roomSizeSqft;
    
    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;
    
    @Column(name = "amenities", columnDefinition = "TEXT")
    private String amenities;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RoomStatus status;
    
    @Column(name = "is_smoking_allowed")
    private Boolean isSmokingAllowed = false;
    
    @Column(name = "has_balcony")
    private Boolean hasBalcony = false;
    
    @Column(name = "has_sea_view")
    private Boolean hasSeaView = false;
    
    @Column(name = "has_city_view")
    private Boolean hasCityView = false;
    
    @Column(name = "is_accessible")
    private Boolean isAccessible = false;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
