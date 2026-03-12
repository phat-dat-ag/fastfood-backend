package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.enums.PaymentStatus;
import com.example.fastfoodshop.request.OrderCancelRequest;
import com.example.fastfoodshop.request.OrderCreateRequest;
import com.example.fastfoodshop.response.order.OrderPageResponse;
import com.example.fastfoodshop.response.order.OrderResponse;
import com.example.fastfoodshop.response.order.OrderUpdateResponse;

public interface OrderService {
    Order findOrderOrThrow(Long orderId);

    OrderResponse createCashOnDeliveryOrder(String phone, OrderCreateRequest orderCreateRequest);

    OrderResponse createStripePaymentOrder(String phone, OrderCreateRequest orderCreateRequest);

    OrderResponse getPaymentIntent(String phone, Long orderId);

    OrderResponse getOrder(String phone, Long orderId);

    OrderPageResponse getAllUnfinishedOrders(int page, int size);

    OrderResponse getUnfinishedOrder(Long orderId);

    void updatePaymentStatus(Long orderId, PaymentStatus paymentStatus);

    OrderUpdateResponse confirmOrder(Long orderId);

    OrderUpdateResponse markAsDelivering(Long orderId);

    OrderUpdateResponse markAsDelivered(Long orderId);

    OrderPageResponse getAllActiveOrders(String phone, int page, int size);

    OrderResponse getActiveOrder(Long orderId, String phone);

    OrderPageResponse getAllOrderHistory(String phone, int page, int size);

    OrderResponse getOrderHistory(Long orderId, String phone);

    OrderUpdateResponse cancelOrderByUser(Long orderId, OrderCancelRequest orderCancelRequest);

    OrderUpdateResponse cancelOrderByStaff(Long orderId, OrderCancelRequest orderCancelRequest);

    OrderPageResponse getAllOrdersByAdmin(int page, int size);
}
