package com.tirana.smartparking.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_id", unique = true, nullable = false)
    private String notificationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "JSONB")
    private String data; // JSON string for additional data

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "notification_channels", joinColumns = @JoinColumn(name = "notification_id"))
    @Column(name = "channel")
    private List<NotificationChannel> channels;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationPriority priority = NotificationPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "template_id")
    private String templateId;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NotificationRecipient> recipients;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    public enum NotificationType {
        PARKING_ALERT,
        BOOKING_CONFIRMATION,
        BOOKING_REMINDER,
        BOOKING_CANCELLED,
        SESSION_STARTED,
        SESSION_ENDING,
        SESSION_ENDED,
        PAYMENT_CONFIRMATION,
        PAYMENT_FAILED,
        MAINTENANCE_ALERT,
        SECURITY_ALERT,
        ACCOUNT_NOTIFICATION,
        MARKETING,
        SYSTEM_UPDATE
    }

    public enum NotificationChannel {
        EMAIL,
        SMS,
        PUSH,
        IN_APP
    }

    public enum NotificationPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }

    public enum NotificationStatus {
        PENDING,
        QUEUED,
        SENDING,
        DELIVERED,
        FAILED,
        CANCELLED
    }
}
