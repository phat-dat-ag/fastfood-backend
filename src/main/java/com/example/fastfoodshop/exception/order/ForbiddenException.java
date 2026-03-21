package com.example.fastfoodshop.exception.order;

import com.example.fastfoodshop.exception.base.BusinessException;

public class ForbiddenException extends BusinessException {
    public ForbiddenException() {
        super("CANCELLATION_FORBIDDEN", "Không có đủ quyền để hủy đơn hàng");
    }
}
