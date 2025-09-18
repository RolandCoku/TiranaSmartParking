package com.tirana.smartparking.bookings.service.implementation;

import com.tirana.smartparking.bookings.dto.BookingDTO;
import com.tirana.smartparking.bookings.dto.BookingQuoteDTO;
import com.tirana.smartparking.bookings.dto.BookingRegistrationDTO;
import com.tirana.smartparking.bookings.dto.BookingUpdateDTO;
import com.tirana.smartparking.bookings.entity.Booking;
import com.tirana.smartparking.bookings.repository.BookingRepository;
import com.tirana.smartparking.bookings.service.BookingService;
import com.tirana.smartparking.common.dto.Money;
import com.tirana.smartparking.common.dto.PaginatedResponse;
import com.tirana.smartparking.common.exception.ResourceConflictException;
import com.tirana.smartparking.common.exception.ResourceNotFoundException;
import com.tirana.smartparking.common.service.SecurityContextService;
import com.tirana.smartparking.common.util.PaginationUtil;
import com.tirana.smartparking.parking.dto.ParkingSessionStartDTO;
import com.tirana.smartparking.parking.entity.ParkingSpace;
import com.tirana.smartparking.parking.repository.ParkingSpaceRepository;
import com.tirana.smartparking.parking.service.ParkingSessionService;
import com.tirana.smartparking.parking.service.PricingService;
import com.tirana.smartparking.user.entity.User;
import com.tirana.smartparking.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {
    
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final PricingService pricingService;
    private final SecurityContextService securityContextService;
    private final ParkingSessionService parkingSessionService;
    
    public BookingServiceImpl(BookingRepository bookingRepository,
                             UserRepository userRepository,
                             ParkingSpaceRepository parkingSpaceRepository,
                             PricingService pricingService,
                             SecurityContextService securityContextService,
                             ParkingSessionService parkingSessionService) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.parkingSpaceRepository = parkingSpaceRepository;
        this.pricingService = pricingService;
        this.securityContextService = securityContextService;
        this.parkingSessionService = parkingSessionService;
    }
    
    @Override
    public BookingDTO createBooking(BookingRegistrationDTO registrationDTO) {
        // Get current user ID from SecurityContext
        Long userId = securityContextService.getCurrentUserId();
        
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Validate parking space exists
        ParkingSpace parkingSpace = parkingSpaceRepository.findById(registrationDTO.parkingSpaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Parking space not found with id: " + registrationDTO.parkingSpaceId()));
        
        // Check for conflicts
        if (!isSpaceAvailable(registrationDTO.parkingSpaceId(), registrationDTO.startTime(), registrationDTO.endTime())) {
            throw new ResourceConflictException("Parking space is not available for the selected time period");
        }
        
        // Get pricing quote
        Money quote = pricingService.quote(
                parkingSpace.getParkingLot() != null ? parkingSpace.getParkingLot().getId() : null,
                registrationDTO.parkingSpaceId(),
                registrationDTO.vehicleType(),
                registrationDTO.userGroup(),
                registrationDTO.startTime(),
                registrationDTO.endTime()
        );
        
        // Create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setParkingSpace(parkingSpace);
        booking.setVehiclePlate(registrationDTO.vehiclePlate());
        booking.setVehicleType(registrationDTO.vehicleType());
        booking.setUserGroup(registrationDTO.userGroup());
        booking.setStartTime(registrationDTO.startTime());
        booking.setEndTime(registrationDTO.endTime());
        booking.setTotalPrice(quote.getAmount());
        booking.setCurrency(quote.getCurrency());
        booking.setStatus(Booking.BookingStatus.UPCOMING);
        booking.setBookingReference(generateBookingReference());
        booking.setPaymentMethodId(registrationDTO.paymentMethodId());
        booking.setNotes(registrationDTO.notes());
        
        Booking savedBooking = bookingRepository.save(booking);
        return mapToBookingDTO(savedBooking);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BookingDTO getBookingById(Long id) {
        Long userId = securityContextService.getCurrentUserId();
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        
        // Allow admin access OR user accessing their own booking
        if (securityContextService.isCurrentUserAdmin() && !booking.getUser().getId().equals(userId)) {
            throw new ResourceConflictException("Access denied: You can only view your own bookings");
        }
        
        return mapToBookingDTO(booking);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BookingDTO getBookingByReference(String bookingReference) {
        Long userId = securityContextService.getCurrentUserId();
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with reference: " + bookingReference));
        
        // Allow admin access OR user accessing their own booking
        if (securityContextService.isCurrentUserAdmin() && !booking.getUser().getId().equals(userId)) {
            throw new ResourceConflictException("Access denied: You can only view your own bookings");
        }
        
        return mapToBookingDTO(booking);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<BookingDTO> getUserBookings(int page, int size, String sortBy, String sortDir) {
        Long userId = securityContextService.getCurrentUserId();
        
        // If user is admin, return all bookings instead of just user's bookings
        if (securityContextService.isCurrentUserAdmin()) {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);
            Page<Booking> bookings = bookingRepository.findAll(pageable);
            return PaginationUtil.toPaginatedResponse(bookings.map(this::mapToBookingDTO));
        }
        
        return getBookingDTOPaginatedResponse(page, size, sortBy, sortDir, userId);
    }

    private PaginatedResponse<BookingDTO> getBookingDTOPaginatedResponse(int page, int size, String sortBy, String sortDir, Long userId) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);
        Page<Booking> bookings = bookingRepository.findByUserId(userId, pageable);
        return PaginationUtil.toPaginatedResponse(bookings.map(this::mapToBookingDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<BookingDTO> getCurrentBookings(int page, int size) {
        Long userId = securityContextService.getCurrentUserId();
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "startTime"));
        
        // If user is admin, return all current bookings instead of just user's bookings
        if (securityContextService.isCurrentUserAdmin()) {
            Page<Booking> bookings = bookingRepository.findAllCurrentBookings(pageable);
            return PaginationUtil.toPaginatedResponse(bookings.map(this::mapToBookingDTO));
        }
        
        Page<Booking> bookings = bookingRepository.findCurrentBookingsByUser(userId, pageable);
        return PaginationUtil.toPaginatedResponse(bookings.map(this::mapToBookingDTO));
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<BookingDTO> getBookingHistory(int page, int size) {
        Long userId = securityContextService.getCurrentUserId();
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startTime"));
        
        // If user is admin, return all booking history instead of just user's bookings
        if (securityContextService.isCurrentUserAdmin()) {
            Page<Booking> bookings = bookingRepository.findAllBookingHistory(pageable);
            return PaginationUtil.toPaginatedResponse(bookings.map(this::mapToBookingDTO));
        }
        
        Page<Booking> bookings = bookingRepository.findBookingHistoryByUser(userId, pageable);
        return PaginationUtil.toPaginatedResponse(bookings.map(this::mapToBookingDTO));
    }
    
    @Override
    public BookingDTO updateBooking(Long id, BookingUpdateDTO updateDTO) {
        Long userId = securityContextService.getCurrentUserId();
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        
        // Ensure user can only update their own bookings (unless they're admin)
        if (!securityContextService.isCurrentUserAdmin() && !booking.getUser().getId().equals(userId)) {
            throw new ResourceConflictException("Access denied: You can only update your own bookings");
        }
        
        // Only allow updates for upcoming bookings
        if (booking.getStatus() != Booking.BookingStatus.UPCOMING) {
            throw new ResourceConflictException("Only upcoming bookings can be updated");
        }
        
        // Update fields if provided
        if (updateDTO.vehiclePlate() != null) {
            booking.setVehiclePlate(updateDTO.vehiclePlate());
        }
        if (updateDTO.vehicleType() != null) {
            booking.setVehicleType(updateDTO.vehicleType());
        }
        if (updateDTO.userGroup() != null) {
            booking.setUserGroup(updateDTO.userGroup());
        }
        if (updateDTO.startTime() != null) {
            booking.setStartTime(updateDTO.startTime());
        }
        if (updateDTO.endTime() != null) {
            booking.setEndTime(updateDTO.endTime());
        }
        if (updateDTO.paymentMethodId() != null) {
            booking.setPaymentMethodId(updateDTO.paymentMethodId());
        }
        if (updateDTO.notes() != null) {
            booking.setNotes(updateDTO.notes());
        }
        
        // Recalculate price if time or user group changed
        if (updateDTO.startTime() != null || updateDTO.endTime() != null || updateDTO.userGroup() != null) {
            Money quote = pricingService.quote(
                    booking.getParkingSpace().getParkingLot() != null ? booking.getParkingSpace().getParkingLot().getId() : null,
                    booking.getParkingSpace().getId(),
                    booking.getVehicleType(),
                    booking.getUserGroup(),
                    booking.getStartTime(),
                    booking.getEndTime()
            );
            booking.setTotalPrice(quote.getAmount());
            booking.setCurrency(quote.getCurrency());
        }
        
        Booking savedBooking = bookingRepository.save(booking);
        return mapToBookingDTO(savedBooking);
    }
    
    @Override
    public void deleteBooking(Long id) {
        Long userId = securityContextService.getCurrentUserId();
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        
        // Ensure user can only delete their own bookings (unless they're admin)
        if (!securityContextService.isCurrentUserAdmin() && !booking.getUser().getId().equals(userId)) {
            throw new ResourceConflictException("Access denied: You can only delete your own bookings");
        }
        
        // Only allow deletion of upcoming bookings
        if (booking.getStatus() != Booking.BookingStatus.UPCOMING) {
            throw new ResourceConflictException("Only upcoming bookings can be deleted");
        }
        
        bookingRepository.deleteById(id);
    }
    
    @Override
    public BookingDTO cancelBooking(Long id) {
        Long userId = securityContextService.getCurrentUserId();
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        
        // Ensure user can only cancel their own bookings (unless they're admin)
        if (!securityContextService.isCurrentUserAdmin() && !booking.getUser().getId().equals(userId)) {
            throw new ResourceConflictException("Access denied: You can only cancel your own bookings");
        }
        
        // Only allow cancellation of upcoming or active bookings
        if (booking.getStatus() != Booking.BookingStatus.UPCOMING && booking.getStatus() != Booking.BookingStatus.ACTIVE) {
            throw new ResourceConflictException("Only upcoming or active bookings can be cancelled");
        }
        
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        Booking savedBooking = bookingRepository.save(booking);
        return mapToBookingDTO(savedBooking);
    }
    
    @Override
    public BookingDTO startBooking(Long id) {
        Long userId = securityContextService.getCurrentUserId();
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        
        // Ensure user can only start their own bookings (unless they're admin)
        if (!securityContextService.isCurrentUserAdmin() && !booking.getUser().getId().equals(userId)) {
            throw new ResourceConflictException("Access denied: You can only start your own bookings");
        }
        
        // Only allow starting upcoming bookings
        if (booking.getStatus() != Booking.BookingStatus.UPCOMING) {
            throw new ResourceConflictException("Only upcoming bookings can be started");
        }
        
        // Check if it's time to start (admins can start bookings before scheduled time)
        if (!securityContextService.isCurrentUserAdmin() && ZonedDateTime.now().isBefore(booking.getStartTime())) {
            throw new ResourceConflictException("Booking cannot be started before the scheduled start time");
        }
        
        // Update booking status to ACTIVE
        booking.setStatus(Booking.BookingStatus.ACTIVE);
        Booking savedBooking = bookingRepository.save(booking);
        
        // Automatically create a parking session for this booking
        try {
            ParkingSessionStartDTO sessionStartDTO = new ParkingSessionStartDTO(
                    booking.getParkingSpace().getId(),
                    booking.getVehiclePlate(),
                    booking.getVehicleType(),
                    booking.getUserGroup(),
                    booking.getPaymentMethodId(),
                    "Auto-created from booking " + booking.getBookingReference()
            );
            
            parkingSessionService.startSession(sessionStartDTO);
        } catch (Exception e) {
            // If session creation fails, log the error but don't fail the booking start.
            // The booking is already marked as ACTIVE, so we continue
            // In a production environment, you might want to implement retry logic or alerting
            System.err.println("Failed to create parking session for booking " + booking.getBookingReference() + ": " + e.getMessage());
        }
        
        return mapToBookingDTO(savedBooking);
    }
    
    @Override
    public BookingDTO completeBooking(Long id) {
        Long userId = securityContextService.getCurrentUserId();
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        
        // Ensure user can only complete their own bookings (unless they're admin)
        if (!securityContextService.isCurrentUserAdmin() && !booking.getUser().getId().equals(userId)) {
            throw new ResourceConflictException("Access denied: You can only complete your own bookings");
        }
        
        // Only allow completing active bookings
        if (booking.getStatus() != Booking.BookingStatus.ACTIVE) {
            throw new ResourceConflictException("Only active bookings can be completed");
        }
        
        booking.setStatus(Booking.BookingStatus.COMPLETED);
        Booking savedBooking = bookingRepository.save(booking);
        return mapToBookingDTO(savedBooking);
    }
    
    @Override
    public BookingDTO extendBooking(Long id, ZonedDateTime newEndTime) {
        Long userId = securityContextService.getCurrentUserId();
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        
        // Ensure user can only extend their own bookings (unless they're admin)
        if (!securityContextService.isCurrentUserAdmin() && !booking.getUser().getId().equals(userId)) {
            throw new ResourceConflictException("Access denied: You can only extend your own bookings");
        }
        
        // Only allow extending active bookings
        if (booking.getStatus() != Booking.BookingStatus.ACTIVE) {
            throw new ResourceConflictException("Only active bookings can be extended");
        }
        
        // Check for conflicts with the new end time
        if (!isSpaceAvailable(booking.getParkingSpace().getId(), booking.getStartTime(), newEndTime)) {
            throw new ResourceConflictException("Parking space is not available for the extended time period");
        }
        
        booking.setEndTime(newEndTime);
        
        // Recalculate price
        Money quote = pricingService.quote(
                booking.getParkingSpace().getParkingLot() != null ? booking.getParkingSpace().getParkingLot().getId() : null,
                booking.getParkingSpace().getId(),
                booking.getVehicleType(),
                booking.getUserGroup(),
                booking.getStartTime(),
                booking.getEndTime()
        );
        booking.setTotalPrice(quote.getAmount());
        booking.setCurrency(quote.getCurrency());
        
        Booking savedBooking = bookingRepository.save(booking);
        return mapToBookingDTO(savedBooking);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Money getBookingQuote(BookingQuoteDTO quoteDTO) {
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
        List<Booking> conflicts = bookingRepository.findConflictingBookings(spaceId, startTime, endTime);
        return conflicts.isEmpty();
    }
    
    // Admin operations with explicit user ID
    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<BookingDTO> getAllBookings(int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);
        Page<Booking> bookings = bookingRepository.findAll(pageable);
        return PaginationUtil.toPaginatedResponse(bookings.map(this::mapToBookingDTO));
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<BookingDTO> getBookingsBySpace(Long spaceId, int page, int size) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startTime"));
        Page<Booking> bookings = bookingRepository.findByParkingSpaceId(spaceId, pageable);
        return PaginationUtil.toPaginatedResponse(bookings.map(this::mapToBookingDTO));
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<BookingDTO> getBookingsByLot(Long lotId, int page, int size) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startTime"));
        Page<Booking> bookings = bookingRepository.findByParkingLotId(lotId, pageable);
        return PaginationUtil.toPaginatedResponse(bookings.map(this::mapToBookingDTO));
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<BookingDTO> getUserBookings(Long userId, int page, int size, String sortBy, String sortDir) {
        return getBookingDTOPaginatedResponse(page, size, sortBy, sortDir, userId);
    }
    
    @Override
    public void updateExpiredBookings() {
        ZonedDateTime now = ZonedDateTime.now();
        List<Booking> expiredBookings = bookingRepository.findExpiredUpcomingBookings(now);
        
        for (Booking booking : expiredBookings) {
            booking.setStatus(Booking.BookingStatus.EXPIRED);
            bookingRepository.save(booking);
        }
    }
    
    @Override
    public void updateCompletedBookings() {
        ZonedDateTime now = ZonedDateTime.now();
        List<Booking> completedBookings = bookingRepository.findCompletedActiveBookings(now);
        
        for (Booking booking : completedBookings) {
            booking.setStatus(Booking.BookingStatus.COMPLETED);
            bookingRepository.save(booking);
        }
    }
    
    private String generateBookingReference() {
        String prefix = "PCK";
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + randomPart;
    }
    
    @Override
    public void activateDueBookings() {
        ZonedDateTime now = ZonedDateTime.now();
        List<Booking> dueBookings = bookingRepository.findDueBookings(now);
        
        for (Booking booking : dueBookings) {
            try {
                // Update booking status to ACTIVE
                booking.setStatus(Booking.BookingStatus.ACTIVE);
                bookingRepository.save(booking);
                
                // Automatically create a parking session for this booking
                ParkingSessionStartDTO sessionStartDTO = new ParkingSessionStartDTO(
                        booking.getParkingSpace().getId(),
                        booking.getVehiclePlate(),
                        booking.getVehicleType(),
                        booking.getUserGroup(),
                        booking.getPaymentMethodId(),
                        "Auto-created from booking " + booking.getBookingReference()
                );
                
                parkingSessionService.startSession(sessionStartDTO);
                
            } catch (Exception e) {
                // If session creation fails, log the error but don't fail the booking activation.
                // The booking is already marked as ACTIVE, so we continue
                System.err.println("Failed to create parking session for booking " + booking.getBookingReference() + ": " + e.getMessage());
            }
        }
    }
    
    @Override
    public void cancelNoShows() {
        ZonedDateTime now = ZonedDateTime.now();
        
        // Get all upcoming bookings that are past their start time
        List<Booking> upcomingBookings = bookingRepository.findDueBookings(now);
        
        for (Booking booking : upcomingBookings) {
            try {
                // Get grace minutes from the rate plan
                Long lotId = booking.getParkingSpace().getParkingLot() != null ? 
                    booking.getParkingSpace().getParkingLot().getId() : null;
                Long spaceId = booking.getParkingSpace().getId();
                
                Integer graceMinutes = pricingService.getGraceMinutes(lotId, spaceId, booking.getStartTime());
                
                // If no grace period is set, use default of 15 minutes
                if (graceMinutes == null) {
                    graceMinutes = 15;
                }
                
                // Calculate grace cutoff time
                ZonedDateTime graceCutoffTime = booking.getStartTime().plusMinutes(graceMinutes);
                
                // If current time is past the grace period, cancel the booking
                if (now.isAfter(graceCutoffTime)) {
                    booking.setStatus(Booking.BookingStatus.CANCELLED);
                    bookingRepository.save(booking);
                }
                
            } catch (Exception e) {
                // If there's an error getting grace minutes, use default grace period
                ZonedDateTime graceCutoffTime = booking.getStartTime().plusMinutes(15);
                if (now.isAfter(graceCutoffTime)) {
                    booking.setStatus(Booking.BookingStatus.CANCELLED);
                    bookingRepository.save(booking);
                }
                System.err.println("Error processing no-show for booking " + booking.getBookingReference() + ": " + e.getMessage());
            }
        }
    }
    
    private BookingDTO mapToBookingDTO(Booking booking) {
        return new BookingDTO(
                booking.getId(),
                booking.getUser().getId(),
                booking.getUser().getEmail(),
                booking.getParkingSpace().getId(),
                booking.getParkingSpace().getLabel(),
                booking.getParkingSpace().getParkingLot() != null ? booking.getParkingSpace().getParkingLot().getId() : null,
                booking.getParkingSpace().getParkingLot() != null ? booking.getParkingSpace().getParkingLot().getName() : null,
                booking.getParkingSpace().getParkingLot() != null ? booking.getParkingSpace().getParkingLot().getAddress() : null,
                booking.getVehiclePlate(),
                booking.getVehicleType(),
                booking.getUserGroup(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getTotalPrice(),
                booking.getCurrency(),
                booking.getStatus(),
                booking.getBookingReference(),
                booking.getPaymentMethodId(),
                booking.getNotes(),
                booking.getCreatedAt(),
                booking.getUpdatedAt()
        );
    }
}