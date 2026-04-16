package com.gamestore.dto.response;

import java.math.BigDecimal;

public record CartItemResponse(
    Long id,
    Long gameId,
    String gameName,
    String displayGameName,
    String imageUrl,
    String description,
    BigDecimal unitPrice,
    Integer quantity,
    BigDecimal subtotal,
    Boolean selected,
    Boolean alreadyOwned
) {
}
