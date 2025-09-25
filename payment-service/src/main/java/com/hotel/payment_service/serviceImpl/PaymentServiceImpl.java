package com.hotel.payment_service.serviceImpl;

import com.hotel.payment_service.client.NotificationClient;
import com.hotel.payment_service.dto.PaymentListResponseDto;
import com.hotel.payment_service.dto.PaymentRequestDto;
import com.hotel.payment_service.dto.PaymentResponseDto;
import com.hotel.payment_service.dto.RefundRequestDto;
import com.hotel.payment_service.enums.ErrorCode;
import com.hotel.payment_service.enums.PaymentMethod;
import com.hotel.payment_service.enums.PaymentStatus;
import com.hotel.payment_service.enums.TransactionType;
import com.hotel.payment_service.exception.CustomException;
import com.hotel.payment_service.model.Payment;
import com.hotel.payment_service.repository.PaymentRepository;
import com.hotel.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final NotificationClient notificationClient;
    
    @Override
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequest) {
        log.info("Creating payment for booking ID: {}", paymentRequest.getBookingId());
        
        // Generate payment reference
        String paymentReference = generatePaymentReference();
        Long count = paymentRepository.findByHotelIdFromHotelTable(paymentRequest.getHotelId());
        if(count==0){
            throw new CustomException(ErrorCode.HOTELID_NOT_FOUND,"HotelId not found");
        }
        
        // Create payment entity
        Payment payment = Payment.builder()
                .paymentReference(paymentReference)
                .bookingId(paymentRequest.getBookingId())
                .userId(paymentRequest.getUserId())
                .hotelId(paymentRequest.getHotelId())
                .amount(paymentRequest.getAmount())
                .currency(paymentRequest.getCurrency())
                .paymentMethod(paymentRequest.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .transactionType(paymentRequest.getTransactionType())
                .gatewayName(paymentRequest.getGatewayName())
                .cardLastFour(paymentRequest.getCardLastFour())
                .cardBrand(paymentRequest.getCardBrand())
                .metadata(paymentRequest.getMetadata())
                .ipAddress(paymentRequest.getIpAddress())
                .userAgent(paymentRequest.getUserAgent())
                .expiresAt(LocalDateTime.now().plusHours(24)) // 24 hours expiry
                .build();
        
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created successfully with ID: {}", savedPayment.getId());
        
        // Send notification to Slack
        try {
            Map<String, Object> paymentData = new HashMap<>();
            paymentData.put("paymentId", savedPayment.getId());
            paymentData.put("paymentReference", savedPayment.getPaymentReference());
            paymentData.put("bookingId", savedPayment.getBookingId());
            paymentData.put("userId", savedPayment.getUserId());
            paymentData.put("hotelId", savedPayment.getHotelId());
            paymentData.put("amount", savedPayment.getAmount());
            paymentData.put("currency", savedPayment.getCurrency());
            paymentData.put("paymentMethod", savedPayment.getPaymentMethod());
            paymentData.put("status", savedPayment.getStatus());
            paymentData.put("transactionType", savedPayment.getTransactionType());
            
            notificationClient.handlePaymentEvent("PAYMENT_CREATED", paymentData);
            log.info("Payment creation notification sent successfully");
        } catch (Exception e) {
            log.error("Failed to send payment creation notification: {}", e.getMessage());
            // Don't fail the payment creation if notification fails
        }
        
        return convertToResponseDto(savedPayment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentById(Long id) {
        log.info("Fetching payment by ID: {}", id);
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND, "Payment not found with ID: " + id));
        
        return convertToResponseDto(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentByReference(String paymentReference) {
        log.info("Fetching payment by reference: {}", paymentReference);
        
        Payment payment = paymentRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND, "Payment not found with reference: " + paymentReference));
        
        return convertToResponseDto(payment);
    }
    
    @Override
    public PaymentResponseDto updatePayment(Long id, PaymentRequestDto paymentRequest) {
        log.info("Updating payment with ID: {}", id);
        
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND, "Payment not found with ID: " + id));
        
        // Update payment fields
        Payment updatedPayment = existingPayment.toBuilder()
                .bookingId(paymentRequest.getBookingId())
                .userId(paymentRequest.getUserId())
                .hotelId(paymentRequest.getHotelId())
                .amount(paymentRequest.getAmount())
                .currency(paymentRequest.getCurrency())
                .paymentMethod(paymentRequest.getPaymentMethod())
                .transactionType(paymentRequest.getTransactionType())
                .gatewayName(paymentRequest.getGatewayName())
                .cardLastFour(paymentRequest.getCardLastFour())
                .cardBrand(paymentRequest.getCardBrand())
                .metadata(paymentRequest.getMetadata())
                .ipAddress(paymentRequest.getIpAddress())
                .userAgent(paymentRequest.getUserAgent())
                .build();
        
        Payment savedPayment = paymentRepository.save(updatedPayment);
        log.info("Payment updated successfully with ID: {}", savedPayment.getId());
        
        return convertToResponseDto(savedPayment);
    }
    
    @Override
    public void deletePayment(Long id) {
        log.info("Deleting payment with ID: {}", id);
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND, "Payment not found with ID: " + id));
        
        paymentRepository.delete(payment);
        log.info("Payment deleted successfully with ID: {}", id);
    }
    
    @Override
    public PaymentResponseDto processPayment(Long paymentId) {
        log.info("Processing payment with ID: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND, "Payment not found with ID: " + paymentId));
        
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS, "Payment is not in pending status");
        }
        
        if (isPaymentExpired(paymentId)) {
            payment = payment.toBuilder()
                    .status(PaymentStatus.EXPIRED)
                    .build();
            paymentRepository.save(payment);
            throw new CustomException(ErrorCode.PAYMENT_EXPIRED, "Payment has expired");
        }
        
        // Simulate payment processing
        try {
            // In real implementation, integrate with payment gateway
            String gatewayTransactionId = "TXN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
            
            payment = payment.toBuilder()
                    .status(PaymentStatus.COMPLETED)
                    .gatewayTransactionId(gatewayTransactionId)
                    .gatewayResponse("SUCCESS")
                    .processedAt(LocalDateTime.now())
                    .build();
            
            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment processed successfully with ID: {}", savedPayment.getId());
            
            // Send notification to Slack
            try {
                Map<String, Object> paymentData = new HashMap<>();
                paymentData.put("paymentId", savedPayment.getId());
                paymentData.put("paymentReference", savedPayment.getPaymentReference());
                paymentData.put("bookingId", savedPayment.getBookingId());
                paymentData.put("userId", savedPayment.getUserId());
                paymentData.put("hotelId", savedPayment.getHotelId());
                paymentData.put("amount", savedPayment.getAmount());
                paymentData.put("currency", savedPayment.getCurrency());
                paymentData.put("paymentMethod", savedPayment.getPaymentMethod());
                paymentData.put("status", savedPayment.getStatus());
                paymentData.put("gatewayTransactionId", savedPayment.getGatewayTransactionId());
                paymentData.put("gatewayResponse", savedPayment.getGatewayResponse());
                
                notificationClient.handlePaymentEvent("PAYMENT_COMPLETED", paymentData);
                log.info("Payment completion notification sent successfully");
            } catch (Exception e) {
                log.error("Failed to send payment completion notification: {}", e.getMessage());
                // Don't fail the payment processing if notification fails
            }
            
            return convertToResponseDto(savedPayment);
            
        } catch (Exception e) {
            log.error("Payment processing failed for ID: {}", paymentId, e);
            
            payment = payment.toBuilder()
                    .status(PaymentStatus.FAILED)
                    .failureReason(e.getMessage())
                    .build();
            
            paymentRepository.save(payment);
            throw new CustomException(ErrorCode.PAYMENT_PROCESSING_FAILED, "Payment processing failed: " + e.getMessage());
        }
    }
    
    @Override
    public PaymentResponseDto cancelPayment(Long paymentId, String reason) {
        log.info("Cancelling payment with ID: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND, "Payment not found with ID: " + paymentId));
        
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS, "Cannot cancel completed payment");
        }
        
        payment = payment.toBuilder()
                .status(PaymentStatus.CANCELLED)
                .failureReason(reason)
                .build();
        
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment cancelled successfully with ID: {}", savedPayment.getId());
        
        return convertToResponseDto(savedPayment);
    }
    
    @Override
    public PaymentResponseDto updatePaymentStatus(Long paymentId, PaymentStatus status) {
        log.info("Updating payment status for ID: {} to {}", paymentId, status);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND, "Payment not found with ID: " + paymentId));
        
        payment = payment.toBuilder()
                .status(status)
                .build();
        
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment status updated successfully for ID: {}", savedPayment.getId());
        
        return convertToResponseDto(savedPayment);
    }
    
    @Override
    public PaymentResponseDto processRefund(RefundRequestDto refundRequest) {
        log.info("Processing refund for payment ID: {}", refundRequest.getPaymentId());
        
        Payment payment = paymentRepository.findById(refundRequest.getPaymentId())
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND, "Payment not found with ID: " + refundRequest.getPaymentId()));
        
        if (!isPaymentRefundable(refundRequest.getPaymentId())) {
            throw new CustomException(ErrorCode.REFUND_NOT_ALLOWED, "Refund not allowed for this payment");
        }
        
        if (refundRequest.getRefundAmount().compareTo(payment.getAmount()) > 0) {
            throw new CustomException(ErrorCode.REFUND_AMOUNT_EXCEEDED, "Refund amount exceeds payment amount");
        }
        
        BigDecimal currentRefundAmount = payment.getRefundAmount() != null ? payment.getRefundAmount() : BigDecimal.ZERO;
        BigDecimal newRefundAmount = currentRefundAmount.add(refundRequest.getRefundAmount());
        
        if (newRefundAmount.compareTo(payment.getAmount()) > 0) {
            throw new CustomException(ErrorCode.REFUND_AMOUNT_EXCEEDED, "Total refund amount would exceed payment amount");
        }
        
        String refundReference = generateRefundReference();
        
        payment = payment.toBuilder()
                .refundAmount(newRefundAmount)
                .refundReason(refundRequest.getRefundReason())
                .refundedAt(LocalDateTime.now())
                .refundReference(refundReference)
                .status(newRefundAmount.compareTo(payment.getAmount()) == 0 ? PaymentStatus.REFUNDED : PaymentStatus.PARTIALLY_REFUNDED)
                .build();
        
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Refund processed successfully for payment ID: {}", savedPayment.getId());
        
        return convertToResponseDto(savedPayment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getRefundDetails(Long paymentId) {
        log.info("Fetching refund details for payment ID: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND, "Payment not found with ID: " + paymentId));
        
        if (payment.getRefundAmount() == null) {
            throw new CustomException(ErrorCode.PAYMENT_NOT_FOUND, "No refund found for this payment");
        }
        
        return convertToResponseDto(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getRefundsByPaymentId(Long paymentId) {
        log.info("Fetching refunds for payment ID: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND, "Payment not found with ID: " + paymentId));
        
        if (payment.getRefundAmount() == null) {
            return List.of();
        }
        
        return List.of(convertToResponseDto(payment));
    }
    
    // Search and filter operations
    @Override
    @Transactional(readOnly = true)
    public Page<PaymentListResponseDto> getAllPayments(Pageable pageable) {
        log.info("Fetching all payments with pagination");
        
        Page<Payment> payments = paymentRepository.findAll(pageable);
        return payments.map(this::convertToListResponseDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PaymentListResponseDto> searchPayments(Long userId, Long hotelId, PaymentStatus status, 
                                                      PaymentMethod paymentMethod, TransactionType transactionType,
                                                      LocalDateTime startDate, LocalDateTime endDate,
                                                      BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable) {
        log.info("Searching payments with filters");
        
        Page<Payment> payments = paymentRepository.findPaymentsWithFilters(
                userId, hotelId, status, paymentMethod, transactionType,
                startDate, endDate, minAmount, maxAmount, pageable);
        
        return payments.map(this::convertToListResponseDto);
    }
    
    // User related operations
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByUserId(Long userId) {
        log.info("Fetching payments for user ID: {}", userId);
        
        List<Payment> payments = paymentRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByUserIdAndStatus(Long userId, PaymentStatus status) {
        log.info("Fetching payments for user ID: {} with status: {}", userId, status);
        
        List<Payment> payments = paymentRepository.findByUserIdAndStatus(userId, status);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PaymentListResponseDto> getPaymentsByUserId(Long userId, Pageable pageable) {
        log.info("Fetching payments for user ID: {} with pagination", userId);
        
        Page<Payment> payments = paymentRepository.findByUserId(userId, pageable);
        return payments.map(this::convertToListResponseDto);
    }
    
    // Hotel related operations
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByHotelId(Long hotelId) {
        log.info("Fetching payments for hotel ID: {}", hotelId);
        
        List<Payment> payments = paymentRepository.findByHotelIdOrderByCreatedAtDesc(hotelId);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByHotelIdAndStatus(Long hotelId, PaymentStatus status) {
        log.info("Fetching payments for hotel ID: {} with status: {}", hotelId, status);
        
        List<Payment> payments = paymentRepository.findByHotelIdAndStatus(hotelId, status);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PaymentListResponseDto> getPaymentsByHotelId(Long hotelId, Pageable pageable) {
        log.info("Fetching payments for hotel ID: {} with pagination", hotelId);
        
        Page<Payment> payments = paymentRepository.findByHotelId(hotelId, pageable);
        return payments.map(this::convertToListResponseDto);
    }
    
    // Booking related operations
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByBookingId(Long bookingId) {
        log.info("Fetching payments for booking ID: {}", bookingId);
        
        List<Payment> payments = paymentRepository.findByBookingIdOrderByCreatedAtDesc(bookingId);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByBookingIdAndStatus(Long bookingId, PaymentStatus status) {
        log.info("Fetching payments for booking ID: {} with status: {}", bookingId, status);
        
        List<Payment> payments = paymentRepository.findByBookingIdAndStatus(bookingId, status);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    // Status related operations
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByStatus(PaymentStatus status) {
        log.info("Fetching payments with status: {}", status);
        
        List<Payment> payments = paymentRepository.findByStatusOrderByCreatedAtDesc(status);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PaymentListResponseDto> getPaymentsByStatus(PaymentStatus status, Pageable pageable) {
        log.info("Fetching payments with status: {} and pagination", status);
        
        Page<Payment> payments = paymentRepository.findByStatus(status, pageable);
        return payments.map(this::convertToListResponseDto);
    }
    
    // Payment method operations
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByPaymentMethod(PaymentMethod paymentMethod) {
        log.info("Fetching payments with payment method: {}", paymentMethod);
        
        List<Payment> payments = paymentRepository.findByPaymentMethod(paymentMethod);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByPaymentMethodAndStatus(PaymentMethod paymentMethod, PaymentStatus status) {
        log.info("Fetching payments with payment method: {} and status: {}", paymentMethod, status);
        
        List<Payment> payments = paymentRepository.findByPaymentMethodAndStatus(paymentMethod, status);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    // Transaction type operations
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByTransactionType(TransactionType transactionType) {
        log.info("Fetching payments with transaction type: {}", transactionType);
        
        List<Payment> payments = paymentRepository.findByTransactionType(transactionType);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByTransactionTypeAndStatus(TransactionType transactionType, PaymentStatus status) {
        log.info("Fetching payments with transaction type: {} and status: {}", transactionType, status);
        
        List<Payment> payments = paymentRepository.findByTransactionTypeAndStatus(transactionType, status);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    // Date range operations
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching payments between {} and {}", startDate, endDate);
        
        List<Payment> payments = paymentRepository.findByCreatedAtBetween(startDate, endDate);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByDateRangeAndStatus(LocalDateTime startDate, LocalDateTime endDate, PaymentStatus status) {
        log.info("Fetching payments between {} and {} with status: {}", startDate, endDate, status);
        
        List<Payment> payments = paymentRepository.findByCreatedAtBetweenAndStatus(startDate, endDate, status);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getProcessedPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching processed payments between {} and {}", startDate, endDate);
        
        List<Payment> payments = paymentRepository.findByProcessedAtBetween(startDate, endDate);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    // Amount range operations
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        log.info("Fetching payments between {} and {}", minAmount, maxAmount);
        
        List<Payment> payments = paymentRepository.findByAmountBetween(minAmount, maxAmount);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByAmountRangeAndStatus(BigDecimal minAmount, BigDecimal maxAmount, PaymentStatus status) {
        log.info("Fetching payments between {} and {} with status: {}", minAmount, maxAmount, status);
        
        List<Payment> payments = paymentRepository.findByAmountBetweenAndStatus(minAmount, maxAmount, status);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    // Gateway operations
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByGateway(String gatewayName) {
        log.info("Fetching payments for gateway: {}", gatewayName);
        
        List<Payment> payments = paymentRepository.findByGatewayName(gatewayName);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByGatewayAndStatus(String gatewayName, PaymentStatus status) {
        log.info("Fetching payments for gateway: {} with status: {}", gatewayName, status);
        
        List<Payment> payments = paymentRepository.findByGatewayNameAndStatus(gatewayName, status);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    // Refund operations
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getRefundedPayments() {
        log.info("Fetching refunded payments");
        
        List<Payment> payments = paymentRepository.findByRefundAmountIsNotNull();
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getRefundedPaymentsByAmount(BigDecimal minAmount) {
        log.info("Fetching refunded payments with amount >= {}", minAmount);
        
        List<Payment> payments = paymentRepository.findByRefundAmountGreaterThan(minAmount);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getRefundedPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching refunded payments between {} and {}", startDate, endDate);
        
        List<Payment> payments = paymentRepository.findByRefundedAtBetween(startDate, endDate);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    // Statistics operations
    @Override
    @Transactional(readOnly = true)
    public long getPaymentCountByStatus(PaymentStatus status) {
        log.info("Getting payment count for status: {}", status);
        return paymentRepository.countByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getPaymentCountByUserIdAndStatus(Long userId, PaymentStatus status) {
        log.info("Getting payment count for user ID: {} and status: {}", userId, status);
        return paymentRepository.countByUserIdAndStatus(userId, status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getPaymentCountByHotelIdAndStatus(Long hotelId, PaymentStatus status) {
        log.info("Getting payment count for hotel ID: {} and status: {}", hotelId, status);
        return paymentRepository.countByHotelIdAndStatus(hotelId, status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByStatus(PaymentStatus status) {
        log.info("Getting total amount for status: {}", status);
        BigDecimal amount = paymentRepository.sumAmountByStatus(status);
        return amount != null ? amount : BigDecimal.ZERO;
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByUserIdAndStatus(Long userId, PaymentStatus status) {
        log.info("Getting total amount for user ID: {} and status: {}", userId, status);
        BigDecimal amount = paymentRepository.sumAmountByUserIdAndStatus(userId, status);
        return amount != null ? amount : BigDecimal.ZERO;
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByHotelIdAndStatus(Long hotelId, PaymentStatus status) {
        log.info("Getting total amount for hotel ID: {} and status: {}", hotelId, status);
        BigDecimal amount = paymentRepository.sumAmountByHotelIdAndStatus(hotelId, status);
        return amount != null ? amount : BigDecimal.ZERO;
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalRefundAmount() {
        log.info("Getting total refund amount");
        BigDecimal amount = paymentRepository.sumRefundAmount();
        return amount != null ? amount : BigDecimal.ZERO;
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalRefundAmountByHotelId(Long hotelId) {
        log.info("Getting total refund amount for hotel ID: {}", hotelId);
        BigDecimal amount = paymentRepository.sumRefundAmountByHotelId(hotelId);
        return amount != null ? amount : BigDecimal.ZERO;
    }
    
    // Recent operations
    @Override
    @Transactional(readOnly = true)
    public Page<PaymentListResponseDto> getRecentPayments(Pageable pageable) {
        log.info("Fetching recent payments with pagination");
        
        Page<Payment> payments = paymentRepository.findRecentPayments(pageable);
        return payments.map(this::convertToListResponseDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getFailedPaymentsSince(LocalDateTime since) {
        log.info("Fetching failed payments since: {}", since);
        
        List<Payment> payments = paymentRepository.findFailedPaymentsSince(since);
        return payments.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    // Utility operations
    @Override
    @Transactional(readOnly = true)
    public boolean isPaymentRefundable(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND, "Payment not found with ID: " + paymentId));
        
        return payment.getStatus() == PaymentStatus.COMPLETED && 
               (payment.getRefundAmount() == null || payment.getRefundAmount().compareTo(payment.getAmount()) < 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isPaymentExpired(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND, "Payment not found with ID: " + paymentId));
        
        return payment.getExpiresAt() != null && payment.getExpiresAt().isBefore(LocalDateTime.now());
    }
    
    @Override
    public String generatePaymentReference() {
        return "PAY_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    @Override
    public String generateRefundReference() {
        return "REF_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    // Helper methods
    private PaymentResponseDto convertToResponseDto(Payment payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .paymentReference(payment.getPaymentReference())
                .bookingId(payment.getBookingId())
                .userId(payment.getUserId())
                .hotelId(payment.getHotelId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionType(payment.getTransactionType())
                .gatewayTransactionId(payment.getGatewayTransactionId())
                .gatewayResponse(payment.getGatewayResponse())
                .gatewayName(payment.getGatewayName())
                .cardLastFour(payment.getCardLastFour())
                .cardBrand(payment.getCardBrand())
                .failureReason(payment.getFailureReason())
                .refundAmount(payment.getRefundAmount())
                .refundReason(payment.getRefundReason())
                .refundedAt(payment.getRefundedAt())
                .refundReference(payment.getRefundReference())
                .processedAt(payment.getProcessedAt())
                .expiresAt(payment.getExpiresAt())
                .metadata(payment.getMetadata())
                .ipAddress(payment.getIpAddress())
                .userAgent(payment.getUserAgent())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
    
    private PaymentListResponseDto convertToListResponseDto(Payment payment) {
        return PaymentListResponseDto.builder()
                .id(payment.getId())
                .paymentReference(payment.getPaymentReference())
                .bookingId(payment.getBookingId())
                .userId(payment.getUserId())
                .hotelId(payment.getHotelId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionType(payment.getTransactionType())
                .gatewayName(payment.getGatewayName())
                .cardLastFour(payment.getCardLastFour())
                .cardBrand(payment.getCardBrand())
                .refundAmount(payment.getRefundAmount())
                .processedAt(payment.getProcessedAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}

