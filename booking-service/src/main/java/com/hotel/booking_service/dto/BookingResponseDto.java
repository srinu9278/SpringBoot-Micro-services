package com.hotel.booking_service.dto;

import com.hotel.booking_service.enums.BookingStatus;
import com.hotel.booking_service.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookingResponseDto {
    
    private Long id;
    private String bookingReference;
    private Long hotelId;
    private Long roomId;
    private Long userId;
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private Integer numberOfGuests;
    private Integer numberOfRooms;
    private Integer totalNights;
    private BigDecimal roomPricePerNight;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private String specialRequests;
    private String cancellationReason;
    private LocalDateTime cancelledAt;
    private Long cancelledBy;
    private Boolean confirmationSent;
    private LocalDateTime confirmationSentAt;
    private Boolean reminderSent;
    private LocalDateTime reminderSentAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

