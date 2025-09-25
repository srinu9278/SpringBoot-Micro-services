package com.hotel.hotel_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hotels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Hotel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "address", nullable = false)
    private String address;
    
    @Column(name = "city", nullable = false)
    private String city;
    
    @Column(name = "state")
    private String state;
    
    @Column(name = "country", nullable = false)
    private String country;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "website")
    private String website;
    
    @Column(name = "rating")
    private Double rating;
    
    @Column(name = "star_rating")
    private Integer starRating;
    
    @Column(name = "total_rooms")
    private Integer totalRooms;
    
    @Column(name = "amenities", columnDefinition = "TEXT")
    private String amenities; // JSON string of amenities
    
    @Column(name = "images", columnDefinition = "TEXT")
    private String images; // JSON string of image file paths
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private HotelStatus status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum HotelStatus {
        ACTIVE, INACTIVE, MAINTENANCE, SUSPENDED
    }
}
