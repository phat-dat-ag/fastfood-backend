package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.OrderCreateRequest;
import com.example.fastfoodshop.dto.OrderDTO;
import com.example.fastfoodshop.response.OrderResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/cash-on-delivery")
    public ResponseEntity<ResponseWrapper<OrderDTO>> createCashOnDeliveryOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody OrderCreateRequest request
    ) {
        return orderService.createCashOnDeliveryOrder(userDetails.getUsername(), request.getPromotionCode(), request.getUserNote(), request.getAddressId());
    }

    @PostMapping("/stripe-payment")
    public ResponseEntity<ResponseWrapper<OrderDTO>> createStripePaymentOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody OrderCreateRequest request
    ) {
        return orderService.createStripePaymentOrder(userDetails.getUsername(), request.getPromotionCode(), request.getUserNote(), request.getAddressId());
    }

    @GetMapping("/unfinished-orders/all")
    public ResponseEntity<ResponseWrapper<OrderResponse>> getUnfinishedOrders() {
        return orderService.getUnfinishedOrders();
    }

    @PutMapping("/confirm")
    public ResponseEntity<ResponseWrapper<OrderDTO>> confirmOrder(@RequestParam("orderId") Long orderId) {
        return orderService.confirmOrder(orderId);
    }
}
