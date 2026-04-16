package com.gamestore.service;

import com.gamestore.dto.request.LoginRequest;
import com.gamestore.dto.request.RegisterRequest;
import com.gamestore.entity.User;
import com.gamestore.entity.UserSession;
import com.gamestore.exception.CustomException;
import com.gamestore.repository.UserRepository;
import com.gamestore.repository.UserSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserSessionRepository sessionRepository;
    private final PasswordService passwordService;

    // Session过期时间（7天）
    private static final int SESSION_EXPIRE_DAYS = 7;

    public AuthService(UserRepository userRepository, UserSessionRepository sessionRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordService = passwordService;
    }

    /**
     * 用户注册
     */
    @Transactional
    public User register(RegisterRequest request) {
        // 验证两次密码是否一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new CustomException("两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException("用户名已被占用");
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("邮箱已被注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordService.encode(request.getPassword()));
        user.setRole(User.UserRole.USER);
        user.setStatus(User.UserStatus.ACTIVE);

        return userRepository.save(user);
    }

    /**
     * 用户登录
     */
    @Transactional
    public UserSession login(LoginRequest request, String ipAddress, String userAgent) {
        // 查找用户
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException("用户名或密码错误"));

        // 验证密码
        if (!passwordService.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new CustomException("账号已被禁用，请联系管理员");
        }

        // 删除用户的旧会话
        sessionRepository.deleteByUserId(user.getId());

        // 创建新会话
        UserSession session = new UserSession();
        session.setUserId(user.getId());
        session.setSessionToken(generateSessionToken());
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setExpiresAt(LocalDateTime.now().plusDays(SESSION_EXPIRE_DAYS));

        // 更新用户最后登录时间
        if (passwordService.needsUpgrade(user.getPassword())) {
            user.setPassword(passwordService.encode(request.getPassword()));
        }
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return sessionRepository.save(session);
    }

    /**
     * 根据token获取用户
     */
    public User getUserByToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }

        Optional<UserSession> sessionOpt = sessionRepository.findBySessionToken(token);
        if (sessionOpt.isEmpty()) {
            return null;
        }

        UserSession session = sessionOpt.get();

        // 检查会话是否过期
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            sessionRepository.delete(session);
            return null;
        }

        return userRepository.findById(session.getUserId()).orElse(null);
    }

    /**
     * 用户登出
     */
    @Transactional
    public void logout(String token) {
        sessionRepository.findBySessionToken(token).ifPresent(sessionRepository::delete);
    }

    /**
     * 清理过期会话
     */
    @Transactional
    public void cleanExpiredSessions() {
        sessionRepository.deleteExpiredSessions(LocalDateTime.now());
    }

    /**
     * 生成会话令牌
     */
    private String generateSessionToken() {
        return UUID.randomUUID().toString().replace("-", "") + 
               System.currentTimeMillis();
    }

    /**
     * 验证是否为管理员
     */
    public boolean isAdmin(User user) {
        return user != null && user.getRole() == User.UserRole.ADMIN;
    }
}

