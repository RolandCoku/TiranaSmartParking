# Sensor Management Module - Complete API Documentation

This document provides comprehensive documentation for the Sensor Management Module, including all endpoints, request/response examples, and security details.

## Table of Contents

- [Overview](#overview)
- [Authentication & Authorization](#authentication--authorization)
- [API Endpoints](#api-endpoints)
- [Request/Response Examples](#requestresponse-examples)
- [Error Handling](#error-handling)
- [Integration Examples](#integration-examples)
- [Database Schema](#database-schema)

## Overview

The Sensor Management Module handles IoT sensor devices for parking space monitoring and occupancy detection. It provides comprehensive management of sensor devices and real-time event ingestion.

### Key Features

- **Sensor Device Management**: Register, configure, and manage IoT sensors
- **Real-time Event Ingestion**: Process occupancy events from sensors
- **Parking Space Integration**: Link sensors to specific parking spaces
- **Device Monitoring**: Track sensor status, battery levels, and connectivity
- **API Key Management**: Secure authentication for sensor devices
- **Event Processing**: Real-time processing of sensor events

### Module Structure

```
Sensor Module
├── Device Management
│   ├── Sensor Registration
│   ├── Device Configuration
│   └── Status Monitoring
├── Event Ingestion
│   ├── Real-time Events
│   ├── Occupancy Detection
│   └── Data Processing
└── Integration
    ├── Parking Space Linking
    ├── Lot Association
    └── API Authentication
```

### Sensor Types

- **PER_SPACE**: Individual sensors for each parking space
- **GATE_COUNTER**: Sensors that count vehicles entering/exiting lots

### Sensor Status

- **ACTIVE**: Sensor is operational and sending data
- **INACTIVE**: Sensor is offline or disabled
- **MAINTENANCE**: Sensor is under maintenance

## Authentication & Authorization

All endpoints require JWT authentication. Include the token in the Authorization header:

```bash
Authorization: Bearer <your-jwt-token>
```

### Required Permissions

| Endpoint | Permission Required |
|----------|-------------------|
| Sensor CRUD | `SENSOR_READ`, `SENSOR_CREATE`, `SENSOR_UPDATE`, `SENSOR_DELETE` |
| Event Ingestion | No authentication (uses API key) |

### API Key Authentication

Sensor devices authenticate using API keys for event ingestion:

```bash
X-API-Key: <sensor-api-key>
```

## API Endpoints

Base URL: `/api/v1/sensors`

### 1. Register Sensor Device

**POST** `/api/v1/sensors`

Registers a new sensor device in the system.

**Request Body:**
```json
{
  "deviceId": "SENSOR_001",
  "sensorType": "PER_SPACE",
  "parkingLotId": 1,
  "parkingSpaceId": 5,
  "description": "Ultrasonic sensor for space A5",
  "firmware": "v2.1.0"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Sensor device registered successfully",
  "data": {
    "id": 1,
    "deviceId": "SENSOR_001",
    "apiKey": "sk_live_1234567890abcdef",
    "sensorType": "PER_SPACE",
    "parkingLotId": 1,
    "parkingLotName": "City Center Parking",
    "parkingSpaceId": 5,
    "parkingSpaceLabel": "A5",
    "description": "Ultrasonic sensor for space A5",
    "firmware": "v2.1.0",
    "batteryLevel": null,
    "lastSeenAt": null,
    "status": "ACTIVE",
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
}
```

**cURL Example:**
```bash
curl -X POST "http://localhost:8080/api/v1/sensors" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "deviceId": "SENSOR_001",
    "sensorType": "PER_SPACE",
    "parkingLotId": 1,
    "parkingSpaceId": 5,
    "description": "Ultrasonic sensor for space A5",
    "firmware": "v2.1.0"
  }'
```

### 2. Get Sensor Device by ID

**GET** `/api/v1/sensors/{id}`

Retrieves a specific sensor device by ID.

**Path Parameters:**
- `id`: Sensor device ID

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Sensor device fetched successfully",
  "data": {
    "id": 1,
    "deviceId": "SENSOR_001",
    "apiKey": "sk_live_1234567890abcdef",
    "sensorType": "PER_SPACE",
    "parkingLotId": 1,
    "parkingLotName": "City Center Parking",
    "parkingSpaceId": 5,
    "parkingSpaceLabel": "A5",
    "description": "Ultrasonic sensor for space A5",
    "firmware": "v2.1.0",
    "batteryLevel": 85,
    "lastSeenAt": "2024-01-15T14:30:00Z",
    "status": "ACTIVE",
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T14:30:00Z"
  }
}
```

**cURL Example:**
```bash
curl -X GET "http://localhost:8080/api/v1/sensors/1" \
  -H "Authorization: Bearer your-jwt-token"
```

### 3. Update Sensor Device

**PUT** `/api/v1/sensors/{id}`

Updates a sensor device (full update).

**Path Parameters:**
- `id`: Sensor device ID

**Request Body:**
```json
{
  "deviceId": "SENSOR_001",
  "sensorType": "PER_SPACE",
  "parkingLotId": 1,
  "parkingSpaceId": 5,
  "description": "Updated ultrasonic sensor for space A5",
  "firmware": "v2.2.0"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Sensor device updated successfully",
  "data": {
    "id": 1,
    "deviceId": "SENSOR_001",
    "apiKey": "sk_live_1234567890abcdef",
    "sensorType": "PER_SPACE",
    "parkingLotId": 1,
    "parkingLotName": "City Center Parking",
    "parkingSpaceId": 5,
    "parkingSpaceLabel": "A5",
    "description": "Updated ultrasonic sensor for space A5",
    "firmware": "v2.2.0",
    "batteryLevel": 85,
    "lastSeenAt": "2024-01-15T14:30:00Z",
    "status": "ACTIVE",
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T15:00:00Z"
  }
}
```

**cURL Example:**
```bash
curl -X PUT "http://localhost:8080/api/v1/sensors/1" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "deviceId": "SENSOR_001",
    "sensorType": "PER_SPACE",
    "parkingLotId": 1,
    "parkingSpaceId": 5,
    "description": "Updated ultrasonic sensor for space A5",
    "firmware": "v2.2.0"
  }'
```

### 4. Partial Update Sensor Device

**PATCH** `/api/v1/sensors/{id}`

Partially updates a sensor device.

**Path Parameters:**
- `id`: Sensor device ID

**Request Body:**
```json
{
  "description": "Maintenance scheduled sensor",
  "status": "MAINTENANCE"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Sensor device partially updated successfully",
  "data": {
    "id": 1,
    "deviceId": "SENSOR_001",
    "apiKey": "sk_live_1234567890abcdef",
    "sensorType": "PER_SPACE",
    "parkingLotId": 1,
    "parkingLotName": "City Center Parking",
    "parkingSpaceId": 5,
    "parkingSpaceLabel": "A5",
    "description": "Maintenance scheduled sensor",
    "firmware": "v2.2.0",
    "batteryLevel": 85,
    "lastSeenAt": "2024-01-15T14:30:00Z",
    "status": "MAINTENANCE",
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T15:15:00Z"
  }
}
```

**cURL Example:**
```bash
curl -X PATCH "http://localhost:8080/api/v1/sensors/1" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Maintenance scheduled sensor",
    "status": "MAINTENANCE"
  }'
```

### 5. Delete Sensor Device

**DELETE** `/api/v1/sensors/{id}`

Deletes a sensor device.

**Path Parameters:**
- `id`: Sensor device ID

**Response (204 No Content):**
```json
{
  "success": true,
  "message": "Sensor device deleted successfully",
  "data": null
}
```

**cURL Example:**
```bash
curl -X DELETE "http://localhost:8080/api/v1/sensors/1" \
  -H "Authorization: Bearer your-jwt-token"
```

### 6. Ingest Sensor Event

**POST** `/api/v1/sensors/event`

Ingests a real-time event from a sensor device. This endpoint uses API key authentication instead of JWT.

**Request Headers:**
```bash
X-API-Key: sk_live_1234567890abcdef
Content-Type: application/json
```

**Request Body:**
```json
{
  "deviceId": "SENSOR_001",
  "eventType": "OCCUPANCY_CHANGE",
  "timestamp": "2024-01-15T14:30:00Z",
  "data": {
    "occupied": true,
    "confidence": 0.95,
    "batteryLevel": 85,
    "signalStrength": -45
  }
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Sensor event ingested successfully",
  "data": null
}
```

**cURL Example:**
```bash
curl -X POST "http://localhost:8080/api/v1/sensors/event" \
  -H "X-API-Key: sk_live_1234567890abcdef" \
  -H "Content-Type: application/json" \
  -d '{
    "deviceId": "SENSOR_001",
    "eventType": "OCCUPANCY_CHANGE",
    "timestamp": "2024-01-15T14:30:00Z",
    "data": {
      "occupied": true,
      "confidence": 0.95,
      "batteryLevel": 85,
      "signalStrength": -45
    }
  }'
```

## Request/Response Examples

### Event Types

#### Occupancy Change Event
```json
{
  "deviceId": "SENSOR_001",
  "eventType": "OCCUPANCY_CHANGE",
  "timestamp": "2024-01-15T14:30:00Z",
  "data": {
    "occupied": true,
    "confidence": 0.95,
    "batteryLevel": 85,
    "signalStrength": -45
  }
}
```

#### Heartbeat Event
```json
{
  "deviceId": "SENSOR_001",
  "eventType": "HEARTBEAT",
  "timestamp": "2024-01-15T14:30:00Z",
  "data": {
    "batteryLevel": 85,
    "signalStrength": -45,
    "firmware": "v2.2.0",
    "uptime": 86400
  }
}
```

#### Error Event
```json
{
  "deviceId": "SENSOR_001",
  "eventType": "ERROR",
  "timestamp": "2024-01-15T14:30:00Z",
  "data": {
    "errorCode": "LOW_BATTERY",
    "errorMessage": "Battery level below 10%",
    "batteryLevel": 8
  }
}
```

### Error Responses

#### Sensor Not Found (404 Not Found)
```json
{
  "success": false,
  "message": "Sensor with this ID does not exist",
  "data": null
}
```

#### Duplicate Device ID (409 Conflict)
```json
{
  "success": false,
  "message": "Device ID already exists",
  "data": null
}
```

#### Invalid API Key (401 Unauthorized)
```json
{
  "success": false,
  "message": "Invalid API key",
  "data": null
}
```

#### Validation Error (400 Bad Request)
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "errors": [
      {
        "field": "deviceId",
        "message": "Device ID is required"
      },
      {
        "field": "sensorType",
        "message": "Sensor type must be PER_SPACE or GATE_COUNTER"
      }
    ]
  }
}
```

#### Access Denied (403 Forbidden)
```json
{
  "success": false,
  "message": "Access denied: Insufficient permissions",
  "data": null
}
```

## Error Handling

### Common Error Responses

| Status Code | Error Type | Description |
|-------------|------------|-------------|
| 400 | Bad Request | Invalid request data or validation errors |
| 401 | Unauthorized | Missing or invalid API key |
| 403 | Forbidden | Insufficient permissions for the operation |
| 404 | Not Found | Sensor device not found |
| 409 | Conflict | Device ID already exists |
| 500 | Internal Server Error | Server-side error |

## Integration Examples

### Sensor Device Integration (Python)

```python
import requests
import json
import time
from datetime import datetime

class SensorDevice:
    def __init__(self, device_id, api_key, base_url):
        self.device_id = device_id
        self.api_key = api_key
        self.base_url = base_url
        self.headers = {
            'X-API-Key': api_key,
            'Content-Type': 'application/json'
        }
    
    def send_occupancy_event(self, occupied, confidence=0.95, battery_level=None):
        """Send occupancy change event"""
        event_data = {
            'deviceId': self.device_id,
            'eventType': 'OCCUPANCY_CHANGE',
            'timestamp': datetime.utcnow().isoformat() + 'Z',
            'data': {
                'occupied': occupied,
                'confidence': confidence,
                'batteryLevel': battery_level,
                'signalStrength': -45  # Example signal strength
            }
        }
        
        response = requests.post(
            f"{self.base_url}/api/v1/sensors/event",
            headers=self.headers,
            json=event_data
        )
        
        return response.json()
    
    def send_heartbeat(self, battery_level=None, uptime=None):
        """Send heartbeat event"""
        event_data = {
            'deviceId': self.device_id,
            'eventType': 'HEARTBEAT',
            'timestamp': datetime.utcnow().isoformat() + 'Z',
            'data': {
                'batteryLevel': battery_level,
                'signalStrength': -45,
                'firmware': 'v2.2.0',
                'uptime': uptime
            }
        }
        
        response = requests.post(
            f"{self.base_url}/api/v1/sensors/event",
            headers=self.headers,
            json=event_data
        )
        
        return response.json()
    
    def send_error_event(self, error_code, error_message, battery_level=None):
        """Send error event"""
        event_data = {
            'deviceId': self.device_id,
            'eventType': 'ERROR',
            'timestamp': datetime.utcnow().isoformat() + 'Z',
            'data': {
                'errorCode': error_code,
                'errorMessage': error_message,
                'batteryLevel': battery_level
            }
        }
        
        response = requests.post(
            f"{self.base_url}/api/v1/sensors/event",
            headers=self.headers,
            json=event_data
        )
        
        return response.json()

# Usage example
sensor = SensorDevice(
    device_id="SENSOR_001",
    api_key="sk_live_1234567890abcdef",
    base_url="http://localhost:8080"
)

# Send occupancy event
result = sensor.send_occupancy_event(occupied=True, battery_level=85)
print(result)

# Send heartbeat every 5 minutes
while True:
    sensor.send_heartbeat(battery_level=85, uptime=3600)
    time.sleep(300)  # 5 minutes
```

### Admin Integration (JavaScript)

```javascript
class SensorManagementService {
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

  async registerSensor(sensorData) {
    const response = await fetch(`${this.baseURL}/sensors`, {
      method: 'POST',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(sensorData)
    });
    return response.json();
  }

  async getSensor(sensorId) {
    const response = await fetch(`${this.baseURL}/sensors/${sensorId}`, {
      headers: this.getAuthHeaders()
    });
    return response.json();
  }

  async updateSensor(sensorId, sensorData) {
    const response = await fetch(`${this.baseURL}/sensors/${sensorId}`, {
      method: 'PUT',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(sensorData)
    });
    return response.json();
  }

  async patchSensor(sensorId, updates) {
    const response = await fetch(`${this.baseURL}/sensors/${sensorId}`, {
      method: 'PATCH',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(updates)
    });
    return response.json();
  }

  async deleteSensor(sensorId) {
    const response = await fetch(`${this.baseURL}/sensors/${sensorId}`, {
      method: 'DELETE',
      headers: this.getAuthHeaders()
    });
    return response.json();
  }

  async getSensorStatus(sensorId) {
    const sensor = await this.getSensor(sensorId);
    return {
      status: sensor.data.status,
      batteryLevel: sensor.data.batteryLevel,
      lastSeen: sensor.data.lastSeenAt,
      signalStrength: sensor.data.signalStrength
    };
  }
}
```

### Event Processing Integration (Java)

```java
@Service
public class SensorEventProcessor {
    
    @Autowired
    private SensorIngestionService sensorIngestionService;
    
    @Autowired
    private ParkingSpaceRepository parkingSpaceRepository;
    
    @EventListener
    public void handleOccupancyChangeEvent(OccupancyChangeEvent event) {
        // Update parking space status
        ParkingSpace space = parkingSpaceRepository.findBySensorDeviceId(event.getDeviceId());
        if (space != null) {
            space.setSpaceStatus(event.isOccupied() ? 
                ParkingSpace.SpaceStatus.OCCUPIED : 
                ParkingSpace.SpaceStatus.AVAILABLE);
            space.setLastStatusChangedAt(Instant.now());
            parkingSpaceRepository.save(space);
        }
        
        // Trigger notifications if needed
        if (event.isOccupied()) {
            // Send notification to users looking for parking
            notificationService.notifySpaceOccupied(space);
        } else {
            // Send notification that space is available
            notificationService.notifySpaceAvailable(space);
        }
    }
    
    @EventListener
    public void handleLowBatteryEvent(LowBatteryEvent event) {
        // Send maintenance alert
        alertService.sendMaintenanceAlert(event.getDeviceId(), "Low battery");
    }
}
```

## Database Schema

### sensor_devices table
```sql
CREATE TABLE sensor_devices (
    id BIGSERIAL PRIMARY KEY,
    device_id VARCHAR(50) UNIQUE NOT NULL,
    api_key VARCHAR(255) UNIQUE NOT NULL,
    sensor_type VARCHAR(20) NOT NULL DEFAULT 'PER_SPACE',
    parking_lot_id BIGINT REFERENCES parking_lots(id),
    parking_space_id BIGINT REFERENCES parking_spaces(id),
    description TEXT,
    last_seen_at TIMESTAMP,
    firmware VARCHAR(50),
    battery_level INTEGER,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

### sensor_events table
```sql
CREATE TABLE sensor_events (
    id BIGSERIAL PRIMARY KEY,
    device_id VARCHAR(50) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    data JSONB NOT NULL,
    processed BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

### Indexes
```sql
-- Performance indexes
CREATE INDEX idx_sensor_devices_device_id ON sensor_devices(device_id);
CREATE INDEX idx_sensor_devices_api_key ON sensor_devices(api_key);
CREATE INDEX idx_sensor_devices_parking_lot_id ON sensor_devices(parking_lot_id);
CREATE INDEX idx_sensor_devices_parking_space_id ON sensor_devices(parking_space_id);
CREATE INDEX idx_sensor_devices_status ON sensor_devices(status);
CREATE INDEX idx_sensor_events_device_id ON sensor_events(device_id);
CREATE INDEX idx_sensor_events_event_type ON sensor_events(event_type);
CREATE INDEX idx_sensor_events_timestamp ON sensor_events(timestamp);
CREATE INDEX idx_sensor_events_processed ON sensor_events(processed);

-- Unique indexes
CREATE UNIQUE INDEX idx_sensor_devices_device_id_unique ON sensor_devices(device_id);
CREATE UNIQUE INDEX idx_sensor_devices_api_key_unique ON sensor_devices(api_key);
```

## Configuration

### Sensor Configuration
```yaml
# application.yml
sensor:
  api-key-prefix: "sk_live_"
  api-key-length: 32
  heartbeat-interval: 300 # 5 minutes in seconds
  battery-warning-threshold: 20
  battery-critical-threshold: 10
  event-retention-days: 30
```

### Security Configuration
```java
@Configuration
public class SensorSecurityConfig {
    
    @Bean
    public ApiKeyAuthenticationFilter apiKeyAuthenticationFilter() {
        return new ApiKeyAuthenticationFilter();
    }
    
    @Bean
    public ApiKeyAuthenticationProvider apiKeyAuthenticationProvider() {
        return new ApiKeyAuthenticationProvider();
    }
}
```

This comprehensive documentation covers all aspects of the Sensor Management Module, providing developers with everything they need to integrate IoT sensors into the parking system.
