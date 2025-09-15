# Rate Management API Documentation

This document describes the REST API endpoints for managing parking rates in the smart parking system.

## Base URLs
- **Admin Rate Management**: `/api/v1/admin/rates`
- **Public Pricing**: `/api/v1/pricing`

## Rate Plans

Rate plans define the overall pricing structure for parking.

### Endpoints
- `POST /api/v1/admin/rates/plans` - Create a new rate plan
- `GET /api/v1/admin/rates/plans/{id}` - Get rate plan by ID
- `GET /api/v1/admin/rates/plans` - Get all rate plans (paginated)
- `PUT /api/v1/admin/rates/plans/{id}` - Update rate plan
- `DELETE /api/v1/admin/rates/plans/{id}` - Delete rate plan

### Rate Plan Types
- `FLAT_PER_ENTRY` - Fixed price per parking session
- `PER_HOUR` - Hourly rate
- `TIERED` - Different rates based on duration tiers
- `TIME_OF_DAY` - Different rates based on time of day
- `DAY_OF_WEEK` - Different rates based on day of week
- `FREE` - No charge
- `DYNAMIC` - Dynamic pricing based on demand/occupancy

## Rate Rules

Rate rules define specific pricing conditions within a rate plan.

### Endpoints
- `POST /api/v1/admin/rates/rules` - Create a new rate rule
- `GET /api/v1/admin/rates/rules/{id}` - Get rate rule by ID
- `GET /api/v1/admin/rates/rules` - Get all rate rules (paginated)
- `GET /api/v1/admin/rates/plans/{planId}/rules` - Get rules for a specific plan
- `PUT /api/v1/admin/rates/rules/{id}` - Update rate rule
- `DELETE /api/v1/admin/rates/rules/{id}` - Delete rate rule

### Rule Conditions
- **Time Windows**: `startMinute`/`endMinute` for session duration
- **Time of Day**: `startTime`/`endTime` for daily time periods
- **Day of Week**: `dayOfWeek` for specific days
- **Vehicle Type**: `vehicleType` (CAR, MOTORCYCLE, etc.)
- **User Group**: `userGroup` (PUBLIC, RESIDENT, DISABLED, STAFF)

## Lot Rate Assignments

Assign rate plans to parking lots with priority and effective dates.

### Endpoints
- `POST /api/v1/admin/rates/lot-assignments` - Create lot rate assignment
- `GET /api/v1/admin/rates/lot-assignments/{id}` - Get assignment by ID
- `GET /api/v1/admin/rates/lot-assignments` - Get all assignments (paginated)
- `GET /api/v1/admin/rates/lots/{lotId}/rate-assignments` - Get assignments for a lot
- `PUT /api/v1/admin/rates/lot-assignments/{id}` - Update assignment
- `DELETE /api/v1/admin/rates/lot-assignments/{id}` - Delete assignment

## Space Rate Overrides

Override lot rates for specific parking spaces (higher priority than lot assignments).

### Endpoints
- `POST /api/v1/admin/rates/space-overrides` - Create space rate override
- `GET /api/v1/admin/rates/space-overrides/{id}` - Get override by ID
- `GET /api/v1/admin/rates/space-overrides` - Get all overrides (paginated)
- `GET /api/v1/admin/rates/spaces/{spaceId}/rate-overrides` - Get overrides for a space
- `PUT /api/v1/admin/rates/space-overrides/{id}` - Update override
- `DELETE /api/v1/admin/rates/space-overrides/{id}` - Delete override

## Pricing Quotes

Get real-time pricing quotes for parking sessions.

### Endpoints
- `POST /api/v1/pricing/quote` - Get pricing quote (for lots or spaces)
- `GET /api/v1/pricing/spaces/{spaceId}/quote` - Get quote for standalone parking space

### Quote Request (General)
```json
{
  "parkingLotId": 1,
  "parkingSpaceId": 123,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC",
  "startTime": "2024-01-15T09:00:00+01:00",
  "endTime": "2024-01-15T11:30:00+01:00"
}
```

