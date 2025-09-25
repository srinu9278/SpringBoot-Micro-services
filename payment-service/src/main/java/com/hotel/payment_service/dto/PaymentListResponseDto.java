package com.hotel.payment_service.dto;

import com.hotel.payment_service.enums.PaymentMethod;
import com.hotel.payment_service.enums.PaymentStatus;
import com.hotel.payment_service.enums.TransactionType;
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
public class PaymentListResponseDto {
    
    private Long id;
    private String paymentReference;
    private Long bookingId;
    private Long userId;
    private Long hotelId;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private TransactionType transactionType;
    private String gatewayName;
    private String cardLastFour;
    private String cardBrand;
    private BigDecimal refundAmount;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
}

