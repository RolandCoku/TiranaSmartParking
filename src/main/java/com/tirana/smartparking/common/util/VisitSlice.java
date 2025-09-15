package com.tirana.smartparking.common.util;

import com.tirana.smartparking.parking.entity.Enum.UserGroup;
import com.tirana.smartparking.parking.entity.Enum.VehicleType;
import com.tirana.smartparking.parking.entity.RateRule;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public record VisitSlice(ZonedDateTime start, ZonedDateTime end, List<RateRule> rules, long relativeMinutesFromStart) {

    public long minutes() {
        return Duration.between(start, end).toMinutes();
    }

    public LocalDate getDay() {
        return start.toLocalDate();
    }

    public String label() {
        return String.format("%s %02d:%02d-%02d:%02d",
                start.toLocalDate(),
                start.getHour(), start.getMinute(),
                end.getHour(), end.getMinute());
    }

    public Optional<RateRule> matchedRule(VehicleType vehicleType, UserGroup userGroup) {
        return rules.stream()
                .filter(rule -> matchesVehicleType(rule, vehicleType))
                .filter(rule -> matchesUserGroup(rule, userGroup))
                .filter(this::matchesTimeOfDay)
                .filter(this::matchesDayOfWeek)
                .filter(this::matchesTimeWindow)
                .findFirst();
    }

    private boolean matchesVehicleType(RateRule rule, VehicleType vehicleType) {
        return rule.getVehicleType() == null || rule.getVehicleType() == vehicleType;
    }

    private boolean matchesUserGroup(RateRule rule, UserGroup userGroup) {
        return rule.getUserGroup() == null || rule.getUserGroup() == userGroup;
    }

    private boolean matchesTimeOfDay(RateRule rule) {
        if (rule.getStartTime() == null || rule.getEndTime() == null) {
            return true;
        }

        LocalTime currentTime = start.toLocalTime();
        if (rule.getStartTime().isBefore(rule.getEndTime())) {
            // Same day time range
            return !currentTime.isBefore(rule.getStartTime()) && currentTime.isBefore(rule.getEndTime());
        } else {
            // Overnight time range (e.g., 22:00-06:00)
            return !currentTime.isBefore(rule.getStartTime()) || currentTime.isBefore(rule.getEndTime());
        }
    }

    private boolean matchesDayOfWeek(RateRule rule) {
        return rule.getDayOfWeek() == null || rule.getDayOfWeek() == start.getDayOfWeek();
    }

    private boolean matchesTimeWindow(RateRule rule) {
        if (rule.getStartMinute() == null) {
            return true;
        }

        long sessionMinutes = relativeMinutesFromStart;
        boolean afterStart = sessionMinutes >= rule.getStartMinute();
        boolean beforeEnd = rule.getEndMinute() == null || sessionMinutes < rule.getEndMinute();

        return afterStart && beforeEnd;
    }
}
