package com.gamestore.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OwnedDiscountCardResponse(
    Long id,
    String sourceCode,
    String cardName,
    String description,
    Integer pointsCost,
    BigDecimal discountRate,
    BigDecimal maxDiscountAmount,
    LocalDateTime createdAt
) {
}
