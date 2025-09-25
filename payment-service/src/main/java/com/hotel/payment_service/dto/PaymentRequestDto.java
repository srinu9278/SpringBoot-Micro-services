package com.hotel.payment_service.dto;

import com.hotel.payment_service.enums.PaymentMethod;
import com.hotel.payment_service.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PaymentRequestDto {
    
    @NotNull(message = "Booking ID is required")
    private Long bookingId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Hotel ID is required")
    private Long hotelId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Amount must have at most 8 integer digits and 2 decimal places")
    private BigDecimal amount;
    
    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    private String currency;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    
    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;
    
    private String gatewayName;
    
    @Size(max = 4, message = "Card last four digits must be at most 4 characters")
    private String cardLastFour;
    
    @Size(max = 20, message = "Card brand must be at most 20 characters")
    private String cardBrand;
    
    private String metadata;
    
    @Size(max = 45, message = "IP address must be at most 45 characters")
    private String ipAddress;
    
    @Size(max = 500, message = "User agent must be at most 500 characters")
    private String userAgent;
}

