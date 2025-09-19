package com.tirana.smartparking.notification.dto;

import com.tirana.smartparking.notification.entity.Notification;

import java.util.List;

public record NotificationStatusDTO(
        String notificationId,
        Notification.NotificationStatus status,
        Integer totalRecipients,
        Integer delivered,
        Integer failed,
        Integer pending,
        List<DeliveryDetailDTO> deliveryDetails
) {}

