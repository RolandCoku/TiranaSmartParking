# Notification Module - Complete API Documentation

This document provides comprehensive documentation for the Notification Module, including planned endpoints, request/response examples, and integration details.

## Table of Contents

- [Overview](#overview)
- [Planned Features](#planned-features)
- [Authentication & Authorization](#authentication--authorization)
- [Planned API Endpoints](#planned-api-endpoints)
- [Request/Response Examples](#requestresponse-examples)
- [Integration Examples](#integration-examples)
- [Database Schema](#database-schema)

## Overview

The Notification Module is designed to handle real-time notifications for the Smart Parking system. This module will provide comprehensive notification management including push notifications, email notifications, SMS alerts, and in-app notifications.

### Current Status

**⚠️ Module Status: Not Yet Implemented**

The notification module is currently in the planning phase. The following documentation outlines the planned architecture and API design.

### Planned Key Features

- **Multi-Channel Notifications**: Email, SMS, Push notifications, In-app notifications
- **Real-time Alerts**: Instant notifications for parking events
- **Notification Preferences**: User-configurable notification settings
- **Template Management**: Customizable notification templates
- **Delivery Tracking**: Monitor notification delivery status
- **Bulk Notifications**: Send notifications to multiple users
- **Scheduled Notifications**: Time-based notification delivery
- **Integration**: Seamless integration with other modules

### Planned Module Structure

```
Notification Module
├── Notification Management
│   ├── Send Notifications
│   ├── Template Management
│   └── Delivery Tracking
├── User Preferences
│   ├── Notification Settings
│   ├── Channel Preferences
│   └── Frequency Controls
├── Integration Services
│   ├── Email Service
│   ├── SMS Service
│   ├── Push Service
│   └── In-App Service
└── Analytics
    ├── Delivery Reports
    ├── Engagement Metrics
    └── Performance Monitoring
```

## Planned Features

### Notification Types

1. **Parking Events**
   - Space availability alerts
   - Booking confirmations
   - Session start/end notifications
   - Payment confirmations

2. **System Alerts**
   - Maintenance notifications
   - Service updates
   - Security alerts
   - Account notifications

3. **Marketing Communications**
   - Promotional offers
   - Service announcements
   - User engagement campaigns

### Notification Channels

1. **Email Notifications**
   - HTML and plain text support
   - Template-based formatting
   - Attachment support
   - Delivery confirmation

2. **SMS Notifications**
   - Short message format
   - International support
   - Delivery status tracking
   - Cost optimization

3. **Push Notifications**
   - Mobile app notifications
   - Web push notifications
   - Rich media support
   - Action buttons

4. **In-App Notifications**
   - Real-time notifications
   - Notification history
   - Mark as read/unread
   - Notification categories

## Authentication & Authorization

All endpoints will require JWT authentication. Include the token in the Authorization header:

```bash
Authorization: Bearer <your-jwt-token>
```

### Planned Required Permissions

| Endpoint | Permission Required |
|----------|-------------------|
| Send Notifications | `NOTIFICATION_SEND` |
| Manage Templates | `NOTIFICATION_MANAGE` |
| View Analytics | `NOTIFICATION_ANALYTICS` |
| User Preferences | `NOTIFICATION_PREFERENCES` |

## Planned API Endpoints

Base URL: `/api/v1/notifications`

### 1. Send Notification

**POST** `/api/v1/notifications/send`

Sends a notification to one or more users.

**Request Body:**
```json
{
  "recipients": [1, 2, 3],
  "notificationType": "PARKING_ALERT",
  "channels": ["EMAIL", "PUSH", "IN_APP"],
  "templateId": "parking_space_available",
  "data": {
    "spaceId": 5,
    "spaceLabel": "A5",
    "lotName": "City Center Parking",
    "availableUntil": "2024-01-15T15:00:00Z"
  },
  "priority": "HIGH",
  "scheduledAt": null
}
```

**Response (202 Accepted):**
```json
{
  "success": true,
  "message": "Notification queued for delivery",
  "data": {
    "notificationId": "notif_1234567890",
    "recipientCount": 3,
    "estimatedDeliveryTime": "2024-01-15T14:31:00Z",
    "status": "QUEUED"
  }
}
```

### 2. Send Bulk Notification

**POST** `/api/v1/notifications/bulk`

Sends notifications to multiple users based on criteria.

**Request Body:**
```json
{
  "criteria": {
    "userRoles": ["USER"],
    "userGroups": ["PREMIUM"],
    "location": {
      "lotId": 1,
      "radius": 1000
    }
  },
  "notificationType": "MAINTENANCE_ALERT",
  "channels": ["EMAIL", "IN_APP"],
  "templateId": "maintenance_notice",
  "data": {
    "maintenanceDate": "2024-01-20T08:00:00Z",
    "affectedSpaces": [1, 2, 3, 4, 5],
    "estimatedDuration": "4 hours"
  },
  "priority": "MEDIUM"
}
```

**Response (202 Accepted):**
```json
{
  "success": true,
  "message": "Bulk notification queued for delivery",
  "data": {
    "notificationId": "notif_bulk_1234567890",
    "recipientCount": 150,
    "estimatedDeliveryTime": "2024-01-15T14:32:00Z",
    "status": "QUEUED"
  }
}
```

### 3. Get Notification Status

**GET** `/api/v1/notifications/{notificationId}/status`

Retrieves the delivery status of a notification.

**Path Parameters:**
- `notificationId`: Notification ID

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Notification status retrieved successfully",
  "data": {
    "notificationId": "notif_1234567890",
    "status": "DELIVERED",
    "totalRecipients": 3,
    "delivered": 3,
    "failed": 0,
    "pending": 0,
    "deliveryDetails": [
      {
        "userId": 1,
        "channel": "EMAIL",
        "status": "DELIVERED",
        "deliveredAt": "2024-01-15T14:31:15Z"
      },
      {
        "userId": 2,
        "channel": "PUSH",
        "status": "DELIVERED",
        "deliveredAt": "2024-01-15T14:31:20Z"
      },
      {
        "userId": 3,
        "channel": "IN_APP",
        "status": "DELIVERED",
        "deliveredAt": "2024-01-15T14:31:25Z"
      }
    ]
  }
}
```

### 4. Get User Notifications

**GET** `/api/v1/notifications/user`

Retrieves notifications for the current user.

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `status` (optional): Filter by status (READ, UNREAD, ALL)
- `type` (optional): Filter by notification type

**Response (200 OK):**
```json
{
  "success": true,
  "message": "User notifications retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "notificationId": "notif_1234567890",
        "type": "PARKING_ALERT",
        "title": "Parking Space Available",
        "message": "Space A5 is now available at City Center Parking",
        "data": {
          "spaceId": 5,
          "spaceLabel": "A5",
          "lotName": "City Center Parking"
        },
        "channels": ["EMAIL", "PUSH", "IN_APP"],
        "priority": "HIGH",
        "status": "UNREAD",
        "createdAt": "2024-01-15T14:30:00Z",
        "readAt": null
      }
    ],
    "page": {
      "number": 0,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1,
      "first": true,
      "last": true
    },
    "unreadCount": 1
  }
}
```

### 5. Mark Notification as Read

**PATCH** `/api/v1/notifications/{notificationId}/read`

Marks a notification as read for the current user.

**Path Parameters:**
- `notificationId`: Notification ID

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Notification marked as read",
  "data": {
    "notificationId": "notif_1234567890",
    "status": "READ",
    "readAt": "2024-01-15T14:35:00Z"
  }
}
```

### 6. Mark All Notifications as Read

**PATCH** `/api/v1/notifications/read-all`

Marks all unread notifications as read for the current user.

**Response (200 OK):**
```json
{
  "success": true,
  "message": "All notifications marked as read",
  "data": {
    "markedCount": 5,
    "markedAt": "2024-01-15T14:35:00Z"
  }
}
```

### 7. Get Notification Preferences

**GET** `/api/v1/notifications/preferences`

Retrieves notification preferences for the current user.

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Notification preferences retrieved successfully",
  "data": {
    "userId": 1,
    "emailEnabled": true,
    "smsEnabled": false,
    "pushEnabled": true,
    "inAppEnabled": true,
    "preferences": {
      "PARKING_ALERT": {
        "email": true,
        "sms": false,
        "push": true,
        "inApp": true,
        "frequency": "IMMEDIATE"
      },
      "BOOKING_CONFIRMATION": {
        "email": true,
        "sms": false,
        "push": true,
        "inApp": true,
        "frequency": "IMMEDIATE"
      },
      "MAINTENANCE_ALERT": {
        "email": true,
        "sms": false,
        "push": false,
        "inApp": true,
        "frequency": "DAILY"
      }
    },
    "quietHours": {
      "enabled": true,
      "startTime": "22:00",
      "endTime": "08:00",
      "timezone": "Europe/Tirana"
    }
  }
}
```

### 8. Update Notification Preferences

**PUT** `/api/v1/notifications/preferences`

Updates notification preferences for the current user.

**Request Body:**
```json
{
  "emailEnabled": true,
  "smsEnabled": false,
  "pushEnabled": true,
  "inAppEnabled": true,
  "preferences": {
    "PARKING_ALERT": {
      "email": true,
      "sms": false,
      "push": true,
      "inApp": true,
      "frequency": "IMMEDIATE"
    },
    "BOOKING_CONFIRMATION": {
      "email": true,
      "sms": false,
      "push": true,
      "inApp": true,
      "frequency": "IMMEDIATE"
    }
  },
  "quietHours": {
    "enabled": true,
    "startTime": "22:00",
    "endTime": "08:00",
    "timezone": "Europe/Tirana"
  }
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Notification preferences updated successfully",
  "data": {
    "userId": 1,
    "emailEnabled": true,
    "smsEnabled": false,
    "pushEnabled": true,
    "inAppEnabled": true,
    "preferences": {
      "PARKING_ALERT": {
        "email": true,
        "sms": false,
        "push": true,
        "inApp": true,
        "frequency": "IMMEDIATE"
      },
      "BOOKING_CONFIRMATION": {
        "email": true,
        "sms": false,
        "push": true,
        "inApp": true,
        "frequency": "IMMEDIATE"
      }
    },
    "quietHours": {
      "enabled": true,
      "startTime": "22:00",
      "endTime": "08:00",
      "timezone": "Europe/Tirana"
    },
    "updatedAt": "2024-01-15T14:40:00Z"
  }
}
```

## Request/Response Examples

### Error Responses

#### Notification Not Found (404 Not Found)
```json
{
  "success": false,
  "message": "Notification not found",
  "data": null
}
```

#### Invalid Template (400 Bad Request)
```json
{
  "success": false,
  "message": "Invalid notification template",
  "data": {
    "errors": [
      {
        "field": "templateId",
        "message": "Template 'invalid_template' does not exist"
      }
    ]
  }
}
```

#### Delivery Failed (500 Internal Server Error)
```json
{
  "success": false,
  "message": "Notification delivery failed",
  "data": {
    "notificationId": "notif_1234567890",
    "error": "Email service unavailable",
    "retryCount": 3
  }
}
```

## Integration Examples

### Frontend Integration (JavaScript)

```javascript
class NotificationService {
  constructor() {
    this.baseURL = 'http://localhost:8080/api/v1';
    this.token = localStorage.getItem('accessToken');
  }

  getAuthHeaders() {
    return {
      'Authorization': `Bearer ${this.token}`,
      'Content-Type': 'application/json'
    };
  }

  async getUserNotifications(page = 0, size = 20, status = 'ALL') {
    const response = await fetch(
      `${this.baseURL}/notifications/user?page=${page}&size=${size}&status=${status}`,
      { headers: this.getAuthHeaders() }
    );
    return response.json();
  }

  async markAsRead(notificationId) {
    const response = await fetch(
      `${this.baseURL}/notifications/${notificationId}/read`,
      {
        method: 'PATCH',
        headers: this.getAuthHeaders()
      }
    );
    return response.json();
  }

  async markAllAsRead() {
    const response = await fetch(
      `${this.baseURL}/notifications/read-all`,
      {
        method: 'PATCH',
        headers: this.getAuthHeaders()
      }
    );
    return response.json();
  }

  async getPreferences() {
    const response = await fetch(
      `${this.baseURL}/notifications/preferences`,
      { headers: this.getAuthHeaders() }
    );
    return response.json();
  }

  async updatePreferences(preferences) {
    const response = await fetch(
      `${this.baseURL}/notifications/preferences`,
      {
        method: 'PUT',
        headers: this.getAuthHeaders(),
        body: JSON.stringify(preferences)
      }
    );
    return response.json();
  }

  // Real-time notifications using WebSocket
  connectWebSocket() {
    const ws = new WebSocket(`ws://localhost:8080/ws/notifications?token=${this.token}`);
    
    ws.onmessage = (event) => {
      const notification = JSON.parse(event.data);
      this.displayNotification(notification);
    };
    
    return ws;
  }

  displayNotification(notification) {
    // Show browser notification if permission granted
    if (Notification.permission === 'granted') {
      new Notification(notification.title, {
        body: notification.message,
        icon: '/favicon.ico'
      });
    }
    
    // Update in-app notification list
    this.updateNotificationList(notification);
  }
}
```

### Backend Integration (Java)

```java
@Service
public class NotificationIntegrationService {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserRepository userRepository;
    
    // Integration with booking system
    @EventListener
    public void handleBookingCreated(BookingCreatedEvent event) {
        User user = userRepository.findById(event.getUserId()).orElse(null);
        if (user != null) {
            NotificationRequest request = NotificationRequest.builder()
                .recipients(List.of(user.getId()))
                .notificationType(NotificationType.BOOKING_CONFIRMATION)
                .channels(List.of(NotificationChannel.EMAIL, NotificationChannel.PUSH))
                .templateId("booking_confirmation")
                .data(Map.of(
                    "bookingId", event.getBookingId(),
                    "spaceLabel", event.getSpaceLabel(),
                    "startTime", event.getStartTime(),
                    "endTime", event.getEndTime()
                ))
                .priority(NotificationPriority.HIGH)
                .build();
            
            notificationService.sendNotification(request);
        }
    }
    
    // Integration with sensor system
    @EventListener
    public void handleSpaceAvailable(SpaceAvailableEvent event) {
        // Find users looking for parking in the area
        List<User> interestedUsers = findUsersLookingForParking(event.getLotId());
        
        if (!interestedUsers.isEmpty()) {
            List<Long> userIds = interestedUsers.stream()
                .map(User::getId)
                .collect(Collectors.toList());
            
            NotificationRequest request = NotificationRequest.builder()
                .recipients(userIds)
                .notificationType(NotificationType.PARKING_ALERT)
                .channels(List.of(NotificationChannel.PUSH, NotificationChannel.IN_APP))
                .templateId("space_available")
                .data(Map.of(
                    "spaceId", event.getSpaceId(),
                    "spaceLabel", event.getSpaceLabel(),
                    "lotName", event.getLotName(),
                    "availableUntil", event.getAvailableUntil()
                ))
                .priority(NotificationPriority.HIGH)
                .build();
            
            notificationService.sendNotification(request);
        }
    }
}
```

## Database Schema

### notifications table
```sql
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    notification_id VARCHAR(50) UNIQUE NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    data JSONB,
    channels TEXT[] NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    scheduled_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

### notification_recipients table
```sql
CREATE TABLE notification_recipients (
    id BIGSERIAL PRIMARY KEY,
    notification_id VARCHAR(50) NOT NULL REFERENCES notifications(notification_id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    channel VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    delivered_at TIMESTAMP,
    read_at TIMESTAMP,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

### notification_templates table
```sql
CREATE TABLE notification_templates (
    id BIGSERIAL PRIMARY KEY,
    template_id VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    channels TEXT[] NOT NULL,
    subject_template TEXT,
    body_template TEXT NOT NULL,
    variables JSONB,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

### notification_preferences table
```sql
CREATE TABLE notification_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    email_enabled BOOLEAN DEFAULT true,
    sms_enabled BOOLEAN DEFAULT false,
    push_enabled BOOLEAN DEFAULT true,
    in_app_enabled BOOLEAN DEFAULT true,
    preferences JSONB NOT NULL DEFAULT '{}',
    quiet_hours JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(user_id)
);
```

### Indexes
```sql
-- Performance indexes
CREATE INDEX idx_notifications_type ON notifications(type);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_scheduled_at ON notifications(scheduled_at);
CREATE INDEX idx_notification_recipients_notification_id ON notification_recipients(notification_id);
CREATE INDEX idx_notification_recipients_user_id ON notification_recipients(user_id);
CREATE INDEX idx_notification_recipients_status ON notification_recipients(status);
CREATE INDEX idx_notification_templates_template_id ON notification_templates(template_id);
CREATE INDEX idx_notification_templates_type ON notification_templates(type);
CREATE INDEX idx_notification_preferences_user_id ON notification_preferences(user_id);
```

## Configuration

### Notification Configuration
```yaml
# application.yml
notification:
  channels:
    email:
      enabled: true
      provider: "sendgrid"
      from-email: "noreply@smartparking.com"
    sms:
      enabled: true
      provider: "twilio"
    push:
      enabled: true
      provider: "firebase"
    in-app:
      enabled: true
      retention-days: 30
  
  templates:
    base-path: "/templates"
    cache-enabled: true
  
  delivery:
    retry-attempts: 3
    retry-delay: 300 # 5 minutes
    batch-size: 100
    max-concurrent: 10
```

### Service Integration
```java
@Configuration
public class NotificationConfig {
    
    @Bean
    public EmailService emailService() {
        return new SendGridEmailService();
    }
    
    @Bean
    public SmsService smsService() {
        return new TwilioSmsService();
    }
    
    @Bean
    public PushService pushService() {
        return new FirebasePushService();
    }
    
    @Bean
    public NotificationTemplateService templateService() {
        return new ThymeleafTemplateService();
    }
}
```

## Implementation Roadmap

### Phase 1: Core Infrastructure
- [ ] Database schema implementation
- [ ] Basic notification service
- [ ] Email notification integration
- [ ] In-app notification system

### Phase 2: Advanced Features
- [ ] SMS notification integration
- [ ] Push notification service
- [ ] Template management system
- [ ] User preference management

### Phase 3: Analytics & Optimization
- [ ] Delivery tracking and analytics
- [ ] Performance monitoring
- [ ] A/B testing for notifications
- [ ] Advanced scheduling features

### Phase 4: Integration & Automation
- [ ] Event-driven notifications
- [ ] Automated notification workflows
- [ ] Integration with all system modules
- [ ] Advanced personalization

This comprehensive documentation outlines the planned Notification Module architecture and API design. The module will provide a robust foundation for all notification needs in the Smart Parking system.
