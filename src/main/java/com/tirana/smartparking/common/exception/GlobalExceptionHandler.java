package com.tirana.smartparking.common.exception;

import com.tirana.smartparking.common.dto.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleMissingPathVariable(MissingPathVariableException ex) {
        return new ApiResponse<>(false, "Missing required path variable: " + ex.getVariableName(), null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceConflict(ResourceConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResponse<String> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return new ApiResponse<>(false, "HTTP method not allowed: " + ex.getMethod(), null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<String> handleNoResourceFound(NoResourceFoundException ex) {
        return new ApiResponse<>(false, "Endpoint does not exist:" + ex.getMessage(), null);
    }

    @ExceptionHandler(RoleOperationNotAllowedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<String> handleRoleOperationNotAllowed(RoleOperationNotAllowedException ex) {
        return new ApiResponse<>(false, "Role operation not allowed: " + ex.getMessage(), null);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<String> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        return new ApiResponse<>(false, ex.getMessage(), null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<String> handleBadCredentials(BadCredentialsException ex) {
        return new ApiResponse<>(false, ex.getMessage(), null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = ex.getMessage();
        
        // Check if it's a date/time parsing error
        if (message != null && (message.contains("ZonedDateTime") || message.contains("DateTimeParseException"))) {
            message = "Invalid date/time format. Please use one of these formats: " +
                    "yyyy-MM-dd'T'HH:mm:ssXXX (e.g., 2025-09-18T10:51:00+02:00), " +
                    "yyyy-MM-dd'T'HH:mmXXX (e.g., 2025-09-18T10:51+02:00), " +
                    "yyyy-MM-dd'T'HH:mm:ss (e.g., 2025-09-18T10:51:00), " +
                    "yyyy-MM-dd'T'HH:mm (e.g., 2025-09-18T10:51)";
        } else {
            message = "Invalid request format: " + message;
        }
        
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, message, null));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = ex.getMessage();
        
        // Check for specific foreign key constraint violations
        if (message != null) {
            if (message.contains("parking_spaces") && message.contains("bookings")) {
                message = "Cannot delete this parking space because it is currently referenced by existing bookings. Please cancel or complete all related bookings first.";
            } else if (message.contains("foreign key constraint")) {
                message = "Cannot perform this operation because the resource is still referenced by other data. Please remove all dependencies first.";
            } else {
                message = "Data integrity violation: " + message;
            }
        } else {
            message = "Data integrity violation occurred. The operation cannot be completed due to database constraints.";
        }
        
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(false, message, null));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "An unexpected error occurred: " + ex.getMessage(), null));
    }
}
