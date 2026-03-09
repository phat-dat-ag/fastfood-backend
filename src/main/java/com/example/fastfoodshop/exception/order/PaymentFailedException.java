package com.example.fastfoodshop.exception.order;

import com.example.fastfoodshop.exception.base.BusinessException;

public class PaymentFailedException extends BusinessException {
    public PaymentFailedException(String message) {
        super("PAYMENT_FAILED", "Thanh toán thất bại: " + message);
    }
}
