package com.tirana.smartparking.notification.controller;

import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.dto.PaginatedResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import com.tirana.smartparking.common.util.PaginationUtil;
import com.tirana.smartparking.notification.dto.*;
import com.tirana.smartparking.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // ==================== SEND NOTIFICATIONS ====================

    @PreAuthorize("hasAuthority('NOTIFICATION_SEND')")
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<NotificationResponseDTO>> sendNotification(
            @Valid @RequestBody NotificationRequestDTO request) {
        NotificationResponseDTO response = notificationService.sendNotification(request);
        return ResponseHelper.ok("Notification sent successfully", response);
    }

    @PreAuthorize("hasAuthority('NOTIFICATION_SEND')")
    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<NotificationResponseDTO>> sendBulkNotification(
            @Valid @RequestBody BulkNotificationRequestDTO request) {
        NotificationResponseDTO response = notificationService.sendBulkNotification(request);
        return ResponseHelper.ok("Bulk notification sent successfully", response);
    }

    // ==================== GET NOTIFICATIONS ====================

    @PreAuthorize("hasAuthority('NOTIFICATION_READ')")
    @GetMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<NotificationResponseDTO>> getNotificationById(
            @PathVariable String notificationId) {
        NotificationResponseDTO response = notificationService.getNotificationById(notificationId);
        return ResponseHelper.ok("Notification retrieved successfully", response);
    }

    @PreAuthorize("hasAuthority('NOTIFICATION_READ')")
    @GetMapping("/{notificationId}/status")
    public ResponseEntity<ApiResponse<NotificationStatusDTO>> getNotificationStatus(
            @PathVariable String notificationId) {
        NotificationStatusDTO response = notificationService.getNotificationStatus(notificationId);
        return ResponseHelper.ok("Notification status retrieved successfully", response);
    }

    // ==================== USER NOTIFICATIONS ====================

    @PreAuthorize("hasAuthority('NOTIFICATION_READ')")
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<PaginatedResponse<UserNotificationDTO>>> getUserNotifications(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Extract user ID from userDetails - this would need to be implemented based on your user structure
        Long userId = extractUserIdFromUserDetails(userDetails);
        
        Page<UserNotificationDTO> notifications = notificationService.getUserNotifications(userId, pageable);
        return ResponseHelper.ok("User notifications retrieved successfully", 
                PaginationUtil.toPaginatedResponse(notifications));
    }

    @PreAuthorize("hasAuthority('NOTIFICATION_READ')")
    @GetMapping("/user/status/{status}")
    public ResponseEntity<ApiResponse<PaginatedResponse<UserNotificationDTO>>> getUserNotificationsByStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Long userId = extractUserIdFromUserDetails(userDetails);
        
        Page<UserNotificationDTO> notifications = notificationService.getUserNotificationsByStatus(userId, status, pageable);
        return ResponseHelper.ok("User notifications by status retrieved successfully", 
                PaginationUtil.toPaginatedResponse(notifications));
    }

    // ==================== MARK AS READ ====================

    @PreAuthorize("hasAuthority('NOTIFICATION_UPDATE')")
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markNotificationAsRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String notificationId) {
        
        Long userId = extractUserIdFromUserDetails(userDetails);
        notificationService.markNotificationAsRead(userId, notificationId);
        return ResponseHelper.ok("Notification marked as read", null);
    }

    @PreAuthorize("hasAuthority('NOTIFICATION_UPDATE')")
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllNotificationsAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = extractUserIdFromUserDetails(userDetails);
        notificationService.markAllNotificationsAsRead(userId);
        return ResponseHelper.ok("All notifications marked as read", null);
    }

    // ==================== TEMPLATES ====================

    @PreAuthorize("hasAuthority('NOTIFICATION_MANAGE')")
    @GetMapping("/templates")
    public ResponseEntity<ApiResponse<java.util.List<NotificationTemplateDTO>>> getAllTemplates() {
        java.util.List<NotificationTemplateDTO> templates = notificationService.getAllTemplates();
        return ResponseHelper.ok("Templates retrieved successfully", templates);
    }

    @PreAuthorize("hasAuthority('NOTIFICATION_MANAGE')")
    @GetMapping("/templates/{templateId}")
    public ResponseEntity<ApiResponse<NotificationTemplateDTO>> getTemplateById(
            @PathVariable String templateId) {
        NotificationTemplateDTO template = notificationService.getTemplateById(templateId);
        return ResponseHelper.ok("Template retrieved successfully", template);
    }

    @PreAuthorize("hasAuthority('NOTIFICATION_MANAGE')")
    @PostMapping("/templates")
    public ResponseEntity<ApiResponse<NotificationTemplateDTO>> createTemplate(
            @Valid @RequestBody NotificationTemplateDTO template) {
        NotificationTemplateDTO response = notificationService.createTemplate(template);
        return ResponseHelper.created("Template created successfully", response);
    }

    @PreAuthorize("hasAuthority('NOTIFICATION_MANAGE')")
    @PutMapping("/templates/{templateId}")
    public ResponseEntity<ApiResponse<NotificationTemplateDTO>> updateTemplate(
            @PathVariable String templateId,
            @Valid @RequestBody NotificationTemplateDTO template) {
        NotificationTemplateDTO response = notificationService.updateTemplate(templateId, template);
        return ResponseHelper.ok("Template updated successfully", response);
    }

    @PreAuthorize("hasAuthority('NOTIFICATION_MANAGE')")
    @DeleteMapping("/templates/{templateId}")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(@PathVariable String templateId) {
        notificationService.deleteTemplate(templateId);
        return ResponseHelper.ok("Template deleted successfully", null);
    }

    // ==================== SYSTEM OPERATIONS ====================

    @PreAuthorize("hasAuthority('NOTIFICATION_ADMIN')")
    @PostMapping("/process-scheduled")
    public ResponseEntity<ApiResponse<Void>> processScheduledNotifications() {
        notificationService.processScheduledNotifications();
        return ResponseHelper.ok("Scheduled notifications processed", null);
    }

    @PreAuthorize("hasAuthority('NOTIFICATION_ADMIN')")
    @PostMapping("/retry-failed")
    public ResponseEntity<ApiResponse<Void>> retryFailedNotifications() {
        notificationService.retryFailedNotifications();
        return ResponseHelper.ok("Failed notifications retried", null);
    }

    @PreAuthorize("hasAuthority('NOTIFICATION_ADMIN')")
    @PostMapping("/cleanup")
    public ResponseEntity<ApiResponse<Void>> cleanupOldNotifications(
            @RequestParam(defaultValue = "30") int retentionDays) {
        notificationService.cleanupOldNotifications(retentionDays);
        return ResponseHelper.ok("Old notifications cleaned up", null);
    }

    // Helper method to extract user ID from UserDetails
    private Long extractUserIdFromUserDetails(UserDetails userDetails) {
        // This is a simplified implementation - you'll need to adapt this based on your UserDetails implementation
        // For now, we'll assume the username is the user ID or you have a way to get the user ID
        try {
            return Long.parseLong(userDetails.getUsername());
        } catch (NumberFormatException e) {
            // If username is not a number, you might need to query the user repository
            // This is a placeholder - implement based on your user structure
            throw new IllegalArgumentException("Unable to extract user ID from user details");
        }
    }
}
