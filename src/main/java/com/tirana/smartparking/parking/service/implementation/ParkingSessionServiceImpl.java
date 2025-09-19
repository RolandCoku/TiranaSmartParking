package com.tirana.smartparking.parking.service.implementation;

import com.tirana.smartparking.common.dto.Money;
import com.tirana.smartparking.common.dto.PaginatedResponse;
import com.tirana.smartparking.common.exception.ResourceConflictException;
import com.tirana.smartparking.common.exception.ResourceNotFoundException;
import com.tirana.smartparking.common.service.SecurityContextService;
import com.tirana.smartparking.common.util.PaginationUtil;
import com.tirana.smartparking.parking.dto.*;
import com.tirana.smartparking.parking.entity.ParkingSession;
import com.tirana.smartparking.parking.entity.ParkingSpace;
import com.tirana.smartparking.parking.repository.ParkingSessionRepository;
import com.tirana.smartparking.parking.repository.ParkingSpaceRepository;
import com.tirana.smartparking.parking.service.ParkingSessionService;
import com.tirana.smartparking.parking.service.PricingService;
import com.tirana.smartparking.user.entity.User;
import com.tirana.smartparking.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ParkingSessionServiceImpl implements ParkingSessionService {

    private final ParkingSessionRepository parkingSessionRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final UserRepository userRepository;
    private final PricingService pricingService;
    private final SecurityContextService securityContextService;

    public ParkingSessionServiceImpl(ParkingSessionRepository parkingSessionRepository,
                                     ParkingSpaceRepository parkingSpaceRepository,
                                     UserRepository userRepository,
                                     PricingService pricingService,
                                     SecurityContextService securityContextService) {
        this.parkingSessionRepository = parkingSessionRepository;
        this.parkingSpaceRepository = parkingSpaceRepository;
        this.userRepository = userRepository;
        this.pricingService = pricingService;
        this.securityContextService = securityContextService;
    }

    @Override
    public ParkingSessionDTO startSession(ParkingSessionStartDTO startDTO) {
        // Get current user ID from SecurityContext
        Long userId = securityContextService.getCurrentUserId();

        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validate parking space exists
        ParkingSpace parkingSpace = parkingSpaceRepository.findById(startDTO.parkingSpaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Parking space not found with id: " + startDTO.parkingSpaceId()));

        // Check if space is available
        ZonedDateTime now = ZonedDateTime.now();
        if (!isSpaceAvailable(startDTO.parkingSpaceId(), now, now.plusHours(24))) { // Default 24-hour check
            throw new ResourceConflictException("Parking space is not available");
        }

        // Get initial pricing quote (for 1 hour as default)
        Money quote = pricingService.quote(
                parkingSpace.getParkingLot() != null ? parkingSpace.getParkingLot().getId() : null,
                startDTO.parkingSpaceId(),
                startDTO.vehicleType(),
                startDTO.userGroup(),
                now,
                now.plusHours(1)
        );

        // Create session
        ParkingSession session = new ParkingSession();
        session.setUser(user);
        session.setSpace(parkingSpace);
        session.setVehiclePlate(startDTO.vehiclePlate());
        session.setVehicleType(startDTO.vehicleType());
        session.setUserGroup(startDTO.userGroup());
        session.setStartedAt(now);
        session.setStatus(ParkingSession.SessionStatus.ACTIVE);
        session.setBilledAmount(quote.getAmount());
        session.setCurrency(quote.getCurrency());
        session.setSessionReference(generateSessionReference());
        session.setPaymentMethodId(startDTO.paymentMethodId());
        session.setNotes(startDTO.notes());

        ParkingSession savedSession = parkingSessionRepository.save(session);
        return mapToParkingSessionDTO(savedSession);
    }

    @Override
    @Transactional(readOnly = true)
    public ParkingSessionDTO getSessionById(Long id) {
        Long userId = securityContextService.getCurrentUserId();
        ParkingSession session = parkingSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking session not found with id: " + id));

        // Ensure user can only access their own sessions (unless admin)
        if (!session.getUser().getId().equals(userId)) {
            throw new BadCredentialsException("Access denied: You can only view your own sessions");
        }

        return mapToParkingSessionDTO(session);
    }

    @Override
    @Transactional(readOnly = true)
    public ParkingSessionDTO getSessionByReference(String sessionReference) {
        Long userId = securityContextService.getCurrentUserId();
        ParkingSession session = parkingSessionRepository.findBySessionReference(sessionReference)
                .orElseThrow(() -> new ResourceNotFoundException("Parking session not found with reference: " + sessionReference));

        // Ensure user can only access their own sessions (unless admin)
        if (!session.getUser().getId().equals(userId)) {
            throw new BadCredentialsException("Access denied: You can only view your own sessions");
        }

        return mapToParkingSessionDTO(session);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ParkingSessionDTO> getUserSessions(int page, int size, String sortBy, String sortDir) {
        Long userId = securityContextService.getCurrentUserId();
        return getSessionDTOPaginatedResponse(page, size, sortBy, sortDir, userId);
    }

    private PaginatedResponse<ParkingSessionDTO> getSessionDTOPaginatedResponse(int page, int size, String sortBy, String sortDir, Long userId) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);
        Page<ParkingSession> sessions = parkingSessionRepository.findByUserId(userId, pageable);
        return PaginationUtil.toPaginatedResponse(sessions.map(this::mapToParkingSessionDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ParkingSessionDTO> getActiveSessions(int page, int size) {
        Long userId = securityContextService.getCurrentUserId();
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startedAt"));
        Page<ParkingSession> sessions = parkingSessionRepository.findActiveSessionsByUser(userId, pageable);
        return PaginationUtil.toPaginatedResponse(sessions.map(this::mapToParkingSessionDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ParkingSessionDTO> getSessionHistory(int page, int size) {
        Long userId = securityContextService.getCurrentUserId();
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startedAt"));
        Page<ParkingSession> sessions = parkingSessionRepository.findSessionHistoryByUser(userId, pageable);
        return PaginationUtil.toPaginatedResponse(sessions.map(this::mapToParkingSessionDTO));
    }

    @Override
    public ParkingSessionDTO updateSession(Long id, ParkingSessionUpdateDTO updateDTO) {
        Long userId = securityContextService.getCurrentUserId();
        ParkingSession session = parkingSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking session not found with id: " + id));

        // Ensure user can only update their own sessions
        if (!session.getUser().getId().equals(userId)) {
            throw new ResourceConflictException("Access denied: You can only update your own sessions");
        }

        // Only allow updates for active sessions
        if (session.getStatus() != ParkingSession.SessionStatus.ACTIVE) {
            throw new ResourceConflictException("Only active sessions can be updated");
        }

        // Update fields if provided
        if (updateDTO.vehiclePlate() != null) {
            session.setVehiclePlate(updateDTO.vehiclePlate());
        }
        if (updateDTO.vehicleType() != null) {
            session.setVehicleType(updateDTO.vehicleType());
        }
        if (updateDTO.userGroup() != null) {
            session.setUserGroup(updateDTO.userGroup());
        }
        if (updateDTO.endTime() != null) {
            session.setEndedAt(updateDTO.endTime());
        }
        if (updateDTO.status() != null) {
            session.setStatus(updateDTO.status());
        }
        if (updateDTO.paymentMethodId() != null) {
            session.setPaymentMethodId(updateDTO.paymentMethodId());
        }
        if (updateDTO.notes() != null) {
            session.setNotes(updateDTO.notes());
        }

        // Recalculate price if needed
        if (updateDTO.endTime() != null || updateDTO.userGroup() != null) {
            Money quote = pricingService.quote(
                    session.getSpace().getParkingLot() != null ? session.getSpace().getParkingLot().getId() : null,
                    session.getSpace().getId(),
                    session.getVehicleType(),
                    session.getUserGroup(),
                    session.getStartedAt(),
                    session.getEndedAt() != null ? session.getEndedAt() : ZonedDateTime.now()
            );
            session.setBilledAmount(quote.getAmount());
            session.setCurrency(quote.getCurrency());
        }

        ParkingSession savedSession = parkingSessionRepository.save(session);
        return mapToParkingSessionDTO(savedSession);
    }

    @Override
    public void deleteSession(Long id) {
        Long userId = securityContextService.getCurrentUserId();
        ParkingSession session = parkingSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking session not found with id: " + id));

        // Ensure user can only delete their own sessions
        if (!session.getUser().getId().equals(userId)) {
            throw new ResourceConflictException("Access denied: You can only delete your own sessions");
        }

        // Only allow deletion of completed or cancelled sessions
        if (session.getStatus() == ParkingSession.SessionStatus.ACTIVE) {
            throw new ResourceConflictException("Only completed or cancelled sessions can be deleted");
        }

        parkingSessionRepository.deleteById(id);
    }

    @Override
    public ParkingSessionDTO stopSession(Long id, ParkingSessionStopDTO stopDTO) {
        Long userId = securityContextService.getCurrentUserId();
        ParkingSession session = parkingSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking session not found with id: " + id));

        // Ensure user can only stop their own sessions
        if (!session.getUser().getId().equals(userId)) {
            throw new ResourceConflictException("Access denied: You can only stop your own sessions");
        }

        // Only allow stopping active sessions
        if (session.getStatus() != ParkingSession.SessionStatus.ACTIVE) {
            throw new ResourceConflictException("Only active sessions can be stopped");
        }

        session.setEndedAt(stopDTO.endTime());
        session.setStatus(ParkingSession.SessionStatus.COMPLETED);
        if (stopDTO.notes() != null) {
            session.setNotes(stopDTO.notes());
        }

        // Recalculate final price
        Money quote = pricingService.quote(
                session.getSpace().getParkingLot() != null ? session.getSpace().getParkingLot().getId() : null,
                session.getSpace().getId(),
                session.getVehicleType(),
                session.getUserGroup(),
                session.getStartedAt(),
                session.getEndedAt()
        );
        session.setBilledAmount(quote.getAmount());
        session.setCurrency(quote.getCurrency());

        ParkingSession savedSession = parkingSessionRepository.save(session);
        return mapToParkingSessionDTO(savedSession);
    }

    @Override
    public ParkingSessionDTO cancelSession(Long id) {
        Long userId = securityContextService.getCurrentUserId();
        ParkingSession session = parkingSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking session not found with id: " + id));

        // Ensure user can only cancel their own sessions
        if (!session.getUser().getId().equals(userId)) {
            throw new ResourceConflictException("Access denied: You can only cancel your own sessions");
        }

        // Only allow cancellation of active sessions
        if (session.getStatus() != ParkingSession.SessionStatus.ACTIVE) {
            throw new ResourceConflictException("Only active sessions can be cancelled");
        }

        session.setStatus(ParkingSession.SessionStatus.CANCELLED);
        session.setEndedAt(ZonedDateTime.now());

        ParkingSession savedSession = parkingSessionRepository.save(session);
        return mapToParkingSessionDTO(savedSession);
    }

    @Override
    public ParkingSessionDTO extendSession(Long id, ZonedDateTime newEndTime) {
        Long userId = securityContextService.getCurrentUserId();
        ParkingSession session = parkingSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking session not found with id: " + id));

        // Ensure user can only extend their own sessions
        if (!session.getUser().getId().equals(userId)) {
            throw new ResourceConflictException("Access denied: You can only extend your own sessions");
        }

        // Only allow extending active sessions
        if (session.getStatus() != ParkingSession.SessionStatus.ACTIVE) {
            throw new ResourceConflictException("Only active sessions can be extended");
        }

        // Check for conflicts with new end time
        if (!isSpaceAvailable(session.getSpace().getId(), session.getStartedAt(), newEndTime)) {
            throw new ResourceConflictException("Parking space is not available for the extended time period");
        }

        session.setEndedAt(newEndTime);

        // Recalculate price
        Money quote = pricingService.quote(
                session.getSpace().getParkingLot() != null ? session.getSpace().getParkingLot().getId() : null,
                session.getSpace().getId(),
                session.getVehicleType(),
                session.getUserGroup(),
                session.getStartedAt(),
                session.getEndedAt()
        );
        session.setBilledAmount(quote.getAmount());
        session.setCurrency(quote.getCurrency());

        ParkingSession savedSession = parkingSessionRepository.save(session);
        return mapToParkingSessionDTO(savedSession);
    }

    @Override
    @Transactional(readOnly = true)
    public Money getSessionQuote(ParkingSessionQuoteDTO quoteDTO) {
        return pricingService.quote(
                null, // Will be resolved by pricing service
                quoteDTO.parkingSpaceId(),
                quoteDTO.vehicleType(),
                quoteDTO.userGroup(),
                quoteDTO.startTime(),
                quoteDTO.endTime()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSpaceAvailable(Long spaceId, ZonedDateTime startTime, ZonedDateTime endTime) {
        List<ParkingSession> conflicts = parkingSessionRepository.findConflictingSessions(spaceId, startTime, endTime);
        return conflicts.isEmpty();
    }

    // Admin operations with explicit user ID
    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ParkingSessionDTO> getAllSessions(int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);
        Page<ParkingSession> sessions = parkingSessionRepository.findAll(pageable);
        return PaginationUtil.toPaginatedResponse(sessions.map(this::mapToParkingSessionDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ParkingSessionDTO> getSessionsBySpace(Long spaceId, int page, int size) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startedAt"));
        Page<ParkingSession> sessions = parkingSessionRepository.findBySpaceId(spaceId, pageable);
        return PaginationUtil.toPaginatedResponse(sessions.map(this::mapToParkingSessionDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ParkingSessionDTO> getSessionsByLot(Long lotId, int page, int size) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startedAt"));
        Page<ParkingSession> sessions = parkingSessionRepository.findByParkingLotId(lotId, pageable);
        return PaginationUtil.toPaginatedResponse(sessions.map(this::mapToParkingSessionDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ParkingSessionDTO> getSessionsByUser(Long userId, int page, int size, String sortBy, String sortDir) {
        return getSessionDTOPaginatedResponse(page, size, sortBy, sortDir, userId);
    }

    @Override
    public void updateExpiredSessions() {
        ZonedDateTime expiredBefore = ZonedDateTime.now().minusHours(24); // Sessions older than 24 hours
        List<ParkingSession> expiredSessions = parkingSessionRepository.findExpiredActiveSessions(expiredBefore);

        for (ParkingSession session : expiredSessions) {
            session.setStatus(ParkingSession.SessionStatus.EXPIRED);
            session.setEndedAt(ZonedDateTime.now());
            parkingSessionRepository.save(session);
        }
    }

    @Override
    public void updateCompletedSessions() {
        // This method can be used for any cleanup or final processing
        // For now, it's a placeholder for future business logic
    }

    private String generateSessionReference() {
        String prefix = "PSN";
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + randomPart;
    }

    private ParkingSessionDTO mapToParkingSessionDTO(ParkingSession session) {
        return new ParkingSessionDTO(
                session.getId(),
                session.getUser().getId(),
                session.getUser().getEmail(),
                session.getSpace().getId(),
                session.getSpace().getLabel(),
                session.getSpace().getParkingLot() != null ? session.getSpace().getParkingLot().getId() : null,
                session.getSpace().getParkingLot() != null ? session.getSpace().getParkingLot().getName() : null,
                session.getSpace().getParkingLot() != null ? session.getSpace().getParkingLot().getAddress() : null,
                session.getVehiclePlate(),
                session.getVehicleType(),
                session.getUserGroup(),
                session.getStartedAt(),
                session.getEndedAt(),
                session.getBilledAmount(),
                session.getCurrency(),
                session.getStatus(),
                session.getSessionReference(),
                session.getPaymentMethodId(),
                session.getNotes(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }
}
