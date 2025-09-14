package com.tirana.smartparking.parking.entity;

import com.tirana.smartparking.parking.entity.Enum.RateType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rate_plans")
public class RatePlan {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private RateType type;
    private String currency;           // e.g. "ALL"
    private String timeZone;           // e.g. "Europe/Tirane"
    private Integer graceMinutes;      // e.g. 10
    private Integer incrementMinutes;  // e.g. 15 (bill rounding)
    private Integer dailyCap;          // optional, in minor units (e.g. 60000 = 600 ALL)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

}
