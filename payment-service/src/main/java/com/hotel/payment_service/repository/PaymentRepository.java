package com.hotel.payment_service.repository;

import com.hotel.payment_service.enums.PaymentMethod;
import com.hotel.payment_service.enums.PaymentStatus;
import com.hotel.payment_service.enums.TransactionType;
import com.hotel.payment_service.model.Payment;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    // Basic queries
    Optional<Payment> findByPaymentReference(String paymentReference);
    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);
    
    // Booking related queries
    List<Payment> findByBookingId(Long bookingId);
    List<Payment> findByBookingIdAndStatus(Long bookingId, PaymentStatus status);
    List<Payment> findByBookingIdOrderByCreatedAtDesc(Long bookingId);
    
    // User related queries
    List<Payment> findByUserId(Long userId);
    List<Payment> findByUserIdAndStatus(Long userId, PaymentStatus status);
    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);
    Page<Payment> findByUserId(Long userId, Pageable pageable);
    
    // Hotel related queries
    List<Payment> findByHotelId(Long hotelId);
    List<Payment> findByHotelIdAndStatus(Long hotelId, PaymentStatus status);
    List<Payment> findByHotelIdOrderByCreatedAtDesc(Long hotelId);
    Page<Payment> findByHotelId(Long hotelId, Pageable pageable);
    
    // Status related queries
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByStatusOrderByCreatedAtDesc(PaymentStatus status);
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);
    
    // Payment method queries
    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);
    List<Payment> findByPaymentMethodAndStatus(PaymentMethod paymentMethod, PaymentStatus status);
    
    // Transaction type queries
    List<Payment> findByTransactionType(TransactionType transactionType);
    List<Payment> findByTransactionTypeAndStatus(TransactionType transactionType, PaymentStatus status);
    
    // Date range queries
    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Payment> findByCreatedAtBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, PaymentStatus status);
    List<Payment> findByProcessedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Amount range queries
    List<Payment> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    List<Payment> findByAmountBetweenAndStatus(BigDecimal minAmount, BigDecimal maxAmount, PaymentStatus status);
    
    // Gateway queries
    List<Payment> findByGatewayName(String gatewayName);
    List<Payment> findByGatewayNameAndStatus(String gatewayName, PaymentStatus status);
    
    // Refund queries
    List<Payment> findByRefundAmountIsNotNull();
    List<Payment> findByRefundAmountGreaterThan(BigDecimal amount);
    List<Payment> findByRefundedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Expired payments
    List<Payment> findByExpiresAtBeforeAndStatus(LocalDateTime currentTime, PaymentStatus status);
    
    // Statistics queries
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    long countByStatus(@Param("status") PaymentStatus status);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.userId = :userId AND p.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") PaymentStatus status);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.hotelId = :hotelId AND p.status = :status")
    long countByHotelIdAndStatus(@Param("hotelId") Long hotelId, @Param("status") PaymentStatus status);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") PaymentStatus status);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.userId = :userId AND p.status = :status")
    BigDecimal sumAmountByUserIdAndStatus(@Param("userId") Long userId, @Param("status") PaymentStatus status);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.hotelId = :hotelId AND p.status = :status")
    BigDecimal sumAmountByHotelIdAndStatus(@Param("hotelId") Long hotelId, @Param("status") PaymentStatus status);
    
    @Query("SELECT SUM(p.refundAmount) FROM Payment p WHERE p.refundAmount IS NOT NULL")
    BigDecimal sumRefundAmount();
    
    @Query("SELECT SUM(p.refundAmount) FROM Payment p WHERE p.hotelId = :hotelId AND p.refundAmount IS NOT NULL")
    BigDecimal sumRefundAmountByHotelId(@Param("hotelId") Long hotelId);
    
    // Complex queries
    @Query("SELECT p FROM Payment p WHERE " +
           "(:userId IS NULL OR p.userId = :userId) AND " +
           "(:hotelId IS NULL OR p.hotelId = :hotelId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:paymentMethod IS NULL OR p.paymentMethod = :paymentMethod) AND " +
           "(:transactionType IS NULL OR p.transactionType = :transactionType) AND " +
           "(:startDate IS NULL OR p.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR p.createdAt <= :endDate) AND " +
           "(:minAmount IS NULL OR p.amount >= :minAmount) AND " +
           "(:maxAmount IS NULL OR p.amount <= :maxAmount)")
    Page<Payment> findPaymentsWithFilters(
            @Param("userId") Long userId,
            @Param("hotelId") Long hotelId,
            @Param("status") PaymentStatus status,
            @Param("paymentMethod") PaymentMethod paymentMethod,
            @Param("transactionType") TransactionType transactionType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            Pageable pageable);
    
    // Recent payments
    @Query("SELECT p FROM Payment p ORDER BY p.createdAt DESC")
    Page<Payment> findRecentPayments(Pageable pageable);
    
    // Failed payments
    @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED' AND p.createdAt >= :since ORDER BY p.createdAt DESC")
    List<Payment> findFailedPaymentsSince(@Param("since") LocalDateTime since);


    @Query(value = """
        SELECT COUNT(*) FROM public.hotels ht WHERE ht.id = :hotelId""", nativeQuery = true)
    Long findByHotelIdFromHotelTable(Long hotelId);
}

