package com.gamestore.dto.response;

import java.util.List;

public record DecisionInsightResponse(
    Long gameId,
    Integer overallScore,
    Integer interestMatchScore,
    Integer communityHeatScore,
    Integer valueScore,
    Integer supportScore,
    String summary,
    String suggestedAction,
    String caution,
    List<String> reasons,
    List<String> matchedTags
) {
}
