package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.CartCreateRequest;
import com.example.fastfoodshop.request.CartUpdateRequest;
import com.example.fastfoodshop.response.cart.CartResponse;
import com.example.fastfoodshop.response.cart.CartDetailResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.cart.CartUpdateResponse;
import com.example.fastfoodshop.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController extends BaseController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<CartResponse>> addProductToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CartCreateRequest cartCreateRequest
    ) {
        return okResponse(cartService.addProductToCart(userDetails.getUsername(), cartCreateRequest));
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseWrapper<CartDetailResponse>> getCartDetailByUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Long addressId
    ) {
        return okResponse(cartService.getCartDetailByUser(userDetails.getUsername(), code, addressId));
    }

    @PatchMapping("/me/items/{productId}")
    public ResponseEntity<ResponseWrapper<CartResponse>> updateCartItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("productId") Long productId,
            @RequestBody @Valid CartUpdateRequest cartUpdateRequest
    ) {
        return okResponse(cartService.updateCartItem(
                userDetails.getUsername(), productId, cartUpdateRequest.quantity()
        ));
    }

    @DeleteMapping("/me/items/{productId}")
    public ResponseEntity<ResponseWrapper<CartUpdateResponse>> deleteProductFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("productId") Long productId
    ) {
        return okResponse(cartService.deleteProductFromCart(userDetails.getUsername(), productId));
    }
}
