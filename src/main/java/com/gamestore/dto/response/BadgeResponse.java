package com.gamestore.dto.response;

import java.time.LocalDateTime;

public record BadgeResponse(
    String code,
    String name,
    String description,
    LocalDateTime earnedAt
) {
}
