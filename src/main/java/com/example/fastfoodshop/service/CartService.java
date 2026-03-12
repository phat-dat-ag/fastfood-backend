package com.example.fastfoodshop.service;

import com.example.fastfoodshop.request.CartCreateRequest;
import com.example.fastfoodshop.request.CartUpdateRequest;
import com.example.fastfoodshop.request.DeliveryRequest;
import com.example.fastfoodshop.response.cart.CartResponse;
import com.example.fastfoodshop.response.cart.CartDetailResponse;
import com.example.fastfoodshop.response.cart.CartUpdateResponse;

public interface CartService {
    CartResponse addProductToCart(String userPhone, CartCreateRequest cartCreateRequest);

    CartDetailResponse getCartResponse(String phone, String promotionCode, DeliveryRequest deliveryRequest);

    CartDetailResponse getCartDetailByUser(String phone, String promotionCode, DeliveryRequest deliveryRequest);

    CartResponse updateCart(String userPhone, CartUpdateRequest cartUpdateRequest);

    CartUpdateResponse deleteProductFromCart(String phone, Long productId);

    void deleteAllProductFromCart(String phone);
}
