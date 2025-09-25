package com.hotel.booking_service.model;

import com.hotel.booking_service.enums.BookingStatus;
import com.hotel.booking_service.enums.PaymentStatus;
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
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "booking_reference", nullable = false, unique = true)
    private String bookingReference;
    
    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;
    
    @Column(name = "room_id", nullable = false)
    private Long roomId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "guest_name", nullable = false)
    private String guestName;
    
    @Column(name = "guest_email", nullable = false)
    private String guestEmail;
    
    @Column(name = "guest_phone")
    private String guestPhone;
    
    @Column(name = "check_in_date", nullable = false)
    private LocalDateTime checkInDate;
    
    @Column(name = "check_out_date", nullable = false)
    private LocalDateTime checkOutDate;
    
    @Column(name = "number_of_guests", nullable = false)
    private Integer numberOfGuests;
    
    @Column(name = "number_of_rooms", nullable = false)
    private Integer numberOfRooms = 1;
    
    @Column(name = "total_nights")
    private Integer totalNights;
    
    @Column(name = "room_price_per_night", precision = 10, scale = 2)
    private BigDecimal roomPricePerNight;
    
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount;
    
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;
    
    @Column(name = "final_amount", precision = 10, scale = 2)
    private BigDecimal finalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;
    
    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;
    
    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @Column(name = "cancelled_by")
    private Long cancelledBy;
    
    @Column(name = "confirmation_sent")
    private Boolean confirmationSent = false;
    
    @Column(name = "confirmation_sent_at")
    private LocalDateTime confirmationSentAt;
    
    @Column(name = "reminder_sent")
    private Boolean reminderSent = false;
    
    @Column(name = "reminder_sent_at")
    private LocalDateTime reminderSentAt;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
