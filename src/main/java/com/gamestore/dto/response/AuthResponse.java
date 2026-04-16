package com.gamestore.dto.response;

import com.gamestore.entity.User;

public class AuthResponse {
    
    private String token;
    private UserInfo user;
    
    // 构造函数
    public AuthResponse() {}
    
    public AuthResponse(String token, UserInfo user) {
        this.token = token;
        this.user = user;
    }
    
    public AuthResponse(String token, User user) {
        this.token = token;
        this.user = new UserInfo(user);
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public UserInfo getUser() {
        return user;
    }
    
    public void setUser(UserInfo user) {
        this.user = user;
    }
    
    // 内部类 UserInfo
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String avatar;
        private String role;
        
        // 构造函数
        public UserInfo() {}
        
        public UserInfo(Long id, String username, String email, String avatar, String role) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.avatar = avatar;
            this.role = role;
        }
        
        public UserInfo(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.avatar = user.getAvatar();
            this.role = user.getRole().name();
        }
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getAvatar() {
            return avatar;
        }
        
        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
    }
}