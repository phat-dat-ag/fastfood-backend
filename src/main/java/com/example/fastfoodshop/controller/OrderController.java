package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.enums.OrderQueryType;
import com.example.fastfoodshop.request.OrderCreateRequest;
import com.example.fastfoodshop.request.OrderStatusUpdateRequest;
import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.request.ReviewForm;
import com.example.fastfoodshop.response.order.OrderPageResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.order.OrderResponse;
import com.example.fastfoodshop.response.order.OrderUpdateResponse;
import com.example.fastfoodshop.response.review.ReviewUpdateResponse;
import com.example.fastfoodshop.service.OrderService;
import com.example.fastfoodshop.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController extends BaseController {
    private final OrderService orderService;
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<OrderResponse>> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid OrderCreateRequest orderCreateRequest
    ) {
        return okResponse(orderService.createOrder(userDetails.getUsername(), orderCreateRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseWrapper<OrderUpdateResponse>> updateOrderStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long orderId,
            @RequestBody @Valid OrderStatusUpdateRequest orderStatusUpdateRequest
    ) {
        return okResponse(orderService.updateOrder(orderId, userDetails.getUsername(), orderStatusUpdateRequest));
    }

    @GetMapping("/{id}/payment-intent")
    public ResponseEntity<ResponseWrapper<OrderResponse>> getPaymentIntent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long orderId
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

    @PostMapping("/{id}/reviews")
    public ResponseEntity<ResponseWrapper<ReviewUpdateResponse>> createReviews(
            @PathVariable("id") Long orderId,
            @Valid @ModelAttribute ReviewForm reviewsForm
    ) {
        return okResponse(reviewService.createReviews(reviewsForm.reviews(), orderId));
    }
}
