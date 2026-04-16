package com.gamestore.dto.response;

import java.math.BigDecimal;

public record PointShopItemResponse(
    String code,
    String cardName,
    String description,
    Integer pointsCost,
    BigDecimal discountRate,
    BigDecimal maxDiscountAmount
) {
}
