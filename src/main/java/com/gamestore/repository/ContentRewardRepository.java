package com.gamestore.repository;

import com.gamestore.entity.ContentReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRewardRepository extends JpaRepository<ContentReward, Long> {

    List<ContentReward> findByTargetTypeAndTargetIdOrderByCreatedAtAsc(
        ContentReward.TargetType targetType,
        Long targetId
    );
}
