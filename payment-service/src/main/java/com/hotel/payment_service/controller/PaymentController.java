package com.hotel.payment_service.controller;

import com.hotel.payment_service.dto.PaymentListResponseDto;
import com.hotel.payment_service.dto.PaymentRequestDto;
import com.hotel.payment_service.dto.PaymentResponseDto;
import com.hotel.payment_service.dto.RefundRequestDto;
import com.hotel.payment_service.enums.PaymentMethod;
import com.hotel.payment_service.enums.PaymentStatus;
import com.hotel.payment_service.enums.TransactionType;
import com.hotel.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v2/payment")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    // Basic CRUD operations
    @PostMapping("/create")
    public ResponseEntity<PaymentResponseDto> createPayment(@Valid @RequestBody PaymentRequestDto paymentRequest) {
        log.info("Creating payment for booking ID: {}", paymentRequest.getBookingId());
        PaymentResponseDto response = paymentService.createPayment(paymentRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable Long id) {
        log.info("Fetching payment by ID: {}", id);
        PaymentResponseDto response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/reference/{paymentReference}")
    public ResponseEntity<PaymentResponseDto> getPaymentByReference(@PathVariable String paymentReference) {
        log.info("Fetching payment by reference: {}", paymentReference);
        PaymentResponseDto response = paymentService.getPaymentByReference(paymentReference);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<PaymentResponseDto> updatePayment(@PathVariable Long id, @Valid @RequestBody PaymentRequestDto paymentRequest) {
        log.info("Updating payment with ID: {}", id);
        PaymentResponseDto response = paymentService.updatePayment(id, paymentRequest);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        log.info("Deleting payment with ID: {}", id);
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
    
    // Payment processing
    @PostMapping("/process/{id}")
    public ResponseEntity<PaymentResponseDto> processPayment(@PathVariable Long id) {
        log.info("Processing payment with ID: {}", id);
        PaymentResponseDto response = paymentService.processPayment(id);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/cancel/{id}")
    public ResponseEntity<PaymentResponseDto> cancelPayment(@PathVariable Long id, @RequestParam String reason) {
        log.info("Cancelling payment with ID: {}", id);
        PaymentResponseDto response = paymentService.cancelPayment(id, reason);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/status/{id}")
    public ResponseEntity<PaymentResponseDto> updatePaymentStatus(@PathVariable Long id, @RequestParam PaymentStatus status) {
        log.info("Updating payment status for ID: {} to {}", id, status);
        PaymentResponseDto response = paymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(response);
    }
    
    // Refund operations
    @PostMapping("/refund")
    public ResponseEntity<PaymentResponseDto> processRefund(@Valid @RequestBody RefundRequestDto refundRequest) {
        log.info("Processing refund for payment ID: {}", refundRequest.getPaymentId());
        PaymentResponseDto response = paymentService.processRefund(refundRequest);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/refund/{paymentId}")
    public ResponseEntity<PaymentResponseDto> getRefundDetails(@PathVariable Long paymentId) {
        log.info("Fetching refund details for payment ID: {}", paymentId);
        PaymentResponseDto response = paymentService.getRefundDetails(paymentId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/refunds/{paymentId}")
    public ResponseEntity<List<PaymentResponseDto>> getRefundsByPaymentId(@PathVariable Long paymentId) {
        log.info("Fetching refunds for payment ID: {}", paymentId);
        List<PaymentResponseDto> response = paymentService.getRefundsByPaymentId(paymentId);
        return ResponseEntity.ok(response);
    }
    
    // Search and filter operations
    @GetMapping("/all")
    public ResponseEntity<Page<PaymentListResponseDto>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Fetching all payments with pagination");
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PaymentListResponseDto> response = paymentService.getAllPayments(pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<PaymentListResponseDto>> searchPayments(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Searching payments with filters");
        
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : null;
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : null;
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PaymentListResponseDto> response = paymentService.searchPayments(
                userId, hotelId, status, paymentMethod, transactionType,
                start, end, minAmount, maxAmount, pageable);
        
        return ResponseEntity.ok(response);
    }
    
    // User related operations
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByUserId(@PathVariable Long userId) {
        log.info("Fetching payments for user ID: {}", userId);
        List<PaymentResponseDto> response = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByUserIdAndStatus(
            @PathVariable Long userId, @PathVariable PaymentStatus status) {
        log.info("Fetching payments for user ID: {} with status: {}", userId, status);
        List<PaymentResponseDto> response = paymentService.getPaymentsByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user/{userId}/paginated")
    public ResponseEntity<Page<PaymentListResponseDto>> getPaymentsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Fetching payments for user ID: {} with pagination", userId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PaymentListResponseDto> response = paymentService.getPaymentsByUserId(userId, pageable);
        return ResponseEntity.ok(response);
    }
    
    // Hotel related operations
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByHotelId(@PathVariable Long hotelId) {
        log.info("Fetching payments for hotel ID: {}", hotelId);
        List<PaymentResponseDto> response = paymentService.getPaymentsByHotelId(hotelId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/hotel/{hotelId}/status/{status}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByHotelIdAndStatus(
            @PathVariable Long hotelId, @PathVariable PaymentStatus status) {
        log.info("Fetching payments for hotel ID: {} with status: {}", hotelId, status);
        List<PaymentResponseDto> response = paymentService.getPaymentsByHotelIdAndStatus(hotelId, status);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/hotel/{hotelId}/paginated")
    public ResponseEntity<Page<PaymentListResponseDto>> getPaymentsByHotelId(
            @PathVariable Long hotelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Fetching payments for hotel ID: {} with pagination", hotelId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PaymentListResponseDto> response = paymentService.getPaymentsByHotelId(hotelId, pageable);
        return ResponseEntity.ok(response);
    }
    
    // Booking related operations
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByBookingId(@PathVariable Long bookingId) {
        log.info("Fetching payments for booking ID: {}", bookingId);
        List<PaymentResponseDto> response = paymentService.getPaymentsByBookingId(bookingId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/booking/{bookingId}/status/{status}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByBookingIdAndStatus(
            @PathVariable Long bookingId, @PathVariable PaymentStatus status) {
        log.info("Fetching payments for booking ID: {} with status: {}", bookingId, status);
        List<PaymentResponseDto> response = paymentService.getPaymentsByBookingIdAndStatus(bookingId, status);
        return ResponseEntity.ok(response);
    }
    
    // Status related operations
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        log.info("Fetching payments with status: {}", status);
        List<PaymentResponseDto> response = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status/{status}/paginated")
    public ResponseEntity<Page<PaymentListResponseDto>> getPaymentsByStatus(
            @PathVariable PaymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Fetching payments with status: {} and pagination", status);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PaymentListResponseDto> response = paymentService.getPaymentsByStatus(status, pageable);
        return ResponseEntity.ok(response);
    }
    
    // Payment method operations
    @GetMapping("/method/{paymentMethod}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByPaymentMethod(@PathVariable PaymentMethod paymentMethod) {
        log.info("Fetching payments with payment method: {}", paymentMethod);
        List<PaymentResponseDto> response = paymentService.getPaymentsByPaymentMethod(paymentMethod);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/method/{paymentMethod}/status/{status}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByPaymentMethodAndStatus(
            @PathVariable PaymentMethod paymentMethod, @PathVariable PaymentStatus status) {
        log.info("Fetching payments with payment method: {} and status: {}", paymentMethod, status);
        List<PaymentResponseDto> response = paymentService.getPaymentsByPaymentMethodAndStatus(paymentMethod, status);
        return ResponseEntity.ok(response);
    }
    
    // Transaction type operations
    @GetMapping("/transaction-type/{transactionType}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByTransactionType(@PathVariable TransactionType transactionType) {
        log.info("Fetching payments with transaction type: {}", transactionType);
        List<PaymentResponseDto> response = paymentService.getPaymentsByTransactionType(transactionType);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/transaction-type/{transactionType}/status/{status}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByTransactionTypeAndStatus(
            @PathVariable TransactionType transactionType, @PathVariable PaymentStatus status) {
        log.info("Fetching payments with transaction type: {} and status: {}", transactionType, status);
        List<PaymentResponseDto> response = paymentService.getPaymentsByTransactionTypeAndStatus(transactionType, status);
        return ResponseEntity.ok(response);
    }
    
    // Date range operations
    @GetMapping("/date-range")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByDateRange(
            @RequestParam String startDate, @RequestParam String endDate) {
        log.info("Fetching payments between {} and {}", startDate, endDate);
        
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        
        List<PaymentResponseDto> response = paymentService.getPaymentsByDateRange(start, end);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/date-range/status/{status}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByDateRangeAndStatus(
            @RequestParam String startDate, @RequestParam String endDate, @PathVariable PaymentStatus status) {
        log.info("Fetching payments between {} and {} with status: {}", startDate, endDate, status);
        
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        
        List<PaymentResponseDto> response = paymentService.getPaymentsByDateRangeAndStatus(start, end, status);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/processed/date-range")
    public ResponseEntity<List<PaymentResponseDto>> getProcessedPaymentsByDateRange(
            @RequestParam String startDate, @RequestParam String endDate) {
        log.info("Fetching processed payments between {} and {}", startDate, endDate);
        
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        
        List<PaymentResponseDto> response = paymentService.getProcessedPaymentsByDateRange(start, end);
        return ResponseEntity.ok(response);
    }
    
    // Amount range operations
    @GetMapping("/amount-range")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByAmountRange(
            @RequestParam BigDecimal minAmount, @RequestParam BigDecimal maxAmount) {
        log.info("Fetching payments between {} and {}", minAmount, maxAmount);
        
        List<PaymentResponseDto> response = paymentService.getPaymentsByAmountRange(minAmount, maxAmount);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/amount-range/status/{status}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByAmountRangeAndStatus(
            @RequestParam BigDecimal minAmount, @RequestParam BigDecimal maxAmount, @PathVariable PaymentStatus status) {
        log.info("Fetching payments between {} and {} with status: {}", minAmount, maxAmount, status);
        
        List<PaymentResponseDto> response = paymentService.getPaymentsByAmountRangeAndStatus(minAmount, maxAmount, status);
        return ResponseEntity.ok(response);
    }
    
    // Gateway operations
    @GetMapping("/gateway/{gatewayName}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByGateway(@PathVariable String gatewayName) {
        log.info("Fetching payments for gateway: {}", gatewayName);
        List<PaymentResponseDto> response = paymentService.getPaymentsByGateway(gatewayName);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/gateway/{gatewayName}/status/{status}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByGatewayAndStatus(
            @PathVariable String gatewayName, @PathVariable PaymentStatus status) {
        log.info("Fetching payments for gateway: {} with status: {}", gatewayName, status);
        List<PaymentResponseDto> response = paymentService.getPaymentsByGatewayAndStatus(gatewayName, status);
        return ResponseEntity.ok(response);
    }
    
    // Refund operations
    @GetMapping("/refunded")
    public ResponseEntity<List<PaymentResponseDto>> getRefundedPayments() {
        log.info("Fetching refunded payments");
        List<PaymentResponseDto> response = paymentService.getRefundedPayments();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/refunded/amount/{minAmount}")
    public ResponseEntity<List<PaymentResponseDto>> getRefundedPaymentsByAmount(@PathVariable BigDecimal minAmount) {
        log.info("Fetching refunded payments with amount >= {}", minAmount);
        List<PaymentResponseDto> response = paymentService.getRefundedPaymentsByAmount(minAmount);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/refunded/date-range")
    public ResponseEntity<List<PaymentResponseDto>> getRefundedPaymentsByDateRange(
            @RequestParam String startDate, @RequestParam String endDate) {
        log.info("Fetching refunded payments between {} and {}", startDate, endDate);
        
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        
        List<PaymentResponseDto> response = paymentService.getRefundedPaymentsByDateRange(start, end);
        return ResponseEntity.ok(response);
    }
    
    // Statistics operations
    @GetMapping("/statistics/count/status/{status}")
    public ResponseEntity<Long> getPaymentCountByStatus(@PathVariable PaymentStatus status) {
        log.info("Getting payment count for status: {}", status);
        long count = paymentService.getPaymentCountByStatus(status);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/statistics/count/user/{userId}/status/{status}")
    public ResponseEntity<Long> getPaymentCountByUserIdAndStatus(
            @PathVariable Long userId, @PathVariable PaymentStatus status) {
        log.info("Getting payment count for user ID: {} and status: {}", userId, status);
        long count = paymentService.getPaymentCountByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/statistics/count/hotel/{hotelId}/status/{status}")
    public ResponseEntity<Long> getPaymentCountByHotelIdAndStatus(
            @PathVariable Long hotelId, @PathVariable PaymentStatus status) {
        log.info("Getting payment count for hotel ID: {} and status: {}", hotelId, status);
        long count = paymentService.getPaymentCountByHotelIdAndStatus(hotelId, status);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/statistics/amount/status/{status}")
    public ResponseEntity<BigDecimal> getTotalAmountByStatus(@PathVariable PaymentStatus status) {
        log.info("Getting total amount for status: {}", status);
        BigDecimal amount = paymentService.getTotalAmountByStatus(status);
        return ResponseEntity.ok(amount);
    }
    
    @GetMapping("/statistics/amount/user/{userId}/status/{status}")
    public ResponseEntity<BigDecimal> getTotalAmountByUserIdAndStatus(
            @PathVariable Long userId, @PathVariable PaymentStatus status) {
        log.info("Getting total amount for user ID: {} and status: {}", userId, status);
        BigDecimal amount = paymentService.getTotalAmountByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(amount);
    }
    
    @GetMapping("/statistics/amount/hotel/{hotelId}/status/{status}")
    public ResponseEntity<BigDecimal> getTotalAmountByHotelIdAndStatus(
            @PathVariable Long hotelId, @PathVariable PaymentStatus status) {
        log.info("Getting total amount for hotel ID: {} and status: {}", hotelId, status);
        BigDecimal amount = paymentService.getTotalAmountByHotelIdAndStatus(hotelId, status);
        return ResponseEntity.ok(amount);
    }
    
    @GetMapping("/statistics/refund/total")
    public ResponseEntity<BigDecimal> getTotalRefundAmount() {
        log.info("Getting total refund amount");
        BigDecimal amount = paymentService.getTotalRefundAmount();
        return ResponseEntity.ok(amount);
    }
    
    @GetMapping("/statistics/refund/hotel/{hotelId}")
    public ResponseEntity<BigDecimal> getTotalRefundAmountByHotelId(@PathVariable Long hotelId) {
        log.info("Getting total refund amount for hotel ID: {}", hotelId);
        BigDecimal amount = paymentService.getTotalRefundAmountByHotelId(hotelId);
        return ResponseEntity.ok(amount);
    }
    
    // Recent operations
    @GetMapping("/recent")
    public ResponseEntity<Page<PaymentListResponseDto>> getRecentPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Fetching recent payments with pagination");
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PaymentListResponseDto> response = paymentService.getRecentPayments(pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/failed/since")
    public ResponseEntity<List<PaymentResponseDto>> getFailedPaymentsSince(@RequestParam String since) {
        log.info("Fetching failed payments since: {}", since);
        
        LocalDateTime sinceDate = LocalDateTime.parse(since);
        List<PaymentResponseDto> response = paymentService.getFailedPaymentsSince(sinceDate);
        return ResponseEntity.ok(response);
    }
    
    // Utility operations
    @GetMapping("/refundable/{paymentId}")
    public ResponseEntity<Boolean> isPaymentRefundable(@PathVariable Long paymentId) {
        log.info("Checking if payment is refundable: {}", paymentId);
        boolean refundable = paymentService.isPaymentRefundable(paymentId);
        return ResponseEntity.ok(refundable);
    }
    
    @GetMapping("/expired/{paymentId}")
    public ResponseEntity<Boolean> isPaymentExpired(@PathVariable Long paymentId) {
        log.info("Checking if payment is expired: {}", paymentId);
        boolean expired = paymentService.isPaymentExpired(paymentId);
        return ResponseEntity.ok(expired);
    }
    
    @GetMapping("/reference/generate")
    public ResponseEntity<String> generatePaymentReference() {
        log.info("Generating payment reference");
        String reference = paymentService.generatePaymentReference();
        return ResponseEntity.ok(reference);
    }
    
    @GetMapping("/refund-reference/generate")
    public ResponseEntity<String> generateRefundReference() {
        log.info("Generating refund reference");
        String reference = paymentService.generateRefundReference();
        return ResponseEntity.ok(reference);
    }
}

