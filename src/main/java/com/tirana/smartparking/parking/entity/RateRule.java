package com.tirana.smartparking.parking.entity;

import com.tirana.smartparking.parking.entity.Enum.UserGroup;
import com.tirana.smartparking.parking.entity.Enum.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rate_rules")
public class RateRule {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(optional = false)
    private RatePlan ratePlan;

    // For TIERED/PER_HOUR: minute window from session start (inclusive)
    private Integer startMinute;  // null if not used
    private Integer endMinute;    // null = open-ended

    // Time of day / day-of-week filters (for TIME_OF_DAY / DAY_OF_WEEK)
    private LocalTime startTime;  // inclusive
    private LocalTime endTime;    // exclusive
    private DayOfWeek dayOfWeek;  // nullable = any day

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType; // nullable
    @Enumerated(EnumType.STRING)
    private UserGroup userGroup;     // nullable

    // Prices in minor units (e.g. 100 = 100 ALL)
    private Integer pricePerHour; // for PER_HOUR / TIME_OF_DAY / DAY_OF_WEEK
    private Integer priceFlat;    // for FLAT_PER_ENTRY or tier flat

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}