package com.hotel.booking_service.controller;

import com.hotel.booking_service.dto.*;
import com.hotel.booking_service.service.BookingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v2/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // Basic CRUD operations
    @PostMapping("/create")
    public ResponseEntity<BookingResponseDto> createBooking(@Valid @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("Creating new booking for hotel: {}, room: {}", bookingRequestDto.getHotelId(), bookingRequestDto.getRoomId());
        BookingResponseDto response = bookingService.createBooking(bookingRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDto> getBookingById(@PathVariable Long id) {
        log.info("Fetching booking by ID: {}", id);
        BookingResponseDto response = bookingService.getBookingById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reference/{bookingReference}")
    public ResponseEntity<BookingResponseDto> getBookingByReference(@PathVariable String bookingReference) {
        log.info("Fetching booking by reference: {}", bookingReference);
        BookingResponseDto response = bookingService.getBookingByReference(bookingReference);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookingListResponseDto>> getAllBookings() {
        log.info("Fetching all bookings");
        List<BookingListResponseDto> response = bookingService.getAllBookings();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BookingResponseDto> updateBooking(@PathVariable Long id, @Valid @RequestBody UpdateBookingRequest updateRequest) {
        log.info("Updating booking with ID: {}", id);
        BookingResponseDto response = bookingService.updateBooking(id, updateRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteBooking(@PathVariable Long id) {
        log.info("Deleting booking with ID: {}", id);
        Map<String, String> response = bookingService.deleteBooking(id);
        return ResponseEntity.ok(response);
    }

    // User-specific operations
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingListResponseDto>> getBookingsByUserId(@PathVariable Long userId) {
        log.info("Fetching bookings for user ID: {}", userId);
        List<BookingListResponseDto> response = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<BookingListResponseDto>> getBookingsByUserIdAndStatus(@PathVariable Long userId, @PathVariable String status) {
        log.info("Fetching bookings for user ID: {} with status: {}", userId, status);
        List<BookingListResponseDto> response = bookingService.getBookingsByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> getBookingCountByUserId(@PathVariable Long userId) {
        log.info("Fetching booking count for user ID: {}", userId);
        long count = bookingService.getBookingCountByUserId(userId);
        Map<String, Long> response = Map.of("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/total-amount")
    public ResponseEntity<Map<String, BigDecimal>> getTotalAmountByUserId(@PathVariable Long userId) {
        log.info("Fetching total amount for user ID: {}", userId);
        BigDecimal totalAmount = bookingService.getTotalAmountByUserId(userId);
        Map<String, BigDecimal> response = Map.of("totalAmount", totalAmount);
        return ResponseEntity.ok(response);
    }

    // Hotel-specific operations
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<BookingListResponseDto>> getBookingsByHotelId(@PathVariable Long hotelId) {
        log.info("Fetching bookings for hotel ID: {}", hotelId);
        List<BookingListResponseDto> response = bookingService.getBookingsByHotelId(hotelId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hotel/{hotelId}/status/{status}")
    public ResponseEntity<List<BookingListResponseDto>> getBookingsByHotelIdAndStatus(@PathVariable Long hotelId, @PathVariable String status) {
        log.info("Fetching bookings for hotel ID: {} with status: {}", hotelId, status);
        List<BookingListResponseDto> response = bookingService.getBookingsByHotelIdAndStatus(hotelId, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hotel/{hotelId}/count")
    public ResponseEntity<Map<String, Long>> getBookingCountByHotelId(@PathVariable Long hotelId) {
        log.info("Fetching booking count for hotel ID: {}", hotelId);
        long count = bookingService.getBookingCountByHotelId(hotelId);
        Map<String, Long> response = Map.of("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hotel/{hotelId}/total-amount")
    public ResponseEntity<Map<String, BigDecimal>> getTotalAmountByHotelId(@PathVariable Long hotelId) {
        log.info("Fetching total amount for hotel ID: {}", hotelId);
        BigDecimal totalAmount = bookingService.getTotalAmountByHotelId(hotelId);
        Map<String, BigDecimal> response = Map.of("totalAmount", totalAmount);
        return ResponseEntity.ok(response);
    }

    // Room-specific operations
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<BookingListResponseDto>> getBookingsByRoomId(@PathVariable Long roomId) {
        log.info("Fetching bookings for room ID: {}", roomId);
        List<BookingListResponseDto> response = bookingService.getBookingsByRoomId(roomId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/room/{roomId}/status/{status}")
    public ResponseEntity<List<BookingListResponseDto>> getBookingsByRoomIdAndStatus(@PathVariable Long roomId, @PathVariable String status) {
        log.info("Fetching bookings for room ID: {} with status: {}", roomId, status);
        List<BookingListResponseDto> response = bookingService.getBookingsByRoomIdAndStatus(roomId, status);
        return ResponseEntity.ok(response);
    }

    // Status management
    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookingListResponseDto>> getBookingsByStatus(@PathVariable String status) {
        log.info("Fetching bookings with status: {}", status);
        List<BookingListResponseDto> response = bookingService.getBookingsByStatus(status);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<Map<String, String>> updateBookingStatus(@PathVariable Long id, @PathVariable String status) {
        log.info("Updating booking status for ID: {} to status: {}", id, status);
        Map<String, String> response = bookingService.updateBookingStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, String>> cancelBooking(@PathVariable Long id, @RequestParam(required = false) String reason, @RequestParam(required = false) Long cancelledBy) {
        log.info("Cancelling booking with ID: {}", id);
        Map<String, String> response = bookingService.cancelBooking(id, reason, cancelledBy);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Map<String, String>> confirmBooking(@PathVariable Long id) {
        log.info("Confirming booking with ID: {}", id);
        Map<String, String> response = bookingService.confirmBooking(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/check-in")
    public ResponseEntity<Map<String, String>> checkInBooking(@PathVariable Long id) {
        log.info("Checking in booking with ID: {}", id);
        Map<String, String> response = bookingService.checkInBooking(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/check-out")
    public ResponseEntity<Map<String, String>> checkOutBooking(@PathVariable Long id) {
        log.info("Checking out booking with ID: {}", id);
        Map<String, String> response = bookingService.checkOutBooking(id);
        return ResponseEntity.ok(response);
    }

    // Payment status management
    @GetMapping("/payment-status/{paymentStatus}")
    public ResponseEntity<List<BookingListResponseDto>> getBookingsByPaymentStatus(@PathVariable String paymentStatus) {
        log.info("Fetching bookings with payment status: {}", paymentStatus);
        List<BookingListResponseDto> response = bookingService.getBookingsByPaymentStatus(paymentStatus);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/payment-status/{paymentStatus}")
    public ResponseEntity<Map<String, String>> updatePaymentStatus(@PathVariable Long id, @PathVariable String paymentStatus) {
        log.info("Updating payment status for booking ID: {} to status: {}", id, paymentStatus);
        Map<String, String> response = bookingService.updatePaymentStatus(id, paymentStatus);
        return ResponseEntity.ok(response);
    }

    // Guest management
    @GetMapping("/guest/{guestEmail}")
    public ResponseEntity<List<BookingListResponseDto>> getBookingsByGuestEmail(@PathVariable String guestEmail) {
        log.info("Fetching bookings for guest email: {}", guestEmail);
        List<BookingListResponseDto> response = bookingService.getBookingsByGuestEmail(guestEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hotel/{hotelId}/guest/{guestEmail}")
    public ResponseEntity<List<BookingListResponseDto>> getBookingsByHotelIdAndGuestEmail(@PathVariable Long hotelId, @PathVariable String guestEmail) {
        log.info("Fetching bookings for hotel ID: {} and guest email: {}", hotelId, guestEmail);
        List<BookingListResponseDto> response = bookingService.getBookingsByHotelIdAndGuestEmail(hotelId, guestEmail);
        return ResponseEntity.ok(response);
    }

    // Availability search
    @PostMapping("/search/availability")
    public ResponseEntity<List<BookingListResponseDto>> searchAvailability(@Valid @RequestBody AvailabilitySearchRequest searchRequest) {
        log.info("Searching availability for hotel: {} from {} to {}", searchRequest.getHotelId(), searchRequest.getCheckInDate(), searchRequest.getCheckOutDate());
        List<BookingListResponseDto> response = bookingService.searchAvailability(searchRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/room/{roomId}/availability")
    public ResponseEntity<Map<String, Boolean>> checkRoomAvailability(@PathVariable Long roomId, 
                                                                     @RequestParam LocalDateTime checkInDate, 
                                                                     @RequestParam LocalDateTime checkOutDate) {
        log.info("Checking availability for room: {} from {} to {}", roomId, checkInDate, checkOutDate);
        boolean isAvailable = bookingService.isRoomAvailable(roomId, checkInDate, checkOutDate);
        Map<String, Boolean> response = Map.of("available", isAvailable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hotel/{hotelId}/available-rooms")
    public ResponseEntity<List<Long>> getAvailableRooms(@PathVariable Long hotelId, 
                                                       @RequestParam LocalDateTime checkInDate, 
                                                       @RequestParam LocalDateTime checkOutDate, 
                                                       @RequestParam Integer numberOfGuests) {
        log.info("Getting available rooms for hotel: {} from {} to {} for {} guests", hotelId, checkInDate, checkOutDate, numberOfGuests);
        List<Long> response = bookingService.getAvailableRooms(hotelId, checkInDate, checkOutDate, numberOfGuests);
        return ResponseEntity.ok(response);
    }

    // Booking history
    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<BookingListResponseDto>> getBookingHistoryByUserId(@PathVariable Long userId) {
        log.info("Fetching booking history for user ID: {}", userId);
        List<BookingListResponseDto> response = bookingService.getBookingHistoryByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hotel/{hotelId}/history")
    public ResponseEntity<List<BookingListResponseDto>> getBookingHistoryByHotelId(@PathVariable Long hotelId) {
        log.info("Fetching booking history for hotel ID: {}", hotelId);
        List<BookingListResponseDto> response = bookingService.getBookingHistoryByHotelId(hotelId);
        return ResponseEntity.ok(response);
    }

    // Statistics
    @GetMapping("/hotel/{hotelId}/statistics")
    public ResponseEntity<Map<String, Long>> getBookingStatisticsByHotelId(@PathVariable Long hotelId) {
        log.info("Fetching booking statistics for hotel ID: {}", hotelId);
        Map<String, Long> response = bookingService.getBookingStatisticsByHotelId(hotelId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<Map<String, Long>> getBookingStatisticsByUserId(@PathVariable Long userId) {
        log.info("Fetching booking statistics for user ID: {}", userId);
        Map<String, Long> response = bookingService.getBookingStatisticsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics/overall")
    public ResponseEntity<Map<String, Long>> getOverallBookingStatistics() {
        log.info("Fetching overall booking statistics");
        Map<String, Long> response = bookingService.getOverallBookingStatistics();
        return ResponseEntity.ok(response);
    }

    // Revenue statistics
    @GetMapping("/hotel/{hotelId}/revenue")
    public ResponseEntity<Map<String, BigDecimal>> getRevenueStatisticsByHotelId(@PathVariable Long hotelId) {
        log.info("Fetching revenue statistics for hotel ID: {}", hotelId);
        Map<String, BigDecimal> response = bookingService.getRevenueStatisticsByHotelId(hotelId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/revenue")
    public ResponseEntity<Map<String, BigDecimal>> getRevenueStatisticsByUserId(@PathVariable Long userId) {
        log.info("Fetching revenue statistics for user ID: {}", userId);
        Map<String, BigDecimal> response = bookingService.getRevenueStatisticsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/revenue/overall")
    public ResponseEntity<Map<String, BigDecimal>> getOverallRevenueStatistics() {
        log.info("Fetching overall revenue statistics");
        Map<String, BigDecimal> response = bookingService.getOverallRevenueStatistics();
        return ResponseEntity.ok(response);
    }
}


