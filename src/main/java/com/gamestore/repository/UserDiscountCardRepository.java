package com.gamestore.repository;

import com.gamestore.entity.UserDiscountCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDiscountCardRepository extends JpaRepository<UserDiscountCard, Long> {

    List<UserDiscountCard> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, UserDiscountCard.CardStatus status);

    Optional<UserDiscountCard> findByIdAndUserId(Long id, Long userId);
}
