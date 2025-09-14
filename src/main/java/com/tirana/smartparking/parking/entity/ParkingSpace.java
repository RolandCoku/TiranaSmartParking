package com.tirana.smartparking.parking.entity;

import com.tirana.smartparking.parking.sensor.entity.SensorDevice;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
    @JoinColumn(name = "lot_id")
    private ParkingLot parkingLot;

    @Column(columnDefinition = "geography(Point, 4326)")
    private Point location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpaceType spaceType = SpaceType.STANDARD;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpaceStatus spaceStatus = SpaceStatus.AVAILABLE;

    private String label;
    private String description;

    private Instant lastStatusChangedAt = Instant.now();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id")
    private SensorDevice sensorDevice;

    @OneToMany(mappedBy = "parkingSpace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParkingSpaceImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "parkingSpace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

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
