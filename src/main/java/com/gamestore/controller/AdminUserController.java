package com.gamestore.controller;

import com.gamestore.dto.response.ApiResponse;
import com.gamestore.entity.User;
import com.gamestore.service.UserService;
import com.gamestore.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台用户管理API
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取所有用户
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseUtil.success("获取成功", users);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseUtil.success("获取成功", user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {
        
        user.setId(id);
        User updatedUser = userService.updateUser(user);
        return ResponseUtil.success("更新成功", updatedUser);
    }

    /**
     * 修改用户角色
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(
            @PathVariable Long id,
            @RequestParam String role) {
        
        User user = userService.findById(id);
        user.setRole(User.UserRole.valueOf(role));
        userService.updateUser(user);
        return ResponseUtil.success("角色修改成功", null);
    }

    /**
     * 修改用户状态（封禁/解封）
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        User user = userService.findById(id);
        user.setStatus(User.UserStatus.valueOf(status));
        userService.updateUser(user);
        
        String message = status.equals("ACTIVE") ? "解封成功" : "封禁成功";
        return ResponseUtil.success(message, null);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        User user = userService.findById(id);
        user.setStatus(User.UserStatus.DELETED);
        userService.updateUser(user);
        return ResponseUtil.success("删除成功", null);
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/{id}/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        String newPassword = request.get("newPassword");
        userService.resetPassword(id, newPassword);
        return ResponseUtil.success("密码重置成功", null);
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStats() {
        List<User> users = userService.getAllUsers();
        
        long totalUsers = users.size();
        long activeUsers = users.stream()
                .filter(u -> u.getStatus() == User.UserStatus.ACTIVE)
                .count();
        long bannedUsers = users.stream()
                .filter(u -> u.getStatus() == User.UserStatus.BANNED)
                .count();
        long adminUsers = users.stream()
                .filter(u -> u.getRole() == User.UserRole.ADMIN)
                .count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("bannedUsers", bannedUsers);
        stats.put("adminUsers", adminUsers);
        
        return ResponseUtil.success("获取统计成功", stats);
    }
}

