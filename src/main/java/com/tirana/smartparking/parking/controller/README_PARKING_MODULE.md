# Smart Parking Module - Complete API Documentation

This document provides comprehensive documentation for the entire Smart Parking Module, including all subsystems: Rate Management, Pricing, Booking Management, and Session Management.

## Table of Contents

- [Overview](#overview)
- [Module Architecture](#module-architecture)
- [Authentication](#authentication)
- [Rate Management System](#rate-management-system)
- [Pricing System](#pricing-system)
- [Booking Management System](#booking-management-system)
- [Session Management System](#session-management-system)
- [Integration Examples](#integration-examples)
- [Error Handling](#error-handling)
- [Best Practices](#best-practices)
- [Database Schema](#database-schema)

## Overview

The Smart Parking Module is a comprehensive system for managing parking operations including:

- **Rate Management**: Define and manage parking rates with complex pricing rules
- **Pricing Engine**: Real-time cost calculation based on various factors
- **Booking System**: Reserve parking spaces for future use
- **Session Management**: Handle real-time parking sessions

### Key Features

- **Multi-tier Pricing**: Support for different rate types (hourly, daily, tiered, etc.)
- **Space Management**: Handle both lot-based and standalone parking spaces
- **User Management**: Support for different user groups and vehicle types
- **Real-time Operations**: Live session management and pricing
- **Admin Controls**: Comprehensive administrative functions
- **Security**: JWT-based authentication with role-based access

## Module Architecture

```
Parking Module
├── Rate Management
│   ├── Rate Plans (PER_HOUR, TIERED, TIME_OF_DAY, etc.)
│   ├── Rate Rules (time-based, vehicle-specific, user-specific)
│   ├── Lot Rate Assignments
│   └── Space Rate Overrides
├── Pricing Engine
│   ├── Visit Slicing (time-based calculation)
│   ├── Rule Matching
│   └── Cost Calculation
├── Booking System
│   ├── Future Reservations
│   ├── Availability Checking
│   └── Booking Lifecycle
└── Session Management
    ├── Real-time Parking
    ├── Session Lifecycle
    └── Live Pricing
```

## Authentication

All endpoints require JWT authentication. Include the token in the Authorization header:

```bash
Authorization: Bearer <your-jwt-token>
```

### User Roles and Permissions

- **USER**: Can manage their own bookings and sessions
- **ADMIN**: Full access to all operations
- **OPERATOR**: Limited administrative access

## Rate Management System

The rate management system allows you to define complex pricing structures for parking spaces.

### Core Concepts

#### Rate Plans
High-level pricing strategies:
- `PER_HOUR`: Hourly billing
- `TIERED`: Progressive pricing tiers
- `TIME_OF_DAY`: Time-based pricing
- `DAY_OF_WEEK`: Day-specific pricing
- `FLAT_PER_ENTRY`: Fixed price per entry
- `FREE`: No charge
- `DYNAMIC`: Demand-based pricing

#### Rate Rules
Specific conditions within a rate plan:
- Time windows (start/end times)
- Day of week restrictions
- Vehicle type filters
- User group discounts
- Session duration limits

### Rate Management Endpoints

#### 1. Create Rate Plan

**POST** `/api/v1/admin/rate-plans`

```bash
curl -X POST "http://localhost:8080/api/v1/admin/rate-plans" \
  -H "Authorization: Bearer admin-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "City Center Hourly",
    "description": "Standard hourly rates for city center",
    "type": "PER_HOUR",
    "currency": "ALL",
    "timeZone": "Europe/Tirana",
    "graceMinutes": 15,
    "incrementMinutes": 15,
    "dailyCap": 2000,
    "isActive": true
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Rate plan created successfully",
  "data": {
    "id": 1,
    "name": "City Center Hourly",
    "description": "Standard hourly rates for city center",
    "type": "PER_HOUR",
    "currency": "ALL",
    "timeZone": "Europe/Tirana",
    "graceMinutes": 15,
    "incrementMinutes": 15,
    "dailyCap": 2000,
    "isActive": true,
    "createdAt": "2024-01-15T09:00:00Z",
    "updatedAt": "2024-01-15T09:00:00Z"
  }
}
```

#### 2. Create Rate Rule

**POST** `/api/v1/admin/rate-plans/{planId}/rules`

```bash
curl -X POST "http://localhost:8080/api/v1/admin/rate-plans/1/rules" \
  -H "Authorization: Bearer admin-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "startTime": "09:00",
    "endTime": "18:00",
    "dayOfWeek": "MONDAY",
    "vehicleType": "CAR",
    "userGroup": "PUBLIC",
    "pricePerHour": 500,
    "priceFlat": null
  }'
```

#### 3. Assign Rate Plan to Parking Lot

**POST** `/api/v1/admin/lot-rate-assignments`

```bash
curl -X POST "http://localhost:8080/api/v1/admin/lot-rate-assignments" \
  -H "Authorization: Bearer admin-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "parkingLotId": 123,
    "ratePlanId": 1,
    "priority": 100,
    "effectiveFrom": "2024-01-15T00:00:00+01:00",
    "effectiveTo": null
  }'
```

#### 4. Override Rate Plan for Specific Space

**POST** `/api/v1/admin/space-rate-overrides`

```bash
curl -X POST "http://localhost:8080/api/v1/admin/space-rate-overrides" \
  -H "Authorization: Bearer admin-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "parkingSpaceId": 456,
    "ratePlanId": 2,
    "priority": 200,
    "effectiveFrom": "2024-01-15T00:00:00+01:00",
    "effectiveTo": null
  }'
```

### Rate Management Examples

#### Complex Tiered Pricing Setup

```bash
# 1. Create tiered rate plan
curl -X POST "http://localhost:8080/api/v1/admin/rate-plans" \
  -H "Authorization: Bearer admin-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tiered Airport Parking",
    "description": "Progressive pricing for airport parking",
    "type": "TIERED",
    "currency": "ALL",
    "timeZone": "Europe/Tirana",
    "graceMinutes": 30,
    "incrementMinutes": 60,
    "dailyCap": 5000,
    "isActive": true
  }'

# 2. Create tier rules
# First hour: 200 ALL
curl -X POST "http://localhost:8080/api/v1/admin/rate-plans/3/rules" \
  -H "Authorization: Bearer admin-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "startMinute": 0,
    "endMinute": 60,
    "pricePerHour": 200,
    "vehicleType": "CAR"
  }'

# Second hour: 150 ALL
curl -X POST "http://localhost:8080/api/v1/admin/rate-plans/3/rules" \
  -H "Authorization: Bearer admin-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "startMinute": 60,
    "endMinute": 120,
    "pricePerHour": 150,
    "vehicleType": "CAR"
  }'

# Additional hours: 100 ALL
curl -X POST "http://localhost:8080/api/v1/admin/rate-plans/3/rules" \
  -H "Authorization: Bearer admin-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "startMinute": 120,
    "endMinute": null,
    "pricePerHour": 100,
    "vehicleType": "CAR"
  }'
```

#### Time-of-Day Pricing Setup

```bash
# 1. Create time-of-day rate plan
curl -X POST "http://localhost:8080/api/v1/admin/rate-plans" \
  -H "Authorization: Bearer admin-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Peak Hours Pricing",
    "description": "Higher rates during peak hours",
    "type": "TIME_OF_DAY",
    "currency": "ALL",
    "timeZone": "Europe/Tirana",
    "graceMinutes": 15,
    "incrementMinutes": 30,
    "dailyCap": 3000,
    "isActive": true
  }'

# 2. Peak hours (8 AM - 6 PM): 300 ALL/hour
curl -X POST "http://localhost:8080/api/v1/admin/rate-plans/4/rules" \
  -H "Authorization: Bearer admin-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "startTime": "08:00",
    "endTime": "18:00",
    "pricePerHour": 300,
    "vehicleType": "CAR"
  }'

# 3. Off-peak hours (6 PM - 8 AM): 150 ALL/hour
curl -X POST "http://localhost:8080/api/v1/admin/rate-plans/4/rules" \
  -H "Authorization: Bearer admin-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "startTime": "18:00",
    "endTime": "08:00",
    "pricePerHour": 150,
    "vehicleType": "CAR"
  }'
```

## Pricing System

The pricing system calculates real-time costs based on rate plans and rules.

### Pricing Endpoints

#### 1. Get Pricing Quote

**POST** `/api/v1/pricing/quote`

```bash
curl -X POST "http://localhost:8080/api/v1/pricing/quote" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "lotId": 123,
    "spaceId": 456,
    "vehicleType": "CAR",
    "userGroup": "PUBLIC",
    "startTime": "2024-01-15T09:00:00+01:00",
    "endTime": "2024-01-15T11:00:00+01:00"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Pricing quote calculated successfully",
  "data": {
    "currency": "ALL",
    "amount": 1000,
    "breakdown": "{\"2024-01-15 09:00-11:00\": 1000}"
  }
}
```

#### 2. Get Standalone Space Quote

**POST** `/api/v1/pricing/standalone-quote`

```bash
curl -X POST "http://localhost:8080/api/v1/pricing/standalone-quote" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "spaceId": 789,
    "vehicleType": "MOTORCYCLE",
    "userGroup": "STUDENT",
    "startTime": "2024-01-15T10:00:00+01:00",
    "endTime": "2024-01-15T12:00:00+01:00"
  }'
```

### Pricing Examples

#### Multi-Day Parking Calculation

```bash
curl -X POST "http://localhost:8080/api/v1/pricing/quote" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "lotId": 123,
    "spaceId": 456,
    "vehicleType": "CAR",
    "userGroup": "PUBLIC",
    "startTime": "2024-01-15T09:00:00+01:00",
    "endTime": "2024-01-17T09:00:00+01:00"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Pricing quote calculated successfully",
  "data": {
    "currency": "ALL",
    "amount": 4800,
    "breakdown": "{\"2024-01-15 09:00-18:00\": 2700, \"2024-01-15 18:00-2024-01-16 08:00\": 2100, \"2024-01-16 08:00-18:00\": 3000, \"2024-01-16 18:00-2024-01-17 08:00\": 2100, \"2024-01-17 08:00-09:00\": 300}"
  }
}
```

## Booking Management System

The booking system handles future parking reservations.

### Booking Lifecycle

```
UPCOMING → ACTIVE → COMPLETED
    ↓
CANCELLED
    ↓
EXPIRED (automatic)
```

### Booking Endpoints

#### 1. Create Booking

**POST** `/api/v1/bookings`

```bash
curl -X POST "http://localhost:8080/api/v1/bookings" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "parkingSpaceId": 123,
    "vehiclePlate": "ABC-1234",
    "vehicleType": "CAR",
    "userGroup": "PUBLIC",
    "startTime": "2024-01-16T09:00:00+01:00",
    "endTime": "2024-01-16T11:00:00+01:00",
    "paymentMethodId": "pm_1234567890",
    "notes": "Business meeting parking"
  }'
```

**Response:**
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
    "startTime": "2024-01-16T09:00:00+01:00",
    "endTime": "2024-01-16T11:00:00+01:00",
    "totalPrice": 1000,
    "currency": "ALL",
    "status": "UPCOMING",
    "bookingReference": "PCK1A2B3C",
    "paymentMethodId": "pm_1234567890",
    "notes": "Business meeting parking",
    "createdAt": "2024-01-15T10:00:00Z",
    "updatedAt": "2024-01-15T10:00:00Z"
  }
}
```

#### 2. Get Booking Quote

**POST** `/api/v1/bookings/quote`

```bash
curl -X POST "http://localhost:8080/api/v1/bookings/quote" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "parkingSpaceId": 123,
    "vehicleType": "CAR",
    "userGroup": "PUBLIC",
    "startTime": "2024-01-16T09:00:00+01:00",
    "endTime": "2024-01-16T11:00:00+01:00"
  }'
```

#### 3. Start Booking (Convert to Session)

**POST** `/api/v1/bookings/{id}/start`

```bash
curl -X POST "http://localhost:8080/api/v1/bookings/1/start" \
  -H "Authorization: Bearer your-jwt-token"
```

#### 4. Cancel Booking

**POST** `/api/v1/bookings/{id}/cancel`

```bash
curl -X POST "http://localhost:8080/api/v1/bookings/1/cancel" \
  -H "Authorization: Bearer your-jwt-token"
```

#### 5. Get User Bookings

**GET** `/api/v1/bookings`

```bash
curl -X GET "http://localhost:8080/api/v1/bookings?page=0&size=10&sortBy=startTime&sortDir=asc" \
  -H "Authorization: Bearer your-jwt-token"
```

### Booking Examples

#### Complete Booking Flow

```bash
# 1. Check availability
curl -X GET "http://localhost:8080/api/v1/bookings/availability?spaceId=123&startTime=2024-01-16T09:00:00%2B01:00&endTime=2024-01-16T11:00:00%2B01:00" \
  -H "Authorization: Bearer your-jwt-token"

# 2. Get pricing quote
curl -X POST "http://localhost:8080/api/v1/bookings/quote" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "parkingSpaceId": 123,
    "vehicleType": "CAR",
    "userGroup": "PUBLIC",
    "startTime": "2024-01-16T09:00:00+01:00",
    "endTime": "2024-01-16T11:00:00+01:00"
  }'

# 3. Create booking
curl -X POST "http://localhost:8080/api/v1/bookings" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "parkingSpaceId": 123,
    "vehiclePlate": "ABC-1234",
    "vehicleType": "CAR",
    "userGroup": "PUBLIC",
    "startTime": "2024-01-16T09:00:00+01:00",
    "endTime": "2024-01-16T11:00:00+01:00",
    "paymentMethodId": "pm_1234567890",
    "notes": "Business meeting parking"
  }'

# 4. When arriving, start the booking
curl -X POST "http://localhost:8080/api/v1/bookings/1/start" \
  -H "Authorization: Bearer your-jwt-token"

# 5. When leaving, complete the booking
curl -X POST "http://localhost:8080/api/v1/bookings/1/complete" \
  -H "Authorization: Bearer your-jwt-token"
```

## Session Management System

The session management system handles real-time parking operations.

### Session Lifecycle

```
ACTIVE → COMPLETED
   ↓
CANCELLED
   ↓
EXPIRED (automatic)
```

### Session Endpoints

#### 1. Start Parking Session

**POST** `/api/v1/parking-sessions`

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
    "notes": "Immediate parking"
  }'
```

**Response:**
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
    "notes": "Immediate parking",
    "createdAt": "2024-01-15T09:00:00Z",
    "updatedAt": "2024-01-15T09:00:00Z"
  }
}
```

#### 2. Stop Session

**POST** `/api/v1/parking-sessions/{id}/stop`

```bash
curl -X POST "http://localhost:8080/api/v1/parking-sessions/1/stop" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "endTime": "2024-01-15T11:00:00+01:00",
    "notes": "Leaving early"
  }'
```

#### 3. Extend Session

**POST** `/api/v1/parking-sessions/{id}/extend`

```bash
curl -X POST "http://localhost:8080/api/v1/parking-sessions/1/extend?newEndTime=2024-01-15T13:00:00%2B01:00" \
  -H "Authorization: Bearer your-jwt-token"
```

#### 4. Get Active Sessions

**GET** `/api/v1/parking-sessions/active`

```bash
curl -X GET "http://localhost:8080/api/v1/parking-sessions/active?page=0&size=10" \
  -H "Authorization: Bearer your-jwt-token"
```

### Session Examples

#### Complete Session Flow

```bash
# 1. Check space availability
curl -X GET "http://localhost:8080/api/v1/parking-sessions/availability?spaceId=123&startTime=2024-01-15T09:00:00%2B01:00&endTime=2024-01-15T11:00:00%2B01:00" \
  -H "Authorization: Bearer your-jwt-token"

# 2. Get pricing quote
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

# 3. Start session
curl -X POST "http://localhost:8080/api/v1/parking-sessions" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "parkingSpaceId": 123,
    "vehiclePlate": "ABC-1234",
    "vehicleType": "CAR",
    "userGroup": "PUBLIC",
    "paymentMethodId": "pm_1234567890",
    "notes": "Immediate parking"
  }'

# 4. Extend session if needed
curl -X POST "http://localhost:8080/api/v1/parking-sessions/1/extend?newEndTime=2024-01-15T13:00:00%2B01:00" \
  -H "Authorization: Bearer your-jwt-token"

# 5. Stop session when leaving
curl -X POST "http://localhost:8080/api/v1/parking-sessions/1/stop" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "endTime": "2024-01-15T11:00:00+01:00",
    "notes": "Leaving early"
  }'
```

## Integration Examples

### Booking to Session Conversion

```bash
# 1. User books parking for tomorrow
curl -X POST "http://localhost:8080/api/v1/bookings" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "parkingSpaceId": 123,
    "vehiclePlate": "ABC-1234",
    "vehicleType": "CAR",
    "userGroup": "PUBLIC",
    "startTime": "2024-01-16T09:00:00+01:00",
    "endTime": "2024-01-16T11:00:00+01:00",
    "paymentMethodId": "pm_1234567890",
    "notes": "Business meeting parking"
  }'

