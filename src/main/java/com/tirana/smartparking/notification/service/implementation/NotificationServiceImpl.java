package com.tirana.smartparking.notification.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tirana.smartparking.common.exception.ResourceNotFoundException;
import com.tirana.smartparking.notification.dto.*;
import com.tirana.smartparking.notification.entity.*;
import com.tirana.smartparking.notification.repository.*;
import com.tirana.smartparking.notification.service.NotificationService;
import com.tirana.smartparking.user.entity.User;
import com.tirana.smartparking.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository recipientRepository;
    private final NotificationTemplateRepository templateRepository;
    private final NotificationPreferencesRepository preferencesRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   NotificationRecipientRepository recipientRepository,
                                   NotificationTemplateRepository templateRepository,
                                   NotificationPreferencesRepository preferencesRepository,
                                   UserRepository userRepository,
                                   ObjectMapper objectMapper) {
        this.notificationRepository = notificationRepository;
        this.recipientRepository = recipientRepository;
        this.templateRepository = templateRepository;
        this.preferencesRepository = preferencesRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public NotificationResponseDTO sendNotification(NotificationRequestDTO request) {
        logger.info("Sending notification to {} recipients", request.recipients().size());

        // Create notification entity
        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setType(request.notificationType());
        notification.setTitle(request.title());
        notification.setMessage(request.message());
        notification.setChannels(request.channels());
        notification.setPriority(request.priority());
        notification.setTemplateId(request.templateId());
        notification.setScheduledAt(request.scheduledAt());
        notification.setStatus(Notification.NotificationStatus.QUEUED);

        // Serialize data to JSON
        if (request.data() != null) {
            try {
                notification.setData(objectMapper.writeValueAsString(request.data()));
            } catch (JsonProcessingException e) {
                logger.error("Failed to serialize notification data", e);
                throw new RuntimeException("Failed to serialize notification data", e);
            }
        }

        // Save notification
        final Notification savedNotification = notificationRepository.save(notification);

        // Create recipients
        List<NotificationRecipient> recipients = request.recipients().stream()
                .map(userId -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                    
                    return request.channels().stream()
                            .map(channel -> {
                                NotificationRecipient recipient = new NotificationRecipient();
                                recipient.setNotification(notification);
                                recipient.setUser(user);
                                recipient.setChannel(channel);
                                recipient.setStatus(NotificationRecipient.DeliveryStatus.PENDING);
                                return recipient;
                            })
                            .collect(Collectors.toList());
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());

        recipientRepository.saveAll(recipients);

        logger.info("Notification {} created with {} recipients", 
                   savedNotification.getNotificationId(), recipients.size());

        return mapToNotificationResponseDTO(savedNotification);
    }

    @Override
    public NotificationResponseDTO sendBulkNotification(BulkNotificationRequestDTO request) {
        logger.info("Sending bulk notification with criteria: {}", request.criteria());

        // Find users based on criteria
        List<User> users = findUsersByCriteria(request.criteria());
        
        if (users.isEmpty()) {
            logger.warn("No users found matching criteria");
            return null;
        }

        // Convert to notification request
        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        NotificationRequestDTO notificationRequest = new NotificationRequestDTO(
                userIds,
                request.notificationType(),
                request.channels(),
                request.title(),
                request.message(),
                request.templateId(),
                request.data(),
                request.priority(),
                request.scheduledAt()
        );

        return sendNotification(notificationRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponseDTO getNotificationById(String notificationId) {
        Notification notification = notificationRepository.findByNotificationId(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        
        return mapToNotificationResponseDTO(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationStatusDTO getNotificationStatus(String notificationId) {
        Notification notification = notificationRepository.findByNotificationId(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));

        List<NotificationRecipient> recipients = recipientRepository.findByNotificationId(notification.getId());
        
        int totalRecipients = recipients.size();
        int delivered = (int) recipients.stream().filter(r -> r.getStatus() == NotificationRecipient.DeliveryStatus.DELIVERED).count();
        int failed = (int) recipients.stream().filter(r -> r.getStatus() == NotificationRecipient.DeliveryStatus.FAILED).count();
        int pending = totalRecipients - delivered - failed;

        List<DeliveryDetailDTO> deliveryDetails = recipients.stream()
                .map(this::mapToDeliveryDetailDTO)
                .collect(Collectors.toList());

        return new NotificationStatusDTO(
                notificationId,
                notification.getStatus(),
                totalRecipients,
                delivered,
                failed,
                pending,
                deliveryDetails
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserNotificationDTO> getUserNotifications(Long userId, Pageable pageable) {
        Page<NotificationRecipient> recipients = recipientRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return recipients.map(this::mapToUserNotificationDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserNotificationDTO> getUserNotificationsByStatus(Long userId, String status, Pageable pageable) {
        NotificationRecipient.DeliveryStatus deliveryStatus = NotificationRecipient.DeliveryStatus.valueOf(status.toUpperCase());
        Page<NotificationRecipient> recipients = recipientRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, deliveryStatus, pageable);
        return recipients.map(this::mapToUserNotificationDTO);
    }

    @Override
    public void markNotificationAsRead(Long userId, String notificationId) {
        Notification notification = notificationRepository.findByNotificationId(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));

        List<NotificationRecipient> recipients = recipientRepository.findByNotificationId(notification.getId());
        
        recipients.stream()
                .filter(r -> r.getUser().getId().equals(userId))
                .forEach(r -> {
                    r.setReadAt(Instant.now());
                    r.setStatus(NotificationRecipient.DeliveryStatus.READ);
                });

        recipientRepository.saveAll(recipients);
        logger.info("Marked notification {} as read for user {}", notificationId, userId);
    }

    @Override
    public void markAllNotificationsAsRead(Long userId) {
        recipientRepository.markAllAsReadByUserId(userId, Instant.now());
        logger.info("Marked all notifications as read for user {}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationPreferencesDTO getUserPreferences(Long userId) {
        NotificationPreferences preferences = preferencesRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                    
                    NotificationPreferences newPrefs = new NotificationPreferences();
                    newPrefs.setUser(user);
                    newPrefs.setEmailEnabled(true);
                    newPrefs.setSmsEnabled(false);
                    newPrefs.setPushEnabled(true);
                    newPrefs.setInAppEnabled(true);
                    
                    try {
                        Map<String, Object> defaultPrefs = Map.of(
                                "PARKING_ALERT", Map.of("email", true, "sms", false, "push", true, "inApp", true, "frequency", "IMMEDIATE"),
                                "BOOKING_CONFIRMATION", Map.of("email", true, "sms", false, "push", true, "inApp", true, "frequency", "IMMEDIATE"),
                                "MAINTENANCE_ALERT", Map.of("email", true, "sms", false, "push", false, "inApp", true, "frequency", "DAILY")
                        );
                        newPrefs.setPreferences(objectMapper.writeValueAsString(defaultPrefs));
                    } catch (JsonProcessingException e) {
                        logger.error("Failed to serialize default preferences", e);
                    }
                    
                    return preferencesRepository.save(newPrefs);
                });
        
        return mapToNotificationPreferencesDTO(preferences);
    }

    @Override
    public NotificationPreferencesDTO updateUserPreferences(Long userId, NotificationPreferencesDTO preferencesDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        NotificationPreferences preferences = preferencesRepository.findByUser(user)
                .orElseGet(() -> {
                    NotificationPreferences newPrefs = new NotificationPreferences();
                    newPrefs.setUser(user);
                    return newPrefs;
                });

        preferences.setEmailEnabled(preferencesDTO.emailEnabled());
        preferences.setSmsEnabled(preferencesDTO.smsEnabled());
        preferences.setPushEnabled(preferencesDTO.pushEnabled());
        preferences.setInAppEnabled(preferencesDTO.inAppEnabled());

        // Serialize preferences and quiet hours
        try {
            if (preferencesDTO.preferences() != null) {
                preferences.setPreferences(objectMapper.writeValueAsString(preferencesDTO.preferences()));
            }
            if (preferencesDTO.quietHours() != null) {
                preferences.setQuietHours(objectMapper.writeValueAsString(preferencesDTO.quietHours()));
            }
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize preferences", e);
            throw new RuntimeException("Failed to serialize preferences", e);
        }

        preferences = preferencesRepository.save(preferences);
        logger.info("Updated notification preferences for user {}", userId);

        return mapToNotificationPreferencesDTO(preferences);
    }

    @Override
    public NotificationPreferencesDTO createDefaultPreferences(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        NotificationPreferences preferences = new NotificationPreferences();
        preferences.setUser(user);
        preferences.setEmailEnabled(true);
        preferences.setSmsEnabled(false);
        preferences.setPushEnabled(true);
        preferences.setInAppEnabled(true);

        // Set default preferences
        try {
            Map<String, Object> defaultPrefs = Map.of(
                    "PARKING_ALERT", Map.of("email", true, "sms", false, "push", true, "inApp", true, "frequency", "IMMEDIATE"),
                    "BOOKING_CONFIRMATION", Map.of("email", true, "sms", false, "push", true, "inApp", true, "frequency", "IMMEDIATE"),
                    "MAINTENANCE_ALERT", Map.of("email", true, "sms", false, "push", false, "inApp", true, "frequency", "DAILY")
            );
            preferences.setPreferences(objectMapper.writeValueAsString(defaultPrefs));
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize default preferences", e);
        }

        preferences = preferencesRepository.save(preferences);
        logger.info("Created default notification preferences for user {}", userId);

        return mapToNotificationPreferencesDTO(preferences);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplateDTO> getAllTemplates() {
        return templateRepository.findByIsActiveTrue().stream()
                .map(this::mapToNotificationTemplateDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationTemplateDTO getTemplateById(String templateId) {
        NotificationTemplate template = templateRepository.findActiveByTemplateId(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found: " + templateId));
        
        return mapToNotificationTemplateDTO(template);
    }

    @Override
    public NotificationTemplateDTO createTemplate(NotificationTemplateDTO templateDTO) {
        NotificationTemplate template = new NotificationTemplate();
        template.setTemplateId(templateDTO.templateId());
        template.setName(templateDTO.name());
        template.setType(templateDTO.type());
        template.setChannels(templateDTO.channels());
        template.setSubjectTemplate(templateDTO.subjectTemplate());
        template.setBodyTemplate(templateDTO.bodyTemplate());
        template.setIsActive(templateDTO.isActive());

        try {
            if (templateDTO.variables() != null) {
                template.setVariables(objectMapper.writeValueAsString(templateDTO.variables()));
            }
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize template variables", e);
            throw new RuntimeException("Failed to serialize template variables", e);
        }

        template = templateRepository.save(template);
        logger.info("Created notification template: {}", template.getTemplateId());

        return mapToNotificationTemplateDTO(template);
    }

    @Override
    public NotificationTemplateDTO updateTemplate(String templateId, NotificationTemplateDTO templateDTO) {
        NotificationTemplate template = templateRepository.findByTemplateId(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found: " + templateId));

        template.setName(templateDTO.name());
        template.setType(templateDTO.type());
        template.setChannels(templateDTO.channels());
        template.setSubjectTemplate(templateDTO.subjectTemplate());
        template.setBodyTemplate(templateDTO.bodyTemplate());
        template.setIsActive(templateDTO.isActive());

        try {
            if (templateDTO.variables() != null) {
                template.setVariables(objectMapper.writeValueAsString(templateDTO.variables()));
            }
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize template variables", e);
            throw new RuntimeException("Failed to serialize template variables", e);
        }

        template = templateRepository.save(template);
        logger.info("Updated notification template: {}", templateId);

        return mapToNotificationTemplateDTO(template);
    }

    @Override
    public void deleteTemplate(String templateId) {
        NotificationTemplate template = templateRepository.findByTemplateId(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found: " + templateId));

        template.setIsActive(false);
        templateRepository.save(template);
        logger.info("Deactivated notification template: {}", templateId);
    }

    @Override
    public void processScheduledNotifications() {
        List<Notification> scheduledNotifications = notificationRepository.findScheduledNotifications(
                Notification.NotificationStatus.QUEUED, Instant.now());

        for (Notification notification : scheduledNotifications) {
            notification.setStatus(Notification.NotificationStatus.SENDING);
            notificationRepository.save(notification);
            
            // Process notification delivery
            processNotificationDelivery(notification);
        }

        logger.info("Processed {} scheduled notifications", scheduledNotifications.size());
    }

    @Override
    public void retryFailedNotifications() {
        List<NotificationRecipient> failedRecipients = recipientRepository.findFailedNotificationsForRetry(
                NotificationRecipient.DeliveryStatus.FAILED, 3);

        for (NotificationRecipient recipient : failedRecipients) {
            recipient.setRetryCount(recipient.getRetryCount() + 1);
            recipient.setStatus(NotificationRecipient.DeliveryStatus.PENDING);
            recipientRepository.save(recipient);
        }

        logger.info("Retried {} failed notifications", failedRecipients.size());
    }

    @Override
    public void cleanupOldNotifications(int retentionDays) {
        Instant cutoffDate = Instant.now().minusSeconds(retentionDays * 24 * 60 * 60);
        
        // Delete old notification recipients first (due to foreign key constraints)
        List<NotificationRecipient> oldRecipients = recipientRepository.findAll().stream()
                .filter(r -> r.getCreatedAt().isBefore(cutoffDate))
                .collect(Collectors.toList());
        
        recipientRepository.deleteAll(oldRecipients);
        
        // Delete old notifications
        List<Notification> oldNotifications = notificationRepository.findAll().stream()
                .filter(n -> n.getCreatedAt().isBefore(cutoffDate))
                .collect(Collectors.toList());
        
        notificationRepository.deleteAll(oldNotifications);
        
        logger.info("Cleaned up {} old notifications and {} recipients", 
                   oldNotifications.size(), oldRecipients.size());
    }

    // Helper methods
    private List<User> findUsersByCriteria(NotificationCriteriaDTO criteria) {
        // This is a simplified implementation - in a real system, you'd have more complex criteria
        if (criteria.userRoles() != null && !criteria.userRoles().isEmpty()) {
            return userRepository.findAll().stream()
                    .filter(user -> user.getRoles().stream()
                            .anyMatch(role -> criteria.userRoles().contains(role.getName())))
                    .collect(Collectors.toList());
        }
        
        return userRepository.findAll();
    }

    private void processNotificationDelivery(Notification notification) {
        // This would integrate with actual delivery services (email, SMS, push, etc.)
        List<NotificationRecipient> recipients = recipientRepository.findByNotificationId(notification.getId());
        
        for (NotificationRecipient recipient : recipients) {
            try {
                // Simulate delivery
                recipient.setStatus(NotificationRecipient.DeliveryStatus.DELIVERED);
                recipient.setDeliveredAt(Instant.now());
                recipientRepository.save(recipient);
            } catch (Exception e) {
                recipient.setStatus(NotificationRecipient.DeliveryStatus.FAILED);
                recipient.setErrorMessage(e.getMessage());
                recipientRepository.save(recipient);
            }
        }
        
        // Update notification status
        boolean allDelivered = recipients.stream()
                .allMatch(r -> r.getStatus() == NotificationRecipient.DeliveryStatus.DELIVERED);
        
        if (allDelivered) {
            notification.setStatus(Notification.NotificationStatus.DELIVERED);
        } else {
            notification.setStatus(Notification.NotificationStatus.FAILED);
        }
        
        notificationRepository.save(notification);
    }

    // Mapping methods
    private NotificationResponseDTO mapToNotificationResponseDTO(Notification notification) {
        List<NotificationRecipientDTO> recipientDTOs = notification.getRecipients() != null ?
                notification.getRecipients().stream()
                        .map(this::mapToNotificationRecipientDTO)
                        .collect(Collectors.toList()) : List.of();

        Map<String, Object> data = null;
        if (notification.getData() != null) {
            try {
                data = objectMapper.readValue(notification.getData(), Map.class);
            } catch (JsonProcessingException e) {
                logger.error("Failed to deserialize notification data", e);
            }
        }

        return new NotificationResponseDTO(
                notification.getId(),
                notification.getNotificationId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                data,
                notification.getChannels(),
                notification.getPriority(),
                notification.getStatus(),
                notification.getTemplateId(),
                notification.getScheduledAt(),
                notification.getCreatedAt(),
                notification.getUpdatedAt(),
                recipientDTOs
        );
    }

    private NotificationRecipientDTO mapToNotificationRecipientDTO(NotificationRecipient recipient) {
        return new NotificationRecipientDTO(
                recipient.getId(),
                recipient.getUser().getId(),
                recipient.getUser().getEmail(),
                recipient.getChannel(),
                recipient.getStatus(),
                recipient.getDeliveredAt(),
                recipient.getReadAt(),
                recipient.getErrorMessage(),
                recipient.getRetryCount(),
                recipient.getCreatedAt(),
                recipient.getUpdatedAt()
        );
    }

    private DeliveryDetailDTO mapToDeliveryDetailDTO(NotificationRecipient recipient) {
        return new DeliveryDetailDTO(
                recipient.getUser().getId(),
                recipient.getUser().getEmail(),
                recipient.getChannel(),
                recipient.getStatus(),
                recipient.getDeliveredAt(),
                recipient.getErrorMessage()
        );
    }

    private UserNotificationDTO mapToUserNotificationDTO(NotificationRecipient recipient) {
        Map<String, Object> data = null;
        if (recipient.getNotification().getData() != null) {
            try {
                data = objectMapper.readValue(recipient.getNotification().getData(), Map.class);
            } catch (JsonProcessingException e) {
                logger.error("Failed to deserialize notification data", e);
            }
        }

        return new UserNotificationDTO(
                recipient.getId(),
                recipient.getNotification().getNotificationId(),
                recipient.getNotification().getType(),
                recipient.getNotification().getTitle(),
                recipient.getNotification().getMessage(),
                data,
                recipient.getNotification().getChannels(),
                recipient.getNotification().getPriority(),
                recipient.getStatus(),
                recipient.getCreatedAt(),
                recipient.getReadAt()
        );
    }

    private NotificationPreferencesDTO mapToNotificationPreferencesDTO(NotificationPreferences preferences) {
        Map<String, Object> preferencesMap = null;
        if (preferences.getPreferences() != null) {
            try {
                preferencesMap = objectMapper.readValue(preferences.getPreferences(), Map.class);
            } catch (JsonProcessingException e) {
                logger.error("Failed to deserialize preferences", e);
            }
        }

        Map<String, Object> quietHoursMap = null;
        if (preferences.getQuietHours() != null) {
            try {
                quietHoursMap = objectMapper.readValue(preferences.getQuietHours(), Map.class);
            } catch (JsonProcessingException e) {
                logger.error("Failed to deserialize quiet hours", e);
            }
        }

        return new NotificationPreferencesDTO(
                preferences.getUser().getId(),
                preferences.getEmailEnabled(),
                preferences.getSmsEnabled(),
                preferences.getPushEnabled(),
                preferences.getInAppEnabled(),
                (Map<String, TypePreferenceDTO>) (Map<?, ?>) preferencesMap,
                (QuietHoursDTO) (Object) quietHoursMap,
                preferences.getCreatedAt(),
                preferences.getUpdatedAt()
        );
    }

    private NotificationTemplateDTO mapToNotificationTemplateDTO(NotificationTemplate template) {
        Map<String, Object> variables = null;
        if (template.getVariables() != null) {
            try {
                variables = objectMapper.readValue(template.getVariables(), Map.class);
            } catch (JsonProcessingException e) {
                logger.error("Failed to deserialize template variables", e);
            }
        }

        return new NotificationTemplateDTO(
                template.getId(),
                template.getTemplateId(),
                template.getName(),
                template.getType(),
                template.getChannels(),
                template.getSubjectTemplate(),
                template.getBodyTemplate(),
                variables,
                template.getIsActive(),
                template.getCreatedAt(),
                template.getUpdatedAt()
        );
    }
}
