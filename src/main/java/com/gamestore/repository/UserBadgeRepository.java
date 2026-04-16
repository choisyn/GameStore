package com.gamestore.repository;

import com.gamestore.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    boolean existsByUserIdAndCode(Long userId, String code);

    List<UserBadge> findByUserIdOrderByEarnedAtDesc(Long userId);
}
