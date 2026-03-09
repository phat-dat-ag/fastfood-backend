package com.example.fastfoodshop.exception.promotion;

import com.example.fastfoodshop.exception.base.BusinessException;

public class DeletedPromotionException extends BusinessException {
    public DeletedPromotionException() {
        super("DELETED_PROMOTION", "Mã khuyến mãi đã bị xóa");
    }
}
