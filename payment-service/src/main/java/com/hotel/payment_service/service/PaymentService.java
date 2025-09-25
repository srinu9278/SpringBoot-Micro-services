package com.hotel.payment_service.service;

import com.hotel.payment_service.dto.PaymentListResponseDto;
import com.hotel.payment_service.dto.PaymentRequestDto;
import com.hotel.payment_service.dto.PaymentResponseDto;
import com.hotel.payment_service.dto.RefundRequestDto;
import com.hotel.payment_service.enums.PaymentMethod;
import com.hotel.payment_service.enums.PaymentStatus;
import com.hotel.payment_service.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {
    
    // Basic CRUD operations
    PaymentResponseDto createPayment(PaymentRequestDto paymentRequest);
    PaymentResponseDto getPaymentById(Long id);
    PaymentResponseDto getPaymentByReference(String paymentReference);
    PaymentResponseDto updatePayment(Long id, PaymentRequestDto paymentRequest);
    void deletePayment(Long id);
    
    // Payment processing
    PaymentResponseDto processPayment(Long paymentId);
    PaymentResponseDto cancelPayment(Long paymentId, String reason);
    PaymentResponseDto updatePaymentStatus(Long paymentId, PaymentStatus status);
    
    // Refund operations
    PaymentResponseDto processRefund(RefundRequestDto refundRequest);
    PaymentResponseDto getRefundDetails(Long paymentId);
    List<PaymentResponseDto> getRefundsByPaymentId(Long paymentId);
    
    // Search and filter operations
    Page<PaymentListResponseDto> getAllPayments(Pageable pageable);
    Page<PaymentListResponseDto> searchPayments(
            Long userId, Long hotelId, PaymentStatus status, PaymentMethod paymentMethod,
            TransactionType transactionType, LocalDateTime startDate, LocalDateTime endDate,
            BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable);
    
    // User related operations
    List<PaymentResponseDto> getPaymentsByUserId(Long userId);
    List<PaymentResponseDto> getPaymentsByUserIdAndStatus(Long userId, PaymentStatus status);
    Page<PaymentListResponseDto> getPaymentsByUserId(Long userId, Pageable pageable);
    
    // Hotel related operations
    List<PaymentResponseDto> getPaymentsByHotelId(Long hotelId);
    List<PaymentResponseDto> getPaymentsByHotelIdAndStatus(Long hotelId, PaymentStatus status);
    Page<PaymentListResponseDto> getPaymentsByHotelId(Long hotelId, Pageable pageable);
    
    // Booking related operations
    List<PaymentResponseDto> getPaymentsByBookingId(Long bookingId);
    List<PaymentResponseDto> getPaymentsByBookingIdAndStatus(Long bookingId, PaymentStatus status);
    
    // Status related operations
    List<PaymentResponseDto> getPaymentsByStatus(PaymentStatus status);
    Page<PaymentListResponseDto> getPaymentsByStatus(PaymentStatus status, Pageable pageable);
    
    // Payment method operations
    List<PaymentResponseDto> getPaymentsByPaymentMethod(PaymentMethod paymentMethod);
    List<PaymentResponseDto> getPaymentsByPaymentMethodAndStatus(PaymentMethod paymentMethod, PaymentStatus status);
    
    // Transaction type operations
    List<PaymentResponseDto> getPaymentsByTransactionType(TransactionType transactionType);
    List<PaymentResponseDto> getPaymentsByTransactionTypeAndStatus(TransactionType transactionType, PaymentStatus status);
    
    // Date range operations
    List<PaymentResponseDto> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<PaymentResponseDto> getPaymentsByDateRangeAndStatus(LocalDateTime startDate, LocalDateTime endDate, PaymentStatus status);
    List<PaymentResponseDto> getProcessedPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    // Amount range operations
    List<PaymentResponseDto> getPaymentsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount);
    List<PaymentResponseDto> getPaymentsByAmountRangeAndStatus(BigDecimal minAmount, BigDecimal maxAmount, PaymentStatus status);
    
    // Gateway operations
    List<PaymentResponseDto> getPaymentsByGateway(String gatewayName);
    List<PaymentResponseDto> getPaymentsByGatewayAndStatus(String gatewayName, PaymentStatus status);
    
    // Refund operations
    List<PaymentResponseDto> getRefundedPayments();
    List<PaymentResponseDto> getRefundedPaymentsByAmount(BigDecimal minAmount);
    List<PaymentResponseDto> getRefundedPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    // Statistics operations
    long getPaymentCountByStatus(PaymentStatus status);
    long getPaymentCountByUserIdAndStatus(Long userId, PaymentStatus status);
    long getPaymentCountByHotelIdAndStatus(Long hotelId, PaymentStatus status);
    
    BigDecimal getTotalAmountByStatus(PaymentStatus status);
    BigDecimal getTotalAmountByUserIdAndStatus(Long userId, PaymentStatus status);
    BigDecimal getTotalAmountByHotelIdAndStatus(Long hotelId, PaymentStatus status);
    BigDecimal getTotalRefundAmount();
    BigDecimal getTotalRefundAmountByHotelId(Long hotelId);
    
    // Recent operations
    Page<PaymentListResponseDto> getRecentPayments(Pageable pageable);
    List<PaymentResponseDto> getFailedPaymentsSince(LocalDateTime since);
    
    // Utility operations
    boolean isPaymentRefundable(Long paymentId);
    boolean isPaymentExpired(Long paymentId);
    String generatePaymentReference();
    String generateRefundReference();
}


