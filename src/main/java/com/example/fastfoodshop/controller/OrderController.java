package com.example.fastfoodshop.controller;

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

    @PostMapping("/cash-on-delivery")
    public ResponseEntity<ResponseWrapper<OrderResponse>> createCashOnDeliveryOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody OrderCreateRequest orderCreateRequest
    ) {
        return okResponse(orderService.createCashOnDeliveryOrder(userDetails.getUsername(), orderCreateRequest));
    }

    @PostMapping("/stripe-payment")
    public ResponseEntity<ResponseWrapper<OrderResponse>> createStripePaymentOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody OrderCreateRequest orderCreateRequest
    ) {
        return okResponse(orderService.createStripePaymentOrder(userDetails.getUsername(), orderCreateRequest));
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

    @GetMapping("/by-order-id")
    public ResponseEntity<ResponseWrapper<OrderResponse>> getOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("orderId") Long orderId) {
        return okResponse(orderService.getOrder(userDetails.getUsername(), orderId));
    }

    @GetMapping("/staff/unfinished-orders/all")
    public ResponseEntity<ResponseWrapper<OrderPageResponse>> getAllUnfinishedOrders(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(orderService.getAllUnfinishedOrders(request.getPage(), request.getSize()));
    }

    @GetMapping("/staff/unfinished-order")
    public ResponseEntity<ResponseWrapper<OrderResponse>> getUnfinishedOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("orderId") Long orderId
    ) {
        return okResponse(orderService.getUnfinishedOrder(orderId));
    }

    @GetMapping("/active-order/all")
    public ResponseEntity<ResponseWrapper<OrderPageResponse>> getAllActiveOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(orderService.getAllActiveOrders(userDetails.getUsername(), request.getPage(), request.getSize()));
    }

    @GetMapping("/active-order")
    public ResponseEntity<ResponseWrapper<OrderResponse>> getActiveOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("orderId") Long orderId
    ) {
        return okResponse(orderService.getActiveOrder(orderId, userDetails.getUsername()));
    }

    @GetMapping("/order-history/all")
    public ResponseEntity<ResponseWrapper<OrderPageResponse>> getAllOrderHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(orderService.getAllOrderHistory(userDetails.getUsername(), request.getPage(), request.getSize()));
    }

    @GetMapping("/order-history")
    public ResponseEntity<ResponseWrapper<OrderResponse>> getOrderHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("orderId") Long orderId
    ) {
        return okResponse(orderService.getOrderHistory(orderId, userDetails.getUsername()));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<ResponseWrapper<OrderPageResponse>> getAllOrdersByAdmin(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(orderService.getAllOrdersByAdmin(request.getPage(), request.getSize()));
    }
}
