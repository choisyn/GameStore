package com.gamestore.exception;

import com.gamestore.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        System.err.println("业务异常: " + e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        System.err.println("参数错误: " + e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "参数错误: " + e.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(NoResourceFoundException e) {
        if ("favicon.ico".equals(e.getResourcePath())) {
            return ResponseEntity.notFound().build();
        }
        System.err.println("资源不存在: " + e.getResourcePath());
        return ResponseEntity.status(404)
                .body(ApiResponse.error(404, "资源不存在"));
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        System.err.println("运行时异常: " + e.getMessage());
        return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "系统运行时错误"));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        System.err.println("系统异常: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "系统内部错误"));
    }
}
