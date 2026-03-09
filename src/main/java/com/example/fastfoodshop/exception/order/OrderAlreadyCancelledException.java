package com.example.fastfoodshop.exception.order;

import com.example.fastfoodshop.exception.base.BusinessException;

public class OrderAlreadyCancelledException extends BusinessException {
    public OrderAlreadyCancelledException() {
        super("ORDER_ALREADY_CANCELLED", "Đơn hàng đã bị hủy");
    }
}
