package com.gamestore.dto.response;

public class ContentRewardActionResponse {

    private Integer rewardedPoints;
    private Integer senderPointsBalance;
    private Integer receiverPointsBalance;
    private Integer totalRewardPoints;
    private Integer supporterCount;

    public Integer getRewardedPoints() {
        return rewardedPoints;
    }

    public void setRewardedPoints(Integer rewardedPoints) {
        this.rewardedPoints = rewardedPoints;
    }

    public Integer getSenderPointsBalance() {
        return senderPointsBalance;
    }

    public void setSenderPointsBalance(Integer senderPointsBalance) {
        this.senderPointsBalance = senderPointsBalance;
    }

    public Integer getReceiverPointsBalance() {
        return receiverPointsBalance;
    }

    public void setReceiverPointsBalance(Integer receiverPointsBalance) {
        this.receiverPointsBalance = receiverPointsBalance;
    }

    public Integer getTotalRewardPoints() {
        return totalRewardPoints;
    }

    public void setTotalRewardPoints(Integer totalRewardPoints) {
        this.totalRewardPoints = totalRewardPoints;
    }

    public Integer getSupporterCount() {
        return supporterCount;
    }

    public void setSupporterCount(Integer supporterCount) {
        this.supporterCount = supporterCount;
    }
}
