package com.tirana.smartparking.parking.service;

import com.tirana.smartparking.common.dto.Money;
import com.tirana.smartparking.common.dto.PaginatedResponse;
import com.tirana.smartparking.parking.dto.*;

import java.time.ZonedDateTime;

public interface ParkingSessionService {

    // CRUD operations
    ParkingSessionDTO startSession(ParkingSessionStartDTO startDTO);
    ParkingSessionDTO getSessionById(Long id);
    ParkingSessionDTO getSessionByReference(String sessionReference);
    PaginatedResponse<ParkingSessionDTO> getUserSessions(int page, int size, String sortBy, String sortDir);
    PaginatedResponse<ParkingSessionDTO> getActiveSessions(int page, int size);
    PaginatedResponse<ParkingSessionDTO> getSessionHistory(int page, int size);
    ParkingSessionDTO updateSession(Long id, ParkingSessionUpdateDTO updateDTO);
    void deleteSession(Long id);

    // Session lifecycle operations
    ParkingSessionDTO stopSession(Long id, ParkingSessionStopDTO stopDTO);
    ParkingSessionDTO cancelSession(Long id);
    ParkingSessionDTO extendSession(Long id, ZonedDateTime newEndTime);

    // Pricing and availability
    Money getSessionQuote(ParkingSessionQuoteDTO quoteDTO);
    boolean isSpaceAvailable(Long spaceId, ZonedDateTime startTime, ZonedDateTime endTime);

    // Admin operations
    PaginatedResponse<ParkingSessionDTO> getAllSessions(int page, int size, String sortBy, String sortDir);
    PaginatedResponse<ParkingSessionDTO> getSessionsBySpace(Long spaceId, int page, int size);
    PaginatedResponse<ParkingSessionDTO> getSessionsByLot(Long lotId, int page, int size);
    PaginatedResponse<ParkingSessionDTO> getSessionsByUser(Long userId, int page, int size, String sortBy, String sortDir);
    void updateExpiredSessions();
    void updateCompletedSessions();
}
