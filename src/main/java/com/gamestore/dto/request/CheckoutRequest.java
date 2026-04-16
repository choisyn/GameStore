package com.gamestore.dto.request;

public class CheckoutRequest {

    private String paymentMethod = "MOCK";
    private Long discountCardId;
    private Integer pointsToUse = 0;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Long getDiscountCardId() {
        return discountCardId;
    }

    public void setDiscountCardId(Long discountCardId) {
        this.discountCardId = discountCardId;
    }

    public Integer getPointsToUse() {
        return pointsToUse;
    }

    public void setPointsToUse(Integer pointsToUse) {
        this.pointsToUse = pointsToUse;
    }
}
