package com.tirana.smartparking.parking.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_lots",
uniqueConstraints = @UniqueConstraint(name = "uk_osm_type_d", columnNames = {"osmType", "osmId"}))

@Getter
@Setter
public class ParkingLot {
    public enum Source {
        OSM, // OpenStreetMap
        MANUAL // Manually added
    }

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Enumerated (EnumType.STRING)
    @Column(nullable = false)
    private Source source = Source.OSM;

    @Column(nullable = false)
    private String osmType;

    @Column(nullable = false)
    private Long osmId;

    private  String name;
    private String description;
    private String address;

    private Boolean active;
    private Boolean publicAccess;
    private Boolean hasChargingStations;
    private Boolean hasDisabledAccess;
    private Boolean hasCctv;

    private Integer capacity;
    private Integer availableSpaces;
    private Instant availabilityUpdatedAt;

    @Column(nullable = false, columnDefinition = "geography(Point, 4326)")
    private Point location;

    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParkingSpace> parkingSpaces = new ArrayList<>();

    @Version
    private Long version;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public void addParkingSpace(ParkingSpace space) {
        if (space != null) {
            space.setParkingLot(this);
            this.parkingSpaces.add(space);
        }
    }

    public void removeParkingSpace(ParkingSpace space) {
        if (space != null) {
            this.parkingSpaces.remove(space);
            space.setParkingLot(null);
        }
    }
}
