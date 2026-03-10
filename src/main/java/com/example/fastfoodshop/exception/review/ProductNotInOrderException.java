package com.example.fastfoodshop.exception.review;

import com.example.fastfoodshop.exception.base.BusinessException;

public class ProductNotInOrderException extends BusinessException {
    public ProductNotInOrderException() {
        super("PRODUCT_NOT_INT_ORDER", "Lỗi sản phẩm được đánh giá không có trong đơn hàng");
    }
}
