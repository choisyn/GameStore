package com.gamestore.dto.response;

import java.util.List;

public class ContentRewardSummaryResponse {

    private Integer totalRewardPoints;
    private Integer supporterCount;
    private List<RewardSupporterResponse> supporters;

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

    public List<RewardSupporterResponse> getSupporters() {
        return supporters;
    }

    public void setSupporters(List<RewardSupporterResponse> supporters) {
        this.supporters = supporters;
    }
}
