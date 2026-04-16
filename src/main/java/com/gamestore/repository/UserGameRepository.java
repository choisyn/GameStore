package com.gamestore.repository;

import com.gamestore.entity.UserGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGameRepository extends JpaRepository<UserGame, Long> {

    boolean existsByUserIdAndGameId(Long userId, Long gameId);

    List<UserGame> findByUserIdOrderByAcquiredAtDesc(Long userId);

    long countByUserId(Long userId);
}
