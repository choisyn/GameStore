package com.gamestore.repository;

import com.gamestore.entity.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

    List<PointTransaction> findTop20ByUserIdOrderByCreatedAtDesc(Long userId);
}
