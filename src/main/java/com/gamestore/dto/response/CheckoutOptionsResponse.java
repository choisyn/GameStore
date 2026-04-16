package com.gamestore.dto.response;

import java.util.List;

public record CheckoutOptionsResponse(
    Integer pointsBalance,
    Integer pointsEarnRate,
    Integer pointsRedeemRate,
    List<OwnedDiscountCardResponse> availableDiscountCards
) {
}
