package com.gamestore.service;

import com.gamestore.dto.request.ChangePasswordRequest;
import com.gamestore.dto.request.RegisterRequest;
import com.gamestore.dto.request.UpdateProfileRequest;
import com.gamestore.entity.User;
import com.gamestore.exception.CustomException;
import com.gamestore.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public UserService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public User register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new CustomException("两次输入的密码不一致");
        }
        if (existsByUsername(request.getUsername())) {
            throw new CustomException("用户名已存在");
        }
        if (existsByEmail(request.getEmail())) {
            throw new CustomException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordService.encode(request.getPassword()));
        user.setRole(User.UserRole.USER);
        user.setStatus(User.UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("用户不存在"));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException("用户不存在"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void updateLastLoginTime(Long userId) {
        User user = findById(userId);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 更新用户信息
     */
    public User updateUser(User user) {
        User existing = findById(user.getId());

        if (user.getUsername() != null && !user.getUsername().isBlank() &&
                !user.getUsername().equals(existing.getUsername())) {
            if (existsByUsername(user.getUsername())) {
                throw new CustomException("用户名已存在");
            }
            existing.setUsername(user.getUsername());
        }

        if (user.getEmail() != null && !user.getEmail().isBlank() &&
                !user.getEmail().equals(existing.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(user.getEmail(), existing.getId())) {
                throw new CustomException("邮箱已被注册");
            }
            existing.setEmail(user.getEmail());
        }

        if (user.getAvatar() != null) {
            existing.setAvatar(user.getAvatar());
        }

        if (user.getRole() != null) {
            existing.setRole(user.getRole());
        }

        if (user.getStatus() != null) {
            existing.setStatus(user.getStatus());
        }

        if (user.getPoints() != null) {
            existing.setPoints(user.getPoints());
        }

        existing.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(existing);
    }

    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findById(userId);

        if (request.getEmail() != null && !request.getEmail().isBlank() &&
                !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), user.getId())) {
                throw new CustomException("邮箱已被注册");
            }
            user.setEmail(request.getEmail().trim());
        }

        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar().trim().isEmpty() ? null : request.getAvatar().trim());
        }

        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public void changePassword(User user, ChangePasswordRequest request) {
        if (!passwordService.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new CustomException("当前密码不正确");
        }
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new CustomException("新密码长度不能少于6位");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new CustomException("两次输入的新密码不一致");
        }

        user.setPassword(passwordService.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void resetPassword(Long userId, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new CustomException("密码长度不能少于6位");
        }

        User user = findById(userId);
        user.setPassword(passwordService.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public boolean matchesPassword(User user, String rawPassword) {
        return passwordService.matches(rawPassword, user.getPassword());
    }

    public User updatePoints(Long userId, int newPoints) {
        User user = findById(userId);
        user.setPoints(newPoints);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}
