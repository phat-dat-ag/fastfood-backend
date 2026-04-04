package com.example.fastfoodshop.factory.cart;

import com.example.fastfoodshop.constant.CartConstant;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.request.CartCreateRequest;

public class CartCreateRequestFactory {
    private static final int VALID_QUANTITY = 3;

    public static CartCreateRequest createValidForProduct(Product product) {
        return new CartCreateRequest(VALID_QUANTITY, product.getId());
    }

    public static CartCreateRequest createInvalidForProductWithMaxQuantity(Product product) {
        return new CartCreateRequest(CartConstant.MAX_QUANTITY_PER_PRODUCT, product.getId());
    }
}
