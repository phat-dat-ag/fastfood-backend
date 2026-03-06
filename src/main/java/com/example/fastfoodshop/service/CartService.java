package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.CartDTO;
import com.example.fastfoodshop.request.DeliveryRequest;
import com.example.fastfoodshop.response.CartResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import org.springframework.http.ResponseEntity;

public interface CartService {
    ResponseEntity<ResponseWrapper<CartDTO>> addProductToCart(String userPhone, Long productId, int quantity);

    CartResponse getCartResponse(String phone, String promotionCode, DeliveryRequest deliveryRequest);

    ResponseEntity<ResponseWrapper<CartResponse>> getCartDetailByUser(String phone, String promotionCode, DeliveryRequest deliveryRequest);

    ResponseEntity<ResponseWrapper<CartDTO>> updateCart(String userPhone, Long productId, int quantity);

    ResponseEntity<ResponseWrapper<CartDTO>> deleteProductFromCart(String phone, Long productId);

    void deleteAllProductFromCart(String phone);
}
