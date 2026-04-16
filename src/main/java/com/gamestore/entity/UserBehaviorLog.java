package com.gamestore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "user_behavior_logs",
    indexes = {
        @Index(name = "idx_behavior_user_id", columnList = "user_id"),
        @Index(name = "idx_behavior_game_id", columnList = "game_id"),
        @Index(name = "idx_behavior_type", columnList = "behavior_type"),
        @Index(name = "idx_behavior_created_at", columnList = "created_at")
    }
)
public class UserBehaviorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "behavior_type", nullable = false, length = 50)
    private BehaviorType behaviorType;

    @Column(name = "game_id")
    private Long gameId;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(length = 255)
    private String detail;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum BehaviorType {
        VIEW_GAME,
        ADD_TO_CART,
        PURCHASE_GAME,
        CLAIM_FREE_GAME,
        VIEW_FORUM_POST,
        CREATE_FORUM_POST,
        COMMENT_FORUM_POST,
        VIEW_COMMUNITY_POST,
        CREATE_COMMUNITY_POST,
        COMMENT_COMMUNITY_POST,
        REDEEM_POINT_ITEM,
        DAILY_CHECK_IN
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

    public BehaviorType getBehaviorType() {
        return behaviorType;
    }

    public void setBehaviorType(BehaviorType behaviorType) {
        this.behaviorType = behaviorType;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
