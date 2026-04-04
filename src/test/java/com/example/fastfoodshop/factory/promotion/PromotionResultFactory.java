package com.example.fastfoodshop.factory.promotion;

import com.example.fastfoodshop.dto.PromotionResult;

public class PromotionResultFactory {
    private static final int DISCOUNTED_PRICE = 10000;

    public static PromotionResult createValid(Long promotionId) {
        return new PromotionResult(DISCOUNTED_PRICE, promotionId);
    }
}
