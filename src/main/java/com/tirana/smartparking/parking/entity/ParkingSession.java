package com.tirana.smartparking.parking.entity;

import com.tirana.smartparking.parking.entity.Enum.UserGroup;
import com.tirana.smartparking.parking.entity.Enum.VehicleType;
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
@Table(name = "parking_sessions")
public class ParkingSession {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private ParkingSpace space;
    private String vehiclePlate;
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;
    @Enumerated(EnumType.STRING)
    private UserGroup userGroup;
    private ZonedDateTime startedAt;
    private ZonedDateTime endedAt;

    private Integer billedAmount; // minor units
    private String currency;
    private String status; // STARTED, QUOTED, BILLED

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
