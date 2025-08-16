package com.tirana.smartparking.parking.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "parking_spaces",
        indexes = @Index(name = "idx_space_lot", columnList = "lot_id"))
@Getter
@Setter
public class ParkingSpace {
    public enum SpaceType {
        STANDARD,
        DISABLED,
        EV,
        MOTORCYCLE
    }

    public enum SpaceStatus {
        AVAILABLE,
        OCCUPIED,
        RESERVED,
        OUT_OF_SERVICE
    }

    @Id
    @GeneratedValue
    @Column(updatable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    private ParkingLot parkingLot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpaceType spaceType = SpaceType.STANDARD;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpaceStatus spaceStatus = SpaceStatus.AVAILABLE;

    private String label;
    private String description;

    private Instant lastStatusChangedAt = Instant.now();

    @Version
    private Long version;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    private void onUpdate() {
        updatedAt = Instant.now();
    }

}
