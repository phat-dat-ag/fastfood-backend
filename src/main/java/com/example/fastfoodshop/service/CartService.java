package com.example.fastfoodshop.service;

import com.example.fastfoodshop.request.CartCreateRequest;
import com.example.fastfoodshop.response.cart.CartResponse;
import com.example.fastfoodshop.response.cart.CartDetailResponse;
import com.example.fastfoodshop.response.cart.CartUpdateResponse;

public interface CartService {
    CartResponse addProductToCart(String userPhone, CartCreateRequest cartCreateRequest);

    CartDetailResponse getCartResponse(String phone, String promotionCode, Long addressId);

    CartDetailResponse getCartDetailByUser(String phone, String promotionCode, Long addressId);

    CartResponse updateCartItem(String userPhone, Long productId, int quantity);

    CartUpdateResponse deleteProductFromCart(String phone, Long productId);

    void deleteAllProductFromCart(String phone);
}
