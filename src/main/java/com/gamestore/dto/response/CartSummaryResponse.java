package com.gamestore.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CartSummaryResponse(
    List<CartItemResponse> items,
    Integer totalItems,
    BigDecimal totalAmount,
    Boolean hasOwnedItems
) {
}