### Quote Request (Standalone Space)
```json
{
  "parkingSpaceId": 123,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC",
  "startTime": "2024-01-15T09:00:00+01:00",
  "endTime": "2024-01-15T11:30:00+01:00"
}
```

### Standalone Space Quote (GET)
```
GET /api/v1/pricing/spaces/123/quote?vehicleType=CAR&userGroup=PUBLIC&startTime=2024-01-15T09:00:00+01:00&endTime=2024-01-15T11:30:00+01:00
```

### Quote Response
```json
{
  "currency": "ALL",
  "amount": 1500,
  "breakdown": "{\"2024-01-15 09:00-11:30\": 1500}"
}
```

## Priority System

1. **Space Rate Overrides** (highest priority)
2. **Lot Rate Assignments** (fallback for spaces within lots)
3. **Standalone Space Rates** (for spaces not assigned to lots)
4. **Default rates** (if no assignments found)

## Standalone Parking Spaces

The system supports parking spaces that are not assigned to a specific parking lot (e.g., roadside parking spots). These spaces can have their own independent pricing:

### Key Features:
- **Independent Pricing**: Standalone spaces can have their own rate plans
- **No Lot Dependency**: Spaces don't need to be assigned to a parking lot
- **Space-Specific Rates**: Each standalone space can have unique pricing rules
- **Flexible Assignment**: Spaces can be moved between standalone and lot-assigned states

### Usage Scenarios:
- **Roadside Parking**: Individual parking spots along streets
- **Temporary Spaces**: Pop-up parking areas
- **Special Locations**: Unique pricing for specific locations
- **Event Parking**: Temporary spaces for special events

## Common Query Parameters

- `page` - Page number (default: 0)
- `size` - Page size (default: 10)
- `sort` - Sort criteria (default: "id,asc")

## Example Usage

### 1. Create a Rate Plan
```bash
POST /api/v1/admin/rates/plans
{
  "name": "Standard Hourly",
  "type": "PER_HOUR",
  "currency": "ALL",
  "timeZone": "Europe/Tirane",
  "graceMinutes": 15,
  "incrementMinutes": 15,
  "dailyCap": 2000,
  "active": true
}
```

### 2. Add Rate Rules
```bash
POST /api/v1/admin/rates/rules
{
  "ratePlanId": 1,
  "pricePerHour": 100,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC"
}
```

### 3. Assign to Parking Lot
```bash
POST /api/v1/admin/rates/lot-assignments
{
  "parkingLotId": 1,
  "ratePlanId": 1,
  "priority": 0,
  "effectiveFrom": "2024-01-01T00:00:00+01:00"
}
```

### 4. Get Pricing Quote (Lot-based)
```bash
POST /api/v1/pricing/quote
{
  "parkingLotId": 1,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC",
  "startTime": "2024-01-15T09:00:00+01:00",
  "endTime": "2024-01-15T11:30:00+01:00"
}
```

### 5. Get Pricing Quote (Standalone Space)
```bash
POST /api/v1/pricing/quote
{
  "parkingSpaceId": 123,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC",
  "startTime": "2024-01-15T09:00:00+01:00",
  "endTime": "2024-01-15T11:30:00+01:00"
}
```

### 6. Get Standalone Space Quote (GET)
```bash
GET /api/v1/pricing/spaces/123/quote?vehicleType=CAR&userGroup=PUBLIC&startTime=2024-01-15T09:00:00+01:00&endTime=2024-01-15T11:30:00+01:00
```

## Assigning Rate Rules to Parking Spots

There are several ways to assign rate rules to parking spots (spaces) in the system. Here are the different approaches with detailed examples:

### Method 1: Space Rate Overrides (Recommended for Individual Spaces)

This is the most direct way to assign specific rate plans to individual parking spaces, especially for standalone spaces or spaces that need different pricing than their parent lot.

#### Step 1: Create a Rate Plan
```bash
POST /api/v1/admin/rates/plans
{
  "name": "Roadside Parking Plan",
  "type": "PER_HOUR",
  "currency": "ALL",
  "timeZone": "Europe/Tirane",
  "graceMinutes": 15,
  "incrementMinutes": 15,
  "dailyCap": 2000,
  "active": true
}
```

