package com.example.fastfoodshop.factory.promotion;

import com.example.fastfoodshop.entity.Promotion;

import java.time.Instant;

public class PromotionFactory {
    private static Promotion createPromotion() {
        Promotion promotion = new Promotion();

        promotion.setCode("KM-15-PHAN-TRAM");
        promotion.setCreatedAt(Instant.now());
        promotion.setUpdatedAt(Instant.now());

        return promotion;
    }

    public static Promotion createActivatedPromotion() {
        Promotion promotion = createPromotion();

        promotion.setActivated(true);
        promotion.setDeleted(false);

        return promotion;
    }
}
