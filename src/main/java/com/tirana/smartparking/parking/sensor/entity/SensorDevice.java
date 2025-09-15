package com.tirana.smartparking.parking.sensor.entity;

import com.tirana.smartparking.parking.entity.ParkingLot;
import com.tirana.smartparking.parking.entity.ParkingSpace;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "sensor_devices")
@Getter
@Setter
public class SensorDevice {

    public enum SensorType {
        PER_SPACE,
        GATE_COUNTER
    }

    public enum SensorStatus {
        ACTIVE,
        INACTIVE,
        MAINTENANCE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String deviceId;

    @Column(nullable = false, unique = true)
    private String apiKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SensorType sensorType = SensorType.PER_SPACE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_lot_id")
    private ParkingLot parkingLot;

    @OneToOne(mappedBy = "sensorDevice")
    private ParkingSpace parkingSpace;

    private String description;

    private Instant LastSeenAt;
    private String firmware;
    private Integer batteryLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SensorStatus status = SensorStatus.ACTIVE;

    @Version
    private Long version;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

}
