package com.gamestore.repository;

import com.gamestore.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<CartItem> findByUserIdAndSelectedTrueOrderByCreatedAtDesc(Long userId);

    Optional<CartItem> findByUserIdAndGameId(Long userId, Long gameId);

    long countByUserId(Long userId);

    void deleteByUserId(Long userId);
}
