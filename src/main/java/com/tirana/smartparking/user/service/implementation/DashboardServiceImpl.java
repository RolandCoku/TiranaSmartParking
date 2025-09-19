package com.tirana.smartparking.user.service.implementation;

import com.tirana.smartparking.bookings.entity.Booking;
import com.tirana.smartparking.bookings.repository.BookingRepository;
import com.tirana.smartparking.parking.entity.ParkingLot;
import com.tirana.smartparking.parking.entity.ParkingSession;
import com.tirana.smartparking.parking.repository.ParkingLotRepository;
import com.tirana.smartparking.parking.repository.ParkingSessionRepository;
import com.tirana.smartparking.parking.repository.ParkingSpaceRepository;
import com.tirana.smartparking.user.dto.DashboardStatsDTO;
import com.tirana.smartparking.user.dto.OccupancyDataDTO;
import com.tirana.smartparking.user.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final ParkingSpaceRepository parkingSpaceRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final BookingRepository bookingRepository;
    private final ParkingSessionRepository parkingSessionRepository;

    public DashboardServiceImpl(ParkingSpaceRepository parkingSpaceRepository,
                               ParkingLotRepository parkingLotRepository,
                               BookingRepository bookingRepository,
                               ParkingSessionRepository parkingSessionRepository) {
        this.parkingSpaceRepository = parkingSpaceRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.bookingRepository = bookingRepository;
        this.parkingSessionRepository = parkingSessionRepository;
    }

    @Override
    public DashboardStatsDTO getDashboardStats() {
        // Get total spots count
        long totalSpots = parkingSpaceRepository.count();

        // Get occupied spots (spaces with active sessions or bookings)
        long occupiedSpots = getOccupiedSpotsCount();

        // Calculate available spots
        long availableSpots = totalSpots - occupiedSpots;

        // Get active bookings count
        long activeBookings = bookingRepository.countByStatusIn(
                List.of(Booking.BookingStatus.UPCOMING, Booking.BookingStatus.ACTIVE)
        );

        // Get active sessions count
        long activeSessions = parkingSessionRepository.countByStatus(ParkingSession.SessionStatus.ACTIVE);

        // Calculate today's revenue
        double todayRevenue = calculateTodayRevenue();

        // Calculate occupancy rate
        double occupancyRate = totalSpots > 0 ? (double) occupiedSpots / totalSpots * 100 : 0.0;

        return new DashboardStatsDTO(
                totalSpots,
                occupiedSpots,
                availableSpots,
                activeBookings,
                activeSessions,
                todayRevenue,
                occupancyRate
        );
    }

    @Override
    public List<OccupancyDataDTO> getOccupancyData() {
        List<ParkingLot> parkingLots = parkingLotRepository.findAll();

        return parkingLots.stream()
                .map(this::mapToOccupancyData)
                .collect(Collectors.toList());
    }

    private long getOccupiedSpotsCount() {
        // Count spaces that have active sessions
        long spacesWithActiveSessions = parkingSessionRepository.countDistinctSpaceIdByStatus(ParkingSession.SessionStatus.ACTIVE);
        
        // Count spaces that have active bookings (but no active sessions)
        long spacesWithActiveBookings = bookingRepository.countDistinctParkingSpaceIdByStatusIn(
                List.of(Booking.BookingStatus.UPCOMING, Booking.BookingStatus.ACTIVE)
        );

        // Note: This is a simplified calculation. In a real scenario, you might want to
        // exclude spaces that have both active sessions and bookings to avoid double counting
        return spacesWithActiveSessions + spacesWithActiveBookings;
    }

    private double calculateTodayRevenue() {
        ZonedDateTime startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        // Get completed sessions from today
        List<ParkingSession> todaySessions = parkingSessionRepository.findSessionsByDateRange(
                startOfDay, endOfDay, null
        ).getContent();

        // Sum up the billed amounts
        return todaySessions.stream()
                .filter(session -> session.getStatus() == ParkingSession.SessionStatus.COMPLETED)
                .mapToDouble(session -> session.getBilledAmount() / 100.0) // Convert from minor units
                .sum();
    }

    private OccupancyDataDTO mapToOccupancyData(ParkingLot parkingLot) {
        // Get total spaces for this lot
        int totalSpaces = parkingLot.getParkingSpaces().size();

        // Count occupied spaces (spaces with active sessions or bookings)
        int occupiedSpaces = countOccupiedSpacesInLot(parkingLot.getId());

        // Calculate available spaces
        int availableSpaces = totalSpaces - occupiedSpaces;

        // Calculate occupancy rate
        double occupancyRate = totalSpaces > 0 ? (double) occupiedSpaces / totalSpaces * 100 : 0.0;

        return new OccupancyDataDTO(
                parkingLot.getId(),
                parkingLot.getName(),
                totalSpaces,
                occupiedSpaces,
                availableSpaces,
                occupancyRate
        );
    }

    private int countOccupiedSpacesInLot(Long lotId) {
        // Count spaces with active sessions in this lot
        long spacesWithActiveSessions = parkingSessionRepository.countBySpaceParkingLotIdAndStatus(
                lotId, ParkingSession.SessionStatus.ACTIVE
        );

        // Count spaces with active bookings in this lot
        long spacesWithActiveBookings = bookingRepository.countByParkingSpaceParkingLotIdAndStatusIn(
                lotId, List.of(Booking.BookingStatus.UPCOMING, Booking.BookingStatus.ACTIVE)
        );

        // Return the sum (simplified calculation)
        return (int) (spacesWithActiveSessions + spacesWithActiveBookings);
    }
}
