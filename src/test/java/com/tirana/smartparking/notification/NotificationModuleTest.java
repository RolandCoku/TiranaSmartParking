package com.tirana.smartparking.notification;

import com.tirana.smartparking.notification.entity.Notification;
import com.tirana.smartparking.notification.entity.NotificationRecipient;
import com.tirana.smartparking.notification.entity.NotificationTemplate;
import com.tirana.smartparking.notification.entity.NotificationPreferences;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NotificationModuleTest {

    @Test
    public void testNotificationEntity() {
        Notification notification = new Notification();
        notification.setNotificationId("test-123");
        notification.setType(Notification.NotificationType.BOOKING_CONFIRMATION);
        notification.setTitle("Test Notification");
        notification.setMessage("This is a test notification");
        notification.setPriority(Notification.NotificationPriority.HIGH);
        notification.setStatus(Notification.NotificationStatus.PENDING);

        assertEquals("test-123", notification.getNotificationId());
        assertEquals(Notification.NotificationType.BOOKING_CONFIRMATION, notification.getType());
        assertEquals("Test Notification", notification.getTitle());
        assertEquals("This is a test notification", notification.getMessage());
        assertEquals(Notification.NotificationPriority.HIGH, notification.getPriority());
        assertEquals(Notification.NotificationStatus.PENDING, notification.getStatus());
    }

    @Test
    public void testNotificationRecipientEntity() {
        NotificationRecipient recipient = new NotificationRecipient();
        recipient.setChannel(Notification.NotificationChannel.EMAIL);
        recipient.setStatus(NotificationRecipient.DeliveryStatus.PENDING);
        recipient.setRetryCount(0);

        assertEquals(Notification.NotificationChannel.EMAIL, recipient.getChannel());
        assertEquals(NotificationRecipient.DeliveryStatus.PENDING, recipient.getStatus());
        assertEquals(0, recipient.getRetryCount());
    }

    @Test
    public void testNotificationTemplateEntity() {
        NotificationTemplate template = new NotificationTemplate();
        template.setTemplateId("test-template");
        template.setName("Test Template");
        template.setType(Notification.NotificationType.BOOKING_CONFIRMATION);
        template.setBodyTemplate("Hello {{name}}, your booking is confirmed!");
        template.setIsActive(true);

        assertEquals("test-template", template.getTemplateId());
        assertEquals("Test Template", template.getName());
        assertEquals(Notification.NotificationType.BOOKING_CONFIRMATION, template.getType());
        assertEquals("Hello {{name}}, your booking is confirmed!", template.getBodyTemplate());
        assertTrue(template.getIsActive());
    }

    @Test
    public void testNotificationPreferencesEntity() {
        NotificationPreferences preferences = new NotificationPreferences();
        preferences.setEmailEnabled(true);
        preferences.setSmsEnabled(false);
        preferences.setPushEnabled(true);
        preferences.setInAppEnabled(true);

        assertTrue(preferences.getEmailEnabled());
        assertFalse(preferences.getSmsEnabled());
        assertTrue(preferences.getPushEnabled());
        assertTrue(preferences.getInAppEnabled());
    }

    @Test
    public void testNotificationEnums() {
        // Test NotificationType enum
        assertEquals("BOOKING_CONFIRMATION", Notification.NotificationType.BOOKING_CONFIRMATION.name());
        assertEquals("PARKING_ALERT", Notification.NotificationType.PARKING_ALERT.name());
        assertEquals("PAYMENT_CONFIRMATION", Notification.NotificationType.PAYMENT_CONFIRMATION.name());

        // Test NotificationChannel enum
        assertEquals("EMAIL", Notification.NotificationChannel.EMAIL.name());
        assertEquals("SMS", Notification.NotificationChannel.SMS.name());
        assertEquals("PUSH", Notification.NotificationChannel.PUSH.name());
        assertEquals("IN_APP", Notification.NotificationChannel.IN_APP.name());

        // Test NotificationPriority enum
        assertEquals("LOW", Notification.NotificationPriority.LOW.name());
        assertEquals("MEDIUM", Notification.NotificationPriority.MEDIUM.name());
        assertEquals("HIGH", Notification.NotificationPriority.HIGH.name());
        assertEquals("URGENT", Notification.NotificationPriority.URGENT.name());

        // Test NotificationStatus enum
        assertEquals("PENDING", Notification.NotificationStatus.PENDING.name());
        assertEquals("QUEUED", Notification.NotificationStatus.QUEUED.name());
        assertEquals("DELIVERED", Notification.NotificationStatus.DELIVERED.name());
        assertEquals("FAILED", Notification.NotificationStatus.FAILED.name());

        // Test DeliveryStatus enum
        assertEquals("PENDING", NotificationRecipient.DeliveryStatus.PENDING.name());
        assertEquals("DELIVERED", NotificationRecipient.DeliveryStatus.DELIVERED.name());
        assertEquals("FAILED", NotificationRecipient.DeliveryStatus.FAILED.name());
        assertEquals("READ", NotificationRecipient.DeliveryStatus.READ.name());
    }
}
