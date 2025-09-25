package com.hotel.payment_service.dto;

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
public class RefundRequestDto {
    
    @NotNull(message = "Payment ID is required")
    private Long paymentId;
    
    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "0.01", message = "Refund amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Refund amount must have at most 8 integer digits and 2 decimal places")
    private BigDecimal refundAmount;
    
    @NotBlank(message = "Refund reason is required")
    @Size(max = 500, message = "Refund reason must be at most 500 characters")
    private String refundReason;
    
    private String refundReference;
    
    @Size(max = 500, message = "Notes must be at most 500 characters")
    private String notes;
}

