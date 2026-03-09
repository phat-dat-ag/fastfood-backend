package com.example.fastfoodshop.exception.order;

import com.example.fastfoodshop.exception.base.BusinessException;

public class PaymentNotAllowedException extends BusinessException {
    public PaymentNotAllowedException() {
        super("PAYMENT_NOT_ALLOWED", "Phương thức thanh toán không hợp lệ hoặc đơn hàng đã được thanh toán");
    }
}
