package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.CartCreateRequest;
import com.example.fastfoodshop.request.CartUpdateRequest;
import com.example.fastfoodshop.request.DeliveryRequest;
import com.example.fastfoodshop.response.cart.CartResponse;
import com.example.fastfoodshop.response.cart.CartDetailResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.cart.CartUpdateResponse;
import com.example.fastfoodshop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController extends BaseController {
    private final CartService cartService;

    @PostMapping()
    public ResponseEntity<ResponseWrapper<CartResponse>> addProductToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CartCreateRequest cartCreateRequest
    ) {
        return okResponse(cartService.addProductToCart(userDetails.getUsername(), cartCreateRequest));
    }

    @PostMapping("/my-cart")
    public ResponseEntity<ResponseWrapper<CartDetailResponse>> getCartDetailByUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("code") String code,
            @RequestBody(required = false) DeliveryRequest deliveryRequest
    ) {
        return okResponse(cartService.getCartDetailByUser(userDetails.getUsername(), code, deliveryRequest));
    }

    @PutMapping()
    public ResponseEntity<ResponseWrapper<CartResponse>> updateCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CartUpdateRequest cartUpdateRequest
    ) {
        return okResponse(cartService.updateCart(userDetails.getUsername(), cartUpdateRequest));
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<CartUpdateResponse>> deleteProductFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("productId") Long productId
    ) {
        return okResponse(cartService.deleteProductFromCart(userDetails.getUsername(), productId));
    }
}
