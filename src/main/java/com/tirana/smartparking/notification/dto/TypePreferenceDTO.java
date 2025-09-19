package com.tirana.smartparking.notification.dto;

public record TypePreferenceDTO(
        Boolean email,
        Boolean sms,
        Boolean push,
        Boolean inApp,
        String frequency
) {}
