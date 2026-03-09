package com.example.fastfoodshop.exception.order;

import com.example.fastfoodshop.exception.base.BusinessException;

public class OrderCannotBeCancelledException extends BusinessException {
    public OrderCannotBeCancelledException() {
        super("ORDER_CANNOT_BE_CANCELLED", "Không thể hủy đơn hàng này");
    }
}
