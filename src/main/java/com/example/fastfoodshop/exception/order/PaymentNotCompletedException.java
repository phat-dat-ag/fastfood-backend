package com.example.fastfoodshop.exception.order;

import com.example.fastfoodshop.exception.base.BusinessException;

public class PaymentNotCompletedException extends BusinessException {
    public PaymentNotCompletedException() {
        super("PAYMENT_NOT_COMPLETED", "Đơn hàng chưa được thanh toán trực tuyến hoặc thanh toán thất bại");
    }
}
