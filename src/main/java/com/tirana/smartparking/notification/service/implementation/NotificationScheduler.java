package com.tirana.smartparking.notification.service.implementation;

import com.tirana.smartparking.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);

    private final NotificationService notificationService;

    // Process scheduled notifications every minute
    @Scheduled(fixedDelayString = "${notification.scheduler.processScheduled.delayMs:60000}")
    public void processScheduledNotifications() {
        try {
            logger.debug("Processing scheduled notifications");
            notificationService.processScheduledNotifications();
        } catch (Exception e) {
            logger.error("Error processing scheduled notifications", e);
        }
    }

    // Retry failed notifications every 5 minutes
    @Scheduled(fixedDelayString = "${notification.scheduler.retryFailed.delayMs:300000}")
    public void retryFailedNotifications() {
        try {
            logger.debug("Retrying failed notifications");
            notificationService.retryFailedNotifications();
        } catch (Exception e) {
            logger.error("Error retrying failed notifications", e);
        }
    }

    // Cleanup old notifications daily at 2 AM
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldNotifications() {
        try {
            logger.info("Starting cleanup of old notifications");
            notificationService.cleanupOldNotifications(30); // Keep notifications for 30 days
            logger.info("Completed cleanup of old notifications");
        } catch (Exception e) {
            logger.error("Error cleaning up old notifications", e);
        }
    }
}
