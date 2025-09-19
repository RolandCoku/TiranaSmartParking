package com.tirana.smartparking.notification.controller;

import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.response.ResponseHelper;
import com.tirana.smartparking.notification.dto.NotificationPreferencesDTO;
import com.tirana.smartparking.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications/preferences")
@Validated
public class NotificationPreferencesController {

    private final NotificationService notificationService;

    public NotificationPreferencesController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PreAuthorize("hasAuthority('NOTIFICATION_PREFERENCES')")
    @GetMapping
    public ResponseEntity<ApiResponse<NotificationPreferencesDTO>> getUserPreferences(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = extractUserIdFromUserDetails(userDetails);
        NotificationPreferencesDTO preferences = notificationService.getUserPreferences(userId);
        return ResponseHelper.ok("Notification preferences retrieved successfully", preferences);
    }

    @PreAuthorize("hasAuthority('NOTIFICATION_PREFERENCES')")
    @PutMapping
    public ResponseEntity<ApiResponse<NotificationPreferencesDTO>> updateUserPreferences(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody NotificationPreferencesDTO preferences) {
        
        Long userId = extractUserIdFromUserDetails(userDetails);
        NotificationPreferencesDTO response = notificationService.updateUserPreferences(userId, preferences);
        return ResponseHelper.ok("Notification preferences updated successfully", response);
    }

    @PreAuthorize("hasAuthority('NOTIFICATION_PREFERENCES')")
    @PostMapping("/default")
    public ResponseEntity<ApiResponse<NotificationPreferencesDTO>> createDefaultPreferences(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = extractUserIdFromUserDetails(userDetails);
        NotificationPreferencesDTO preferences = notificationService.createDefaultPreferences(userId);
        return ResponseHelper.created("Default notification preferences created successfully", preferences);
    }

    // Helper method to extract user ID from UserDetails
    private Long extractUserIdFromUserDetails(UserDetails userDetails) {
        // This is a simplified implementation - you'll need to adapt this based on your UserDetails implementation
        try {
            return Long.parseLong(userDetails.getUsername());
        } catch (NumberFormatException e) {
            // If username is not a number, you might need to query the user repository
            // This is a placeholder - implement based on your user structure
            throw new IllegalArgumentException("Unable to extract user ID from user details");
        }
    }
}
