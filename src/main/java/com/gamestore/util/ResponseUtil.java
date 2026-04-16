package com.gamestore.util;

import com.gamestore.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        return ResponseEntity.ok(ApiResponse.success(message, data));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> success(String message) {
        return ResponseEntity.ok(ApiResponse.success(message));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> error(Integer code, String message) {
        return ResponseEntity.status(code).body(ApiResponse.error(code, message));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> error(String message) {
        return ResponseEntity.status(500).body(ApiResponse.error(message));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message) {
        return ResponseEntity.badRequest().body(ApiResponse.error(400, message));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message) {
        return ResponseEntity.status(401).body(ApiResponse.error(401, message));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> forbidden(String message) {
        return ResponseEntity.status(403).body(ApiResponse.error(403, message));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return ResponseEntity.status(404).body(ApiResponse.error(404, message));
    }
}
