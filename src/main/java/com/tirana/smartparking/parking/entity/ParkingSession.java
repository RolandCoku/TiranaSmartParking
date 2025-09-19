package com.tirana.smartparking.parking.entity;

import com.tirana.smartparking.parking.entity.Enum.UserGroup;
import com.tirana.smartparking.parking.entity.Enum.VehicleType;
import com.tirana.smartparking.user.entity.User;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "parking_space_id")
    private ParkingSpace space;

    @Column(nullable = false)
    private String vehiclePlate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserGroup userGroup;

    @Column(nullable = false)
    private ZonedDateTime startedAt;

    private ZonedDateTime endedAt;

    @Column(nullable = false)
    private Integer billedAmount; // minor units

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.ACTIVE;

    private String sessionReference; // Auto-generated reference like "PSN001234"

    private String paymentMethodId;

    private String notes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public enum SessionStatus {
        ACTIVE,      // Currently parking
        COMPLETED,   // Finished parking
        CANCELLED,   // Canceled by a user
        EXPIRED      // Expired due to timeout
    }
}
