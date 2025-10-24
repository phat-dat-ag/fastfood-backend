package com.example.fastfoodshop.util;

import com.example.fastfoodshop.dto.PromotionDTO;
import com.example.fastfoodshop.enums.PromotionType;

public class PromotionUtils {
    public static int calculateDiscountedPrice(int originalPrice, PromotionDTO promotion) {
        if (promotion == null) return NumberUtils.roundToThousand(originalPrice);

        double discounted = originalPrice;

        if (promotion.getType() == PromotionType.PERCENTAGE) {
            discounted = originalPrice * (1 - promotion.getValue() / 100.0);
        } else if (promotion.getType() == PromotionType.FIXED_AMOUNT) {
            discounted = originalPrice - promotion.getValue();
        }

        if (promotion.getMaxDiscountAmount() > 0) {
            double maxDiscount = promotion.getMaxDiscountAmount();
            discounted = Math.max(originalPrice - maxDiscount, discounted);
        }

        discounted = Math.max(discounted, 0);

        return NumberUtils.roundToThousand((int) discounted);
    }
}
