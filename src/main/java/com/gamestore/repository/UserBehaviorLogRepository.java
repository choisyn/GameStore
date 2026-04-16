package com.gamestore.repository;

import com.gamestore.entity.UserBehaviorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserBehaviorLogRepository extends JpaRepository<UserBehaviorLog, Long> {

    List<UserBehaviorLog> findTop300ByUserIdOrderByCreatedAtDesc(Long userId);

    List<UserBehaviorLog> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
        Long userId,
        LocalDateTime start,
        LocalDateTime end
    );

    List<UserBehaviorLog> findByUserIdAndBehaviorTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
        Long userId,
        UserBehaviorLog.BehaviorType behaviorType,
        LocalDateTime start,
        LocalDateTime end
    );

    List<UserBehaviorLog> findByUserIdAndBehaviorTypeOrderByCreatedAtDesc(
        Long userId,
        UserBehaviorLog.BehaviorType behaviorType
    );
}
