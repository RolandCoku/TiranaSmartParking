package com.tirana.smartparking.parking.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.ZonedDateTime;

@Entity
@Table(name = "lot_rate_assignments")
public class LotRateAssignment {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(optional = false)
    private ParkingLot lot;
    @ManyToOne(optional = false)
    private RatePlan ratePlan;
    private Integer priority = 0;
    private ZonedDateTime effectiveFrom;
    private ZonedDateTime effectiveTo;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
