package com.gamestore.repository;

import com.gamestore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 按用户名查找
    Optional<User> findByUsername(String username);
    
    // 按邮箱查找
    Optional<User> findByEmail(String email);
    
    // 检查用户名是否存在
    boolean existsByUsername(String username);
    
    // 检查邮箱是否存在
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    long countByStatus(User.UserStatus status);

    long countByRole(User.UserRole role);
}
