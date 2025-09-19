package com.tirana.smartparking.notification.service;

import com.tirana.smartparking.notification.dto.NotificationRequestDTO;
import com.tirana.smartparking.notification.entity.Notification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class NotificationIntegrationService {

    private final NotificationService notificationService;

    public NotificationIntegrationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // ==================== BOOKING INTEGRATION ====================

    public void sendBookingConfirmation(Long userId, String spaceLabel, String lotName, 
                                        Instant startTime, Instant endTime) {
        NotificationRequestDTO request = new NotificationRequestDTO(
                List.of(userId),
                Notification.NotificationType.BOOKING_CONFIRMATION,
                List.of(Notification.NotificationChannel.EMAIL, Notification.NotificationChannel.PUSH, Notification.NotificationChannel.IN_APP),
                "Booking Confirmed - " + spaceLabel,
                String.format("Your booking for space %s at %s has been confirmed. Start time: %s, End time: %s", 
                             spaceLabel, lotName, startTime, endTime),
                "booking_confirmation",
                Map.of(
                        "spaceLabel", spaceLabel,
                        "lotName", lotName,
                        "startTime", startTime.toString(),
                        "endTime", endTime.toString()
                ),
                Notification.NotificationPriority.HIGH,
                null
        );
        
        notificationService.sendNotification(request);
    }

    public void sendBookingReminder(Long userId, String spaceLabel, String lotName, Instant startTime) {
        NotificationRequestDTO request = new NotificationRequestDTO(
                List.of(userId),
                Notification.NotificationType.BOOKING_REMINDER,
                List.of(Notification.NotificationChannel.PUSH, Notification.NotificationChannel.IN_APP),
                "Booking Reminder - " + spaceLabel,
                String.format("Reminder: Your booking for space %s starts in 15 minutes at %s", 
                             spaceLabel, lotName),
                "booking_reminder",
                Map.of(
                        "spaceLabel", spaceLabel,
                        "lotName", lotName,
                        "startTime", startTime.toString()
                ),
                Notification.NotificationPriority.MEDIUM,
                null
        );
        
        notificationService.sendNotification(request);
    }

    public void sendBookingCancellation(Long userId, String spaceLabel, String lotName, String reason) {
        NotificationRequestDTO request = new NotificationRequestDTO(
                List.of(userId),
                Notification.NotificationType.BOOKING_CANCELLED,
                List.of(Notification.NotificationChannel.EMAIL, Notification.NotificationChannel.PUSH, Notification.NotificationChannel.IN_APP),
                "Booking Cancelled - " + spaceLabel,
                String.format("Your booking for space %s at %s has been cancelled. Reason: %s. Refund will be processed within 3-5 business days.", 
                             spaceLabel, lotName, reason),
                "booking_cancelled",
                Map.of(
                        "spaceLabel", spaceLabel,
                        "lotName", lotName,
                        "reason", reason
                ),
                Notification.NotificationPriority.HIGH,
                null
        );
        
        notificationService.sendNotification(request);
    }

    // ==================== SESSION INTEGRATION ====================

    public void sendSessionStarted(Long userId, String spaceLabel, String lotName, Instant endTime) {
        NotificationRequestDTO request = new NotificationRequestDTO(
                List.of(userId),
                Notification.NotificationType.SESSION_STARTED,
                List.of(Notification.NotificationChannel.PUSH, Notification.NotificationChannel.IN_APP),
                "Session Started - " + spaceLabel,
                String.format("Your parking session has started at space %s in %s. Session will end at %s.", 
                             spaceLabel, lotName, endTime),
                "session_started",
                Map.of(
                        "spaceLabel", spaceLabel,
                        "lotName", lotName,
                        "endTime", endTime.toString()
                ),
                Notification.NotificationPriority.MEDIUM,
                null
        );
        
        notificationService.sendNotification(request);
    }

    public void sendSessionEnding(Long userId, String spaceLabel, String lotName, Instant endTime) {
        NotificationRequestDTO request = new NotificationRequestDTO(
                List.of(userId),
                Notification.NotificationType.SESSION_ENDING,
                List.of(Notification.NotificationChannel.PUSH, Notification.NotificationChannel.IN_APP),
                "Session Ending Soon - " + spaceLabel,
                String.format("Your parking session at space %s will end in 10 minutes. Please prepare to vacate the space.", 
                             spaceLabel),
                "session_ending",
                Map.of(
                        "spaceLabel", spaceLabel,
                        "lotName", lotName,
                        "endTime", endTime.toString()
                ),
                Notification.NotificationPriority.HIGH,
                null
        );
        
        notificationService.sendNotification(request);
    }

    public void sendSessionEnded(Long userId, String spaceLabel, String lotName, 
                                String totalCost, String currency) {
        NotificationRequestDTO request = new NotificationRequestDTO(
                List.of(userId),
                Notification.NotificationType.SESSION_ENDED,
                List.of(Notification.NotificationChannel.EMAIL, Notification.NotificationChannel.PUSH, Notification.NotificationChannel.IN_APP),
                "Session Ended - " + spaceLabel,
                String.format("Your parking session at space %s has ended. Total cost: %s %s", 
                             spaceLabel, totalCost, currency),
                "session_ended",
                Map.of(
                        "spaceLabel", spaceLabel,
                        "lotName", lotName,
                        "totalCost", totalCost,
                        "currency", currency
                ),
                Notification.NotificationPriority.MEDIUM,
                null
        );
        
        notificationService.sendNotification(request);
    }

    // ==================== PAYMENT INTEGRATION ====================

    public void sendPaymentConfirmation(Long userId, String amount, String currency, String transactionId) {
        NotificationRequestDTO request = new NotificationRequestDTO(
                List.of(userId),
                Notification.NotificationType.PAYMENT_CONFIRMATION,
                List.of(Notification.NotificationChannel.EMAIL, Notification.NotificationChannel.PUSH, Notification.NotificationChannel.IN_APP),
                "Payment Confirmed",
                String.format("Your payment of %s %s has been processed successfully. Transaction ID: %s", 
                             amount, currency, transactionId),
                "payment_confirmation",
                Map.of(
                        "amount", amount,
                        "currency", currency,
                        "transactionId", transactionId
                ),
                Notification.NotificationPriority.HIGH,
                null
        );
        
        notificationService.sendNotification(request);
    }

    public void sendPaymentFailed(Long userId, String amount, String currency, String reason) {
        NotificationRequestDTO request = new NotificationRequestDTO(
                List.of(userId),
                Notification.NotificationType.PAYMENT_FAILED,
                List.of(Notification.NotificationChannel.EMAIL, Notification.NotificationChannel.PUSH, Notification.NotificationChannel.IN_APP),
                "Payment Failed",
                String.format("Your payment of %s %s could not be processed. Reason: %s. Please update your payment method and try again.", 
                             amount, currency, reason),
                "payment_failed",
                Map.of(
                        "amount", amount,
                        "currency", currency,
                        "reason", reason
                ),
                Notification.NotificationPriority.HIGH,
                null
        );
        
        notificationService.sendNotification(request);
    }

    // ==================== PARKING INTEGRATION ====================

    public void sendParkingSpaceAvailable(List<Long> userIds, String spaceLabel, String lotName, Instant availableUntil) {
        NotificationRequestDTO request = new NotificationRequestDTO(
                userIds,
                Notification.NotificationType.PARKING_ALERT,
                List.of(Notification.NotificationChannel.PUSH, Notification.NotificationChannel.IN_APP),
                "Space Available - " + spaceLabel,
                String.format("Space %s is now available at %s. Available until: %s", 
                             spaceLabel, lotName, availableUntil),
                "parking_alert",
                Map.of(
                        "spaceLabel", spaceLabel,
                        "lotName", lotName,
                        "availableUntil", availableUntil.toString()
                ),
                Notification.NotificationPriority.HIGH,
                null
        );
        
        notificationService.sendNotification(request);
    }

    public void sendMaintenanceAlert(List<Long> userIds, String lotName, Instant maintenanceDate, 
                                    String estimatedDuration, List<String> affectedSpaces) {
        NotificationRequestDTO request = new NotificationRequestDTO(
                userIds,
                Notification.NotificationType.MAINTENANCE_ALERT,
                List.of(Notification.NotificationChannel.EMAIL, Notification.NotificationChannel.IN_APP),
                "Maintenance Scheduled - " + lotName,
                String.format("Maintenance is scheduled for %s on %s. Duration: %s. Affected spaces: %s", 
                             lotName, maintenanceDate, estimatedDuration, String.join(", ", affectedSpaces)),
                "maintenance_alert",
                Map.of(
                        "lotName", lotName,
                        "maintenanceDate", maintenanceDate.toString(),
                        "estimatedDuration", estimatedDuration,
                        "affectedSpaces", affectedSpaces
                ),
                Notification.NotificationPriority.MEDIUM,
                null
        );
        
        notificationService.sendNotification(request);
    }

    // ==================== SECURITY INTEGRATION ====================

    public void sendSecurityAlert(List<Long> userIds, String lotName, String alertMessage) {
        NotificationRequestDTO request = new NotificationRequestDTO(
                userIds,
                Notification.NotificationType.SECURITY_ALERT,
                List.of(Notification.NotificationChannel.EMAIL, Notification.NotificationChannel.SMS, 
                        Notification.NotificationChannel.PUSH, Notification.NotificationChannel.IN_APP),
                "Security Alert - " + lotName,
                String.format("Security alert at %s: %s. Please contact security if you have any concerns.", 
                             lotName, alertMessage),
                "security_alert",
                Map.of(
                        "lotName", lotName,
                        "alertMessage", alertMessage
                ),
                Notification.NotificationPriority.URGENT,
                null
        );
        
        notificationService.sendNotification(request);
    }

    // ==================== ACCOUNT INTEGRATION ====================

    public void sendAccountNotification(Long userId, String message, Notification.NotificationPriority priority) {
        NotificationRequestDTO request = new NotificationRequestDTO(
                List.of(userId),
                Notification.NotificationType.ACCOUNT_NOTIFICATION,
                List.of(Notification.NotificationChannel.EMAIL, Notification.NotificationChannel.IN_APP),
                "Account Update",
                message,
                "account_notification",
                Map.of("message", message),
                priority,
                null
        );
        
        notificationService.sendNotification(request);
    }

    // ==================== SYSTEM INTEGRATION ====================

    public void sendSystemUpdate(List<Long> userIds, String updateMessage, Instant scheduledAt) {
        NotificationRequestDTO request = new NotificationRequestDTO(
                userIds,
                Notification.NotificationType.SYSTEM_UPDATE,
                List.of(Notification.NotificationChannel.EMAIL, Notification.NotificationChannel.IN_APP),
                "System Update",
                updateMessage,
                "system_update",
                Map.of("updateMessage", updateMessage),
                Notification.NotificationPriority.MEDIUM,
                scheduledAt
        );
        
        notificationService.sendNotification(request);
    }
}
