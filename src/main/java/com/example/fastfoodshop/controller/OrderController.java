package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.OrderCancelRequest;
import com.example.fastfoodshop.request.OrderCreateRequest;
import com.example.fastfoodshop.dto.OrderDTO;
import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.response.OrderResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.OrderService;
import jakarta.validation.Valid;
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

    @GetMapping("/payment-intent")
    public ResponseEntity<ResponseWrapper<OrderDTO>> getPaymentIntent(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("orderId") Long orderId
    ) {
        return orderService.getPaymentIntent(userDetails.getUsername(), orderId);
    }

    @GetMapping("/by-order-id")
    public ResponseEntity<ResponseWrapper<OrderDTO>> getOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("orderId") Long orderId) {
        return orderService.getOrder(userDetails.getUsername(), orderId);
    }

    @GetMapping("/staff/unfinished-orders/all")
    public ResponseEntity<ResponseWrapper<OrderResponse>> getAllUnfinishedOrders(
            @Valid @ModelAttribute PageRequest request
    ) {
        return orderService.getAllUnfinishedOrders(request.getPage(), request.getSize());
    }

    @GetMapping("/staff/unfinished-order")
    public ResponseEntity<ResponseWrapper<OrderDTO>> getUnfinishedOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("orderId") Long orderId
    ) {
        return orderService.getUnfinishedOrder(orderId);
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

    @GetMapping("/active-order/all")
    public ResponseEntity<ResponseWrapper<OrderResponse>> getAllActiveOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute PageRequest request
    ) {
        return orderService.getAllActiveOrders(userDetails.getUsername(), request.getPage(), request.getSize());
    }

    @GetMapping("/active-order")
    public ResponseEntity<ResponseWrapper<OrderDTO>> getActiveOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("orderId") Long orderId
    ) {
        return orderService.getActiveOrder(orderId, userDetails.getUsername());
    }

    @GetMapping("/order-history/all")
    public ResponseEntity<ResponseWrapper<OrderResponse>> getAllOrderHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute PageRequest request
    ) {
        return orderService.getAllOrderHistory(userDetails.getUsername(), request.getPage(), request.getSize());
    }

    @GetMapping("/order-history")
    public ResponseEntity<ResponseWrapper<OrderDTO>> getOrderHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("orderId") Long orderId
    ) {
        return orderService.getOrderHistory(orderId, userDetails.getUsername());
    }

    @GetMapping("/admin/all")
    public ResponseEntity<ResponseWrapper<OrderResponse>> getAllOrdersByAdmin(
            @Valid @ModelAttribute PageRequest request
    ) {
        return orderService.getAllOrdersByAdmin(request.getPage(), request.getSize());
    }
}