# 2. User arrives and starts the booking (automatically creates parking session)
curl -X POST "http://localhost:8080/api/v1/bookings/1/start" \
  -H "Authorization: Bearer your-jwt-token"

# 3. User leaves and completes the session
curl -X POST "http://localhost:8080/api/v1/bookings/1/complete" \
  -H "Authorization: Bearer your-jwt-token"
```

### Standalone Space Management

```bash
# 1. Create rate plan for standalone spaces
curl -X POST "http://localhost:8080/api/v1/admin/rate-plans" \
  -H "Authorization: Bearer admin-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Street Parking",
    "description": "On-street parking rates",
    "type": "PER_HOUR",
    "currency": "ALL",
    "timeZone": "Europe/Tirana",
    "graceMinutes": 15,
    "incrementMinutes": 30,
    "dailyCap": 1500,
    "isActive": true
  }'

# 2. Assign rate plan to standalone space
curl -X POST "http://localhost:8080/api/v1/admin/space-rate-overrides" \
  -H "Authorization: Bearer admin-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "parkingSpaceId": 999,
    "ratePlanId": 5,
    "priority": 100,
    "effectiveFrom": "2024-01-15T00:00:00+01:00",
    "effectiveTo": null
  }'

# 3. Get quote for standalone space
curl -X POST "http://localhost:8080/api/v1/pricing/standalone-quote" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "spaceId": 999,
    "vehicleType": "MOTORCYCLE",
    "userGroup": "STUDENT",
    "startTime": "2024-01-15T10:00:00+01:00",
    "endTime": "2024-01-15T12:00:00+01:00"
  }'
