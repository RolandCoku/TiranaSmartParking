# Parking Session Management API Documentation

This document provides comprehensive documentation for the Parking Session Management API, including detailed examples for all endpoints.

## Table of Contents

- [Overview](#overview)
- [Authentication](#authentication)
- [Session Lifecycle](#session-lifecycle)
- [User Session Endpoints](#user-session-endpoints)
- [Admin Session Endpoints](#admin-session-endpoints)
- [Integration with Booking System](#integration-with-booking-system)
- [Error Handling](#error-handling)
- [Examples](#examples)
- [Best Practices](#best-practices)

## Overview

The Parking Session Management API allows users to:
- Start, stop, and manage parking sessions
- View session history and current active sessions
- Get pricing quotes for sessions
- Check space availability
- Extend or cancel sessions

Parking sessions represent actual parking events (when someone is currently parked), while bookings represent reservations for future parking.

## Authentication

All endpoints require JWT authentication. Include the token in the Authorization header:

```bash
Authorization: Bearer <your-jwt-token>
```

## Session Lifecycle

The parking session system follows a clear lifecycle:

```
ACTIVE → COMPLETED
   ↓
CANCELLED
   ↓
EXPIRED (automatic)
```

### Status Transitions

1. **ACTIVE**: Session is currently in progress
   - Can be stopped, cancelled, or extended
   - Automatically becomes EXPIRED if running too long

2. **COMPLETED**: Session has finished successfully
   - Final state, no further changes allowed

3. **CANCELLED**: Session was cancelled by user or admin
   - Final state, no further changes allowed

4. **EXPIRED**: Session was automatically expired due to timeout
   - Final state, no further changes allowed

## User Session Endpoints

Base URL: `/api/v1/parking-sessions`

### 1. Start Parking Session

**POST** `/api/v1/parking-sessions`

Starts a new parking session for the authenticated user.

#### Request Body
```json
{
  "parkingSpaceId": 123,
  "vehiclePlate": "ABC-1234",
  "vehicleType": "CAR",
  "userGroup": "PUBLIC",
  "paymentMethodId": "pm_1234567890",
  "notes": "Parking near the entrance"
}
```

#### Response
```json
{
  "success": true,
  "message": "Parking session started successfully",
  "data": {
    "id": 1,
    "userId": 456,
    "userEmail": "user@example.com",
    "parkingSpaceId": 123,
    "parkingSpaceLabel": "A-12",
    "parkingLotId": 789,
    "parkingLotName": "City Center Garage",
    "parkingLotAddress": "123 Main St, Downtown",
    "vehiclePlate": "ABC-1234",
    "vehicleType": "CAR",
    "userGroup": "PUBLIC",
    "startedAt": "2024-01-15T09:00:00+01:00",
    "endedAt": null,
    "billedAmount": 500,
    "currency": "ALL",
    "status": "ACTIVE",
    "sessionReference": "PSN1A2B3C",
    "paymentMethodId": "pm_1234567890",
    "notes": "Parking near the entrance",
    "createdAt": "2024-01-15T09:00:00Z",
    "updatedAt": "2024-01-15T09:00:00Z"
  }
}
```

#### Example Usage
```bash
curl -X POST "http://localhost:8080/api/v1/parking-sessions" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "parkingSpaceId": 123,
    "vehiclePlate": "ABC-1234",
    "vehicleType": "CAR",
    "userGroup": "PUBLIC",
    "paymentMethodId": "pm_1234567890",
    "notes": "Parking near the entrance"
  }'
```

### 2. Get Session by ID

**GET** `/api/v1/parking-sessions/{id}`

Retrieves a specific session by its ID.

#### Example Usage
```bash
curl -X GET "http://localhost:8080/api/v1/parking-sessions/1" \
  -H "Authorization: Bearer your-jwt-token"
```

### 3. Get Session by Reference

**GET** `/api/v1/parking-sessions/reference/{reference}`

Retrieves a session by its reference number.

#### Example Usage
```bash
curl -X GET "http://localhost:8080/api/v1/parking-sessions/reference/PSN1A2B3C" \
  -H "Authorization: Bearer your-jwt-token"
```

### 4. Get User Sessions

**GET** `/api/v1/parking-sessions`

Retrieves paginated list of user's sessions.

#### Query Parameters
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `sortBy` (optional): Sort field (default: "startedAt")
- `sortDir` (optional): Sort direction - "asc" or "desc" (default: "desc")

#### Example Usage
```bash
curl -X GET "http://localhost:8080/api/v1/parking-sessions?page=0&size=10&sortBy=startedAt&sortDir=desc" \
  -H "Authorization: Bearer your-jwt-token"
```

### 5. Get Active Sessions

**GET** `/api/v1/parking-sessions/active`

Retrieves user's currently active sessions.

#### Query Parameters
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)

#### Example Usage
```bash
curl -X GET "http://localhost:8080/api/v1/parking-sessions/active?page=0&size=10" \
  -H "Authorization: Bearer your-jwt-token"
```

### 6. Get Session History

**GET** `/api/v1/parking-sessions/history`

Retrieves user's session history (COMPLETED, CANCELLED, EXPIRED).

#### Query Parameters
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)

#### Example Usage
```bash
curl -X GET "http://localhost:8080/api/v1/parking-sessions/history?page=0&size=10" \
  -H "Authorization: Bearer your-jwt-token"
```

### 7. Update Session

**PUT** `/api/v1/parking-sessions/{id}`

Updates an existing session. Only ACTIVE sessions can be updated.

#### Request Body
```json
{
  "vehiclePlate": "XYZ-9876",
  "vehicleType": "MOTORCYCLE",
  "userGroup": "STUDENT",
  "endTime": "2024-01-15T12:00:00+01:00",
  "paymentMethodId": "pm_0987654321",
  "notes": "Updated notes"
}
```

#### Example Usage
```bash
curl -X PUT "http://localhost:8080/api/v1/parking-sessions/1" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "vehiclePlate": "XYZ-9876",
    "vehicleType": "MOTORCYCLE",
    "userGroup": "STUDENT",
    "endTime": "2024-01-15T12:00:00+01:00",
    "paymentMethodId": "pm_0987654321",
    "notes": "Updated notes"
  }'
```

### 8. Delete Session

**DELETE** `/api/v1/parking-sessions/{id}`

Deletes a session. Only COMPLETED or CANCELLED sessions can be deleted.

#### Example Usage
```bash
curl -X DELETE "http://localhost:8080/api/v1/parking-sessions/1" \
  -H "Authorization: Bearer your-jwt-token"
```

### 9. Stop Session

**POST** `/api/v1/parking-sessions/{id}/stop`

Stops an active session and calculates final billing.

#### Request Body
```json
{
  "endTime": "2024-01-15T11:00:00+01:00",
  "notes": "Leaving early"
}
```

#### Response
```json
{
  "success": true,
  "message": "Session stopped successfully",
  "data": {
    "id": 1,
    "userId": 456,
    "userEmail": "user@example.com",
    "parkingSpaceId": 123,
    "parkingSpaceLabel": "A-12",
    "parkingLotId": 789,
    "parkingLotName": "City Center Garage",
    "parkingLotAddress": "123 Main St, Downtown",
    "vehiclePlate": "ABC-1234",
    "vehicleType": "CAR",
    "userGroup": "PUBLIC",
    "startedAt": "2024-01-15T09:00:00+01:00",
    "endedAt": "2024-01-15T11:00:00+01:00",
    "billedAmount": 1000,
    "currency": "ALL",
    "status": "COMPLETED",
    "sessionReference": "PSN1A2B3C",
    "paymentMethodId": "pm_1234567890",
    "notes": "Leaving early",
    "createdAt": "2024-01-15T09:00:00Z",
    "updatedAt": "2024-01-15T11:00:00Z"
  }
}
```

#### Example Usage
```bash
curl -X POST "http://localhost:8080/api/v1/parking-sessions/1/stop" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "endTime": "2024-01-15T11:00:00+01:00",
    "notes": "Leaving early"
  }'
```

### 10. Cancel Session

**POST** `/api/v1/parking-sessions/{id}/cancel`

Cancels an active session.

#### Response
```json
{
  "success": true,
  "message": "Session cancelled successfully",
  "data": {
    "id": 1,
    "userId": 456,
    "userEmail": "user@example.com",
    "parkingSpaceId": 123,
    "parkingSpaceLabel": "A-12",
    "parkingLotId": 789,
    "parkingLotName": "City Center Garage",
    "parkingLotAddress": "123 Main St, Downtown",
    "vehiclePlate": "ABC-1234",
    "vehicleType": "CAR",
    "userGroup": "PUBLIC",
    "startedAt": "2024-01-15T09:00:00+01:00",
    "endedAt": "2024-01-15T09:30:00+01:00",
    "billedAmount": 500,
    "currency": "ALL",
    "status": "CANCELLED",
    "sessionReference": "PSN1A2B3C",
    "paymentMethodId": "pm_1234567890",
    "notes": "Parking near the entrance",
    "createdAt": "2024-01-15T09:00:00Z",
    "updatedAt": "2024-01-15T09:30:00Z"
  }
}
```

#### Example Usage
```bash
curl -X POST "http://localhost:8080/api/v1/parking-sessions/1/cancel" \
  -H "Authorization: Bearer your-jwt-token"
```

### 11. Extend Session

**POST** `/api/v1/parking-sessions/{id}/extend`

Extends an active session by changing the end time.

#### Query Parameters
- `newEndTime`: New end time for the session

#### Response
```json
{
  "success": true,
  "message": "Session extended successfully",
  "data": {
    "id": 1,
    "userId": 456,
    "userEmail": "user@example.com",
    "parkingSpaceId": 123,
    "parkingSpaceLabel": "A-12",
    "parkingLotId": 789,
    "parkingLotName": "City Center Garage",
    "parkingLotAddress": "123 Main St, Downtown",
    "vehiclePlate": "ABC-1234",
    "vehicleType": "CAR",
    "userGroup": "PUBLIC",
    "startedAt": "2024-01-15T09:00:00+01:00",
    "endedAt": "2024-01-15T13:00:00+01:00",
    "billedAmount": 2000,
    "currency": "ALL",
    "status": "ACTIVE",
    "sessionReference": "PSN1A2B3C",
    "paymentMethodId": "pm_1234567890",
    "notes": "Parking near the entrance",
    "createdAt": "2024-01-15T09:00:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
}
```

#### Example Usage
```bash
curl -X POST "http://localhost:8080/api/v1/parking-sessions/1/extend?newEndTime=2024-01-15T13:00:00%2B01:00" \
  -H "Authorization: Bearer your-jwt-token"
```

### 12. Get Session Quote

**POST** `/api/v1/parking-sessions/quote`

Gets a pricing quote for a potential session without starting it.

#### Request Body
```json
{
  "parkingSpaceId": 123,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC",
  "startTime": "2024-01-15T09:00:00+01:00",
  "endTime": "2024-01-15T11:00:00+01:00"
}
```

#### Response
```json
{
  "success": true,
  "message": "Session quote calculated successfully",
  "data": {
    "currency": "ALL",
    "amount": 1000,
    "breakdown": "{\"2024-01-15 09:00-11:00\": 1000}"
  }
}
```

#### Example Usage
```bash
curl -X POST "http://localhost:8080/api/v1/parking-sessions/quote" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "parkingSpaceId": 123,
    "vehicleType": "CAR",
    "userGroup": "PUBLIC",
    "startTime": "2024-01-15T09:00:00+01:00",
    "endTime": "2024-01-15T11:00:00+01:00"
  }'
```

### 13. Check Space Availability

**GET** `/api/v1/parking-sessions/availability`

Checks if a parking space is available for a specific time period.

#### Query Parameters
- `spaceId`: Parking space ID
- `startTime`: Start time (ISO 8601 format)
- `endTime`: End time (ISO 8601 format)

#### Response
```json
{
  "success": true,
  "message": "Availability checked successfully",
  "data": true
}
```

#### Example Usage
```bash
curl -X GET "http://localhost:8080/api/v1/parking-sessions/availability?spaceId=123&startTime=2024-01-15T09:00:00%2B01:00&endTime=2024-01-15T11:00:00%2B01:00" \
  -H "Authorization: Bearer your-jwt-token"
```

## Admin Session Endpoints

Base URL: `/api/v1/admin/parking-sessions`

### 1. Get All Sessions

**GET** `/api/v1/admin/parking-sessions`

Retrieves all sessions in the system (admin only).

#### Query Parameters
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `sortBy` (optional): Sort field (default: "startedAt")
- `sortDir` (optional): Sort direction - "asc" or "desc" (default: "desc")

#### Example Usage
```bash
curl -X GET "http://localhost:8080/api/v1/admin/parking-sessions?page=0&size=20&sortBy=startedAt&sortDir=desc" \
  -H "Authorization: Bearer admin-jwt-token"
```

### 2. Get Sessions by Space

**GET** `/api/v1/admin/parking-sessions/spaces/{spaceId}`

Retrieves all sessions for a specific parking space.

#### Query Parameters
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)

#### Example Usage
```bash
curl -X GET "http://localhost:8080/api/v1/admin/parking-sessions/spaces/123?page=0&size=10" \
  -H "Authorization: Bearer admin-jwt-token"
```

### 3. Get Sessions by Lot

**GET** `/api/v1/admin/parking-sessions/lots/{lotId}`

Retrieves all sessions for a specific parking lot.

#### Query Parameters
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)

#### Example Usage
```bash
curl -X GET "http://localhost:8080/api/v1/admin/parking-sessions/lots/789?page=0&size=10" \
  -H "Authorization: Bearer admin-jwt-token"
```

### 4. Get Sessions by User

**GET** `/api/v1/admin/parking-sessions/users/{userId}`

Retrieves all sessions for a specific user.

#### Query Parameters
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `sortBy` (optional): Sort field (default: "startedAt")
- `sortDir` (optional): Sort direction - "asc" or "desc" (default: "desc")

#### Example Usage
```bash
curl -X GET "http://localhost:8080/api/v1/admin/parking-sessions/users/456?page=0&size=10&sortBy=startedAt&sortDir=desc" \
  -H "Authorization: Bearer admin-jwt-token"
```

### 5. Update Expired Sessions

**POST** `/api/v1/admin/parking-sessions/maintenance/update-expired`

Updates all ACTIVE sessions that have been running too long to EXPIRED status.

#### Response
```json
{
  "success": true,
  "message": "Expired sessions updated successfully",
  "data": null
}
```

#### Example Usage
```bash
curl -X POST "http://localhost:8080/api/v1/admin/parking-sessions/maintenance/update-expired" \
  -H "Authorization: Bearer admin-jwt-token"
```

### 6. Update Completed Sessions

**POST** `/api/v1/admin/parking-sessions/maintenance/update-completed`

Performs maintenance on completed sessions.

#### Response
```json
{
  "success": true,
  "message": "Completed sessions updated successfully",
  "data": null
}
```

#### Example Usage
```bash
curl -X POST "http://localhost:8080/api/v1/admin/parking-sessions/maintenance/update-completed" \
  -H "Authorization: Bearer admin-jwt-token"
```

## Integration with Booking System

### Key Differences

| Feature | Booking System | Session System |
|---------|---------------|----------------|
| **Purpose** | Future reservations | Current parking |
| **Status** | UPCOMING → ACTIVE → COMPLETED | ACTIVE → COMPLETED |
| **Time** | Scheduled future times | Real-time parking |
| **Reference** | PCK + 6 chars | PSN + 6 chars |
| **Pricing** | Pre-calculated | Real-time calculation |

### Workflow Integration

1. **User books parking** → Creates booking (UPCOMING)
2. **User arrives** → **Automatically creates parking session** when booking becomes ACTIVE
3. **User parks** → Session continues (ACTIVE)
4. **User leaves** → Session ends (COMPLETED)

### Automatic Session Creation

When a booking transitions from UPCOMING to ACTIVE status:
- **Automatic**: A parking session is automatically created
- **Data Transfer**: Session inherits vehicle info, space, and user details from booking
- **Reference**: Session includes a note indicating it was auto-created from the booking
- **Error Handling**: If session creation fails, booking still becomes ACTIVE (error logged)

### API Integration Points

- Both systems use the same `PricingService`
- Both systems check space availability
- Both systems use the same user authentication
- Sessions can be created from existing bookings

## Error Handling

### Common Error Responses

#### 400 Bad Request
```json
{
  "success": false,
  "message": "Validation failed",
  "data": null
}
```

#### 401 Unauthorized
```json
{
  "success": false,
  "message": "User not authenticated",
  "data": null
}
```

#### 403 Forbidden
```json
{
  "success": false,
  "message": "Access denied: You can only view your own sessions",
  "data": null
}
```

#### 404 Not Found
```json
{
  "success": false,
  "message": "Parking session not found with id: 999",
  "data": null
}
```

#### 409 Conflict
```json
{
  "success": false,
  "message": "Parking space is not available",
  "data": null
}
```

#### 422 Unprocessable Entity
```json
{
  "success": false,
  "message": "Only active sessions can be updated",
  "data": null
}
```

## Examples

### Complete Session Flow

#### 1. Check Availability and Get Quote
```bash
# Check if space is available
curl -X GET "http://localhost:8080/api/v1/parking-sessions/availability?spaceId=123&startTime=2024-01-15T09:00:00%2B01:00&endTime=2024-01-15T11:00:00%2B01:00" \
  -H "Authorization: Bearer your-jwt-token"

# Get pricing quote
curl -X POST "http://localhost:8080/api/v1/parking-sessions/quote" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "parkingSpaceId": 123,
    "vehicleType": "CAR",
    "userGroup": "PUBLIC",
    "startTime": "2024-01-15T09:00:00+01:00",
    "endTime": "2024-01-15T11:00:00+01:00"
  }'
```

#### 2. Start Session
```bash
curl -X POST "http://localhost:8080/api/v1/parking-sessions" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "parkingSpaceId": 123,
    "vehiclePlate": "ABC-1234",
    "vehicleType": "CAR",
    "userGroup": "PUBLIC",
    "paymentMethodId": "pm_1234567890",
    "notes": "Parking near the entrance"
  }'
```

#### 3. Extend Session (if needed)
```bash
curl -X POST "http://localhost:8080/api/v1/parking-sessions/1/extend?newEndTime=2024-01-15T13:00:00%2B01:00" \
  -H "Authorization: Bearer your-jwt-token"
```

#### 4. Stop Session (when leaving)
```bash
curl -X POST "http://localhost:8080/api/v1/parking-sessions/1/stop" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "endTime": "2024-01-15T11:00:00+01:00",
    "notes": "Leaving early"
  }'
```

### Viewing Sessions

#### Get Active Sessions
```bash
curl -X GET "http://localhost:8080/api/v1/parking-sessions/active" \
  -H "Authorization: Bearer your-jwt-token"
```

#### Get Session History
```bash
curl -X GET "http://localhost:8080/api/v1/parking-sessions/history?page=0&size=20" \
  -H "Authorization: Bearer your-jwt-token"
```

#### Get Specific Session
```bash
curl -X GET "http://localhost:8080/api/v1/parking-sessions/1" \
  -H "Authorization: Bearer your-jwt-token"
```

### Admin Operations

#### Get All Sessions
```bash
curl -X GET "http://localhost:8080/api/v1/admin/parking-sessions?page=0&size=50&sortBy=startedAt&sortDir=desc" \
  -H "Authorization: Bearer admin-jwt-token"
```

#### Get Sessions for Specific Space
```bash
curl -X GET "http://localhost:8080/api/v1/admin/parking-sessions/spaces/123?page=0&size=10" \
  -H "Authorization: Bearer admin-jwt-token"
```

#### Run Maintenance Tasks
```bash
# Update expired sessions
curl -X POST "http://localhost:8080/api/v1/admin/parking-sessions/maintenance/update-expired" \
  -H "Authorization: Bearer admin-jwt-token"

# Update completed sessions
curl -X POST "http://localhost:8080/api/v1/admin/parking-sessions/maintenance/update-completed" \
  -H "Authorization: Bearer admin-jwt-token"
```

## Best Practices

### 1. Always Check Availability First
Before starting a session, always check space availability to avoid conflicts.

### 2. Get Quotes Before Starting
Use the quote endpoint to show users the cost before they start parking.

### 3. Handle Time Zones Properly
Always use ISO 8601 format with timezone information for all datetime fields.

### 4. Implement Proper Error Handling
Handle all possible error responses in your client application.

### 5. Use Pagination
For list endpoints, always implement pagination to avoid performance issues.

### 6. Regular Maintenance
Run the maintenance endpoints regularly to keep session statuses up to date.

### 7. Security Considerations
- Always validate user permissions before allowing operations
- Use HTTPS in production
- Implement rate limiting for API endpoints
- Log all session operations for audit purposes

### 8. Frontend Integration
- Store session references for easy lookup
- Implement real-time updates for session status changes
- Provide clear feedback for all user actions
- Handle network errors gracefully
- Show session duration and cost in real-time

### 9. Mobile App Integration
- Use background tasks to monitor active sessions
- Implement push notifications for session reminders
- Provide easy access to stop/extend session functionality
- Show parking location and directions

This comprehensive API provides all the functionality needed for a complete parking session management system, with proper security, validation, and user experience considerations.
