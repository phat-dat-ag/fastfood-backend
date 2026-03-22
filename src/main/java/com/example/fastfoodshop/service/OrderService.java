package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.enums.OrderQueryType;
import com.example.fastfoodshop.enums.PaymentStatus;
import com.example.fastfoodshop.request.OrderCreateRequest;
import com.example.fastfoodshop.request.OrderStatusUpdateRequest;
import com.example.fastfoodshop.response.order.OrderPageResponse;
import com.example.fastfoodshop.response.order.OrderResponse;
import com.example.fastfoodshop.response.order.OrderUpdateResponse;

public interface OrderService {
    Order findOrderOrThrow(Long orderId);

    OrderResponse createOrder(String phone, OrderCreateRequest orderCreateRequest);

    OrderResponse getPaymentIntent(String phone, Long orderId);

    void updatePaymentStatus(Long orderId, PaymentStatus paymentStatus);

    OrderUpdateResponse updateStatus(Long orderId, String phone, OrderStatusUpdateRequest request);

    OrderResponse getOrder(String phone, Long orderId);

    OrderPageResponse getOrders(String phone, OrderQueryType orderQueryType, int page, int size);
}
