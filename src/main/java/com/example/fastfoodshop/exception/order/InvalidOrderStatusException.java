package com.example.fastfoodshop.exception.order;

import com.example.fastfoodshop.exception.base.BusinessException;

public class InvalidOrderStatusException extends BusinessException {
    public InvalidOrderStatusException() {
        super("INVALID_ORDER_STATUS", "Trạng thái đơn hàng không hợp lệ");
    }
}
