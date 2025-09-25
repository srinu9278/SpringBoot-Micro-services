package com.hotel.booking_service.serviceImpl;

import com.hotel.booking_service.client.NotificationClient;
import com.hotel.booking_service.dto.*;
import com.hotel.booking_service.enums.BookingStatus;
import com.hotel.booking_service.enums.ErrorCode;
import com.hotel.booking_service.enums.PaymentStatus;
import com.hotel.booking_service.exception.CustomException;
import com.hotel.booking_service.model.Booking;
import com.hotel.booking_service.repository.BookingRepository;
import com.hotel.booking_service.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private NotificationClient notificationClient;

    @Override
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) {
        log.info("Creating new booking for hotel: {}, room: {}, user: {}", 
                bookingRequestDto.getHotelId(), bookingRequestDto.getRoomId(), bookingRequestDto.getUserId());
        
        // Validate date range
        if (bookingRequestDto.getCheckInDate().isAfter(bookingRequestDto.getCheckOutDate())) {
            throw new CustomException(ErrorCode.INVALID_DATE_RANGE, "Check-in date must be before check-out date");
        }
        
        // Check room availability
        if (!isRoomAvailable(bookingRequestDto.getRoomId(), bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate())) {
            throw new CustomException(ErrorCode.ROOM_NOT_AVAILABLE, "Room is not available for the selected dates");
        }
        
        // Generate unique booking reference
        String bookingReference = generateBookingReference();
        
        // Calculate total nights
        int totalNights = (int) ChronoUnit.DAYS.between(bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate());
        
        // Calculate amounts
        BigDecimal totalAmount = bookingRequestDto.getRoomPricePerNight().multiply(BigDecimal.valueOf(totalNights));
        BigDecimal finalAmount = totalAmount.add(bookingRequestDto.getTaxAmount()).subtract(bookingRequestDto.getDiscountAmount());
        Long bookingCount = bookingRepository.findByHotelIdAndUserIdAndRoomId(bookingRequestDto.getHotelId(),bookingRequestDto.getUserId(),bookingRequestDto.getRoomId());
        if(bookingCount==0){
            throw new CustomException(ErrorCode.HOTELID_ROOMID_USERID_NOT_EXISTS,"HotelId or RoomId or UserId not exists in the table");
        }
        
        Booking booking = Booking.builder()
            .bookingReference(bookingReference)
            .hotelId(bookingRequestDto.getHotelId())
            .roomId(bookingRequestDto.getRoomId())
            .userId(bookingRequestDto.getUserId())
            .guestName(bookingRequestDto.getGuestName())
            .guestEmail(bookingRequestDto.getGuestEmail())
            .guestPhone(bookingRequestDto.getGuestPhone())
            .checkInDate(bookingRequestDto.getCheckInDate())
            .checkOutDate(bookingRequestDto.getCheckOutDate())
            .numberOfGuests(bookingRequestDto.getNumberOfGuests())
            .numberOfRooms(bookingRequestDto.getNumberOfRooms())
            .totalNights(totalNights)
            .roomPricePerNight(bookingRequestDto.getRoomPricePerNight())
            .totalAmount(totalAmount)
            .taxAmount(bookingRequestDto.getTaxAmount())
            .discountAmount(bookingRequestDto.getDiscountAmount())
            .finalAmount(finalAmount)
            .status(bookingRequestDto.getStatus())
            .paymentStatus(bookingRequestDto.getPaymentStatus())
            .specialRequests(bookingRequestDto.getSpecialRequests())
            .build();
        
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully with ID: {} and reference: {}", savedBooking.getId(), savedBooking.getBookingReference());
        
        // Send notification to Slack
        try {
            Map<String, Object> bookingData = new HashMap<>();
            bookingData.put("bookingId", savedBooking.getId());
            bookingData.put("bookingReference", savedBooking.getBookingReference());
            bookingData.put("hotelId", savedBooking.getHotelId());
            bookingData.put("roomId", savedBooking.getRoomId());
            bookingData.put("userId", savedBooking.getUserId());
            bookingData.put("guestName", savedBooking.getGuestName());
            bookingData.put("guestEmail", savedBooking.getGuestEmail());
            bookingData.put("checkInDate", savedBooking.getCheckInDate());
            bookingData.put("checkOutDate", savedBooking.getCheckOutDate());
            bookingData.put("finalAmount", savedBooking.getFinalAmount());
            bookingData.put("status", savedBooking.getStatus());
            bookingData.put("paymentStatus", savedBooking.getPaymentStatus());
            
            notificationClient.handleBookingEvent("BOOKING_CREATED", bookingData);
            log.info("Booking creation notification sent successfully");
        } catch (Exception e) {
            log.error("Failed to send booking creation notification: {}", e.getMessage());
            // Don't fail the booking creation if notification fails
        }
        
        return convertToResponseDto(savedBooking);
    }

    @Override
    public BookingResponseDto getBookingById(Long id) {
        log.info("Fetching booking by ID: {}", id);
        
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND, "Booking not found with ID: " + id));
        
        return convertToResponseDto(booking);
    }

    @Override
    public BookingResponseDto getBookingByReference(String bookingReference) {
        log.info("Fetching booking by reference: {}", bookingReference);
        
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND, "Booking not found with reference: " + bookingReference));
        
        return convertToResponseDto(booking);
    }

    @Override
    public List<BookingListResponseDto> getAllBookings() {
        log.info("Fetching all bookings");
        
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponseDto updateBooking(Long id, UpdateBookingRequest updateRequest) {
        log.info("Updating booking with ID: {}", id);
        
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND, "Booking not found with ID: " + id));
        
        // Check if booking can be modified
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.CHECKED_OUT) {
            throw new CustomException(ErrorCode.BOOKING_CANNOT_BE_MODIFIED, "Booking cannot be modified in current status");
        }
        
        // Update fields if provided using builder pattern
        Booking.BookingBuilder bookingBuilder = booking.toBuilder();
        
        if (updateRequest.getHotelId() != null) bookingBuilder.hotelId(updateRequest.getHotelId());
        if (updateRequest.getRoomId() != null) bookingBuilder.roomId(updateRequest.getRoomId());
        if (updateRequest.getUserId() != null) bookingBuilder.userId(updateRequest.getUserId());
        if (updateRequest.getGuestName() != null) bookingBuilder.guestName(updateRequest.getGuestName());
        if (updateRequest.getGuestEmail() != null) bookingBuilder.guestEmail(updateRequest.getGuestEmail());
        if (updateRequest.getGuestPhone() != null) bookingBuilder.guestPhone(updateRequest.getGuestPhone());
        if (updateRequest.getCheckInDate() != null) bookingBuilder.checkInDate(updateRequest.getCheckInDate());
        if (updateRequest.getCheckOutDate() != null) bookingBuilder.checkOutDate(updateRequest.getCheckOutDate());
        if (updateRequest.getNumberOfGuests() != null) bookingBuilder.numberOfGuests(updateRequest.getNumberOfGuests());
        if (updateRequest.getNumberOfRooms() != null) bookingBuilder.numberOfRooms(updateRequest.getNumberOfRooms());
        if (updateRequest.getRoomPricePerNight() != null) bookingBuilder.roomPricePerNight(updateRequest.getRoomPricePerNight());
        if (updateRequest.getTaxAmount() != null) bookingBuilder.taxAmount(updateRequest.getTaxAmount());
        if (updateRequest.getDiscountAmount() != null) bookingBuilder.discountAmount(updateRequest.getDiscountAmount());
        if (updateRequest.getStatus() != null) bookingBuilder.status(updateRequest.getStatus());
        if (updateRequest.getPaymentStatus() != null) bookingBuilder.paymentStatus(updateRequest.getPaymentStatus());
        if (updateRequest.getSpecialRequests() != null) bookingBuilder.specialRequests(updateRequest.getSpecialRequests());
        if (updateRequest.getCancellationReason() != null) bookingBuilder.cancellationReason(updateRequest.getCancellationReason());
        
        // Recalculate amounts if needed
        if (updateRequest.getCheckInDate() != null || updateRequest.getCheckOutDate() != null || 
            updateRequest.getRoomPricePerNight() != null || updateRequest.getTaxAmount() != null || 
            updateRequest.getDiscountAmount() != null) {
            
            LocalDateTime checkIn = updateRequest.getCheckInDate() != null ? updateRequest.getCheckInDate() : booking.getCheckInDate();
            LocalDateTime checkOut = updateRequest.getCheckOutDate() != null ? updateRequest.getCheckOutDate() : booking.getCheckOutDate();
            BigDecimal roomPrice = updateRequest.getRoomPricePerNight() != null ? updateRequest.getRoomPricePerNight() : booking.getRoomPricePerNight();
            BigDecimal tax = updateRequest.getTaxAmount() != null ? updateRequest.getTaxAmount() : booking.getTaxAmount();
            BigDecimal discount = updateRequest.getDiscountAmount() != null ? updateRequest.getDiscountAmount() : booking.getDiscountAmount();
            
            int totalNights = (int) ChronoUnit.DAYS.between(checkIn, checkOut);
            BigDecimal totalAmount = roomPrice.multiply(BigDecimal.valueOf(totalNights));
            BigDecimal finalAmount = totalAmount.add(tax).subtract(discount);
            
            bookingBuilder.totalNights(totalNights);
            bookingBuilder.totalAmount(totalAmount);
            bookingBuilder.finalAmount(finalAmount);
        }
        
        Booking updatedBooking = bookingBuilder.build();
        updatedBooking = bookingRepository.save(updatedBooking);
        log.info("Booking updated successfully with ID: {}", updatedBooking.getId());
        
        return convertToResponseDto(updatedBooking);
    }

    @Override
    public Map<String, String> deleteBooking(Long id) {
        log.info("Deleting booking with ID: {}", id);
        
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND, "Booking not found with ID: " + id));
        
        bookingRepository.delete(booking);
        log.info("Booking deleted successfully with ID: {}", id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Booking deleted successfully");
        response.put("bookingId", id.toString());
        return response;
    }

    // Additional methods would be implemented here...
    // For brevity, I'll include key methods only

    @Override
    public List<BookingListResponseDto> getBookingsByUserId(Long userId) {
        log.info("Fetching bookings by user ID: {}", userId);
        
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingListResponseDto> getBookingsByHotelId(Long hotelId) {
        log.info("Fetching bookings by hotel ID: {}", hotelId);
        
        List<Booking> bookings = bookingRepository.findByHotelId(hotelId);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isRoomAvailable(Long roomId, LocalDateTime checkInDate, LocalDateTime checkOutDate) {
        log.info("Checking room availability for room: {} from {} to {}", roomId, checkInDate, checkOutDate);
        
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(roomId, checkInDate, checkOutDate);
        return conflictingBookings.isEmpty();
    }

    @Override
    public Map<String, String> cancelBooking(Long id, String cancellationReason, Long cancelledBy) {
        log.info("Cancelling booking with ID: {}", id);
        
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND, "Booking not found with ID: " + id));
        
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new CustomException(ErrorCode.BOOKING_ALREADY_CANCELLED, "Booking is already cancelled");
        }
        
        if (booking.getStatus() == BookingStatus.CHECKED_OUT) {
            throw new CustomException(ErrorCode.BOOKING_CANNOT_BE_CANCELLED, "Cannot cancel checked-out booking");
        }
        
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(cancellationReason);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setCancelledBy(cancelledBy);
        
        bookingRepository.save(booking);
        log.info("Booking cancelled successfully with ID: {}", id);
        
        // Send notification to Slack
        try {
            Map<String, Object> bookingData = new HashMap<>();
            bookingData.put("bookingId", booking.getId());
            bookingData.put("bookingReference", booking.getBookingReference());
            bookingData.put("hotelId", booking.getHotelId());
            bookingData.put("roomId", booking.getRoomId());
            bookingData.put("userId", booking.getUserId());
            bookingData.put("guestName", booking.getGuestName());
            bookingData.put("guestEmail", booking.getGuestEmail());
            bookingData.put("cancellationReason", cancellationReason);
            bookingData.put("cancelledBy", cancelledBy);
            bookingData.put("status", BookingStatus.CANCELLED);
            
            notificationClient.handleBookingEvent("BOOKING_CANCELLED", bookingData);
            log.info("Booking cancellation notification sent successfully");
        } catch (Exception e) {
            log.error("Failed to send booking cancellation notification: {}", e.getMessage());
            // Don't fail the booking cancellation if notification fails
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Booking cancelled successfully");
        response.put("bookingId", id.toString());
        response.put("status", BookingStatus.CANCELLED.name());
        return response;
    }

    @Override
    public List<BookingListResponseDto> getBookingsByUserIdAndStatus(Long userId, String status) {
        log.info("Fetching bookings for user ID: {} with status: {}", userId, status);
        
        try {
            BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
            List<Booking> bookings = bookingRepository.findByUserIdAndStatus(userId, bookingStatus);
            return bookings.stream()
                    .map(this::convertToListResponseDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_BOOKING_STATUS, "Invalid status: " + status);
        }
    }

    @Override
    public long getBookingCountByUserId(Long userId) {
        return bookingRepository.countByUserId(userId);
    }

    @Override
    public long getBookingCountByUserIdAndStatus(Long userId, String status) {
        try {
            BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
            return bookingRepository.countByUserIdAndStatus(userId, bookingStatus);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_BOOKING_STATUS, "Invalid status: " + status);
        }
    }

    @Override
    public BigDecimal getTotalAmountByUserId(Long userId) {
        return bookingRepository.sumFinalAmountByUserId(userId);
    }

    @Override
    public List<BookingListResponseDto> getBookingsByHotelIdAndStatus(Long hotelId, String status) {
        log.info("Fetching bookings for hotel ID: {} with status: {}", hotelId, status);
        
        try {
            BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
            List<Booking> bookings = bookingRepository.findByHotelIdAndStatus(hotelId, bookingStatus);
            return bookings.stream()
                    .map(this::convertToListResponseDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_BOOKING_STATUS, "Invalid status: " + status);
        }
    }

    @Override
    public long getBookingCountByHotelId(Long hotelId) {
        return bookingRepository.countByHotelId(hotelId);
    }

    @Override
    public long getBookingCountByHotelIdAndStatus(Long hotelId, String status) {
        try {
            BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
            return bookingRepository.countByHotelIdAndStatus(hotelId, bookingStatus);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_BOOKING_STATUS, "Invalid status: " + status);
        }
    }

    @Override
    public BigDecimal getTotalAmountByHotelId(Long hotelId) {
        return bookingRepository.sumFinalAmountByHotelId(hotelId);
    }

    @Override
    public List<BookingListResponseDto> getBookingsByRoomId(Long roomId) {
        log.info("Fetching bookings for room ID: {}", roomId);
        
        List<Booking> bookings = bookingRepository.findByRoomId(roomId);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingListResponseDto> getBookingsByRoomIdAndStatus(Long roomId, String status) {
        log.info("Fetching bookings for room ID: {} with status: {}", roomId, status);
        
        try {
            BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
            List<Booking> bookings = bookingRepository.findByRoomIdAndStatus(roomId, bookingStatus);
            return bookings.stream()
                    .map(this::convertToListResponseDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_BOOKING_STATUS, "Invalid status: " + status);
        }
    }

    @Override
    public long getBookingCountByRoomId(Long roomId) {
        return bookingRepository.countByRoomId(roomId);
    }

    @Override
    public List<BookingListResponseDto> getBookingsByStatus(String status) {
        log.info("Fetching bookings with status: {}", status);
        
        try {
            BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
            List<Booking> bookings = bookingRepository.findByStatus(bookingStatus);
            return bookings.stream()
                    .map(this::convertToListResponseDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_BOOKING_STATUS, "Invalid status: " + status);
        }
    }

    @Override
    public Map<String, String> updateBookingStatus(Long id, String status) {
        log.info("Updating booking status for ID: {} to status: {}", id, status);
        
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND, "Booking not found with ID: " + id));
        
        try {
            BookingStatus newStatus = BookingStatus.valueOf(status.toUpperCase());
            
            // Validate status transition
            if (!isValidStatusTransition(booking.getStatus(), newStatus)) {
                throw new CustomException(ErrorCode.INVALID_BOOKING_STATUS, 
                    "Invalid status transition from " + booking.getStatus() + " to " + newStatus);
            }
            
            booking.setStatus(newStatus);
            bookingRepository.save(booking);
            
            log.info("Booking status updated successfully for ID: {}", id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Booking status updated successfully");
            response.put("bookingId", id.toString());
            response.put("status", newStatus.name());
            return response;
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_BOOKING_STATUS, "Invalid status: " + status);
        }
    }

    @Override
    public Map<String, String> confirmBooking(Long id) {
        log.info("Confirming booking with ID: {}", id);
        
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND, "Booking not found with ID: " + id));
        
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new CustomException(ErrorCode.BOOKING_ALREADY_CONFIRMED, "Booking is not in pending status");
        }
        
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        
        log.info("Booking confirmed successfully with ID: {}", id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Booking confirmed successfully");
        response.put("bookingId", id.toString());
        response.put("status", BookingStatus.CONFIRMED.name());
        return response;
    }

    @Override
    public Map<String, String> checkInBooking(Long id) {
        log.info("Checking in booking with ID: {}", id);
        
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND, "Booking not found with ID: " + id));
        
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new CustomException(ErrorCode.BOOKING_ALREADY_CHECKED_IN, "Booking must be confirmed before check-in");
        }
        
        booking.setStatus(BookingStatus.CHECKED_IN);
        bookingRepository.save(booking);
        
        log.info("Booking checked in successfully with ID: {}", id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Booking checked in successfully");
        response.put("bookingId", id.toString());
        response.put("status", BookingStatus.CHECKED_IN.name());
        return response;
    }

    @Override
    public Map<String, String> checkOutBooking(Long id) {
        log.info("Checking out booking with ID: {}", id);
        
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND, "Booking not found with ID: " + id));
        
        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new CustomException(ErrorCode.BOOKING_ALREADY_CHECKED_OUT, "Booking must be checked in before check-out");
        }
        
        booking.setStatus(BookingStatus.CHECKED_OUT);
        bookingRepository.save(booking);
        
        log.info("Booking checked out successfully with ID: {}", id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Booking checked out successfully");
        response.put("bookingId", id.toString());
        response.put("status", BookingStatus.CHECKED_OUT.name());
        return response;
    }
    @Override
    public List<BookingListResponseDto> getBookingsByPaymentStatus(String paymentStatus) {
        log.info("Fetching bookings with payment status: {}", paymentStatus);
        
        try {
            PaymentStatus paymentStatusEnum = PaymentStatus.valueOf(paymentStatus.toUpperCase());
            List<Booking> bookings = bookingRepository.findByPaymentStatus(paymentStatusEnum);
            return bookings.stream()
                    .map(this::convertToListResponseDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS, "Invalid payment status: " + paymentStatus);
        }
    }

    @Override
    public Map<String, String> updatePaymentStatus(Long id, String paymentStatus) {
        log.info("Updating payment status for booking ID: {} to status: {}", id, paymentStatus);
        
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND, "Booking not found with ID: " + id));
        
        try {
            PaymentStatus newPaymentStatus = PaymentStatus.valueOf(paymentStatus.toUpperCase());
            booking.setPaymentStatus(newPaymentStatus);
            bookingRepository.save(booking);
            
            log.info("Payment status updated successfully for booking ID: {}", id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Payment status updated successfully");
            response.put("bookingId", id.toString());
            response.put("paymentStatus", newPaymentStatus.name());
            return response;
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS, "Invalid payment status: " + paymentStatus);
        }
    }

    @Override
    public List<BookingListResponseDto> getBookingsByGuestEmail(String guestEmail) {
        log.info("Fetching bookings for guest email: {}", guestEmail);
        
        List<Booking> bookings = bookingRepository.findByGuestEmail(guestEmail);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingListResponseDto> getBookingsByHotelIdAndGuestEmail(Long hotelId, String guestEmail) {
        log.info("Fetching bookings for hotel ID: {} and guest email: {}", hotelId, guestEmail);
        
        List<Booking> bookings = bookingRepository.findByHotelIdAndGuestEmail(hotelId, guestEmail);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }
    @Override
    public List<BookingListResponseDto> getBookingsByCheckInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching bookings by check-in date range: {} to {}", startDate, endDate);
        
        List<Booking> bookings = bookingRepository.findByCheckInDateBetween(startDate, endDate);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingListResponseDto> getBookingsByCheckOutDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching bookings by check-out date range: {} to {}", startDate, endDate);
        
        List<Booking> bookings = bookingRepository.findByCheckOutDateBetween(startDate, endDate);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingListResponseDto> getBookingsByHotelIdAndCheckInDateRange(Long hotelId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching bookings for hotel ID: {} by check-in date range: {} to {}", hotelId, startDate, endDate);
        
        List<Booking> bookings = bookingRepository.findByHotelIdAndCheckInDateBetween(hotelId, startDate, endDate);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingListResponseDto> getBookingsByHotelIdAndCheckOutDateRange(Long hotelId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching bookings for hotel ID: {} by check-out date range: {} to {}", hotelId, startDate, endDate);
        
        List<Booking> bookings = bookingRepository.findByHotelIdAndCheckOutDateBetween(hotelId, startDate, endDate);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingListResponseDto> getBookingsByUserIdAndCheckInDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching bookings for user ID: {} by check-in date range: {} to {}", userId, startDate, endDate);
        
        List<Booking> bookings = bookingRepository.findByUserIdAndCheckInDateBetween(userId, startDate, endDate);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }
    @Override
    public List<BookingListResponseDto> getBookingsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        log.info("Fetching bookings by amount range: {} to {}", minAmount, maxAmount);
        
        List<Booking> bookings = bookingRepository.findByFinalAmountBetween(minAmount, maxAmount);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingListResponseDto> getBookingsByHotelIdAndAmountRange(Long hotelId, BigDecimal minAmount, BigDecimal maxAmount) {
        log.info("Fetching bookings for hotel ID: {} by amount range: {} to {}", hotelId, minAmount, maxAmount);
        
        List<Booking> bookings = bookingRepository.findByHotelIdAndFinalAmountBetween(hotelId, minAmount, maxAmount);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingListResponseDto> getBookingsByUserIdAndAmountRange(Long userId, BigDecimal minAmount, BigDecimal maxAmount) {
        log.info("Fetching bookings for user ID: {} by amount range: {} to {}", userId, minAmount, maxAmount);
        
        List<Booking> bookings = bookingRepository.findByUserIdAndFinalAmountBetween(userId, minAmount, maxAmount);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingListResponseDto> getBookingsByNumberOfGuests(Integer numberOfGuests) {
        log.info("Fetching bookings by number of guests: {}", numberOfGuests);
        
        List<Booking> bookings = bookingRepository.findByNumberOfGuests(numberOfGuests);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingListResponseDto> getBookingsByHotelIdAndNumberOfGuests(Long hotelId, Integer numberOfGuests) {
        log.info("Fetching bookings for hotel ID: {} by number of guests: {}", hotelId, numberOfGuests);
        
        List<Booking> bookings = bookingRepository.findByHotelIdAndNumberOfGuests(hotelId, numberOfGuests);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingListResponseDto> getBookingsBySpecialRequests(String specialRequest) {
        log.info("Fetching bookings by special requests containing: {}", specialRequest);
        
        List<Booking> bookings = bookingRepository.findBySpecialRequestsContaining(specialRequest);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingListResponseDto> getBookingsByHotelIdAndSpecialRequests(Long hotelId, String specialRequest) {
        log.info("Fetching bookings for hotel ID: {} by special requests containing: {}", hotelId, specialRequest);
        
        List<Booking> bookings = bookingRepository.findByHotelIdAndSpecialRequestsContaining(hotelId, specialRequest);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }
    @Override
    public List<BookingListResponseDto> searchAvailability(AvailabilitySearchRequest searchRequest) {
        log.info("Searching availability for hotel: {} from {} to {} for {} guests", 
                searchRequest.getHotelId(), searchRequest.getCheckInDate(), searchRequest.getCheckOutDate(), searchRequest.getNumberOfGuests());
        
        // This is a simplified implementation - in a real system, you would integrate with room service
        // to get available rooms and then return booking information
        List<Booking> bookings = bookingRepository.findBookingsByHotelAndDateRange(
                searchRequest.getHotelId(), 
                searchRequest.getCheckInDate(), 
                searchRequest.getCheckOutDate()
        );
        
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getAvailableRooms(Long hotelId, LocalDateTime checkInDate, LocalDateTime checkOutDate, Integer numberOfGuests) {
        log.info("Getting available rooms for hotel: {} from {} to {} for {} guests", hotelId, checkInDate, checkOutDate, numberOfGuests);
        
        // This is a simplified implementation - in a real system, you would integrate with room service
        // to get all rooms for the hotel and filter out conflicting bookings
        List<Booking> conflictingBookings = bookingRepository.findBookingsByHotelAndDateRange(hotelId, checkInDate, checkOutDate);
        Set<Long> occupiedRoomIds = conflictingBookings.stream()
                .map(Booking::getRoomId)
                .collect(Collectors.toSet());
        
        // In a real implementation, you would call room service to get all rooms for the hotel
        // and filter out the occupied ones. For now, return empty list as placeholder.
        return new ArrayList<>();
    }

    @Override
    public List<BookingListResponseDto> getBookingHistoryByUserId(Long userId) {
        log.info("Fetching booking history for user ID: {}", userId);
        
        List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAt(userId);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingListResponseDto> getBookingHistoryByHotelId(Long hotelId) {
        log.info("Fetching booking history for hotel ID: {}", hotelId);
        
        List<Booking> bookings = bookingRepository.findByHotelIdOrderByCreatedAt(hotelId);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingListResponseDto> getBookingHistoryByRoomId(Long roomId) {
        log.info("Fetching booking history for room ID: {}", roomId);
        
        List<Booking> bookings = bookingRepository.findByRoomId(roomId);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingListResponseDto> getBookingHistoryByGuestEmail(String guestEmail) {
        log.info("Fetching booking history for guest email: {}", guestEmail);
        
        List<Booking> bookings = bookingRepository.findByGuestEmail(guestEmail);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }
    @Override
    public List<BookingListResponseDto> getBookingsNeedingConfirmation() {
        log.info("Fetching bookings needing confirmation");
        
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24); // 24 hours ago
        List<Booking> bookings = bookingRepository.findBookingsNeedingConfirmation(cutoffTime);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingListResponseDto> getBookingsNeedingReminder() {
        log.info("Fetching bookings needing reminder");
        
        LocalDateTime startTime = LocalDateTime.now().plusHours(24); // 24 hours from now
        LocalDateTime endTime = LocalDateTime.now().plusHours(48); // 48 hours from now
        List<Booking> bookings = bookingRepository.findBookingsNeedingReminder(startTime, endTime);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> markConfirmationSent(Long id) {
        log.info("Marking confirmation sent for booking ID: {}", id);
        
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND, "Booking not found with ID: " + id));
        
        booking.setConfirmationSent(true);
        booking.setConfirmationSentAt(LocalDateTime.now());
        bookingRepository.save(booking);
        
        log.info("Confirmation marked as sent for booking ID: {}", id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Confirmation marked as sent");
        response.put("bookingId", id.toString());
        response.put("confirmationSentAt", booking.getConfirmationSentAt().toString());
        return response;
    }

    @Override
    public Map<String, String> markReminderSent(Long id) {
        log.info("Marking reminder sent for booking ID: {}", id);
        
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND, "Booking not found with ID: " + id));
        
        booking.setReminderSent(true);
        booking.setReminderSentAt(LocalDateTime.now());
        bookingRepository.save(booking);
        
        log.info("Reminder marked as sent for booking ID: {}", id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Reminder marked as sent");
        response.put("bookingId", id.toString());
        response.put("reminderSentAt", booking.getReminderSentAt().toString());
        return response;
    }
    @Override
    public Map<String, Long> getBookingStatisticsByHotelId(Long hotelId) {
        log.info("Fetching booking statistics for hotel ID: {}", hotelId);
        
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("totalBookings", bookingRepository.countByHotelId(hotelId));
        statistics.put("confirmedBookings", bookingRepository.countByHotelIdAndStatus(hotelId, BookingStatus.CONFIRMED));
        statistics.put("cancelledBookings", bookingRepository.countByHotelIdAndStatus(hotelId, BookingStatus.CANCELLED));
        statistics.put("checkedInBookings", bookingRepository.countByHotelIdAndStatus(hotelId, BookingStatus.CHECKED_IN));
        statistics.put("checkedOutBookings", bookingRepository.countByHotelIdAndStatus(hotelId, BookingStatus.CHECKED_OUT));
        statistics.put("pendingBookings", bookingRepository.countByHotelIdAndStatus(hotelId, BookingStatus.PENDING));
        
        return statistics;
    }

    @Override
    public Map<String, Long> getBookingStatisticsByUserId(Long userId) {
        log.info("Fetching booking statistics for user ID: {}", userId);
        
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("totalBookings", bookingRepository.countByUserId(userId));
        statistics.put("confirmedBookings", bookingRepository.countByUserIdAndStatus(userId, BookingStatus.CONFIRMED));
        statistics.put("cancelledBookings", bookingRepository.countByUserIdAndStatus(userId, BookingStatus.CANCELLED));
        statistics.put("checkedInBookings", bookingRepository.countByUserIdAndStatus(userId, BookingStatus.CHECKED_IN));
        statistics.put("checkedOutBookings", bookingRepository.countByUserIdAndStatus(userId, BookingStatus.CHECKED_OUT));
        statistics.put("pendingBookings", bookingRepository.countByUserIdAndStatus(userId, BookingStatus.PENDING));
        
        return statistics;
    }

    @Override
    public Map<String, Long> getBookingStatisticsByStatus(String status) {
        log.info("Fetching booking statistics by status: {}", status);
        
        try {
            BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
            Map<String, Long> statistics = new HashMap<>();
            statistics.put("totalBookings", bookingRepository.countByStatus(bookingStatus));
            return statistics;
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_BOOKING_STATUS, "Invalid status: " + status);
        }
    }

    @Override
    public Map<String, Long> getBookingStatisticsByPaymentStatus(String paymentStatus) {
        log.info("Fetching booking statistics by payment status: {}", paymentStatus);
        
        try {
            PaymentStatus paymentStatusEnum = PaymentStatus.valueOf(paymentStatus.toUpperCase());
            Map<String, Long> statistics = new HashMap<>();
            statistics.put("totalBookings", bookingRepository.countByPaymentStatus(paymentStatusEnum));
            return statistics;
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS, "Invalid payment status: " + paymentStatus);
        }
    }

    @Override
    public Map<String, Long> getOverallBookingStatistics() {
        log.info("Fetching overall booking statistics");
        
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("totalBookings", bookingRepository.count());
        statistics.put("pendingBookings", bookingRepository.countByStatus(BookingStatus.PENDING));
        statistics.put("confirmedBookings", bookingRepository.countByStatus(BookingStatus.CONFIRMED));
        statistics.put("cancelledBookings", bookingRepository.countByStatus(BookingStatus.CANCELLED));
        statistics.put("checkedInBookings", bookingRepository.countByStatus(BookingStatus.CHECKED_IN));
        statistics.put("checkedOutBookings", bookingRepository.countByStatus(BookingStatus.CHECKED_OUT));
        statistics.put("noShowBookings", bookingRepository.countByStatus(BookingStatus.NO_SHOW));
        statistics.put("modifiedBookings", bookingRepository.countByStatus(BookingStatus.MODIFIED));
        
        return statistics;
    }
    @Override
    public Map<String, BigDecimal> getRevenueStatisticsByHotelId(Long hotelId) {
        log.info("Fetching revenue statistics for hotel ID: {}", hotelId);
        
        Map<String, BigDecimal> statistics = new HashMap<>();
        BigDecimal totalRevenue = bookingRepository.sumFinalAmountByHotelId(hotelId);
        statistics.put("totalRevenue", totalRevenue);
        statistics.put("averageBookingValue", totalRevenue.divide(BigDecimal.valueOf(bookingRepository.countByHotelId(hotelId)), 2, BigDecimal.ROUND_HALF_UP));
        
        return statistics;
    }

    @Override
    public Map<String, BigDecimal> getRevenueStatisticsByUserId(Long userId) {
        log.info("Fetching revenue statistics for user ID: {}", userId);
        
        Map<String, BigDecimal> statistics = new HashMap<>();
        BigDecimal totalRevenue = bookingRepository.sumFinalAmountByUserId(userId);
        statistics.put("totalRevenue", totalRevenue);
        statistics.put("averageBookingValue", totalRevenue.divide(BigDecimal.valueOf(bookingRepository.countByUserId(userId)), 2, BigDecimal.ROUND_HALF_UP));
        
        return statistics;
    }

    @Override
    public Map<String, BigDecimal> getOverallRevenueStatistics() {
        log.info("Fetching overall revenue statistics");
        
        Map<String, BigDecimal> statistics = new HashMap<>();
        // This would require a custom query to sum all final amounts
        // For now, return placeholder values
        statistics.put("totalRevenue", BigDecimal.ZERO);
        statistics.put("averageBookingValue", BigDecimal.ZERO);
        
        return statistics;
    }

    @Override
    public Map<String, String> processBookingLifecycle(Long id, String action) {
        log.info("Processing booking lifecycle for ID: {} with action: {}", id, action);
        
        return switch (action.toLowerCase()) {
            case "confirm" -> confirmBooking(id);
            case "checkin" -> checkInBooking(id);
            case "checkout" -> checkOutBooking(id);
            case "cancel" -> cancelBooking(id, "Lifecycle cancellation", null);
            default -> {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Invalid lifecycle action: " + action);
                response.put("bookingId", id.toString());
                yield response;
            }
        };
    }

    @Override
    public List<BookingListResponseDto> getBookingsByLifecycleStage(String stage) {
        log.info("Fetching bookings by lifecycle stage: {}", stage);
        
        return switch (stage.toLowerCase()) {
            case "pending" -> getBookingsByStatus("PENDING");
            case "confirmed" -> getBookingsByStatus("CONFIRMED");
            case "checkedin" -> getBookingsByStatus("CHECKED_IN");
            case "checkedout" -> getBookingsByStatus("CHECKED_OUT");
            case "cancelled" -> getBookingsByStatus("CANCELLED");
            default -> {
                log.warn("Invalid lifecycle stage: {}", stage);
                yield new ArrayList<>();
            }
        };
    }

    @Override
    public List<BookingListResponseDto> searchBookings(String query) {
        log.info("Searching bookings with query: {}", query);
        
        // This is a simplified search implementation
        // In a real system, you would use full-text search or more sophisticated search
        List<Booking> bookings = bookingRepository.findByGuestNameContainingIgnoreCaseOrGuestEmailContainingIgnoreCase(query, query);
        return bookings.stream()
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingListResponseDto> filterBookings(Map<String, Object> filters) {
        log.info("Filtering bookings with filters: {}", filters);
        
        // This is a simplified filter implementation
        // In a real system, you would use dynamic query building
        List<Booking> allBookings = bookingRepository.findAll();
        
        return allBookings.stream()
                .filter(booking -> {
                    if (filters.containsKey("hotelId") && !booking.getHotelId().equals(filters.get("hotelId"))) {
                        return false;
                    }
                    if (filters.containsKey("userId") && !booking.getUserId().equals(filters.get("userId"))) {
                        return false;
                    }
                    if (filters.containsKey("status") && !booking.getStatus().name().equals(filters.get("status"))) {
                        return false;
                    }
                    return true;
                })
                .map(this::convertToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> bulkUpdateBookingStatus(List<Long> bookingIds, String status) {
        log.info("Bulk updating booking status for {} bookings to status: {}", bookingIds.size(), status);
        
        try {
            BookingStatus newStatus = BookingStatus.valueOf(status.toUpperCase());
            int updatedCount = 0;
            
            for (Long id : bookingIds) {
                try {
                    Booking booking = bookingRepository.findById(id).orElse(null);
                    if (booking != null && isValidStatusTransition(booking.getStatus(), newStatus)) {
                        booking.setStatus(newStatus);
                        bookingRepository.save(booking);
                        updatedCount++;
                    }
                } catch (Exception e) {
                    log.warn("Failed to update booking ID: {}", id, e);
                }
            }
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Bulk status update completed");
            response.put("totalRequested", String.valueOf(bookingIds.size()));
            response.put("successfullyUpdated", String.valueOf(updatedCount));
            response.put("status", newStatus.name());
            return response;
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_BOOKING_STATUS, "Invalid status: " + status);
        }
    }

    @Override
    public Map<String, String> bulkUpdatePaymentStatus(List<Long> bookingIds, String paymentStatus) {
        log.info("Bulk updating payment status for {} bookings to status: {}", bookingIds.size(), paymentStatus);
        
        try {
            PaymentStatus newPaymentStatus = PaymentStatus.valueOf(paymentStatus.toUpperCase());
            int updatedCount = 0;
            
            for (Long id : bookingIds) {
                try {
                    Booking booking = bookingRepository.findById(id).orElse(null);
                    if (booking != null) {
                        booking.setPaymentStatus(newPaymentStatus);
                        bookingRepository.save(booking);
                        updatedCount++;
                    }
                } catch (Exception e) {
                    log.warn("Failed to update payment status for booking ID: {}", id, e);
                }
            }
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Bulk payment status update completed");
            response.put("totalRequested", String.valueOf(bookingIds.size()));
            response.put("successfullyUpdated", String.valueOf(updatedCount));
            response.put("paymentStatus", newPaymentStatus.name());
            return response;
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS, "Invalid payment status: " + paymentStatus);
        }
    }

    @Override
    public Map<String, String> bulkCancelBookings(List<Long> bookingIds, String cancellationReason, Long cancelledBy) {
        log.info("Bulk cancelling {} bookings", bookingIds.size());
        
        int cancelledCount = 0;
        
        for (Long id : bookingIds) {
            try {
                Booking booking = bookingRepository.findById(id).orElse(null);
                if (booking != null && booking.getStatus() != BookingStatus.CANCELLED && booking.getStatus() != BookingStatus.CHECKED_OUT) {
                    booking.setStatus(BookingStatus.CANCELLED);
                    booking.setCancellationReason(cancellationReason);
                    booking.setCancelledAt(LocalDateTime.now());
                    booking.setCancelledBy(cancelledBy);
                    bookingRepository.save(booking);
                    cancelledCount++;
                }
            } catch (Exception e) {
                log.warn("Failed to cancel booking ID: {}", id, e);
            }
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Bulk cancellation completed");
        response.put("totalRequested", String.valueOf(bookingIds.size()));
        response.put("successfullyCancelled", String.valueOf(cancelledCount));
        return response;
    }

    private String generateBookingReference() {
        return "BK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private boolean isValidStatusTransition(BookingStatus currentStatus, BookingStatus newStatus) {
        return switch (currentStatus) {
            case PENDING -> newStatus == BookingStatus.CONFIRMED || newStatus == BookingStatus.CANCELLED;
            case CONFIRMED -> newStatus == BookingStatus.CHECKED_IN || newStatus == BookingStatus.CANCELLED || newStatus == BookingStatus.MODIFIED;
            case CHECKED_IN -> newStatus == BookingStatus.CHECKED_OUT || newStatus == BookingStatus.CANCELLED;
            case CHECKED_OUT -> false; // Cannot transition from checked out
            case CANCELLED -> false; // Cannot transition from cancelled
            case NO_SHOW -> false; // Cannot transition from no show
            case MODIFIED -> newStatus == BookingStatus.CONFIRMED || newStatus == BookingStatus.CANCELLED;
        };
    }

    private BookingResponseDto convertToResponseDto(Booking booking) {
        return BookingResponseDto.builder()
            .id(booking.getId())
            .bookingReference(booking.getBookingReference())
            .hotelId(booking.getHotelId())
            .roomId(booking.getRoomId())
            .userId(booking.getUserId())
            .guestName(booking.getGuestName())
            .guestEmail(booking.getGuestEmail())
            .guestPhone(booking.getGuestPhone())
            .checkInDate(booking.getCheckInDate())
            .checkOutDate(booking.getCheckOutDate())
            .numberOfGuests(booking.getNumberOfGuests())
            .numberOfRooms(booking.getNumberOfRooms())
            .totalNights(booking.getTotalNights())
            .roomPricePerNight(booking.getRoomPricePerNight())
            .totalAmount(booking.getTotalAmount())
            .taxAmount(booking.getTaxAmount())
            .discountAmount(booking.getDiscountAmount())
            .finalAmount(booking.getFinalAmount())
            .status(booking.getStatus())
            .paymentStatus(booking.getPaymentStatus())
            .specialRequests(booking.getSpecialRequests())
            .cancellationReason(booking.getCancellationReason())
            .cancelledAt(booking.getCancelledAt())
            .cancelledBy(booking.getCancelledBy())
            .confirmationSent(booking.getConfirmationSent())
            .confirmationSentAt(booking.getConfirmationSentAt())
            .reminderSent(booking.getReminderSent())
            .reminderSentAt(booking.getReminderSentAt())
            .createdAt(booking.getCreatedAt())
            .updatedAt(booking.getUpdatedAt())
            .build();
    }

    private BookingListResponseDto convertToListResponseDto(Booking booking) {
        return BookingListResponseDto.builder()
            .id(booking.getId())
            .bookingReference(booking.getBookingReference())
            .hotelId(booking.getHotelId())
            .roomId(booking.getRoomId())
            .userId(booking.getUserId())
            .guestName(booking.getGuestName())
            .guestEmail(booking.getGuestEmail())
            .checkInDate(booking.getCheckInDate())
            .checkOutDate(booking.getCheckOutDate())
            .numberOfGuests(booking.getNumberOfGuests())
            .totalNights(booking.getTotalNights())
            .finalAmount(booking.getFinalAmount())
            .status(booking.getStatus())
            .paymentStatus(booking.getPaymentStatus())
            .createdAt(booking.getCreatedAt())
            .build();
    }
}