#### Step 2: Create Rate Rules for the Plan
```bash
POST /api/v1/admin/rates/rules
{
  "ratePlanId": 1,
  "pricePerHour": 100,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC"
}
```

#### Step 3: Assign the Rate Plan to the Parking Space
```bash
POST /api/v1/admin/rates/space-overrides
{
  "parkingSpaceId": 123,
  "ratePlanId": 1,
  "priority": 100,
  "effectiveFrom": "2024-01-01T00:00:00+01:00"
}
```

### Method 2: Lot Rate Assignment (For Spaces Within Lots)

If your parking space is part of a parking lot, you can assign rate plans at the lot level, and all spaces in that lot will inherit the pricing.

#### Step 1: Create Rate Plan and Rules (same as above)

#### Step 2: Assign Rate Plan to the Parking Lot
```bash
POST /api/v1/admin/rates/lot-assignments
{
  "parkingLotId": 1,
  "ratePlanId": 1,
  "priority": 0,
  "effectiveFrom": "2024-01-01T00:00:00+01:00"
}
```

### Method 3: Multiple Rate Rules for Complex Pricing

You can create multiple rate rules within a single rate plan to handle different scenarios:

#### Example: Time-based Pricing
```bash
# Rule 1: Daytime pricing (9 AM - 6 PM)
POST /api/v1/admin/rates/rules
{
  "ratePlanId": 1,
  "startTime": "09:00",
  "endTime": "18:00",
  "pricePerHour": 150,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC"
}

# Rule 2: Evening pricing (6 PM - 9 AM)
POST /api/v1/admin/rates/rules
{
  "ratePlanId": 1,
  "startTime": "18:00",
  "endTime": "09:00",
  "pricePerHour": 100,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC"
}
```

#### Example: Vehicle Type Specific Pricing
```bash
# Rule 1: Car pricing
POST /api/v1/admin/rates/rules
{
  "ratePlanId": 1,
  "pricePerHour": 100,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC"
}

# Rule 2: Motorcycle pricing (50% discount)
POST /api/v1/admin/rates/rules
{
  "ratePlanId": 1,
  "pricePerHour": 50,
  "vehicleType": "MOTORCYCLE",
  "userGroup": "PUBLIC"
}
```

## Priority System

The system uses the following priority order:

1. **Space Rate Overrides** (highest priority - 100+)
2. **Lot Rate Assignments** (lower priority - 0+)
3. **Default rates** (if no assignments found)

## Practical Examples

### Example 1: Roadside Parking Spot
```bash
# Create a simple hourly rate plan
POST /api/v1/admin/rates/plans
{
  "name": "Street Parking",
  "type": "PER_HOUR",
  "currency": "ALL",
  "timeZone": "Europe/Tirane",
  "graceMinutes": 10,
  "incrementMinutes": 30,
  "active": true
}

# Add a basic rule
POST /api/v1/admin/rates/rules
{
  "ratePlanId": 1,
  "pricePerHour": 80,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC"
}

# Assign to specific parking space
POST /api/v1/admin/rates/space-overrides
{
  "parkingSpaceId": 456,
  "ratePlanId": 1,
  "priority": 100
}
```

### Example 2: Premium Parking Space
```bash
# Create a premium rate plan
POST /api/v1/admin/rates/plans
{
  "name": "Premium Parking",
  "type": "PER_HOUR",
  "currency": "ALL",
  "timeZone": "Europe/Tirane",
  "graceMinutes": 5,
  "incrementMinutes": 15,
  "dailyCap": 5000,
  "active": true
}

# Add premium pricing rules
POST /api/v1/admin/rates/rules
{
  "ratePlanId": 2,
  "pricePerHour": 200,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC"
}

# Assign to premium space
POST /api/v1/admin/rates/space-overrides
{
  "parkingSpaceId": 789,
  "ratePlanId": 2,
  "priority": 150
}
```

