package com.gamestore.dto.response;

import java.util.List;

public record ProfileSummaryResponse(
    String username,
    String email,
    String avatar,
    String role,
    Integer points,
    Long forumPostCount,
    Long forumCommentCount,
    Long communityPostCount,
    Long communityCommentCount,
    Long orderCount,
    Long libraryCount,
    List<PointTransactionResponse> recentPointTransactions
) {
}
