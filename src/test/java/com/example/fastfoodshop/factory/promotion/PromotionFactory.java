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

    public static Promotion createDeactivatedPromotion(Long promotionId) {
        Promotion promotion = createValidPromotion(promotionId);

        promotion.setActivated(false);

        return promotion;
    }

    public static Promotion createDeletedPromotion(Long promotionId) {
        Promotion promotion = createValidPromotion(promotionId);

        promotion.setDeleted(true);

        return promotion;
    }

    public static Promotion createGlobalPromotion(Long promotionId) {
        Promotion promotion = createValidPromotion(promotionId);

        promotion.setGlobal(true);

        return promotion;
    }

    public static Promotion createPromotionNotStartedYet(Long promotionId) {
        Promotion promotion = createValidPromotion(promotionId);

        LocalDateTime now = LocalDateTime.now();
        promotion.setStartAt(now.plusDays(1));
        promotion.setEndAt(now.plusDays(2));

        return promotion;
    }

    public static Promotion createExpiredPromotion(Long promotionId) {
        Promotion promotion = createValidPromotion(promotionId);

        LocalDateTime now = LocalDateTime.now();
        promotion.setStartAt(now.minusMonths(2));
        promotion.setEndAt(now.minusMonths(1));

        return promotion;
    }

    public static Promotion createExhaustedPromotion(Long promotionId) {
        Promotion promotion = createValidPromotion(promotionId);

        promotion.setUsedQuantity(101);
        promotion.setQuantity(100);

        return promotion;
    }
}
