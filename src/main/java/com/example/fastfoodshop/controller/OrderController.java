package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.enums.OrderQueryType;
import com.example.fastfoodshop.request.OrderCreateRequest;
import com.example.fastfoodshop.request.OrderStatusUpdateRequest;
import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.response.order.OrderPageResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.order.OrderResponse;
import com.example.fastfoodshop.response.order.OrderUpdateResponse;
import com.example.fastfoodshop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController extends BaseController {
    private final OrderService orderService;

    @PostMapping()
    public ResponseEntity<ResponseWrapper<OrderResponse>> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody OrderCreateRequest orderCreateRequest
    ) {
        return okResponse(orderService.createOrder(userDetails.getUsername(), orderCreateRequest));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ResponseWrapper<OrderUpdateResponse>> updateOrderStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long orderId,
            @RequestBody OrderStatusUpdateRequest orderStatusUpdateRequest
    ) {
        return okResponse(orderService.updateStatus(orderId, userDetails.getUsername(), orderStatusUpdateRequest));
    }

    @GetMapping("/payment-intent")
    public ResponseEntity<ResponseWrapper<OrderResponse>> getPaymentIntent(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("orderId") Long orderId
    ) {
        return okResponse(orderService.getPaymentIntent(userDetails.getUsername(), orderId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<OrderResponse>> getOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long orderId
    ) {
        return okResponse(orderService.getOrder(userDetails.getUsername(), orderId));
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<OrderPageResponse>> getOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "ACTIVE") OrderQueryType orderQueryType,
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(
                orderService.getOrders(
                        userDetails.getUsername(), orderQueryType, request.getPage(), request.getSize()
                )
        );
    }
}
