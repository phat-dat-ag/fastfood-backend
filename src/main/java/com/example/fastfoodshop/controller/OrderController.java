package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.OrderCancelRequest;
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

    @GetMapping("/by-order-id")
    public ResponseEntity<ResponseWrapper<OrderDTO>> getOrder(@RequestParam("orderId") Long orderId) {
        return orderService.getOrder(orderId);
    }

    @GetMapping("/unfinished-orders/by-staff")
    public ResponseEntity<ResponseWrapper<OrderResponse>> getUnfinishedOrders() {
        return orderService.getUnfinishedOrders();
    }

    @PutMapping("/confirm")
    public ResponseEntity<ResponseWrapper<OrderDTO>> confirmOrder(@RequestParam("orderId") Long orderId) {
        return orderService.confirmOrder(orderId);
    }

    @PutMapping("/mark-delivering")
    public ResponseEntity<ResponseWrapper<OrderDTO>> markAsDelivering(@RequestParam("orderId") Long orderId) {
        return orderService.markAsDelivering(orderId);
    }

    @PutMapping("/mark-delivered")
    public ResponseEntity<ResponseWrapper<OrderDTO>> markAsDelivered(@RequestParam("orderId") Long orderId) {
        return orderService.markAsDelivered(orderId);
    }

    @PutMapping("/user/cancel-order")
    public ResponseEntity<ResponseWrapper<OrderDTO>> cancelOrderByUser(
            @RequestParam("orderId") Long orderId,
            @RequestBody OrderCancelRequest orderCancelRequest
    ) {
        return orderService.cancelOrderByUser(orderId, orderCancelRequest.getReason());
    }

    @PutMapping("/staff/cancel-order")
    public ResponseEntity<ResponseWrapper<OrderDTO>> cancelOrderByStaff(
            @RequestParam("orderId") Long orderId,
            @RequestBody OrderCancelRequest orderCancelRequest
    ) {
        return orderService.cancelOrderByStaff(orderId, orderCancelRequest.getReason());
    }

    @GetMapping("/unfinished-orders/by-user")
    public ResponseEntity<ResponseWrapper<OrderResponse>> getUnfinishedOrdersByUser(@AuthenticationPrincipal UserDetails userDetails) {
        return orderService.getUnfinishedOrdersByUser(userDetails.getUsername());
    }

    @GetMapping("/order-history/all")
    public ResponseEntity<ResponseWrapper<OrderResponse>> getOrdersByUser(@AuthenticationPrincipal UserDetails userDetails) {
        return orderService.getOrdersByUser(userDetails.getUsername());
    }

    @GetMapping("/order-history")
    public ResponseEntity<ResponseWrapper<OrderDTO>> getOrderHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("orderId") Long orderId
    ) {
        return orderService.getOrderHistory(orderId, userDetails.getUsername());
    }
}
