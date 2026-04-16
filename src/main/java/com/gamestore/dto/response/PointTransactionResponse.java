package com.gamestore.dto.response;

import java.time.LocalDateTime;

public record PointTransactionResponse(
    Long id,
    Integer changeAmount,
    Integer balanceAfter,
    String type,
    String description,
    LocalDateTime createdAt
) {
}
