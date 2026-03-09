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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController extends BaseController {
    private final OrderService orderService;

    @PostMapping("/cash-on-delivery")
    public ResponseEntity<ResponseWrapper<OrderDTO>> createCashOnDeliveryOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody OrderCreateRequest orderCreateRequest
    ) {
        return okResponse(orderService.createCashOnDeliveryOrder(userDetails.getUsername(), orderCreateRequest));
    }

    @PostMapping("/stripe-payment")
    public ResponseEntity<ResponseWrapper<OrderDTO>> createStripePaymentOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody OrderCreateRequest orderCreateRequest
    ) {
        return okResponse(orderService.createStripePaymentOrder(userDetails.getUsername(), orderCreateRequest));
    }

    @GetMapping("/payment-intent")
    public ResponseEntity<ResponseWrapper<OrderDTO>> getPaymentIntent(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("orderId") Long orderId
    ) {
        return okResponse(orderService.getPaymentIntent(userDetails.getUsername(), orderId));
    }

    @GetMapping("/by-order-id")
    public ResponseEntity<ResponseWrapper<OrderDTO>> getOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("orderId") Long orderId) {
        return okResponse(orderService.getOrder(userDetails.getUsername(), orderId));
    }

    @GetMapping("/staff/unfinished-orders/all")
    public ResponseEntity<ResponseWrapper<OrderResponse>> getAllUnfinishedOrders(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(orderService.getAllUnfinishedOrders(request.getPage(), request.getSize()));
    }

    @GetMapping("/staff/unfinished-order")
    public ResponseEntity<ResponseWrapper<OrderDTO>> getUnfinishedOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("orderId") Long orderId
    ) {
        return okResponse(orderService.getUnfinishedOrder(orderId));
    }

    @PutMapping("/confirm")
    public ResponseEntity<ResponseWrapper<OrderDTO>> confirmOrder(@RequestParam("orderId") Long orderId) {
        return okResponse(orderService.confirmOrder(orderId));
    }

    @PutMapping("/mark-delivering")
    public ResponseEntity<ResponseWrapper<OrderDTO>> markAsDelivering(@RequestParam("orderId") Long orderId) {
        return okResponse(orderService.markAsDelivering(orderId));
    }

    @PutMapping("/mark-delivered")
    public ResponseEntity<ResponseWrapper<OrderDTO>> markAsDelivered(@RequestParam("orderId") Long orderId) {
        return okResponse(orderService.markAsDelivered(orderId));
    }

    @PutMapping("/user/cancel-order")
    public ResponseEntity<ResponseWrapper<OrderDTO>> cancelOrderByUser(
            @RequestParam("orderId") Long orderId,
            @RequestBody OrderCancelRequest orderCancelRequest
    ) {
        return okResponse(orderService.cancelOrderByUser(orderId, orderCancelRequest));
    }

    @PutMapping("/staff/cancel-order")
    public ResponseEntity<ResponseWrapper<OrderDTO>> cancelOrderByStaff(
            @RequestParam("orderId") Long orderId,
            @RequestBody OrderCancelRequest orderCancelRequest
    ) {
        return okResponse(orderService.cancelOrderByStaff(orderId, orderCancelRequest));
    }

    @GetMapping("/active-order/all")
    public ResponseEntity<ResponseWrapper<OrderResponse>> getAllActiveOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(orderService.getAllActiveOrders(userDetails.getUsername(), request.getPage(), request.getSize()));
    }

    @GetMapping("/active-order")
    public ResponseEntity<ResponseWrapper<OrderDTO>> getActiveOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("orderId") Long orderId
    ) {
        return okResponse(orderService.getActiveOrder(orderId, userDetails.getUsername()));
    }

    @GetMapping("/order-history/all")
    public ResponseEntity<ResponseWrapper<OrderResponse>> getAllOrderHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(orderService.getAllOrderHistory(userDetails.getUsername(), request.getPage(), request.getSize()));
    }

    @GetMapping("/order-history")
    public ResponseEntity<ResponseWrapper<OrderDTO>> getOrderHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("orderId") Long orderId
    ) {
        return okResponse(orderService.getOrderHistory(orderId, userDetails.getUsername()));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<ResponseWrapper<OrderResponse>> getAllOrdersByAdmin(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(orderService.getAllOrdersByAdmin(request.getPage(), request.getSize()));
    }
}
