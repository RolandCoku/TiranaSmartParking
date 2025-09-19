package com.tirana.smartparking.parking.repository;

import com.tirana.smartparking.parking.entity.ParkingSession;
import com.tirana.smartparking.parking.entity.ParkingSession.SessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {

    Optional<ParkingSession> findBySessionReference(String sessionReference);

    Page<ParkingSession> findByUserId(Long userId, Pageable pageable);

    // Find current (ACTIVE) sessions for a user
    @Query("SELECT ps FROM ParkingSession ps WHERE ps.user.id = :userId AND ps.status = 'ACTIVE' ORDER BY ps.startedAt DESC")
    Page<ParkingSession> findActiveSessionsByUser(@Param("userId") Long userId, Pageable pageable);

    // Find session history (COMPLETED, CANCELLED, EXPIRED) for a user
    @Query("SELECT ps FROM ParkingSession ps WHERE ps.user.id = :userId AND (ps.status = 'COMPLETED' OR ps.status = 'CANCELLED' OR ps.status = 'EXPIRED') ORDER BY ps.startedAt DESC")
    Page<ParkingSession> findSessionHistoryByUser(@Param("userId") Long userId, Pageable pageable);

    // Check for conflicting sessions for a given space and time range
    @Query("SELECT ps FROM ParkingSession ps WHERE ps.space.id = :spaceId " +
           "AND ps.status = 'ACTIVE' " +
           "AND ((ps.startedAt < :endTime AND (ps.endedAt IS NULL OR ps.endedAt > :startTime)))")
    List<ParkingSession> findConflictingSessions(@Param("spaceId") Long spaceId,
                                                 @Param("startTime") ZonedDateTime startTime,
                                                 @Param("endTime") ZonedDateTime endTime);

    Page<ParkingSession> findBySpaceId(Long spaceId, Pageable pageable);

    @Query("SELECT ps FROM ParkingSession ps WHERE ps.space.parkingLot.id = :lotId")
    Page<ParkingSession> findByParkingLotId(@Param("lotId") Long lotId, Pageable pageable);

    // Find active sessions that should be expired (startedAt is too old and status is ACTIVE)
    @Query("SELECT ps FROM ParkingSession ps WHERE ps.status = 'ACTIVE' AND ps.startedAt < :expiredBefore")
    List<ParkingSession> findExpiredActiveSessions(@Param("expiredBefore") ZonedDateTime expiredBefore);

    Page<ParkingSession> findByStatus(SessionStatus status, Pageable pageable);

    Page<ParkingSession> findByVehiclePlate(String vehiclePlate, Pageable pageable);

    Page<ParkingSession> findByUserIdAndVehiclePlate(Long userId, String vehiclePlate, Pageable pageable);

    // Find sessions within a date range
    @Query("SELECT ps FROM ParkingSession ps WHERE ps.startedAt >= :startDate AND ps.startedAt <= :endDate")
    Page<ParkingSession> findSessionsByDateRange(@Param("startDate") ZonedDateTime startDate,
                                                 @Param("endDate") ZonedDateTime endDate,
                                                 Pageable pageable);

    // Find sessions by user within a date range
    @Query("SELECT ps FROM ParkingSession ps WHERE ps.user.id = :userId AND ps.startedAt >= :startDate AND ps.startedAt <= :endDate")
    Page<ParkingSession> findUserSessionsByDateRange(@Param("userId") Long userId,
                                                     @Param("startDate") ZonedDateTime startDate,
                                                     @Param("endDate") ZonedDateTime endDate,
                                                     Pageable pageable);

    // Count active sessions for a user
    @Query("SELECT COUNT(ps) FROM ParkingSession ps WHERE ps.user.id = :userId AND ps.status = 'ACTIVE'")
    long countActiveSessionsByUser(@Param("userId") Long userId);

    // Count active sessions for a parking space
    @Query("SELECT COUNT(ps) FROM ParkingSession ps WHERE ps.space.id = :spaceId AND ps.status = 'ACTIVE'")
    long countActiveSessionsBySpace(@Param("spaceId") Long spaceId);

    // Find the most recent session for a user
    @Query("SELECT ps FROM ParkingSession ps WHERE ps.user.id = :userId ORDER BY ps.startedAt DESC")
    List<ParkingSession> findMostRecentSessionsByUser(@Param("userId") Long userId, Pageable pageable);

    // Find sessions that need billing (ACTIVE sessions that have been running for a while)
    @Query("SELECT ps FROM ParkingSession ps WHERE ps.status = 'ACTIVE' AND ps.startedAt < :billingThreshold")
    List<ParkingSession> findSessionsNeedingBilling(@Param("billingThreshold") ZonedDateTime billingThreshold);
    
    // Count sessions by status
    long countByStatus(SessionStatus status);
    
    // Count distinct spaces with sessions by status
    @Query("SELECT COUNT(DISTINCT ps.space.id) FROM ParkingSession ps WHERE ps.status = :status")
    long countDistinctSpaceIdByStatus(@Param("status") SessionStatus status);
    
    // Count sessions by parking lot and status
    @Query("SELECT COUNT(ps) FROM ParkingSession ps WHERE ps.space.parkingLot.id = :lotId AND ps.status = :status")
    long countBySpaceParkingLotIdAndStatus(@Param("lotId") Long lotId, @Param("status") SessionStatus status);
}
