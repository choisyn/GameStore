package com.gamestore.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LibraryGameResponse(
    Long gameId,
    String gameName,
    String displayGameName,
    String imageUrl,
    String description,
    String developer,
    BigDecimal acquiredPrice,
    LocalDateTime acquiredAt,
    BigDecimal rating
) {
}
