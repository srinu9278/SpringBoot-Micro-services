package com.hotel.booking_service.repository;

import com.hotel.booking_service.enums.BookingStatus;
import com.hotel.booking_service.enums.PaymentStatus;
import com.hotel.booking_service.model.Booking;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    // Find bookings by user ID
    List<Booking> findByUserId(Long userId);
    
    // Find bookings by hotel ID
    List<Booking> findByHotelId(Long hotelId);
    
    // Find bookings by room ID
    List<Booking> findByRoomId(Long roomId);
    
    // Find bookings by room ID and status
    List<Booking> findByRoomIdAndStatus(Long roomId, BookingStatus status);
    
    // Count bookings by room ID
    long countByRoomId(Long roomId);
    
    // Find bookings by status
    List<Booking> findByStatus(BookingStatus status);
    
    // Find bookings by payment status
    List<Booking> findByPaymentStatus(PaymentStatus paymentStatus);
    
    // Find bookings by hotel ID and status
    List<Booking> findByHotelIdAndStatus(Long hotelId, BookingStatus status);
    
    // Find bookings by user ID and status
    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);
    
    // Find bookings by booking reference
    Optional<Booking> findByBookingReference(String bookingReference);
    
    // Find bookings by guest email
    List<Booking> findByGuestEmail(String guestEmail);
    
    // Find bookings by hotel ID and guest email
    List<Booking> findByHotelIdAndGuestEmail(Long hotelId, String guestEmail);
    
    // Find bookings by check-in date range
    List<Booking> findByCheckInDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find bookings by check-out date range
    List<Booking> findByCheckOutDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find bookings by hotel ID and check-in date range
    List<Booking> findByHotelIdAndCheckInDateBetween(Long hotelId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Find bookings by hotel ID and check-out date range
    List<Booking> findByHotelIdAndCheckOutDateBetween(Long hotelId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Find bookings by room ID and date range (for availability checking)
    @Query("SELECT b FROM Booking b WHERE b.roomId = :roomId AND " +
           "((b.checkInDate <= :checkInDate AND b.checkOutDate > :checkInDate) OR " +
           "(b.checkInDate < :checkOutDate AND b.checkOutDate >= :checkOutDate) OR " +
           "(b.checkInDate >= :checkInDate AND b.checkOutDate <= :checkOutDate)) AND " +
           "b.status IN ('CONFIRMED', 'CHECKED_IN')")
    List<Booking> findConflictingBookings(@Param("roomId") Long roomId, 
                                         @Param("checkInDate") LocalDateTime checkInDate, 
                                         @Param("checkOutDate") LocalDateTime checkOutDate);
    
    // Find bookings by hotel ID and date range
    @Query("SELECT b FROM Booking b WHERE b.hotelId = :hotelId AND " +
           "((b.checkInDate <= :checkInDate AND b.checkOutDate > :checkInDate) OR " +
           "(b.checkInDate < :checkOutDate AND b.checkOutDate >= :checkOutDate) OR " +
           "(b.checkInDate >= :checkInDate AND b.checkOutDate <= :checkOutDate))")
    List<Booking> findBookingsByHotelAndDateRange(@Param("hotelId") Long hotelId, 
                                                  @Param("checkInDate") LocalDateTime checkInDate, 
                                                  @Param("checkOutDate") LocalDateTime checkOutDate);
    
    // Find bookings by user ID and date range
    List<Booking> findByUserIdAndCheckInDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Find bookings by amount range
    List<Booking> findByFinalAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    // Find bookings by hotel ID and amount range
    List<Booking> findByHotelIdAndFinalAmountBetween(Long hotelId, BigDecimal minAmount, BigDecimal maxAmount);
    
    // Find bookings by user ID and amount range
    List<Booking> findByUserIdAndFinalAmountBetween(Long userId, BigDecimal minAmount, BigDecimal maxAmount);
    
    // Find bookings by number of guests
    List<Booking> findByNumberOfGuests(Integer numberOfGuests);
    
    // Find bookings by hotel ID and number of guests
    List<Booking> findByHotelIdAndNumberOfGuests(Long hotelId, Integer numberOfGuests);
    
    // Find bookings by special requests containing
    @Query("SELECT b FROM Booking b WHERE b.specialRequests LIKE %:specialRequest%")
    List<Booking> findBySpecialRequestsContaining(@Param("specialRequest") String specialRequest);
    
    // Find bookings by hotel ID and special requests
    @Query("SELECT b FROM Booking b WHERE b.hotelId = :hotelId AND b.specialRequests LIKE %:specialRequest%")
    List<Booking> findByHotelIdAndSpecialRequestsContaining(@Param("hotelId") Long hotelId, 
                                                           @Param("specialRequest") String specialRequest);
    
    // Find bookings that need confirmation
    @Query("SELECT b FROM Booking b WHERE b.status = 'PENDING' AND b.createdAt < :cutoffTime")
    List<Booking> findBookingsNeedingConfirmation(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    // Find bookings that need reminder
    @Query("SELECT b FROM Booking b WHERE b.status = 'CONFIRMED' AND b.checkInDate BETWEEN :startTime AND :endTime AND b.reminderSent = false")
    List<Booking> findBookingsNeedingReminder(@Param("startTime") LocalDateTime startTime, 
                                             @Param("endTime") LocalDateTime endTime);
    
    // Find bookings by confirmation sent status
    List<Booking> findByConfirmationSent(Boolean confirmationSent);
    
    // Find bookings by reminder sent status
    List<Booking> findByReminderSent(Boolean reminderSent);
    
    // Find bookings by hotel ID and confirmation sent status
    List<Booking> findByHotelIdAndConfirmationSent(Long hotelId, Boolean confirmationSent);
    
    // Find bookings by hotel ID and reminder sent status
    List<Booking> findByHotelIdAndReminderSent(Long hotelId, Boolean reminderSent);
    
    // Count bookings by hotel ID
    long countByHotelId(Long hotelId);
    
    // Count bookings by hotel ID and status
    long countByHotelIdAndStatus(Long hotelId, BookingStatus status);
    
    // Count bookings by user ID
    long countByUserId(Long userId);
    
    // Count bookings by user ID and status
    long countByUserIdAndStatus(Long userId, BookingStatus status);
    
    // Count bookings by status
    long countByStatus(BookingStatus status);
    
    // Count bookings by payment status
    long countByPaymentStatus(PaymentStatus paymentStatus);
    
    // Count bookings by hotel ID and payment status
    long countByHotelIdAndPaymentStatus(Long hotelId, PaymentStatus paymentStatus);
    
    // Count bookings by user ID and payment status
    long countByUserIdAndPaymentStatus(Long userId, PaymentStatus paymentStatus);
    
    // Sum total amount by hotel ID
    @Query("SELECT COALESCE(SUM(b.finalAmount), 0) FROM Booking b WHERE b.hotelId = :hotelId AND b.status IN ('CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT')")
    BigDecimal sumFinalAmountByHotelId(@Param("hotelId") Long hotelId);
    
    // Sum total amount by user ID
    @Query("SELECT COALESCE(SUM(b.finalAmount), 0) FROM Booking b WHERE b.userId = :userId AND b.status IN ('CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT')")
    BigDecimal sumFinalAmountByUserId(@Param("userId") Long userId);
    
    // Find bookings by hotel ID ordered by check-in date
    List<Booking> findByHotelIdOrderByCheckInDate(Long hotelId);
    
    // Find bookings by user ID ordered by check-in date
    List<Booking> findByUserIdOrderByCheckInDate(Long userId);
    
    // Find bookings by hotel ID and status ordered by check-in date
    List<Booking> findByHotelIdAndStatusOrderByCheckInDate(Long hotelId, BookingStatus status);
    
    // Find bookings by user ID and status ordered by check-in date
    List<Booking> findByUserIdAndStatusOrderByCheckInDate(Long userId, BookingStatus status);
    
    // Find bookings by hotel ID ordered by final amount
    List<Booking> findByHotelIdOrderByFinalAmount(Long hotelId);
    
    // Find bookings by user ID ordered by final amount
    List<Booking> findByUserIdOrderByFinalAmount(Long userId);
    
    // Find bookings by hotel ID and status ordered by final amount
    List<Booking> findByHotelIdAndStatusOrderByFinalAmount(Long hotelId, BookingStatus status);
    
    // Find bookings by user ID and status ordered by final amount
    List<Booking> findByUserIdAndStatusOrderByFinalAmount(Long userId, BookingStatus status);
    
    // Find bookings by hotel ID ordered by created date
    List<Booking> findByHotelIdOrderByCreatedAt(Long hotelId);
    
    // Find bookings by user ID ordered by created date
    List<Booking> findByUserIdOrderByCreatedAt(Long userId);
    
    // Find bookings by hotel ID and status ordered by created date
    List<Booking> findByHotelIdAndStatusOrderByCreatedAt(Long hotelId, BookingStatus status);
    
    // Find bookings by user ID and status ordered by created date
    List<Booking> findByUserIdAndStatusOrderByCreatedAt(Long userId, BookingStatus status);
    
    // Search methods
    List<Booking> findByGuestNameContainingIgnoreCaseOrGuestEmailContainingIgnoreCase(String guestName, String guestEmail);

    @Query(value = """
        SELECT COUNT(*) FROM public.hotels ht CROSS JOIN public.users u CROSS JOIN public.rooms rm
        WHERE ht.id = :hotelId AND u.id = :userId AND rm.id = :roomId""", nativeQuery = true)
    Long findByHotelIdAndUserIdAndRoomId(@Param("hotelId") Long hotelId,
                                         @Param("userId") Long userId,
                                         @Param("roomId") Long roomId);

}
