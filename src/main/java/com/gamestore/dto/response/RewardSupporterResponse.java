package com.gamestore.dto.response;

import java.time.LocalDateTime;

public class RewardSupporterResponse {

    private Long userId;
    private String username;
    private String avatar;
    private Integer totalPoints;
    private LocalDateTime firstRewardAt;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public LocalDateTime getFirstRewardAt() {
        return firstRewardAt;
    }

    public void setFirstRewardAt(LocalDateTime firstRewardAt) {
        this.firstRewardAt = firstRewardAt;
    }
}
