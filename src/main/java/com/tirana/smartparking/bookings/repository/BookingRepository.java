package com.tirana.smartparking.bookings.repository;

import com.tirana.smartparking.bookings.entity.Booking;
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
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    Page<Booking> findByUserId(Long userId, Pageable pageable);
    
    Page<Booking> findByUserIdAndStatus(Long userId, Booking.BookingStatus status, Pageable pageable);
    
    // Find current bookings (active and upcoming)
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status IN ('ACTIVE', 'UPCOMING') ORDER BY b.startTime ASC")
    Page<Booking> findCurrentBookingsByUser(@Param("userId") Long userId, Pageable pageable);
    
    // Find all current bookings (active and upcoming) - for admin
    @Query("SELECT b FROM Booking b WHERE b.status IN ('ACTIVE', 'UPCOMING') ORDER BY b.startTime ASC")
    Page<Booking> findAllCurrentBookings(Pageable pageable);
    
    // Find booking history (completed and cancelled)
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status IN ('COMPLETED', 'CANCELLED', 'EXPIRED') ORDER BY b.startTime DESC")
    Page<Booking> findBookingHistoryByUser(@Param("userId") Long userId, Pageable pageable);
    
    // Find all booking history (completed and cancelled) - for admin
    @Query("SELECT b FROM Booking b WHERE b.status IN ('COMPLETED', 'CANCELLED', 'EXPIRED') ORDER BY b.startTime DESC")
    Page<Booking> findAllBookingHistory(Pageable pageable);
    
    // Find booking by reference
    Optional<Booking> findByBookingReference(String bookingReference);
    
    // Check for conflicting bookings on a space
    @Query("SELECT b FROM Booking b WHERE b.parkingSpace.id = :spaceId " +
           "AND b.status IN ('UPCOMING', 'ACTIVE') " +
           "AND ((b.startTime <= :startTime AND b.endTime > :startTime) OR " +
           "     (b.startTime < :endTime AND b.endTime >= :endTime) OR " +
           "     (b.startTime >= :startTime AND b.endTime <= :endTime))")
    List<Booking> findConflictingBookings(@Param("spaceId") Long spaceId, 
                                         @Param("startTime") ZonedDateTime startTime, 
                                         @Param("endTime") ZonedDateTime endTime);
    
    Page<Booking> findByParkingSpaceId(Long parkingSpaceId, Pageable pageable);
    
    @Query("SELECT b FROM Booking b WHERE b.parkingSpace.parkingLot.id = :lotId")
    Page<Booking> findByParkingLotId(@Param("lotId") Long lotId, Pageable pageable);
    
    // Find bookings that need status updates (expired upcoming bookings)
    @Query("SELECT b FROM Booking b WHERE b.status = 'UPCOMING' AND b.startTime < :currentTime")
    List<Booking> findExpiredUpcomingBookings(@Param("currentTime") ZonedDateTime currentTime);
    
    // Find bookings that should be marked as completed (active bookings past end time)
    @Query("SELECT b FROM Booking b WHERE b.status = 'ACTIVE' AND b.endTime < :currentTime")
    List<Booking> findCompletedActiveBookings(@Param("currentTime") ZonedDateTime currentTime);
    
    // Count bookings by status
    long countByStatusIn(List<Booking.BookingStatus> statuses);
    
    // Count distinct parking spaces with bookings by status
    @Query("SELECT COUNT(DISTINCT b.parkingSpace.id) FROM Booking b WHERE b.status IN :statuses")
    long countDistinctParkingSpaceIdByStatusIn(@Param("statuses") List<Booking.BookingStatus> statuses);
    
    // Count bookings by parking lot and status
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.parkingSpace.parkingLot.id = :lotId AND b.status IN :statuses")
    long countByParkingSpaceParkingLotIdAndStatusIn(@Param("lotId") Long lotId, @Param("statuses") List<Booking.BookingStatus> statuses);
    
    // Find bookings that are due to be activated (start time has arrived but still UPCOMING)
    @Query("SELECT b FROM Booking b WHERE b.status = 'UPCOMING' AND b.startTime <= :currentTime")
    List<Booking> findDueBookings(@Param("currentTime") ZonedDateTime currentTime);
    
    // Find bookings that are no-shows (past grace period but still UPCOMING)
    @Query("SELECT b FROM Booking b WHERE b.status = 'UPCOMING' AND b.startTime < :graceCutoffTime")
    List<Booking> findNoShowBookings(@Param("graceCutoffTime") ZonedDateTime graceCutoffTime);
}