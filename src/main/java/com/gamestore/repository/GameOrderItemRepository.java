package com.gamestore.repository;

import com.gamestore.entity.GameOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameOrderItemRepository extends JpaRepository<GameOrderItem, Long> {

    List<GameOrderItem> findByOrderIdOrderByIdAsc(Long orderId);
}
