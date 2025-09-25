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
public class BookingListResponseDto {
    
    private Long id;
    private String bookingReference;
    private Long hotelId;
    private Long roomId;
    private Long userId;
    private String guestName;
    private String guestEmail;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private Integer numberOfGuests;
    private Integer totalNights;
    private BigDecimal finalAmount;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
}

