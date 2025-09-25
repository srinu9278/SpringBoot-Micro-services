package com.hotel.booking_service.service;

import com.hotel.booking_service.dto.AvailabilitySearchRequest;
import com.hotel.booking_service.dto.BookingListResponseDto;
import com.hotel.booking_service.dto.BookingRequestDto;
import com.hotel.booking_service.dto.BookingResponseDto;
import com.hotel.booking_service.dto.UpdateBookingRequest;
import com.hotel.booking_service.enums.BookingStatus;
import com.hotel.booking_service.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface BookingService {
    
    // Basic CRUD operations
    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto);
    BookingResponseDto getBookingById(Long id);
    BookingResponseDto getBookingByReference(String bookingReference);
    List<BookingListResponseDto> getAllBookings();
    BookingResponseDto updateBooking(Long id, UpdateBookingRequest updateRequest);
    Map<String, String> deleteBooking(Long id);
    
    // Booking management by user
    List<BookingListResponseDto> getBookingsByUserId(Long userId);
    List<BookingListResponseDto> getBookingsByUserIdAndStatus(Long userId, String status);
    long getBookingCountByUserId(Long userId);
    long getBookingCountByUserIdAndStatus(Long userId, String status);
    BigDecimal getTotalAmountByUserId(Long userId);
    
    // Booking management by hotel
    List<BookingListResponseDto> getBookingsByHotelId(Long hotelId);
    List<BookingListResponseDto> getBookingsByHotelIdAndStatus(Long hotelId, String status);
    long getBookingCountByHotelId(Long hotelId);
    long getBookingCountByHotelIdAndStatus(Long hotelId, String status);
    BigDecimal getTotalAmountByHotelId(Long hotelId);
    
    // Booking management by room
    List<BookingListResponseDto> getBookingsByRoomId(Long roomId);
    List<BookingListResponseDto> getBookingsByRoomIdAndStatus(Long roomId, String status);
    long getBookingCountByRoomId(Long roomId);
    
    // Booking status management
    List<BookingListResponseDto> getBookingsByStatus(String status);
    Map<String, String> updateBookingStatus(Long id, String status);
    Map<String, String> cancelBooking(Long id, String cancellationReason, Long cancelledBy);
    Map<String, String> confirmBooking(Long id);
    Map<String, String> checkInBooking(Long id);
    Map<String, String> checkOutBooking(Long id);
    
    // Payment status management
    List<BookingListResponseDto> getBookingsByPaymentStatus(String paymentStatus);
    Map<String, String> updatePaymentStatus(Long id, String paymentStatus);
    
    // Guest management
    List<BookingListResponseDto> getBookingsByGuestEmail(String guestEmail);
    List<BookingListResponseDto> getBookingsByHotelIdAndGuestEmail(Long hotelId, String guestEmail);
    
    // Date-based filtering
    List<BookingListResponseDto> getBookingsByCheckInDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<BookingListResponseDto> getBookingsByCheckOutDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<BookingListResponseDto> getBookingsByHotelIdAndCheckInDateRange(Long hotelId, LocalDateTime startDate, LocalDateTime endDate);
    List<BookingListResponseDto> getBookingsByHotelIdAndCheckOutDateRange(Long hotelId, LocalDateTime startDate, LocalDateTime endDate);
    List<BookingListResponseDto> getBookingsByUserIdAndCheckInDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Amount-based filtering
    List<BookingListResponseDto> getBookingsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount);
    List<BookingListResponseDto> getBookingsByHotelIdAndAmountRange(Long hotelId, BigDecimal minAmount, BigDecimal maxAmount);
    List<BookingListResponseDto> getBookingsByUserIdAndAmountRange(Long userId, BigDecimal minAmount, BigDecimal maxAmount);
    
    // Guest count filtering
    List<BookingListResponseDto> getBookingsByNumberOfGuests(Integer numberOfGuests);
    List<BookingListResponseDto> getBookingsByHotelIdAndNumberOfGuests(Long hotelId, Integer numberOfGuests);
    
    // Special requests filtering
    List<BookingListResponseDto> getBookingsBySpecialRequests(String specialRequest);
    List<BookingListResponseDto> getBookingsByHotelIdAndSpecialRequests(Long hotelId, String specialRequest);
    
    // Availability search
    List<BookingListResponseDto> searchAvailability(AvailabilitySearchRequest searchRequest);
    boolean isRoomAvailable(Long roomId, LocalDateTime checkInDate, LocalDateTime checkOutDate);
    List<Long> getAvailableRooms(Long hotelId, LocalDateTime checkInDate, LocalDateTime checkOutDate, Integer numberOfGuests);
    
    // Booking history
    List<BookingListResponseDto> getBookingHistoryByUserId(Long userId);
    List<BookingListResponseDto> getBookingHistoryByHotelId(Long hotelId);
    List<BookingListResponseDto> getBookingHistoryByRoomId(Long roomId);
    List<BookingListResponseDto> getBookingHistoryByGuestEmail(String guestEmail);
    
    // Notification management
    List<BookingListResponseDto> getBookingsNeedingConfirmation();
    List<BookingListResponseDto> getBookingsNeedingReminder();
    Map<String, String> markConfirmationSent(Long id);
    Map<String, String> markReminderSent(Long id);
    
    // Booking statistics
    Map<String, Long> getBookingStatisticsByHotelId(Long hotelId);
    Map<String, Long> getBookingStatisticsByUserId(Long userId);
    Map<String, Long> getBookingStatisticsByStatus(String status);
    Map<String, Long> getBookingStatisticsByPaymentStatus(String paymentStatus);
    Map<String, Long> getOverallBookingStatistics();
    
    // Revenue statistics
    Map<String, BigDecimal> getRevenueStatisticsByHotelId(Long hotelId);
    Map<String, BigDecimal> getRevenueStatisticsByUserId(Long userId);
    Map<String, BigDecimal> getOverallRevenueStatistics();
    
    // Booking lifecycle management
    Map<String, String> processBookingLifecycle(Long id, String action);
    List<BookingListResponseDto> getBookingsByLifecycleStage(String stage);
    
    // Search and filtering
    List<BookingListResponseDto> searchBookings(String query);
    List<BookingListResponseDto> filterBookings(Map<String, Object> filters);
    
    // Bulk operations
    Map<String, String> bulkUpdateBookingStatus(List<Long> bookingIds, String status);
    Map<String, String> bulkUpdatePaymentStatus(List<Long> bookingIds, String paymentStatus);
    Map<String, String> bulkCancelBookings(List<Long> bookingIds, String cancellationReason, Long cancelledBy);
}

