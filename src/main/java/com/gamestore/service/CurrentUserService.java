package com.gamestore.service;

import com.gamestore.entity.User;
import com.gamestore.exception.CustomException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final AuthService authService;

    public CurrentUserService(AuthService authService) {
        this.authService = authService;
    }

    public User getCurrentUser(HttpServletRequest request) {
        String token = extractToken(request);
        return authService.getUserByToken(token);
    }

    public User requireCurrentUser(HttpServletRequest request) {
        User user = getCurrentUser(request);
        if (user == null) {
            throw new CustomException("请先登录");
        }
        return user;
    }

    public String extractToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }

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
}
