package com.example.fastfoodshop.validator;

import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.exception.promotion.InvalidPromotionException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PromotionValidator {
    private void validateGlobal(Promotion promotion) {
        if (!promotion.isGlobal()) {
            throw new InvalidPromotionException("Mã khuyến mãi này không áp dụng cho đơn hàng được");
        }
    }

    private void validateActivated(Promotion promotion) {
        if (!promotion.isActivated()) {
            throw new InvalidPromotionException("Mã khuyến mãi này đã bị vô hiệu hóa");
        }
    }

    private void validateTime(Promotion promotion, LocalDateTime now) {
        if (now.isBefore(promotion.getStartAt())) {
            throw new InvalidPromotionException("Mã khuyến mãi chưa có hiệu lực!");
        }

        if (now.isAfter(promotion.getEndAt())) {
            throw new InvalidPromotionException("Mã khuyến mãi đã hết hiệu lực");
        }
    }

    private void validateQuantity(Promotion promotion) {
        if (promotion.getUsedQuantity() >= promotion.getQuantity()) {
            throw new InvalidPromotionException("Đã hết lượt khuyến mãi!");
        }
    }

    private void validateOrderPrice(Promotion promotion, int orderPrice) {
        if (promotion.getMinSpendAmount() > orderPrice) {
            throw new InvalidPromotionException("Tổng đơn hàng chưa đủ điều kiện khuyến mãi!");
        }
    }

    public void validatePromotion(Promotion promotion, int orderPrice, LocalDateTime now) {
        validateGlobal(promotion);
        validateActivated(promotion);
        validateTime(promotion, now);
        validateQuantity(promotion);
        validateOrderPrice(promotion, orderPrice);
    }
}