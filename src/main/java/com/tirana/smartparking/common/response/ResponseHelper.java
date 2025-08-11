package com.tirana.smartparking.common.response;

import com.tirana.smartparking.common.dto.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ResponseHelper {
    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>(true, message, data);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>(true, message, data);
        return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>(false, message, data);
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>(false, message, data);
        return ResponseEntity.status(404).contentType(MediaType.APPLICATION_JSON).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> internalServerError(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>(false, message, data);
        return ResponseEntity.status(500).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>(false, message, data);
        return ResponseEntity.status(401).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> forbidden(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>(false, message, data);
        return ResponseEntity.status(403).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> conflict(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>(false, message, data);
        return ResponseEntity.status(409).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> noContent() {
        return ResponseEntity.noContent().build();
    }
}
