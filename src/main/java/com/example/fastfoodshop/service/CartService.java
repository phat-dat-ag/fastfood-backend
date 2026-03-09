package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.CartDTO;
import com.example.fastfoodshop.request.CartCreateRequest;
import com.example.fastfoodshop.request.CartUpdateRequest;
import com.example.fastfoodshop.request.DeliveryRequest;
import com.example.fastfoodshop.response.CartResponse;

public interface CartService {
    CartDTO addProductToCart(String userPhone, CartCreateRequest cartCreateRequest);

    CartResponse getCartResponse(String phone, String promotionCode, DeliveryRequest deliveryRequest);

    CartResponse getCartDetailByUser(String phone, String promotionCode, DeliveryRequest deliveryRequest);

    CartDTO updateCart(String userPhone, CartUpdateRequest cartUpdateRequest);

    CartDTO deleteProductFromCart(String phone, Long productId);

    void deleteAllProductFromCart(String phone);
}
