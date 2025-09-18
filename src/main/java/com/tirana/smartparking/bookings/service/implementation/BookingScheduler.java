package com.tirana.smartparking.bookings.service.implementation;

import com.tirana.smartparking.bookings.service.BookingService;
import com.tirana.smartparking.maintenance.service.SchedulerInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingScheduler {
  private final BookingService bookingService;
  private final SchedulerInfoService schedulerInfoService;

  // Every 30s: activate sessions whose startAt has arrived
  @Scheduled(fixedDelayString = "${schedulers.activateBookings.delayMs:30000}")
  public void activateDueBookings() {
    long startTime = System.currentTimeMillis();
    String status = "SUCCESS";
    Integer processedCount = 0;
    String errorMessage = null;
    
    try {
      bookingService.activateDueBookings();
      processedCount = 1; // Could be enhanced to return actual count
    } catch (Exception e) {
      status = "ERROR";
      errorMessage = e.getMessage();
    }
    
    long executionTimeMs = System.currentTimeMillis() - startTime;
    schedulerInfoService.recordJobExecution("activateDueBookings", status, processedCount, errorMessage, executionTimeMs);
  }

  // Every 60s: cancel no-shows past grace window
  @Scheduled(fixedDelayString = "${schedulers.noShows.delayMs:60000}")
  public void cancelNoShows() {
    long startTime = System.currentTimeMillis();
    String status = "SUCCESS";
    Integer processedCount = 0;
    String errorMessage = null;
    
    try {
      bookingService.cancelNoShows();
      processedCount = 1; // Could be enhanced to return actual count
    } catch (Exception e) {
      status = "ERROR";
      errorMessage = e.getMessage();
    }
    
    long executionTimeMs = System.currentTimeMillis() - startTime;
    schedulerInfoService.recordJobExecution("cancelNoShows", status, processedCount, errorMessage, executionTimeMs);
  }
}

