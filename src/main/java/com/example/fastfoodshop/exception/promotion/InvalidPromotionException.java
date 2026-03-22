package com.example.fastfoodshop.exception.promotion;

import com.example.fastfoodshop.exception.base.BusinessException;

public class InvalidPromotionException extends BusinessException {
    public InvalidPromotionException(String message) {
        super("INVALID_PROMOTION", message);
    }
}
