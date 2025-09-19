package com.tirana.smartparking.notification.service;

import com.tirana.smartparking.notification.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    
    // Send notifications
    NotificationResponseDTO sendNotification(NotificationRequestDTO request);
    NotificationResponseDTO sendBulkNotification(BulkNotificationRequestDTO request);
    
    // Get notifications
    NotificationResponseDTO getNotificationById(String notificationId);
    NotificationStatusDTO getNotificationStatus(String notificationId);
    Page<UserNotificationDTO> getUserNotifications(Long userId, Pageable pageable);
    Page<UserNotificationDTO> getUserNotificationsByStatus(Long userId, String status, Pageable pageable);
    
    // Mark notifications as read
    void markNotificationAsRead(Long userId, String notificationId);
    void markAllNotificationsAsRead(Long userId);
    
    // Preferences management
    NotificationPreferencesDTO getUserPreferences(Long userId);
    NotificationPreferencesDTO updateUserPreferences(Long userId, NotificationPreferencesDTO preferences);
    NotificationPreferencesDTO createDefaultPreferences(Long userId);
    
    // Template management
    List<NotificationTemplateDTO> getAllTemplates();
    NotificationTemplateDTO getTemplateById(String templateId);
    NotificationTemplateDTO createTemplate(NotificationTemplateDTO template);
    NotificationTemplateDTO updateTemplate(String templateId, NotificationTemplateDTO template);
    void deleteTemplate(String templateId);
    
    // System operations
    void processScheduledNotifications();
    void retryFailedNotifications();
    void cleanupOldNotifications(int retentionDays);
}
