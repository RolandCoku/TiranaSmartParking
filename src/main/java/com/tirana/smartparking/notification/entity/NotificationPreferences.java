package com.tirana.smartparking.notification.entity;

import com.tirana.smartparking.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notification_preferences")
public class NotificationPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "email_enabled")
    private Boolean emailEnabled = true;

    @Column(name = "sms_enabled")
    private Boolean smsEnabled = false;

    @Column(name = "push_enabled")
    private Boolean pushEnabled = true;

    @Column(name = "in_app_enabled")
    private Boolean inAppEnabled = true;

    @Column(name = "preferences", columnDefinition = "JSONB")
    private String preferences; // JSON string for type-specific preferences

    @Column(name = "quiet_hours", columnDefinition = "JSONB")
    private String quietHours; // JSON string for quiet hours configuration

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
