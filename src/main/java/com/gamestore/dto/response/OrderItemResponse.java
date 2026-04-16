package com.gamestore.dto.response;

import java.math.BigDecimal;

public record OrderItemResponse(
    Long gameId,
    String gameName,
    String displayGameName,
    String gameImageUrl,
    BigDecimal unitPrice,
    Integer quantity,
    BigDecimal subtotal
) {
}
