package com.gamestore.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "user_games",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_games_user_game", columnNames = {"user_id", "game_id"})
    },
    indexes = {
        @Index(name = "idx_user_games_user_id", columnList = "user_id"),
        @Index(name = "idx_user_games_game_id", columnList = "game_id")
    }
)
public class UserGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "acquired_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal acquiredPrice = BigDecimal.ZERO;

    @Column(name = "acquired_at", nullable = false, updatable = false)
    private LocalDateTime acquiredAt;

    @Column(name = "last_played_at")
    private LocalDateTime lastPlayedAt;

    @PrePersist
    protected void onCreate() {
        acquiredAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAcquiredPrice() {
        return acquiredPrice;
    }

    public void setAcquiredPrice(BigDecimal acquiredPrice) {
        this.acquiredPrice = acquiredPrice;
    }

    public LocalDateTime getAcquiredAt() {
        return acquiredAt;
    }

    public void setAcquiredAt(LocalDateTime acquiredAt) {
        this.acquiredAt = acquiredAt;
    }

    public LocalDateTime getLastPlayedAt() {
        return lastPlayedAt;
    }

    public void setLastPlayedAt(LocalDateTime lastPlayedAt) {
        this.lastPlayedAt = lastPlayedAt;
    }
}
