package com.example.fastfoodshop.exception.promotion;

import com.example.fastfoodshop.exception.base.BusinessException;

public class PromotionNotFoundException extends BusinessException {
    public PromotionNotFoundException(Long promotionId) {
        super("PROMOTION_NOT_FOUND", "Mã khuyến mãi không tồn tại hoặc bị xóa: " + promotionId);
    }

    public PromotionNotFoundException(String promotionCode) {
        super("PROMOTION_NOT_FOUND", "Mã khuyến mãi không tồn tại hoặc bị xóa: " + promotionCode);
    }
}
