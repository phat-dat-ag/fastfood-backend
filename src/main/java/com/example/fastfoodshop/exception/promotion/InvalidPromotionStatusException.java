package com.example.fastfoodshop.exception.promotion;

import com.example.fastfoodshop.exception.base.BusinessException;

public class InvalidPromotionStatusException extends BusinessException {
    public InvalidPromotionStatusException() {
        super("INVALID_PROMOTION_STATUS", "Trạng thái mã khuyến mãi không hợp lệ");
    }
}
