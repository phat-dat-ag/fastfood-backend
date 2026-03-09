package com.example.fastfoodshop.exception.promotion;

import com.example.fastfoodshop.exception.base.BusinessException;

public class UnavailablePromotionException extends BusinessException {
    public UnavailablePromotionException(Long promotionId) {
        super("UNAVAILABLE_PROMOTION", "Mã khuyến mãi hết lượt sử dụng: " + promotionId);
    }
}
