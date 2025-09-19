package com.tirana.smartparking.notification.listener;

import com.tirana.smartparking.notification.service.NotificationIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class NotificationEventListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationEventListener.class);

    private final NotificationIntegrationService notificationIntegrationService;

    public NotificationEventListener(NotificationIntegrationService notificationIntegrationService) {
        this.notificationIntegrationService = notificationIntegrationService;
    }

    // ==================== BOOKING EVENTS ====================

    @EventListener
    public void handleBookingCreated(BookingCreatedEvent event) {
        try {
            logger.info("Handling booking created event for user: {}", event.getUserId());
            notificationIntegrationService.sendBookingConfirmation(
                    event.getUserId(),
                    event.getSpaceLabel(),
                    event.getLotName(),
                    event.getStartTime(),
                    event.getEndTime()
            );
        } catch (Exception e) {
            logger.error("Error sending booking confirmation notification", e);
        }
    }

    @EventListener
    public void handleBookingCancelled(BookingCancelledEvent event) {
        try {
            logger.info("Handling booking cancelled event for user: {}", event.getUserId());
            notificationIntegrationService.sendBookingCancellation(
                    event.getUserId(),
                    event.getSpaceLabel(),
                    event.getLotName(),
                    event.getReason()
            );
        } catch (Exception e) {
            logger.error("Error sending booking cancellation notification", e);
        }
    }

    // ==================== SESSION EVENTS ====================

    @EventListener
    public void handleSessionStarted(SessionStartedEvent event) {
        try {
            logger.info("Handling session started event for user: {}", event.getUserId());
            notificationIntegrationService.sendSessionStarted(
                    event.getUserId(),
                    event.getSpaceLabel(),
                    event.getLotName(),
                    event.getEndTime()
            );
        } catch (Exception e) {
            logger.error("Error sending session started notification", e);
        }
    }

    @EventListener
    public void handleSessionEnded(SessionEndedEvent event) {
        try {
            logger.info("Handling session ended event for user: {}", event.getUserId());
            notificationIntegrationService.sendSessionEnded(
                    event.getUserId(),
                    event.getSpaceLabel(),
                    event.getLotName(),
                    event.getTotalCost(),
                    event.getCurrency()
            );
        } catch (Exception e) {
            logger.error("Error sending session ended notification", e);
        }
    }

    // ==================== PAYMENT EVENTS ====================

    @EventListener
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        try {
            logger.info("Handling payment success event for user: {}", event.getUserId());
            notificationIntegrationService.sendPaymentConfirmation(
                    event.getUserId(),
                    event.getAmount(),
                    event.getCurrency(),
                    event.getTransactionId()
            );
        } catch (Exception e) {
            logger.error("Error sending payment confirmation notification", e);
        }
    }

    @EventListener
    public void handlePaymentFailed(PaymentFailedEvent event) {
        try {
            logger.info("Handling payment failed event for user: {}", event.getUserId());
            notificationIntegrationService.sendPaymentFailed(
                    event.getUserId(),
                    event.getAmount(),
                    event.getCurrency(),
                    event.getReason()
            );
        } catch (Exception e) {
            logger.error("Error sending payment failed notification", e);
        }
    }

    // ==================== PARKING EVENTS ====================

    @EventListener
    public void handleSpaceAvailable(SpaceAvailableEvent event) {
        try {
            logger.info("Handling space available event for space: {}", event.getSpaceLabel());
            notificationIntegrationService.sendParkingSpaceAvailable(
                    event.getInterestedUserIds(),
                    event.getSpaceLabel(),
                    event.getLotName(),
                    event.getAvailableUntil()
            );
        } catch (Exception e) {
            logger.error("Error sending space available notification", e);
        }
    }

    @EventListener
    public void handleMaintenanceScheduled(MaintenanceScheduledEvent event) {
        try {
            logger.info("Handling maintenance scheduled event for lot: {}", event.getLotName());
            notificationIntegrationService.sendMaintenanceAlert(
                    event.getAffectedUserIds(),
                    event.getLotName(),
                    event.getMaintenanceDate(),
                    event.getEstimatedDuration(),
                    event.getAffectedSpaces()
            );
        } catch (Exception e) {
            logger.error("Error sending maintenance alert notification", e);
        }
    }

    // ==================== SECURITY EVENTS ====================

    @EventListener
    public void handleSecurityAlert(SecurityAlertEvent event) {
        try {
            logger.info("Handling security alert event for lot: {}", event.getLotName());
            notificationIntegrationService.sendSecurityAlert(
                    event.getAffectedUserIds(),
                    event.getLotName(),
                    event.getAlertMessage()
            );
        } catch (Exception e) {
            logger.error("Error sending security alert notification", e);
        }
    }

    // ==================== ACCOUNT EVENTS ====================

    @EventListener
    public void handleAccountUpdate(AccountUpdateEvent event) {
        try {
            logger.info("Handling account update event for user: {}", event.getUserId());
            notificationIntegrationService.sendAccountNotification(
                    event.getUserId(),
                    event.getMessage(),
                    event.getPriority()
            );
        } catch (Exception e) {
            logger.error("Error sending account notification", e);
        }
    }

    // ==================== EVENT CLASSES ====================

    // Booking Events
    public static class BookingCreatedEvent {
        private final Long userId;
        private final String spaceLabel;
        private final String lotName;
        private final Instant startTime;
        private final Instant endTime;

        public BookingCreatedEvent(Long userId, String spaceLabel, String lotName, Instant startTime, Instant endTime) {
            this.userId = userId;
            this.spaceLabel = spaceLabel;
            this.lotName = lotName;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        // Getters
        public Long getUserId() { return userId; }
        public String getSpaceLabel() { return spaceLabel; }
        public String getLotName() { return lotName; }
        public Instant getStartTime() { return startTime; }
        public Instant getEndTime() { return endTime; }
    }

    public static class BookingCancelledEvent {
        private final Long userId;
        private final String spaceLabel;
        private final String lotName;
        private final String reason;

        public BookingCancelledEvent(Long userId, String spaceLabel, String lotName, String reason) {
            this.userId = userId;
            this.spaceLabel = spaceLabel;
            this.lotName = lotName;
            this.reason = reason;
        }

        // Getters
        public Long getUserId() { return userId; }
        public String getSpaceLabel() { return spaceLabel; }
        public String getLotName() { return lotName; }
        public String getReason() { return reason; }
    }

    // Session Events
    public static class SessionStartedEvent {
        private final Long userId;
        private final String spaceLabel;
        private final String lotName;
        private final Instant endTime;

        public SessionStartedEvent(Long userId, String spaceLabel, String lotName, Instant endTime) {
            this.userId = userId;
            this.spaceLabel = spaceLabel;
            this.lotName = lotName;
            this.endTime = endTime;
        }

        // Getters
        public Long getUserId() { return userId; }
        public String getSpaceLabel() { return spaceLabel; }
        public String getLotName() { return lotName; }
        public Instant getEndTime() { return endTime; }
    }

    public static class SessionEndedEvent {
        private final Long userId;
        private final String spaceLabel;
        private final String lotName;
        private final String totalCost;
        private final String currency;

        public SessionEndedEvent(Long userId, String spaceLabel, String lotName, String totalCost, String currency) {
            this.userId = userId;
            this.spaceLabel = spaceLabel;
            this.lotName = lotName;
            this.totalCost = totalCost;
            this.currency = currency;
        }

        // Getters
        public Long getUserId() { return userId; }
        public String getSpaceLabel() { return spaceLabel; }
        public String getLotName() { return lotName; }
        public String getTotalCost() { return totalCost; }
        public String getCurrency() { return currency; }
    }

    // Payment Events
    public static class PaymentSuccessEvent {
        private final Long userId;
        private final String amount;
        private final String currency;
        private final String transactionId;

        public PaymentSuccessEvent(Long userId, String amount, String currency, String transactionId) {
            this.userId = userId;
            this.amount = amount;
            this.currency = currency;
            this.transactionId = transactionId;
        }

        // Getters
        public Long getUserId() { return userId; }
        public String getAmount() { return amount; }
        public String getCurrency() { return currency; }
        public String getTransactionId() { return transactionId; }
    }

    public static class PaymentFailedEvent {
        private final Long userId;
        private final String amount;
        private final String currency;
        private final String reason;

        public PaymentFailedEvent(Long userId, String amount, String currency, String reason) {
            this.userId = userId;
            this.amount = amount;
            this.currency = currency;
            this.reason = reason;
        }

        // Getters
        public Long getUserId() { return userId; }
        public String getAmount() { return amount; }
        public String getCurrency() { return currency; }
        public String getReason() { return reason; }
    }

    // Parking Events
    public static class SpaceAvailableEvent {
        private final java.util.List<Long> interestedUserIds;
        private final String spaceLabel;
        private final String lotName;
        private final Instant availableUntil;

        public SpaceAvailableEvent(java.util.List<Long> interestedUserIds, String spaceLabel, String lotName, Instant availableUntil) {
            this.interestedUserIds = interestedUserIds;
            this.spaceLabel = spaceLabel;
            this.lotName = lotName;
            this.availableUntil = availableUntil;
        }

        // Getters
        public java.util.List<Long> getInterestedUserIds() { return interestedUserIds; }
        public String getSpaceLabel() { return spaceLabel; }
        public String getLotName() { return lotName; }
        public Instant getAvailableUntil() { return availableUntil; }
    }

    public static class MaintenanceScheduledEvent {
        private final java.util.List<Long> affectedUserIds;
        private final String lotName;
        private final Instant maintenanceDate;
        private final String estimatedDuration;
        private final java.util.List<String> affectedSpaces;

        public MaintenanceScheduledEvent(java.util.List<Long> affectedUserIds, String lotName, Instant maintenanceDate, String estimatedDuration, java.util.List<String> affectedSpaces) {
            this.affectedUserIds = affectedUserIds;
            this.lotName = lotName;
            this.maintenanceDate = maintenanceDate;
            this.estimatedDuration = estimatedDuration;
            this.affectedSpaces = affectedSpaces;
        }

        // Getters
        public java.util.List<Long> getAffectedUserIds() { return affectedUserIds; }
        public String getLotName() { return lotName; }
        public Instant getMaintenanceDate() { return maintenanceDate; }
        public String getEstimatedDuration() { return estimatedDuration; }
        public java.util.List<String> getAffectedSpaces() { return affectedSpaces; }
    }

    // Security Events
    public static class SecurityAlertEvent {
        private final java.util.List<Long> affectedUserIds;
        private final String lotName;
        private final String alertMessage;

        public SecurityAlertEvent(java.util.List<Long> affectedUserIds, String lotName, String alertMessage) {
            this.affectedUserIds = affectedUserIds;
            this.lotName = lotName;
            this.alertMessage = alertMessage;
        }

        // Getters
        public java.util.List<Long> getAffectedUserIds() { return affectedUserIds; }
        public String getLotName() { return lotName; }
        public String getAlertMessage() { return alertMessage; }
    }

    // Account Events
    public static class AccountUpdateEvent {
        private final Long userId;
        private final String message;
        private final com.tirana.smartparking.notification.entity.Notification.NotificationPriority priority;

        public AccountUpdateEvent(Long userId, String message, com.tirana.smartparking.notification.entity.Notification.NotificationPriority priority) {
            this.userId = userId;
            this.message = message;
            this.priority = priority;
        }

        // Getters
        public Long getUserId() { return userId; }
        public String getMessage() { return message; }
        public com.tirana.smartparking.notification.entity.Notification.NotificationPriority getPriority() { return priority; }
    }
}