### Example 3: Complex Tiered Pricing
```bash
# Create a tiered rate plan
POST /api/v1/admin/rates/plans
{
  "name": "Tiered Parking",
  "type": "TIERED",
  "currency": "ALL",
  "timeZone": "Europe/Tirane",
  "graceMinutes": 15,
  "incrementMinutes": 15,
  "active": true
}

# First hour: 100 ALL
POST /api/v1/admin/rates/rules
{
  "ratePlanId": 3,
  "startMinute": 0,
  "endMinute": 60,
  "priceFlat": 100,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC"
}

# Second hour: 80 ALL
POST /api/v1/admin/rates/rules
{
  "ratePlanId": 3,
  "startMinute": 60,
  "endMinute": 120,
  "priceFlat": 80,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC"
}

# Additional hours: 60 ALL per hour
POST /api/v1/admin/rates/rules
{
  "ratePlanId": 3,
  "startMinute": 120,
  "endMinute": null,
  "pricePerHour": 60,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC"
}
```

### Example 4: Day-of-Week Pricing
```bash
# Create a day-of-week rate plan
POST /api/v1/admin/rates/plans
{
  "name": "Weekend Pricing",
  "type": "DAY_OF_WEEK",
  "currency": "ALL",
  "timeZone": "Europe/Tirane",
  "graceMinutes": 15,
  "incrementMinutes": 15,
  "active": true
}

# Weekday pricing
POST /api/v1/admin/rates/rules
{
  "ratePlanId": 4,
  "dayOfWeek": "MONDAY",
  "pricePerHour": 100,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC"
}

POST /api/v1/admin/rates/rules
{
  "ratePlanId": 4,
  "dayOfWeek": "TUESDAY",
  "pricePerHour": 100,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC"
}

POST /api/v1/admin/rates/rules
{
  "ratePlanId": 4,
  "dayOfWeek": "WEDNESDAY",
  "pricePerHour": 100,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC"
}

POST /api/v1/admin/rates/rules
{
  "ratePlanId": 4,
  "dayOfWeek": "THURSDAY",
  "pricePerHour": 100,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC"
}

POST /api/v1/admin/rates/rules
{
  "ratePlanId": 4,
  "dayOfWeek": "FRIDAY",
  "pricePerHour": 100,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC"
}

# Weekend pricing (higher rates)
POST /api/v1/admin/rates/rules
{
  "ratePlanId": 4,
  "dayOfWeek": "SATURDAY",
  "pricePerHour": 150,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC"
}

POST /api/v1/admin/rates/rules
{
  "ratePlanId": 4,
  "dayOfWeek": "SUNDAY",
  "pricePerHour": 150,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC"
}
```

## Managing Existing Assignments

### View Current Assignments
```bash
# Get all space rate overrides
GET /api/v1/admin/rates/space-overrides

# Get overrides for specific space
GET /api/v1/admin/rates/spaces/123/rate-overrides

# Get lot assignments
GET /api/v1/admin/rates/lot-assignments

# Get assignments for specific lot
GET /api/v1/admin/rates/lots/1/rate-assignments

# Get all rate plans
GET /api/v1/admin/rates/plans

# Get rules for a specific plan
GET /api/v1/admin/rates/plans/1/rules
```

### Update Assignments
```bash
# Update space override
PUT /api/v1/admin/rates/space-overrides/1
{
  "parkingSpaceId": 123,
  "ratePlanId": 2,
  "priority": 120,
  "effectiveFrom": "2024-02-01T00:00:00+01:00"
}

# Update lot assignment
PUT /api/v1/admin/rates/lot-assignments/1
{
  "parkingLotId": 1,
  "ratePlanId": 2,
  "priority": 10,
  "effectiveFrom": "2024-02-01T00:00:00+01:00"
}

# Update rate rule
PUT /api/v1/admin/rates/rules/1
{
  "ratePlanId": 1,
  "pricePerHour": 120,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC"
}
```

