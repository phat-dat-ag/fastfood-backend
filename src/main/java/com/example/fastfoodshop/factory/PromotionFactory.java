package com.example.fastfoodshop.factory;

import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.entity.Award;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.enums.PromotionType;
import com.example.fastfoodshop.request.PromotionCreateRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PromotionFactory {

    private static final int QUIZ_PROMOTION_QUANTITY = 1;

    private void initBasePromotion(
            Promotion promotion,
            PromotionType type,
            int value,
            LocalDateTime startAt,
            LocalDateTime endAt,
            int maxDiscountAmount,
            int minSpendAmount,
            String code
    ) {
        promotion.setType(type);
        promotion.setValue(value);
        promotion.setStartAt(startAt);
        promotion.setEndAt(endAt);
        promotion.setMaxDiscountAmount(maxDiscountAmount);
        promotion.setMinSpendAmount(minSpendAmount);
        promotion.setCode(code);
        promotion.setUsedQuantity(0);
        promotion.setDeleted(false);
    }

    private void assignTarget(Promotion promotion, Category category, Product product) {
        promotion.setCategory(null);
        promotion.setProduct(null);
        promotion.setGlobal(false);

        if (category != null) {
            promotion.setCategory(category);
            return;
        }

        if (product != null) {
            promotion.setProduct(product);
            return;
        }

        promotion.setGlobal(true);
    }

    public Promotion buildPromotionFromRequest(
            PromotionCreateRequest promotionCreateRequest,
            Category category,
            Product product
    ) {
        Promotion promotion = new Promotion();

        initBasePromotion(
                promotion,
                promotionCreateRequest.type(),
                promotionCreateRequest.value(),
                promotionCreateRequest.startAt(),
                promotionCreateRequest.endAt(),
                promotionCreateRequest.maxDiscountAmount(),
                promotionCreateRequest.minSpendAmount(),
                promotionCreateRequest.code()
        );

        promotion.setQuantity(promotionCreateRequest.quantity());
        promotion.setActivated(promotionCreateRequest.activated());

        assignTarget(promotion, category, product);

        return promotion;
    }

    public Promotion buildPromotionFromAward(
            User user, Award award, String promotionCode, int value, LocalDateTime startAt, LocalDateTime endAt
    ) {
        Promotion promotion = new Promotion();

        initBasePromotion(
                promotion,
                award.getType(),
                value,
                startAt,
                endAt,
                award.getMaxDiscountAmount(),
                award.getMinSpendAmount(),
                promotionCode
        );

        promotion.setUser(user);
        promotion.setQuantity(QUIZ_PROMOTION_QUANTITY);
        promotion.setGlobal(true);
        promotion.setActivated(true);

        return promotion;
    }
}