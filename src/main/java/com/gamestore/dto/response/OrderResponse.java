package com.gamestore.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    Long id,
    String orderNo,
    String status,
    String paymentMethod,
    BigDecimal totalAmount,
    String couponName,
    BigDecimal couponDiscountAmount,
    Integer pointsUsed,
    BigDecimal pointsDiscountAmount,
    BigDecimal payableAmount,
    Integer pointsEarned,
    LocalDateTime paidAt,
    LocalDateTime createdAt,
    List<OrderItemResponse> items
) {
}
