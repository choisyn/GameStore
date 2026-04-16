package com.gamestore.dto.response;

import java.util.List;

public record GrowthDashboardResponse(
    Integer level,
    String title,
    Integer contributionScore,
    Integer nextLevelScore,
    Integer progressPercent,
    Boolean checkedInToday,
    Integer checkInStreak,
    List<String> interestKeywords,
    List<GrowthTaskResponse> tasks,
    List<BadgeResponse> badges
) {
}
