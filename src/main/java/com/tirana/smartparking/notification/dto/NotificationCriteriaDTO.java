package com.tirana.smartparking.notification.dto;

import java.util.List;

public record NotificationCriteriaDTO(
        List<String> userRoles,
        List<String> userGroups,
        LocationCriteriaDTO location
) {}
