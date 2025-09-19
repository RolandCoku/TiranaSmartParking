package com.tirana.smartparking.parking.controller;

import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.dto.Money;
import com.tirana.smartparking.common.dto.PaginatedResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import com.tirana.smartparking.parking.dto.*;
import com.tirana.smartparking.parking.service.ParkingSessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/v1/parking-sessions")
@Validated
public class ParkingSessionController {

    private final ParkingSessionService parkingSessionService;

    public ParkingSessionController(ParkingSessionService parkingSessionService) {
        this.parkingSessionService = parkingSessionService;
    }

    @PreAuthorize("hasAuthority('SESSION_CREATE')")
    @PostMapping
    public ResponseEntity<ApiResponse<ParkingSessionDTO>> startSession(@Valid @RequestBody ParkingSessionStartDTO startDTO) {
        ParkingSessionDTO session = parkingSessionService.startSession(startDTO);
        return ResponseHelper.created("Parking session started successfully", session);
    }

    @PreAuthorize("hasAuthority('SESSION_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ParkingSessionDTO>> getSessionById(@PathVariable Long id) {
        ParkingSessionDTO session = parkingSessionService.getSessionById(id);
        return ResponseHelper.ok("Session retrieved successfully", session);
    }

    @PreAuthorize("hasAuthority('SESSION_READ')")
    @GetMapping("/reference/{reference}")
    public ResponseEntity<ApiResponse<ParkingSessionDTO>> getSessionByReference(@PathVariable String reference) {
        ParkingSessionDTO session = parkingSessionService.getSessionByReference(reference);
        return ResponseHelper.ok("Session retrieved successfully", session);
    }

    @PreAuthorize("hasAuthority('SESSION_READ')")
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<ParkingSessionDTO>>> getUserSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        PaginatedResponse<ParkingSessionDTO> sessions = parkingSessionService.getUserSessions(page, size, sortBy, sortDir);
        return ResponseHelper.ok("User sessions retrieved successfully", sessions);
    }

    @PreAuthorize("hasAuthority('SESSION_READ')")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<PaginatedResponse<ParkingSessionDTO>>> getActiveSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<ParkingSessionDTO> sessions = parkingSessionService.getActiveSessions(page, size);
        return ResponseHelper.ok("Active sessions retrieved successfully", sessions);
    }

    @PreAuthorize("hasAuthority('SESSION_READ')")
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<PaginatedResponse<ParkingSessionDTO>>> getSessionHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<ParkingSessionDTO> sessions = parkingSessionService.getSessionHistory(page, size);
        return ResponseHelper.ok("Session history retrieved successfully", sessions);
    }

    @PreAuthorize("hasAuthority('SESSION_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ParkingSessionDTO>> updateSession(
            @PathVariable Long id,
            @Valid @RequestBody ParkingSessionUpdateDTO updateDTO) {
        ParkingSessionDTO updatedSession = parkingSessionService.updateSession(id, updateDTO);
        return ResponseHelper.ok("Session updated successfully", updatedSession);
    }

    @PreAuthorize("hasAuthority('SESSION_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable Long id) {
        parkingSessionService.deleteSession(id);
        return ResponseHelper.noContent();
    }

    @PreAuthorize("hasAuthority('SESSION_UPDATE')")
    @PostMapping("/{id}/stop")
    public ResponseEntity<ApiResponse<ParkingSessionDTO>> stopSession(
            @PathVariable Long id,
            @Valid @RequestBody ParkingSessionStopDTO stopDTO) {
        ParkingSessionDTO stoppedSession = parkingSessionService.stopSession(id, stopDTO);
        return ResponseHelper.ok("Session stopped successfully", stoppedSession);
    }

    @PreAuthorize("hasAuthority('SESSION_UPDATE')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ParkingSessionDTO>> cancelSession(@PathVariable Long id) {
        ParkingSessionDTO cancelledSession = parkingSessionService.cancelSession(id);
        return ResponseHelper.ok("Session cancelled successfully", cancelledSession);
    }

    @PreAuthorize("hasAuthority('SESSION_UPDATE')")
    @PostMapping("/{id}/extend")
    public ResponseEntity<ApiResponse<ParkingSessionDTO>> extendSession(
            @PathVariable Long id,
            @RequestParam ZonedDateTime newEndTime) {
        ParkingSessionDTO extendedSession = parkingSessionService.extendSession(id, newEndTime);
        return ResponseHelper.ok("Session extended successfully", extendedSession);
    }

    @PreAuthorize("hasAuthority('PRICING_QUOTE')")
    @PostMapping("/quote")
    public ResponseEntity<ApiResponse<Money>> getSessionQuote(@Valid @RequestBody ParkingSessionQuoteDTO quoteDTO) {
        Money quote = parkingSessionService.getSessionQuote(quoteDTO);
        return ResponseHelper.ok("Session quote calculated successfully", quote);
    }

    @PreAuthorize("hasAuthority('SESSION_READ')")
    @GetMapping("/availability")
    public ResponseEntity<ApiResponse<Boolean>> checkSpaceAvailability(
            @RequestParam Long spaceId,
            @RequestParam ZonedDateTime startTime,
            @RequestParam ZonedDateTime endTime) {
        boolean available = parkingSessionService.isSpaceAvailable(spaceId, startTime, endTime);
        return ResponseHelper.ok("Availability checked successfully", available);
    }
}