```

### Multi-Language Support

```bash
# Create rate plan with Albanian currency
curl -X POST "http://localhost:8080/api/v1/admin/rate-plans" \
  -H "Authorization: Bearer admin-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Parking Urban",
    "description": "Tarifat standarde për parking urban",
    "type": "PER_HOUR",
    "currency": "ALL",
    "timeZone": "Europe/Tirana",
    "graceMinutes": 15,
    "incrementMinutes": 30,
    "dailyCap": 2000,
    "isActive": true
  }'
```

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
  "message": "Parking space not found with id: 999",
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

### Error Handling Examples

#### Handle Booking Conflicts

```bash
# Try to book already occupied space
curl -X POST "http://localhost:8080/api/v1/bookings" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "parkingSpaceId": 123,
    "vehiclePlate": "XYZ-9876",
    "vehicleType": "CAR",
    "userGroup": "PUBLIC",
    "startTime": "2024-01-15T09:00:00+01:00",
    "endTime": "2024-01-15T11:00:00+01:00",
    "paymentMethodId": "pm_0987654321",
    "notes": "Conflicting booking"
  }'

# Response: 409 Conflict
{
  "success": false,
  "message": "Parking space is not available for the selected time period",
  "data": null
}
```

#### Handle Invalid Session Operations

```bash
# Try to stop already completed session
curl -X POST "http://localhost:8080/api/v1/parking-sessions/1/stop" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "endTime": "2024-01-15T11:00:00+01:00",
    "notes": "Already completed"
  }'

# Response: 422 Unprocessable Entity
{
  "success": false,
  "message": "Only active sessions can be stopped",
  "data": null
}
```

## Best Practices

### 1. Always Check Availability First
Before creating bookings or sessions, always check space availability to avoid conflicts.

### 2. Get Quotes Before Committing
Use quote endpoints to show users the cost before they commit to bookings or sessions.

### 3. Handle Time Zones Properly
Always use ISO 8601 format with timezone information for all datetime fields.

### 4. Implement Proper Error Handling
Handle all possible error responses in your client application.

### 5. Use Pagination
For list endpoints, always implement pagination to avoid performance issues.

### 6. Regular Maintenance
Run maintenance endpoints regularly to keep data consistent.

### 7. Security Considerations
- Always validate user permissions before allowing operations
- Use HTTPS in production
- Implement rate limiting for API endpoints
- Log all operations for audit purposes

### 8. Frontend Integration
- Store references (PCK/PSN) for easy lookup
- Implement real-time updates for status changes
- Provide clear feedback for all user actions
- Handle network errors gracefully
- Show duration and cost in real-time

### 9. Mobile App Integration
- Use background tasks to monitor active sessions
- Implement push notifications for reminders
- Provide easy access to stop/extend functionality
- Show parking location and directions

