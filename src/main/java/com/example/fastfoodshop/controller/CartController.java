package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.dto.CartDTO;
import com.example.fastfoodshop.request.CartCreateRequest;
import com.example.fastfoodshop.request.CartUpdateRequest;
import com.example.fastfoodshop.request.DeliveryRequest;
import com.example.fastfoodshop.response.CartResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping()
    public ResponseEntity<ResponseWrapper<CartDTO>> addProductToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CartCreateRequest req
    ) {
        return cartService.addProductToCart(userDetails.getUsername(), req.getProductId(), req.getQuantity());
    }

    @PostMapping("/my-cart")
    public ResponseEntity<ResponseWrapper<CartResponse>> getCartDetailByUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("code") String code,
            @RequestBody(required = false) DeliveryRequest deliveryRequest
    ) {
        return cartService.getCartDetailByUser(userDetails.getUsername(), code, deliveryRequest);
    }

    @PutMapping()
    public ResponseEntity<ResponseWrapper<CartDTO>> updateCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CartUpdateRequest req
    ) {
        return cartService.updateCart(userDetails.getUsername(), req.getProductId(), req.getQuantity());
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<CartDTO>> deleteProductFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("productId") Long productId
    ) {
        return cartService.deleteProductFromCart(userDetails.getUsername(), productId);
    }
}
