# User Management Module - Complete API Documentation

This document provides comprehensive documentation for the User Management Module, including all endpoints, request/response examples, and security details.

## Table of Contents

- [Overview](#overview)
- [Authentication & Authorization](#authentication--authorization)
- [API Endpoints](#api-endpoints)
- [Request/Response Examples](#requestresponse-examples)
- [Error Handling](#error-handling)
- [Integration Examples](#integration-examples)
- [Database Schema](#database-schema)

## Overview

The User Management Module handles user account management, role assignments, and car management for the Smart Parking system. It provides comprehensive CRUD operations for users and their associated data.

### Key Features

- **User Management**: Create, read, update, and delete user accounts
- **Role Management**: Assign and remove roles from users
- **Car Management**: Manage user vehicles
- **Self-Service**: Users can manage their own profile and cars
- **Admin Operations**: Administrative functions for user management
- **Security**: Role-based access control with fine-grained permissions

### Module Structure

```
User Module
├── User Management
│   ├── CRUD Operations
│   ├── Role Assignment
│   └── Profile Management
├── Car Management
│   ├── Vehicle Registration
│   ├── Vehicle Updates
│   └── Vehicle Deletion
└── Self-Service
    ├── Profile Access
    └── Personal Car Management
```

## Authentication & Authorization

All endpoints require JWT authentication. Include the token in the Authorization header:

```bash
Authorization: Bearer <your-jwt-token>
```

### Required Permissions

| Endpoint | Permission Required |
|----------|-------------------|
| User CRUD | `USER_READ`, `USER_CREATE`, `USER_UPDATE`, `USER_DELETE` |
| Car CRUD | `CAR_READ`, `CAR_CREATE`, `CAR_UPDATE`, `CAR_DELETE` |
| Role Management | `ROLE_UPDATE`, `USER_UPDATE` |
| Self-Service | No additional permissions (own data only) |

## API Endpoints

### User Management Endpoints

Base URL: `/api/v1/users`

#### 1. Get All Users

**GET** `/api/v1/users`

Retrieves a paginated list of all users (admin only).

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `sort` (optional): Sort criteria (default: "id,asc")

**Response (200 OK):**
```json
{
  "success": true,
  "message": "List of users fetched successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "firstName": "John",
        "lastName": "Doe",
        "username": "johndoe",
        "email": "john.doe@example.com",
        "phoneNumber": "+355 69 123 4567",
        "roles": ["USER"],
        "permissions": ["BOOKING_READ", "BOOKING_CREATE", "CAR_READ"],
        "isActive": true,
        "createdAt": "2024-01-15T10:30:00Z",
        "updatedAt": "2024-01-15T10:30:00Z"
      }
    ],
    "page": {
      "number": 0,
      "size": 10,
      "totalElements": 1,
      "totalPages": 1,
      "first": true,
      "last": true
    }
  }
}
```

**cURL Example:**
```bash
curl -X GET "http://localhost:8080/api/v1/users?page=0&size=10&sort=id,asc" \
  -H "Authorization: Bearer your-jwt-token"
```

#### 2. Get User by ID

**GET** `/api/v1/users/{id}`

Retrieves a specific user by ID.

**Path Parameters:**
- `id`: User ID

**Response (200 OK):**
```json
{
  "success": true,
  "message": "User fetched successfully",
  "data": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "username": "johndoe",
    "email": "john.doe@example.com",
    "phoneNumber": "+355 69 123 4567",
    "roles": ["USER"],
    "permissions": ["BOOKING_READ", "BOOKING_CREATE", "CAR_READ"],
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
}
```

**cURL Example:**
```bash
curl -X GET "http://localhost:8080/api/v1/users/1" \
  -H "Authorization: Bearer your-jwt-token"
```

#### 3. Create User

**POST** `/api/v1/users`

Creates a new user account.

**Request Body:**
```json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "username": "janesmith",
  "password": "SecurePassword123!",
  "confirmPassword": "SecurePassword123!",
  "email": "jane.smith@example.com",
  "phoneNumber": "+355 69 987 6543",
  "roles": ["USER"]
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "User created successfully",
  "data": {
    "id": 2,
    "firstName": "Jane",
    "lastName": "Smith",
    "username": "janesmith",
    "email": "jane.smith@example.com",
    "phoneNumber": "+355 69 987 6543",
    "roles": ["USER"],
    "permissions": ["BOOKING_READ", "BOOKING_CREATE", "CAR_READ"],
    "isActive": true,
    "createdAt": "2024-01-15T11:00:00Z",
    "updatedAt": "2024-01-15T11:00:00Z"
  }
}
```

**cURL Example:**
```bash
curl -X POST "http://localhost:8080/api/v1/users" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Smith",
    "username": "janesmith",
    "password": "SecurePassword123!",
    "confirmPassword": "SecurePassword123!",
    "email": "jane.smith@example.com",
    "phoneNumber": "+355 69 987 6543",
    "roles": ["USER"]
  }'
```

#### 4. Update User

**PUT** `/api/v1/users/{id}`

Updates a user account (full update).

**Path Parameters:**
- `id`: User ID

**Request Body:**
```json
{
  "firstName": "Jane",
  "lastName": "Smith-Updated",
  "email": "jane.smith.updated@example.com",
  "phoneNumber": "+355 69 987 6544"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "User updated successfully",
  "data": {
    "id": 2,
    "firstName": "Jane",
    "lastName": "Smith-Updated",
    "username": "janesmith",
    "email": "jane.smith.updated@example.com",
    "phoneNumber": "+355 69 987 6544",
    "roles": ["USER"],
    "permissions": ["BOOKING_READ", "BOOKING_CREATE", "CAR_READ"],
    "isActive": true,
    "createdAt": "2024-01-15T11:00:00Z",
    "updatedAt": "2024-01-15T11:30:00Z"
  }
}
```

#### 5. Partial Update User

**PATCH** `/api/v1/users/{id}`

Partially updates a user account.

**Path Parameters:**
- `id`: User ID

**Request Body:**
```json
{
  "phoneNumber": "+355 69 987 6545"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "User patched successfully",
  "data": {
    "id": 2,
    "firstName": "Jane",
    "lastName": "Smith-Updated",
    "username": "janesmith",
    "email": "jane.smith.updated@example.com",
    "phoneNumber": "+355 69 987 6545",
    "roles": ["USER"],
    "permissions": ["BOOKING_READ", "BOOKING_CREATE", "CAR_READ"],
    "isActive": true,
    "createdAt": "2024-01-15T11:00:00Z",
    "updatedAt": "2024-01-15T11:45:00Z"
  }
}
```

#### 6. Delete User

**DELETE** `/api/v1/users/{id}`

Deletes a user account.

**Path Parameters:**
- `id`: User ID

**Response (204 No Content):**
```json
{
  "success": true,
  "message": "User deleted successfully",
  "data": null
}
```

**cURL Example:**
```bash
curl -X DELETE "http://localhost:8080/api/v1/users/2" \
  -H "Authorization: Bearer your-jwt-token"
```

### Car Management Endpoints

Base URL: `/api/v1/cars`

#### 1. Get All Cars

**GET** `/api/v1/cars`

Retrieves a paginated list of all cars (admin only).

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `sort` (optional): Sort criteria (default: "id,asc")

**Response (200 OK):**
```json
{
  "success": true,
  "message": "List of cars fetched successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "plateNumber": "AB123CD",
        "make": "Toyota",
        "model": "Corolla",
        "year": 2020,
        "color": "Silver",
        "vehicleType": "SEDAN",
        "ownerId": 1,
        "ownerName": "John Doe",
        "createdAt": "2024-01-15T10:30:00Z",
        "updatedAt": "2024-01-15T10:30:00Z"
      }
    ],
    "page": {
      "number": 0,
      "size": 10,
      "totalElements": 1,
      "totalPages": 1,
      "first": true,
      "last": true
    }
  }
}
```

#### 2. Get Car by ID

**GET** `/api/v1/cars/{id}`

Retrieves a specific car by ID.

**Path Parameters:**
- `id`: Car ID

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Car fetched successfully",
  "data": {
    "id": 1,
    "plateNumber": "AB123CD",
    "make": "Toyota",
    "model": "Corolla",
    "year": 2020,
    "color": "Silver",
    "vehicleType": "SEDAN",
    "ownerId": 1,
    "ownerName": "John Doe",
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
}
```

#### 3. Create Car

**POST** `/api/v1/cars`

Creates a new car for the authenticated user.

**Request Body:**
```json
{
  "plateNumber": "EF456GH",
  "make": "Honda",
  "model": "Civic",
  "year": 2021,
  "color": "Blue",
  "vehicleType": "SEDAN"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Car added successfully",
  "data": {
    "id": 2,
    "plateNumber": "EF456GH",
    "make": "Honda",
    "model": "Civic",
    "year": 2021,
    "color": "Blue",
    "vehicleType": "SEDAN",
    "ownerId": 1,
    "ownerName": "John Doe",
    "createdAt": "2024-01-15T12:00:00Z",
    "updatedAt": "2024-01-15T12:00:00Z"
  }
}
```

#### 4. Update Car

**PUT** `/api/v1/cars/{id}`

Updates a car (full update).

**Path Parameters:**
- `id`: Car ID

**Request Body:**
```json
{
  "plateNumber": "EF456GH",
  "make": "Honda",
  "model": "Civic",
  "year": 2022,
  "color": "Red",
  "vehicleType": "SEDAN"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Car updated successfully",
  "data": {
    "id": 2,
    "plateNumber": "EF456GH",
    "make": "Honda",
    "model": "Civic",
    "year": 2022,
    "color": "Red",
    "vehicleType": "SEDAN",
    "ownerId": 1,
    "ownerName": "John Doe",
    "createdAt": "2024-01-15T12:00:00Z",
    "updatedAt": "2024-01-15T12:30:00Z"
  }
}
```

#### 5. Partial Update Car

**PATCH** `/api/v1/cars/{id}`

Partially updates a car.

**Path Parameters:**
- `id`: Car ID

**Request Body:**
```json
{
  "color": "Black"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Car patched successfully",
  "data": {
    "id": 2,
    "plateNumber": "EF456GH",
    "make": "Honda",
    "model": "Civic",
    "year": 2022,
    "color": "Black",
    "vehicleType": "SEDAN",
    "ownerId": 1,
    "ownerName": "John Doe",
    "createdAt": "2024-01-15T12:00:00Z",
    "updatedAt": "2024-01-15T12:45:00Z"
  }
}
```

#### 6. Delete Car

**DELETE** `/api/v1/cars/{id}`

Deletes a car.

**Path Parameters:**
- `id`: Car ID

**Response (204 No Content):**
```json
{
  "success": true,
  "message": "Car deleted successfully",
  "data": null
}
```

### Role Management Endpoints

#### 1. Add Role to User

**PATCH** `/api/v1/users/{id}/roles`

Adds roles to a user.

**Path Parameters:**
- `id`: User ID

**Request Body:**
```json
["ADMIN", "OPERATOR"]
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Role added successfully",
  "data": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "username": "johndoe",
    "email": "john.doe@example.com",
    "phoneNumber": "+355 69 123 4567",
    "roles": ["USER", "ADMIN", "OPERATOR"],
    "permissions": ["USER_READ", "USER_CREATE", "BOOKING_READ", "BOOKING_CREATE"],
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T13:00:00Z"
  }
}
```

#### 2. Remove Role from User

**DELETE** `/api/v1/users/{id}/roles/{roleName}`

Removes a specific role from a user.

**Path Parameters:**
- `id`: User ID
- `roleName`: Role name to remove

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Role removed successfully",
  "data": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "username": "johndoe",
    "email": "john.doe@example.com",
    "phoneNumber": "+355 69 123 4567",
    "roles": ["USER", "ADMIN"],
    "permissions": ["USER_READ", "USER_CREATE", "BOOKING_READ", "BOOKING_CREATE"],
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T13:15:00Z"
  }
}
```

### Self-Service Endpoints

Base URL: `/api/v1/me`

#### 1. Get Current User

**GET** `/api/v1/me`

Retrieves the current authenticated user's profile.

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Current user fetched successfully",
  "data": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "username": "johndoe",
    "email": "john.doe@example.com",
    "phoneNumber": "+355 69 123 4567",
    "roles": ["USER"],
    "permissions": ["BOOKING_READ", "BOOKING_CREATE", "CAR_READ"],
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
}
```

#### 2. Get Current User's Cars

**GET** `/api/v1/me/cars`

Retrieves the current user's cars.

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `sort` (optional): Sort criteria (default: "id,asc")

**Response (200 OK):**
```json
{
  "success": true,
  "message": "List of user's cars fetched successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "plateNumber": "AB123CD",
        "make": "Toyota",
        "model": "Corolla",
        "year": 2020,
        "color": "Silver",
        "vehicleType": "SEDAN",
        "ownerId": 1,
        "ownerName": "John Doe",
        "createdAt": "2024-01-15T10:30:00Z",
        "updatedAt": "2024-01-15T10:30:00Z"
      }
    ],
    "page": {
      "number": 0,
      "size": 10,
      "totalElements": 1,
      "totalPages": 1,
      "first": true,
      "last": true
    }
  }
}
```

#### 3. Get Current User's Car by ID

**GET** `/api/v1/me/cars/{carId}`

Retrieves a specific car belonging to the current user.

**Path Parameters:**
- `carId`: Car ID

**Response (200 OK):**
```json
{
  "success": true,
  "message": "User's car fetched successfully",
  "data": {
    "id": 1,
    "plateNumber": "AB123CD",
    "make": "Toyota",
    "model": "Corolla",
    "year": 2020,
    "color": "Silver",
    "vehicleType": "SEDAN",
    "ownerId": 1,
    "ownerName": "John Doe",
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
}
```

## Request/Response Examples

### Error Responses

#### User Not Found (404 Not Found)
```json
{
  "success": false,
  "message": "User not found with id: 999",
  "data": null
}
```

#### Duplicate Username (409 Conflict)
```json
{
  "success": false,
  "message": "Username already exists",
  "data": null
}
```

#### Duplicate Email (409 Conflict)
```json
{
  "success": false,
  "message": "Email already exists",
  "data": null
}
```

#### Duplicate Plate Number (409 Conflict)
```json
{
  "success": false,
  "message": "Plate number already exists",
  "data": null
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

#### Validation Error (400 Bad Request)
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "errors": [
      {
        "field": "email",
        "message": "Email format is invalid"
      },
      {
        "field": "phoneNumber",
        "message": "Phone number format is invalid"
      }
    ]
  }
}
```

## Error Handling

### Common Error Responses

| Status Code | Error Type | Description |
|-------------|------------|-------------|
| 400 | Bad Request | Invalid request data or validation errors |
| 401 | Unauthorized | Missing or invalid authentication token |
| 403 | Forbidden | Insufficient permissions for the operation |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Resource already exists (duplicate data) |
| 500 | Internal Server Error | Server-side error |

## Integration Examples

### Frontend Integration (JavaScript)

```javascript
class UserService {
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

  async getCurrentUser() {
    const response = await fetch(`${this.baseURL}/me`, {
      headers: this.getAuthHeaders()
    });
    return response.json();
  }

  async getUserCars(page = 0, size = 10) {
    const response = await fetch(`${this.baseURL}/me/cars?page=${page}&size=${size}`, {
      headers: this.getAuthHeaders()
    });
    return response.json();
  }

  async addCar(carData) {
    const response = await fetch(`${this.baseURL}/cars`, {
      method: 'POST',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(carData)
    });
    return response.json();
  }

  async updateCar(carId, carData) {
    const response = await fetch(`${this.baseURL}/cars/${carId}`, {
      method: 'PUT',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(carData)
    });
    return response.json();
  }

  async deleteCar(carId) {
    const response = await fetch(`${this.baseURL}/cars/${carId}`, {
      method: 'DELETE',
      headers: this.getAuthHeaders()
    });
    return response.json();
  }
}
```

### Admin Integration (JavaScript)

```javascript
class AdminUserService {
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

  async getAllUsers(page = 0, size = 10, sort = 'id,asc') {
    const response = await fetch(`${this.baseURL}/users?page=${page}&size=${size}&sort=${sort}`, {
      headers: this.getAuthHeaders()
    });
    return response.json();
  }

  async createUser(userData) {
    const response = await fetch(`${this.baseURL}/users`, {
      method: 'POST',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(userData)
    });
    return response.json();
  }

  async updateUser(userId, userData) {
    const response = await fetch(`${this.baseURL}/users/${userId}`, {
      method: 'PUT',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(userData)
    });
    return response.json();
  }

  async addUserRole(userId, roles) {
    const response = await fetch(`${this.baseURL}/users/${userId}/roles`, {
      method: 'PATCH',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(roles)
    });
    return response.json();
  }

  async removeUserRole(userId, roleName) {
    const response = await fetch(`${this.baseURL}/users/${userId}/roles/${roleName}`, {
      method: 'DELETE',
      headers: this.getAuthHeaders()
    });
    return response.json();
  }
}
```

## Database Schema

### users table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

### cars table
```sql
CREATE TABLE cars (
    id BIGSERIAL PRIMARY KEY,
    plate_number VARCHAR(20) UNIQUE NOT NULL,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INTEGER NOT NULL,
    color VARCHAR(30) NOT NULL,
    vehicle_type VARCHAR(20) NOT NULL,
    owner_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

### roles table
```sql
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

### permissions table
```sql
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

### user_roles table
```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id),
    role_id BIGINT NOT NULL REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);
```

### role_permissions table
```sql
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL REFERENCES roles(id),
    permission_id BIGINT NOT NULL REFERENCES permissions(id),
    PRIMARY KEY (role_id, permission_id)
);
```

### Indexes
```sql
-- Performance indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_cars_plate_number ON cars(plate_number);
CREATE INDEX idx_cars_owner_id ON cars(owner_id);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);
```

This comprehensive documentation covers all aspects of the User Management Module, providing developers with everything they need to integrate user management into their applications.
