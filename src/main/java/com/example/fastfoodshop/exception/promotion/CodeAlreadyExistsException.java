package com.example.fastfoodshop.exception.promotion;

import com.example.fastfoodshop.exception.base.BusinessException;

public class CodeAlreadyExistsException extends BusinessException {
    public CodeAlreadyExistsException(String promotionCode) {
        super("CODE_ALREADY_EXISTS", "Mã khuyến mãi này đã tồn tại: " + promotionCode);
    }
}