### Remove Assignments
```bash
# Delete space override
DELETE /api/v1/admin/rates/space-overrides/1

# Delete lot assignment
DELETE /api/v1/admin/rates/lot-assignments/1

# Delete rate rule
DELETE /api/v1/admin/rates/rules/1

# Delete rate plan (will also delete all associated rules)
DELETE /api/v1/admin/rates/plans/1
```

## Testing Your Rate Assignment

### Get Pricing Quote
```bash
# Test pricing for specific space
POST /api/v1/pricing/quote
{
  "parkingSpaceId": 123,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC",
  "startTime": "2024-01-15T09:00:00+01:00",
  "endTime": "2024-01-15T11:30:00+01:00"
}

# Test pricing for lot-based space
POST /api/v1/pricing/quote
{
  "parkingLotId": 1,
  "parkingSpaceId": 456,
  "vehicleType": "CAR",
  "userGroup": "PUBLIC",
  "startTime": "2024-01-15T09:00:00+01:00",
  "endTime": "2024-01-15T11:30:00+01:00"
}

# Or use GET endpoint for standalone spaces
GET /api/v1/pricing/spaces/123/quote?vehicleType=CAR&userGroup=PUBLIC&startTime=2024-01-15T09:00:00+01:00&endTime=2024-01-15T11:30:00+01:00
```

### Expected Quote Response
```json
{
  "currency": "ALL",
  "amount": 1500,
  "breakdown": "{\"2024-01-15 09:00-11:30\": 1500}"
}
```

## Best Practices

1. **Use Space Rate Overrides** for individual spaces that need unique pricing
2. **Use Lot Rate Assignments** for spaces that share the same pricing within a lot
3. **Set appropriate priorities** (higher numbers = higher priority)
4. **Use effective dates** to schedule rate changes
5. **Test your pricing** with the quote endpoints before going live
6. **Create multiple rate rules** within a plan for complex pricing scenarios
7. **Use descriptive names** for rate plans to make management easier
8. **Set daily caps** to prevent excessive charges
9. **Use grace periods** to allow short-term parking without charge
10. **Consider time zones** when setting up rate plans
11. **Avoid duplicate assignments** - each space/lot can only have one assignment per rate plan
12. **Use update operations** instead of creating duplicates when modifying existing assignments

## Duplicate Assignment Prevention

The system prevents duplicate rate plan assignments to ensure data integrity:

### Database Constraints
- **Space Rate Overrides**: Unique constraint on `(space_id, rate_plan_id)`
- **Lot Rate Assignments**: Unique constraint on `(lot_id, rate_plan_id)`

### Service-Level Validation
- **Creation**: Checks for existing assignments before creating new ones
- **Error Response**: Returns `ResourceConflictException` if duplicate detected
- **Update**: Use PUT operations to modify existing assignments instead of creating duplicates

### Example Error Response
```json
{
  "success": false,
  "message": "Space rate override already exists for space 1 and rate plan 1",
  "data": null
}
```

### Handling Existing Duplicates
If you have existing duplicate assignments, you should:

1. **Identify duplicates** using the list endpoints
2. **Delete redundant assignments** keeping only the most recent/appropriate one
3. **Use update operations** for future changes

```bash
# Check for duplicates
GET /api/v1/admin/rates/spaces/1/rate-overrides

# Delete redundant assignment
DELETE /api/v1/admin/rates/space-overrides/1

# Update existing assignment instead of creating new one
PUT /api/v1/admin/rates/space-overrides/2
{
  "parkingSpaceId": 1,
  "ratePlanId": 1,
  "priority": 120,
  "effectiveFrom": "2024-01-01T00:00:00+01:00"
}
```

## Common Use Cases

### Roadside Parking Management
- Create individual rate plans for different street sections
- Assign space-specific overrides for premium locations
- Use time-based rules for rush hour pricing

### Event Parking
- Create temporary rate plans for special events
- Use higher priority overrides for event spaces
- Set effective dates for event duration

### Multi-Level Parking Lots
- Create different rate plans for different floors
- Use lot assignments for floor-level pricing
- Override specific spaces for premium locations

### Residential vs Public Parking
- Create separate rate plans for different user groups
- Use user group filters in rate rules
- Set different priorities for different access levels
