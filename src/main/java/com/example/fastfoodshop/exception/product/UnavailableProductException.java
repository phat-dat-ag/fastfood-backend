package com.example.fastfoodshop.exception.product;

import com.example.fastfoodshop.exception.base.BusinessException;

public class UnavailableProductException extends BusinessException {
    public UnavailableProductException(String productName) {
        super("UNAVAILABLE_PRODUCT", "Sản phẩm bị xóa hoặc ngừng kinh doanh: " + productName);
    }
}
