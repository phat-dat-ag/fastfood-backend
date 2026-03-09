package com.example.fastfoodshop.exception.order;

import com.example.fastfoodshop.exception.base.BusinessException;

public class CODPaymentLimitException extends BusinessException {
    public CODPaymentLimitException() {
        super("COD_PAYMENT_LIMIT_EXCEEDED", "Đơn hàng vượt quá hạn mức thanh toán COD, vui lòng thanh toán trước");
    }
}
