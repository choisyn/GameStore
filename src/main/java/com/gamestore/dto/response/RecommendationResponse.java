package com.gamestore.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record RecommendationResponse(
    Long id,
    String name,
    String displayName,
    String description,
    String imageUrl,
    String tags,
    BigDecimal price,
    BigDecimal discountPrice,
    BigDecimal rating,
    Integer ratingCount,
    Integer recommendationScore,
    String primaryReason,
    String secondaryReason,
    List<RecommendationReasonDetail> detailReasons
) {
}
