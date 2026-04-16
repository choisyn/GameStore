package com.gamestore.repository;

import com.gamestore.entity.GameOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameOrderRepository extends JpaRepository<GameOrder, Long> {

    List<GameOrder> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<GameOrder> findByOrderNo(String orderNo);

    long countByUserId(Long userId);

    long countByStatus(GameOrder.OrderStatus status);

    @Query("select coalesce(sum(o.payableAmount), 0) from GameOrder o where o.status = :status")
    BigDecimal sumPayableAmountByStatus(@Param("status") GameOrder.OrderStatus status);
}
