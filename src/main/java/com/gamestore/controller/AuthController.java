package com.gamestore.controller;

import com.gamestore.dto.request.LoginRequest;
import com.gamestore.dto.request.RegisterRequest;
import com.gamestore.dto.response.ApiResponse;
import com.gamestore.entity.User;
import com.gamestore.entity.UserSession;
import com.gamestore.service.AuthService;
import com.gamestore.util.ResponseUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(
            @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        
        return ResponseUtil.success("注册成功", result);
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        
        String ipAddress = getClientIP(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        
        // 执行登录
        UserSession session = authService.login(request, ipAddress, userAgent);
        User user = authService.getUserByToken(session.getSessionToken());
        
        // 设置Cookie
        Cookie cookie = new Cookie("SESSION_TOKEN", session.getSessionToken());
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7天
        cookie.setHttpOnly(false);  // 允许JavaScript读取Cookie(用于前端登录状态检查)
        httpResponse.addCookie(cookie);
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", session.getSessionToken());
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("role", user.getRole().name());
        result.put("expiresAt", session.getExpiresAt());
        
        return ResponseUtil.success("登录成功", result);
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse response) {
        
        String token = getTokenFromRequest(request);
        if (token != null) {
            authService.logout(token);
        }
        
        // 清除Cookie
        Cookie cookie = new Cookie("SESSION_TOKEN", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        
        return ResponseUtil.success("退出登录成功", null);
    }
    
    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser(
            HttpServletRequest request) {
        
        String token = getTokenFromRequest(request);
        User user = authService.getUserByToken(token);
        
        if (user == null) {
            return ResponseUtil.error(401, "未登录或登录已过期");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        result.put("role", user.getRole().name());
        result.put("avatar", user.getAvatar());
        result.put("points", user.getPoints());
        
        return ResponseUtil.success("获取成功", result);
    }
    
    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 优先从Header获取
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        
        // 从Cookie获取
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("SESSION_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        return null;
    }
    
    /**
     * 获取客户端IP
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
