package com.gamestore.repository;

import com.gamestore.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    
    // 根据token查找会话
    Optional<UserSession> findBySessionToken(String sessionToken);
    
    // 根据用户ID查找会话
    Optional<UserSession> findByUserId(Long userId);
    
    // 删除过期会话
    @Modifying
    @Query("DELETE FROM UserSession s WHERE s.expiresAt < :now")
    void deleteExpiredSessions(@Param("now") LocalDateTime now);
    
    // 删除用户的所有会话
    @Modifying
    void deleteByUserId(Long userId);
}

