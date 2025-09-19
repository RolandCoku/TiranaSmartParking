package com.tirana.smartparking.parking.controller;

import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.dto.PaginatedResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import com.tirana.smartparking.parking.dto.ParkingSessionDTO;
import com.tirana.smartparking.parking.dto.ParkingSessionStopDTO;
import com.tirana.smartparking.parking.dto.ParkingSessionUpdateDTO;
import com.tirana.smartparking.parking.service.ParkingSessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/v1/admin/parking-sessions")
@Validated
public class AdminParkingSessionController {

    private final ParkingSessionService parkingSessionService;

    public AdminParkingSessionController(ParkingSessionService parkingSessionService) {
        this.parkingSessionService = parkingSessionService;
    }

    @PreAuthorize("hasAuthority('SESSION_READ')")
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<ParkingSessionDTO>>> getAllSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        PaginatedResponse<ParkingSessionDTO> sessions = parkingSessionService.getAllSessions(page, size, sortBy, sortDir);
        return ResponseHelper.ok("All parking sessions fetched successfully", sessions);
    }

    @PreAuthorize("hasAuthority('SESSION_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ParkingSessionDTO>> getSessionById(@PathVariable Long id) {
        ParkingSessionDTO session = parkingSessionService.getSessionById(id);
        return ResponseHelper.ok("Session fetched successfully", session);
    }

    @PreAuthorize("hasAuthority('SESSION_READ')")
    @GetMapping("/reference/{reference}")
    public ResponseEntity<ApiResponse<ParkingSessionDTO>> getSessionByReference(@PathVariable String reference) {
        ParkingSessionDTO session = parkingSessionService.getSessionByReference(reference);
        return ResponseHelper.ok("Session fetched successfully", session);
    }

    @PreAuthorize("hasAuthority('SESSION_READ')")
    @GetMapping("/spaces/{spaceId}")
    public ResponseEntity<ApiResponse<PaginatedResponse<ParkingSessionDTO>>> getSessionsBySpace(
            @PathVariable Long spaceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<ParkingSessionDTO> sessions = parkingSessionService.getSessionsBySpace(spaceId, page, size);
        return ResponseHelper.ok("Sessions for space fetched successfully", sessions);
    }

    @PreAuthorize("hasAuthority('SESSION_READ')")
    @GetMapping("/lots/{lotId}")
    public ResponseEntity<ApiResponse<PaginatedResponse<ParkingSessionDTO>>> getSessionsByLot(
            @PathVariable Long lotId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<ParkingSessionDTO> sessions = parkingSessionService.getSessionsByLot(lotId, page, size);
        return ResponseHelper.ok("Sessions for lot fetched successfully", sessions);
    }

    @PreAuthorize("hasAuthority('SESSION_READ')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<PaginatedResponse<ParkingSessionDTO>>> getSessionsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        PaginatedResponse<ParkingSessionDTO> sessions = parkingSessionService.getSessionsByUser(userId, page, size, sortBy, sortDir);
        return ResponseHelper.ok("Sessions for user fetched successfully", sessions);
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
            @RequestParam ZonedDateTime endTime) {
        ParkingSessionStopDTO stopDTO = new ParkingSessionStopDTO(endTime, "Stopped by admin");
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

    @PreAuthorize("hasAuthority('SESSION_UPDATE')")
    @PostMapping("/maintenance/update-expired")
    public ResponseEntity<ApiResponse<Void>> updateExpiredSessions() {
        parkingSessionService.updateExpiredSessions();
        return ResponseHelper.ok("Expired sessions updated successfully", null);
    }

    @PreAuthorize("hasAuthority('SESSION_UPDATE')")
    @PostMapping("/maintenance/update-completed")
    public ResponseEntity<ApiResponse<Void>> updateCompletedSessions() {
        parkingSessionService.updateCompletedSessions();
        return ResponseHelper.ok("Completed sessions updated successfully", null);
    }
}
