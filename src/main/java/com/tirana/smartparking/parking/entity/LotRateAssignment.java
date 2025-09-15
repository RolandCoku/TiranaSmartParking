package com.tirana.smartparking.parking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lot_rate_assignments",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_lot_rate_plan_active",
           columnNames = {"lot_id", "rate_plan_id"}
       ))
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
