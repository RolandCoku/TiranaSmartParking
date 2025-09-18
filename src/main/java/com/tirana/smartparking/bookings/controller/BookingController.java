package com.tirana.smartparking.bookings.controller;

import com.tirana.smartparking.bookings.dto.BookingDTO;
import com.tirana.smartparking.bookings.dto.BookingQuoteDTO;
import com.tirana.smartparking.bookings.dto.BookingRegistrationDTO;
import com.tirana.smartparking.bookings.dto.BookingUpdateDTO;
import com.tirana.smartparking.bookings.service.BookingService;
import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.dto.PaginatedResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/v1/bookings")
@Validated
public class BookingController {
    
    private final BookingService bookingService;
    
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    
    // User booking operations
    @PreAuthorize("hasAuthority('BOOKING_CREATE')")
    @PostMapping
    public ResponseEntity<ApiResponse<BookingDTO>> createBooking(@Valid @RequestBody BookingRegistrationDTO registrationDTO) {
        BookingDTO booking = bookingService.createBooking(registrationDTO);
        return ResponseHelper.created("Booking created successfully", booking);
    }
    
    @PreAuthorize("hasAuthority('BOOKING_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingDTO>> getBookingById(@PathVariable Long id) {
        BookingDTO booking = bookingService.getBookingById(id);
        return ResponseHelper.ok("Booking retrieved successfully", booking);
    }
    
    @PreAuthorize("hasAuthority('BOOKING_READ')")
    @GetMapping("/reference/{reference}")
    public ResponseEntity<ApiResponse<BookingDTO>> getBookingByReference(@PathVariable String reference) {
        BookingDTO booking = bookingService.getBookingByReference(reference);
        return ResponseHelper.ok("Booking retrieved successfully", booking);
    }
    
    @PreAuthorize("hasAuthority('BOOKING_READ')")
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<BookingDTO>>> getUserBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        PaginatedResponse<BookingDTO> bookings = bookingService.getUserBookings(page, size, sortBy, sortDir);
        return ResponseHelper.ok("User bookings retrieved successfully", bookings);
    }
    
    @PreAuthorize("hasAuthority('BOOKING_READ')")
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<PaginatedResponse<BookingDTO>>> getCurrentBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<BookingDTO> bookings = bookingService.getCurrentBookings(page, size);
        return ResponseHelper.ok("Current bookings retrieved successfully", bookings);
    }
    
    @PreAuthorize("hasAuthority('BOOKING_READ')")
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<PaginatedResponse<BookingDTO>>> getBookingHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<BookingDTO> bookings = bookingService.getBookingHistory(page, size);
        return ResponseHelper.ok("Booking history retrieved successfully", bookings);
    }
    
    @PreAuthorize("hasAuthority('BOOKING_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingDTO>> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody BookingUpdateDTO updateDTO) {
        BookingDTO booking = bookingService.updateBooking(id, updateDTO);
        return ResponseHelper.ok("Booking updated successfully", booking);
    }
    
    @PreAuthorize("hasAuthority('BOOKING_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseHelper.ok("Booking deleted successfully", null);
    }
    
    // Booking management operations
    @PreAuthorize("hasAuthority('BOOKING_UPDATE')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingDTO>> cancelBooking(@PathVariable Long id) {
        BookingDTO booking = bookingService.cancelBooking(id);
        return ResponseHelper.ok("Booking cancelled successfully", booking);
    }
    
    @PreAuthorize("hasAuthority('BOOKING_UPDATE')")
    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<BookingDTO>> startBooking(@PathVariable Long id) {
        BookingDTO booking = bookingService.startBooking(id);
        return ResponseHelper.ok("Booking started successfully", booking);
    }
    
    @PreAuthorize("hasAuthority('BOOKING_UPDATE')")
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<BookingDTO>> completeBooking(@PathVariable Long id) {
        BookingDTO booking = bookingService.completeBooking(id);
        return ResponseHelper.ok("Booking completed successfully", booking);
    }
    
    @PreAuthorize("hasAuthority('BOOKING_UPDATE')")
    @PostMapping("/{id}/extend")
    public ResponseEntity<ApiResponse<BookingDTO>> extendBooking(
            @PathVariable Long id,
            @RequestParam ZonedDateTime newEndTime) {
        BookingDTO booking = bookingService.extendBooking(id, newEndTime);
        return ResponseHelper.ok("Booking extended successfully", booking);
    }
    
    // Quote operations
    @PreAuthorize("hasAuthority('PRICING_QUOTE')")
    @PostMapping("/quote")
    public ResponseEntity<ApiResponse<com.tirana.smartparking.common.dto.Money>> getBookingQuote(
            @Valid @RequestBody BookingQuoteDTO quoteDTO) {
        com.tirana.smartparking.common.dto.Money quote = bookingService.getBookingQuote(quoteDTO);
        return ResponseHelper.ok("Booking quote calculated successfully", quote);
    }
    
    // Availability checking
    @PreAuthorize("hasAuthority('BOOKING_READ')")
    @GetMapping("/availability")
    public ResponseEntity<ApiResponse<Boolean>> checkAvailability(
            @RequestParam Long spaceId,
            @RequestParam ZonedDateTime startTime,
            @RequestParam ZonedDateTime endTime) {
        boolean available = bookingService.isSpaceAvailable(spaceId, startTime, endTime);
        return ResponseHelper.ok("Availability checked successfully", available);
    }
}