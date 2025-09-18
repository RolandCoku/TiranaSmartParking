package com.tirana.smartparking.bookings.service;

import com.tirana.smartparking.bookings.dto.BookingDTO;
import com.tirana.smartparking.bookings.dto.BookingQuoteDTO;
import com.tirana.smartparking.bookings.dto.BookingRegistrationDTO;
import com.tirana.smartparking.bookings.dto.BookingUpdateDTO;
import com.tirana.smartparking.common.dto.Money;
import com.tirana.smartparking.common.dto.PaginatedResponse;

import java.time.ZonedDateTime;

public interface BookingService {
    
    // CRUD operations (user ID extracted from SecurityContext)
    BookingDTO createBooking(BookingRegistrationDTO registrationDTO);
    BookingDTO getBookingById(Long id);
    BookingDTO getBookingByReference(String bookingReference);
    PaginatedResponse<BookingDTO> getUserBookings(int page, int size, String sortBy, String sortDir);
    PaginatedResponse<BookingDTO> getCurrentBookings(int page, int size);
    PaginatedResponse<BookingDTO> getBookingHistory(int page, int size);
    BookingDTO updateBooking(Long id, BookingUpdateDTO updateDTO);
    void deleteBooking(Long id);
    
    // Booking management operations
    BookingDTO cancelBooking(Long id);
    BookingDTO startBooking(Long id);
    BookingDTO completeBooking(Long id);
    BookingDTO extendBooking(Long id, ZonedDateTime newEndTime);
    
    // Quote operations
    Money getBookingQuote(BookingQuoteDTO quoteDTO);
    
    // Availability checking
    boolean isSpaceAvailable(Long spaceId, ZonedDateTime startTime, ZonedDateTime endTime);
    
    // Admin operations (with explicit user ID)
    PaginatedResponse<BookingDTO> getAllBookings(int page, int size, String sortBy, String sortDir);
    PaginatedResponse<BookingDTO> getBookingsBySpace(Long spaceId, int page, int size);
    PaginatedResponse<BookingDTO> getBookingsByLot(Long lotId, int page, int size);
    PaginatedResponse<BookingDTO> getUserBookings(Long userId, int page, int size, String sortBy, String sortDir);
    
    // Maintenance operations
    void updateExpiredBookings();
    void updateCompletedBookings();
    
    // Scheduler operations
    void activateDueBookings();
    void cancelNoShows();
}