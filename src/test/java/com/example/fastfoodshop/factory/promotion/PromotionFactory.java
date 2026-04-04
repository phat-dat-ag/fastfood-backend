package com.example.fastfoodshop.factory.promotion;

import com.example.fastfoodshop.entity.Promotion;

import java.time.Instant;
import java.time.LocalDateTime;

public class PromotionFactory {
    public static Promotion createValidPromotion(Long promotionId) {
        Promotion promotion = new Promotion();

        promotion.setId(promotionId);
        promotion.setCode("KM-15-PHAN-TRAM");
        promotion.setCreatedAt(Instant.now());
        promotion.setUpdatedAt(Instant.now());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime previousMonth = now.minusMonths(1);
        LocalDateTime nextMonth = now.plusMonths(1);

        promotion.setActivated(true);
        promotion.setDeleted(false);

        promotion.setStartAt(previousMonth);
        promotion.setEndAt(nextMonth);
        promotion.setUsedQuantity(0);
        promotion.setQuantity(100);

        return promotion;
    }
}
