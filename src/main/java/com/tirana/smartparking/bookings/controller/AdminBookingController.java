package com.tirana.smartparking.bookings.controller;

import com.tirana.smartparking.bookings.dto.BookingDTO;
import com.tirana.smartparking.bookings.dto.BookingUpdateDTO;
import com.tirana.smartparking.bookings.service.BookingService;
import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.dto.PaginatedResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/v1/admin/bookings")
public class AdminBookingController {
    
    private final BookingService bookingService;
    
    public AdminBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    
    @PreAuthorize("hasAuthority('BOOKING_READ')")
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<BookingDTO>>> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        PaginatedResponse<BookingDTO> bookings = bookingService.getAllBookings(page, size, sortBy, sortDir);
        return ResponseHelper.ok("All bookings retrieved successfully", bookings);
    }
    
    @PreAuthorize("hasAuthority('BOOKING_READ')")
    @GetMapping("/spaces/{spaceId}")
    public ResponseEntity<ApiResponse<PaginatedResponse<BookingDTO>>> getBookingsBySpace(
            @PathVariable Long spaceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<BookingDTO> bookings = bookingService.getBookingsBySpace(spaceId, page, size);
        return ResponseHelper.ok("Bookings for space retrieved successfully", bookings);
    }
    
    @PreAuthorize("hasAuthority('BOOKING_READ')")
    @GetMapping("/lots/{lotId}")
    public ResponseEntity<ApiResponse<PaginatedResponse<BookingDTO>>> getBookingsByLot(
            @PathVariable Long lotId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<BookingDTO> bookings = bookingService.getBookingsByLot(lotId, page, size);
        return ResponseHelper.ok("Bookings for lot retrieved successfully", bookings);
    }
    
    @PreAuthorize("hasAuthority('BOOKING_READ')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<PaginatedResponse<BookingDTO>>> getUserBookings(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        PaginatedResponse<BookingDTO> bookings = bookingService.getUserBookings(userId, page, size, sortBy, sortDir);
        return ResponseHelper.ok("User bookings retrieved successfully", bookings);
    }
    
    
    // Admin-specific booking management endpoints
    
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
    
}