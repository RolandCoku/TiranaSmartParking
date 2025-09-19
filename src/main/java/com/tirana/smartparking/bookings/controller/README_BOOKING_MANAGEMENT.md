# Booking Management API Documentation

This document provides comprehensive documentation for the Booking Management API, including detailed examples for all endpoints.

## Table of Contents

- [Overview](#overview)
- [Authentication](#authentication)
- [User Booking Endpoints](#user-booking-endpoints)
- [Admin Booking Endpoints](#admin-booking-endpoints)
- [Booking Lifecycle](#booking-lifecycle)
- [Error Handling](#error-handling)
- [Examples](#examples)
- [Best Practices](#best-practices)

## Overview

The Booking Management API allows users to:
- Create, view, update, and delete parking bookings
- Manage booking lifecycle (start, complete, cancel, extend)
- Get pricing quotes before booking
- Check space availability
- View booking history and current bookings

All user operations automatically use the authenticated user's context, ensuring security and data isolation.

## Authentication

All endpoints require JWT authentication. Include the token in the Authorization header:

```bash
Authorization: Bearer <your-jwt-token>
```

## User Booking Endpoints

Base URL: `/api/v1/bookings`

### 1. Create Booking

**POST** `/api/v1/bookings`

Creates a new parking booking for the authenticated user.

#### Request Body
```json
{
  "parkingSpaceId": 123,
  "vehiclePlate": "ABC-1234",
  "vehicleType": "CAR",
  "userGroup": "PUBLIC",
  "startTime": "2024-01-15T09:00:00+01:00",
  "endTime": "2024-01-15T11:00:00+01:00",
  "paymentMethodId": "pm_1234567890",
  "notes": "Please park in the designated spot"
}
```

#### Response
```json
{
  "success": true,
  "message": "Booking created successfully",
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
    "startTime": "2024-01-15T09:00:00+01:00",
    "endTime": "2024-01-15T11:00:00+01:00",
    "totalPrice": 500,
    "currency": "ALL",
    "status": "UPCOMING",
    "bookingReference": "PCK1A2B3C",
    "paymentMethodId": "pm_1234567890",
    "notes": "Please park in the designated spot",
    "createdAt": "2024-01-14T10:30:00Z",
    "updatedAt": "2024-01-14T10:30:00Z"
  }
}
```

#### Example Usage
```bash
curl -X POST "http://localhost:8080/api/v1/bookings" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "parkingSpaceId": 123,
    "vehiclePlate": "ABC-1234",
    "vehicleType": "CAR",
    "userGroup": "PUBLIC",
    "startTime": "2024-01-15T09:00:00+01:00",
    "endTime": "2024-01-15T11:00:00+01:00",
    "paymentMethodId": "pm_1234567890",
    "notes": "Please park in the designated spot"
  }'
```

### 2. Get Booking by ID

**GET** `/api/v1/bookings/{id}`

Retrieves a specific booking by its ID.

#### Response
```json
{
  "success": true,
  "message": "Booking retrieved successfully",
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
    "startTime": "2024-01-15T09:00:00+01:00",
    "endTime": "2024-01-15T11:00:00+01:00",
    "totalPrice": 500,
    "currency": "ALL",
    "status": "UPCOMING",
    "bookingReference": "PCK1A2B3C",
    "paymentMethodId": "pm_1234567890",
    "notes": "Please park in the designated spot",
    "createdAt": "2024-01-14T10:30:00Z",
    "updatedAt": "2024-01-14T10:30:00Z"
  }
}
```

#### Example Usage
```bash
curl -X GET "http://localhost:8080/api/v1/bookings/1" \
  -H "Authorization: Bearer your-jwt-token"
```

### 3. Get Booking by Reference

**GET** `/api/v1/bookings/reference/{reference}`

Retrieves a booking by its reference number.

#### Example Usage
```bash
curl -X GET "http://localhost:8080/api/v1/bookings/reference/PCK1A2B3C" \
  -H "Authorization: Bearer your-jwt-token"
```

### 4. Get User Bookings

**GET** `/api/v1/bookings`

Retrieves paginated list of user's bookings.

#### Query Parameters
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `sortBy` (optional): Sort field (default: "startTime")
- `sortDir` (optional): Sort direction - "asc" or "desc" (default: "desc")

#### Response
```json
{
  "success": true,
  "message": "User bookings retrieved successfully",
  "data": {
    "content": [
      {
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
        "startTime": "2024-01-15T09:00:00+01:00",
        "endTime": "2024-01-15T11:00:00+01:00",
        "totalPrice": 500,
        "currency": "ALL",
        "status": "UPCOMING",
        "bookingReference": "PCK1A2B3C",
        "paymentMethodId": "pm_1234567890",
        "notes": "Please park in the designated spot",
        "createdAt": "2024-01-14T10:30:00Z",
        "updatedAt": "2024-01-14T10:30:00Z"
      }
    ],
    "page": 0,
    "size": 10,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false,
    "hasContent": true
  }
}
```

#### Example Usage
```bash
curl -X GET "http://localhost:8080/api/v1/bookings?page=0&size=10&sortBy=startTime&sortDir=desc" \
  -H "Authorization: Bearer your-jwt-token"
```

### 5. Get Current Bookings

**GET** `/api/v1/bookings/current`

Retrieves user's current bookings (ACTIVE and UPCOMING status).

#### Query Parameters
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)

#### Example Usage
```bash
curl -X GET "http://localhost:8080/api/v1/bookings/current?page=0&size=10" \
  -H "Authorization: Bearer your-jwt-token"
```

### 6. Get Booking History

**GET** `/api/v1/bookings/history`

Retrieves user's booking history (COMPLETED, CANCELLED, EXPIRED status).

#### Query Parameters
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)

#### Example Usage
```bash
curl -X GET "http://localhost:8080/api/v1/bookings/history?page=0&size=10" \
  -H "Authorization: Bearer your-jwt-token"
```

### 7. Update Booking

**PUT** `/api/v1/bookings/{id}`

Updates an existing booking. Only UPCOMING bookings can be updated.

#### Request Body
```json
{
  "vehiclePlate": "XYZ-9876",
  "vehicleType": "MOTORCYCLE",
  "userGroup": "STUDENT",
  "startTime": "2024-01-15T10:00:00+01:00",
  "endTime": "2024-01-15T12:00:00+01:00",
  "paymentMethodId": "pm_0987654321",
  "notes": "Updated notes"
}
```

#### Response
```json
{
  "success": true,
  "message": "Booking updated successfully",
  "data": {
    "id": 1,
    "userId": 456,
    "userEmail": "user@example.com",
    "parkingSpaceId": 123,
    "parkingSpaceLabel": "A-12",
    "parkingLotId": 789,
    "parkingLotName": "City Center Garage",
    "parkingLotAddress": "123 Main St, Downtown",
    "vehiclePlate": "XYZ-9876",
    "vehicleType": "MOTORCYCLE",
    "userGroup": "STUDENT",
    "startTime": "2024-01-15T10:00:00+01:00",
    "endTime": "2024-01-15T12:00:00+01:00",
    "totalPrice": 300,
    "currency": "ALL",
    "status": "UPCOMING",
    "bookingReference": "PCK1A2B3C",
    "paymentMethodId": "pm_0987654321",
    "notes": "Updated notes",
    "createdAt": "2024-01-14T10:30:00Z",
    "updatedAt": "2024-01-14T11:15:00Z"
  }
}
```

#### Example Usage
```bash
curl -X PUT "http://localhost:8080/api/v1/bookings/1" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "vehiclePlate": "XYZ-9876",
    "vehicleType": "MOTORCYCLE",
    "userGroup": "STUDENT",
    "startTime": "2024-01-15T10:00:00+01:00",
    "endTime": "2024-01-15T12:00:00+01:00",
    "paymentMethodId": "pm_0987654321",
    "notes": "Updated notes"
  }'
```

### 8. Delete Booking

**DELETE** `/api/v1/bookings/{id}`

Deletes a booking. Only UPCOMING bookings can be deleted.

#### Response
```json
{
  "success": true,
  "message": "Booking deleted successfully",
  "data": null
}
```

#### Example Usage
```bash
curl -X DELETE "http://localhost:8080/api/v1/bookings/1" \
  -H "Authorization: Bearer your-jwt-token"
```

### 9. Cancel Booking

**POST** `/api/v1/bookings/{id}/cancel`

Cancels an existing booking. Only UPCOMING or ACTIVE bookings can be cancelled.

#### Response
```json
{
  "success": true,
  "message": "Booking cancelled successfully",
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
    "startTime": "2024-01-15T09:00:00+01:00",
    "endTime": "2024-01-15T11:00:00+01:00",
    "totalPrice": 500,
    "currency": "ALL",
    "status": "CANCELLED",
    "bookingReference": "PCK1A2B3C",
    "paymentMethodId": "pm_1234567890",
    "notes": "Please park in the designated spot",
    "createdAt": "2024-01-14T10:30:00Z",
    "updatedAt": "2024-01-14T12:00:00Z"
  }
}
```

#### Example Usage
```bash
curl -X POST "http://localhost:8080/api/v1/bookings/1/cancel" \
  -H "Authorization: Bearer your-jwt-token"
```

### 10. Start Booking

**POST** `/api/v1/bookings/{id}/start`

Starts a booking (changes status from UPCOMING to ACTIVE). **Automatically creates a parking session** when the booking becomes active.

#### Important Notes
- When a booking is started, the system automatically creates a corresponding parking session
- The parking session uses the same vehicle information and parking space from the booking
- If session creation fails, the booking will still be marked as ACTIVE (error is logged)
- The parking session will have a note indicating it was auto-created from the booking

#### Response
```json
{
  "success": true,
  "message": "Booking started successfully",
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
    "startTime": "2024-01-15T09:00:00+01:00",
    "endTime": "2024-01-15T11:00:00+01:00",
    "totalPrice": 500,
    "currency": "ALL",
    "status": "ACTIVE",
    "bookingReference": "PCK1A2B3C",
    "paymentMethodId": "pm_1234567890",
    "notes": "Please park in the designated spot",
    "createdAt": "2024-01-14T10:30:00Z",
    "updatedAt": "2024-01-15T09:05:00Z"
  }
}
```

#### Example Usage
```bash
curl -X POST "http://localhost:8080/api/v1/bookings/1/start" \
  -H "Authorization: Bearer your-jwt-token"
```

### 11. Complete Booking

**POST** `/api/v1/bookings/{id}/complete`

Completes a booking (changes status from ACTIVE to COMPLETED).

#### Response
```json
{
  "success": true,
  "message": "Booking completed successfully",
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
    "startTime": "2024-01-15T09:00:00+01:00",
    "endTime": "2024-01-15T11:00:00+01:00",
    "totalPrice": 500,
    "currency": "ALL",
    "status": "COMPLETED",
    "bookingReference": "PCK1A2B3C",
    "paymentMethodId": "pm_1234567890",
    "notes": "Please park in the designated spot",
    "createdAt": "2024-01-14T10:30:00Z",
    "updatedAt": "2024-01-15T11:10:00Z"
  }
}
```

#### Example Usage
```bash
curl -X POST "http://localhost:8080/api/v1/bookings/1/complete" \
  -H "Authorization: Bearer your-jwt-token"
```

### 12. Extend Booking

**POST** `/api/v1/bookings/{id}/extend`

Extends an active booking by changing the end time.

#### Query Parameters
- `newEndTime`: New end time for the booking

#### Response
```json
{
  "success": true,
  "message": "Booking extended successfully",
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
    "startTime": "2024-01-15T09:00:00+01:00",
    "endTime": "2024-01-15T13:00:00+01:00",
    "totalPrice": 800,
    "currency": "ALL",
    "status": "ACTIVE",
    "bookingReference": "PCK1A2B3C",
    "paymentMethodId": "pm_1234567890",
    "notes": "Please park in the designated spot",
    "createdAt": "2024-01-14T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
}
```

#### Example Usage
```bash
curl -X POST "http://localhost:8080/api/v1/bookings/1/extend?newEndTime=2024-01-15T13:00:00%2B01:00" \
  -H "Authorization: Bearer your-jwt-token"
```

### 13. Get Booking Quote

**POST** `/api/v1/bookings/quote`

Gets a pricing quote for a potential booking without creating it.

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
  "message": "Booking quote calculated successfully",
  "data": {
    "currency": "ALL",
    "amount": 500,
    "breakdown": "{\"2024-01-15 09:00-11:00\": 500}"
  }
}
```

#### Example Usage
```bash
curl -X POST "http://localhost:8080/api/v1/bookings/quote" \
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

### 14. Check Space Availability

**GET** `/api/v1/bookings/availability`

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
curl -X GET "http://localhost:8080/api/v1/bookings/availability?spaceId=123&startTime=2024-01-15T09:00:00%2B01:00&endTime=2024-01-15T11:00:00%2B01:00" \
  -H "Authorization: Bearer your-jwt-token"
```

## Admin Booking Endpoints

Base URL: `/api/v1/admin/bookings`

### 1. Get All Bookings

**GET** `/api/v1/admin/bookings`

Retrieves all bookings in the system (admin only).

#### Query Parameters
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `sortBy` (optional): Sort field (default: "createdAt")
- `sortDir` (optional): Sort direction - "asc" or "desc" (default: "desc")

#### Example Usage
```bash
curl -X GET "http://localhost:8080/api/v1/admin/bookings?page=0&size=20&sortBy=createdAt&sortDir=desc" \
  -H "Authorization: Bearer admin-jwt-token"
```

### 2. Get Bookings by Space

**GET** `/api/v1/admin/bookings/spaces/{spaceId}`

Retrieves all bookings for a specific parking space.

#### Query Parameters
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)

#### Example Usage
```bash
curl -X GET "http://localhost:8080/api/v1/admin/bookings/spaces/123?page=0&size=10" \
  -H "Authorization: Bearer admin-jwt-token"
```

### 3. Get Bookings by Lot

**GET** `/api/v1/admin/bookings/lots/{lotId}`

Retrieves all bookings for a specific parking lot.

#### Query Parameters
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)

#### Example Usage
```bash
curl -X GET "http://localhost:8080/api/v1/admin/bookings/lots/789?page=0&size=10" \
  -H "Authorization: Bearer admin-jwt-token"
```

### 4. Get Bookings by User

**GET** `/api/v1/admin/bookings/users/{userId}`

Retrieves all bookings for a specific user.

#### Query Parameters
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `sortBy` (optional): Sort field (default: "startTime")
- `sortDir` (optional): Sort direction - "asc" or "desc" (default: "desc")

#### Example Usage
```bash
curl -X GET "http://localhost:8080/api/v1/admin/bookings/users/456?page=0&size=10&sortBy=startTime&sortDir=desc" \
  -H "Authorization: Bearer admin-jwt-token"
```

### 5. Update Expired Bookings

**POST** `/api/v1/admin/bookings/maintenance/update-expired`

Updates all UPCOMING bookings that have passed their start time to EXPIRED status.

#### Response
```json
{
  "success": true,
  "message": "Expired bookings updated successfully",
  "data": null
}
```

#### Example Usage
```bash
curl -X POST "http://localhost:8080/api/v1/admin/bookings/maintenance/update-expired" \
  -H "Authorization: Bearer admin-jwt-token"
```

### 6. Update Completed Bookings

**POST** `/api/v1/admin/bookings/maintenance/update-completed`

Updates all ACTIVE bookings that have passed their end time to COMPLETED status.

#### Response
```json
{
  "success": true,
  "message": "Completed bookings updated successfully",
  "data": null
}
```

#### Example Usage
```bash
curl -X POST "http://localhost:8080/api/v1/admin/bookings/maintenance/update-completed" \
  -H "Authorization: Bearer admin-jwt-token"
```

## Booking Lifecycle

The booking system follows a clear lifecycle:

```
UPCOMING → ACTIVE → COMPLETED
    ↓         ↓
CANCELLED  CANCELLED
    ↓
EXPIRED (automatic)
```

### Status Transitions

1. **UPCOMING**: Initial state when booking is created
   - Can be updated, cancelled, or deleted
   - Automatically becomes EXPIRED if not started on time
   - Can be started when it's time to park

2. **ACTIVE**: Booking is currently in use
   - Can be cancelled or completed
   - Can be extended
   - Automatically becomes COMPLETED if past end time
   - **Automatically creates a parking session** when transitioned from UPCOMING

3. **COMPLETED**: Booking has finished successfully
   - Final state, no further changes allowed

4. **CANCELLED**: Booking was cancelled by user
   - Final state, no further changes allowed

5. **EXPIRED**: Booking was not started on time
   - Final state, no further changes allowed

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
  "message": "Access denied: You can only view your own bookings",
  "data": null
}
```

#### 404 Not Found
```json
{
  "success": false,
  "message": "Booking not found with id: 999",
  "data": null
}
```

#### 409 Conflict
```json
{
  "success": false,
  "message": "Parking space is not available for the selected time period",
  "data": null
}
```

#### 422 Unprocessable Entity
```json
{
  "success": false,
  "message": "Only upcoming bookings can be updated",
  "data": null
}
```

## Examples

### Complete Booking Flow

#### 1. Check Availability and Get Quote
```bash
# Check if space is available
curl -X GET "http://localhost:8080/api/v1/bookings/availability?spaceId=123&startTime=2024-01-15T09:00:00%2B01:00&endTime=2024-01-15T11:00:00%2B01:00" \
  -H "Authorization: Bearer your-jwt-token"

# Get pricing quote
curl -X POST "http://localhost:8080/api/v1/bookings/quote" \
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

#### 2. Create Booking
```bash
curl -X POST "http://localhost:8080/api/v1/bookings" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "parkingSpaceId": 123,
    "vehiclePlate": "ABC-1234",
    "vehicleType": "CAR",
    "userGroup": "PUBLIC",
    "startTime": "2024-01-15T09:00:00+01:00",
    "endTime": "2024-01-15T11:00:00+01:00",
    "paymentMethodId": "pm_1234567890",
    "notes": "Please park in the designated spot"
  }'
```

#### 3. Start Booking (when arriving)
```bash
curl -X POST "http://localhost:8080/api/v1/bookings/1/start" \
  -H "Authorization: Bearer your-jwt-token"
```

**Note**: Starting a booking automatically creates a parking session. The session will be visible in the parking sessions API.

#### 4. Extend Booking (if needed)
```bash
curl -X POST "http://localhost:8080/api/v1/bookings/1/extend?newEndTime=2024-01-15T13:00:00%2B01:00" \
  -H "Authorization: Bearer your-jwt-token"
```

#### 5. Complete Booking (when leaving)
```bash
curl -X POST "http://localhost:8080/api/v1/bookings/1/complete" \
  -H "Authorization: Bearer your-jwt-token"
```

### Viewing Bookings

#### Get Current Bookings
```bash
curl -X GET "http://localhost:8080/api/v1/bookings/current" \
  -H "Authorization: Bearer your-jwt-token"
```

#### Get Booking History
```bash
curl -X GET "http://localhost:8080/api/v1/bookings/history?page=0&size=20" \
  -H "Authorization: Bearer your-jwt-token"
```

#### Get Specific Booking
```bash
curl -X GET "http://localhost:8080/api/v1/bookings/1" \
  -H "Authorization: Bearer your-jwt-token"
```

### Admin Operations

#### Get All Bookings
```bash
curl -X GET "http://localhost:8080/api/v1/admin/bookings?page=0&size=50&sortBy=createdAt&sortDir=desc" \
  -H "Authorization: Bearer admin-jwt-token"
```

#### Get Bookings for Specific Space
```bash
curl -X GET "http://localhost:8080/api/v1/admin/bookings/spaces/123?page=0&size=10" \
  -H "Authorization: Bearer admin-jwt-token"
```

#### Run Maintenance Tasks
```bash
# Update expired bookings
curl -X POST "http://localhost:8080/api/v1/admin/bookings/maintenance/update-expired" \
  -H "Authorization: Bearer admin-jwt-token"

# Update completed bookings
curl -X POST "http://localhost:8080/api/v1/admin/bookings/maintenance/update-completed" \
  -H "Authorization: Bearer admin-jwt-token"
```

## Best Practices

### 1. Always Check Availability First
Before creating a booking, always check space availability to avoid conflicts.

### 2. Get Quotes Before Booking
Use the quote endpoint to show users the cost before they commit to a booking.

### 3. Handle Time Zones Properly
Always use ISO 8601 format with timezone information for all datetime fields.

### 4. Implement Proper Error Handling
Handle all possible error responses in your client application.

### 5. Use Pagination
For list endpoints, always implement pagination to avoid performance issues.

### 6. Regular Maintenance
Run the maintenance endpoints regularly to keep booking statuses up to date.

### 7. Security Considerations
- Always validate user permissions before allowing operations
- Use HTTPS in production
- Implement rate limiting for API endpoints
- Log all booking operations for audit purposes

### 8. Frontend Integration
- Store booking references for easy lookup
- Implement real-time updates for booking status changes
- Provide clear feedback for all user actions
- Handle network errors gracefully

This comprehensive API provides all the functionality needed for a complete parking booking system, with proper security, validation, and user experience considerations.
