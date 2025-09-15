package com.tirana.smartparking.common.util;

import com.tirana.smartparking.parking.entity.RateRule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class VisitSlicer {
    
    public static List<VisitSlice> sliceByDayAndTime(ZonedDateTime start, ZonedDateTime end, 
                                                     List<RateRule> rules, ZoneId zone) {
        List<VisitSlice> slices = new ArrayList<>();
        
        // Convert to target zone
        ZonedDateTime zoneStart = start.withZoneSameInstant(zone);
        ZonedDateTime zoneEnd = end.withZoneSameInstant(zone);
        
        // Collect all time boundaries from rules
        Set<LocalTime> timeBoundaries = new TreeSet<>();
        Set<LocalDate> dateBoundaries = new TreeSet<>();
        
        for (RateRule rule : rules) {
            if (rule.getStartTime() != null) {
                timeBoundaries.add(rule.getStartTime());
            }
            if (rule.getEndTime() != null) {
                timeBoundaries.add(rule.getEndTime());
            }
        }
        
        // Add day boundaries
        LocalDate currentDate = zoneStart.toLocalDate();
        LocalDate endDate = zoneEnd.toLocalDate();
        while (!currentDate.isAfter(endDate)) {
            dateBoundaries.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        
        // Create slices
        ZonedDateTime sliceStart = zoneStart;
        long sessionStartMinutes = 0;
        
        for (LocalDate date : dateBoundaries) {
            ZonedDateTime dayStart = date.atStartOfDay(zone);
            ZonedDateTime dayEnd = date.plusDays(1).atStartOfDay(zone);
            
            // Clamp to actual session bounds
            ZonedDateTime effectiveDayStart = sliceStart.isAfter(dayStart) ? sliceStart : dayStart;
            ZonedDateTime effectiveDayEnd = zoneEnd.isBefore(dayEnd) ? zoneEnd : dayEnd;
            
            if (effectiveDayStart.isBefore(effectiveDayEnd)) {
                // Split by time boundaries within this day
                List<ZonedDateTime> timePoints = new ArrayList<>();
                timePoints.add(effectiveDayStart);
                
                for (LocalTime time : timeBoundaries) {
                    ZonedDateTime timePoint = date.atTime(time).atZone(zone);
                    if (timePoint.isAfter(effectiveDayStart) && timePoint.isBefore(effectiveDayEnd)) {
                        timePoints.add(timePoint);
                    }
                }
                
                timePoints.add(effectiveDayEnd);
                
                // Create slices for each time segment
                for (int i = 0; i < timePoints.size() - 1; i++) {
                    ZonedDateTime segmentStart = timePoints.get(i);
                    ZonedDateTime segmentEnd = timePoints.get(i + 1);
                    
                    if (segmentStart.isBefore(segmentEnd)) {
                        slices.add(new VisitSlice(segmentStart, segmentEnd, rules, sessionStartMinutes));
                        sessionStartMinutes += java.time.Duration.between(segmentStart, segmentEnd).toMinutes();
                    }
                }
            }
        }
        
        return slices;
    }
}
