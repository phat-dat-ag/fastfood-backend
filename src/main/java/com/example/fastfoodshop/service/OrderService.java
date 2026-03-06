package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.enums.PaymentStatus;
import com.example.fastfoodshop.dto.OrderDTO;
import com.example.fastfoodshop.response.OrderResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    Order findOrderOrThrow(Long orderId);

    ResponseEntity<ResponseWrapper<OrderDTO>> createCashOnDeliveryOrder(String phone, String promotionCode, String userNote, Long addressId);

    ResponseEntity<ResponseWrapper<OrderDTO>> createStripePaymentOrder(String phone, String promotionCode, String userNote, Long addressId);

    ResponseEntity<ResponseWrapper<OrderDTO>> getPaymentIntent(String phone, Long orderId);

    ResponseEntity<ResponseWrapper<OrderDTO>> getOrder(String phone, Long orderId);

    ResponseEntity<ResponseWrapper<OrderResponse>> getAllUnfinishedOrders(int page, int size);

    ResponseEntity<ResponseWrapper<OrderDTO>> getUnfinishedOrder(Long orderId);

    Order updatePaymentStatus(Long orderId, PaymentStatus paymentStatus);

    ResponseEntity<ResponseWrapper<OrderDTO>> confirmOrder(Long orderId);

    ResponseEntity<ResponseWrapper<OrderDTO>> markAsDelivering(Long orderId);

    ResponseEntity<ResponseWrapper<OrderDTO>> markAsDelivered(Long orderId);

    ResponseEntity<ResponseWrapper<OrderResponse>> getAllActiveOrders(String phone, int page, int size);

    ResponseEntity<ResponseWrapper<OrderDTO>> getActiveOrder(Long orderId, String phone);

    ResponseEntity<ResponseWrapper<OrderResponse>> getAllOrderHistory(String phone, int page, int size);

    ResponseEntity<ResponseWrapper<OrderDTO>> getOrderHistory(Long orderId, String phone);

    ResponseEntity<ResponseWrapper<OrderDTO>> cancelOrderByUser(Long orderId, String reason);

    ResponseEntity<ResponseWrapper<OrderDTO>> cancelOrderByStaff(Long orderId, String reason);

    ResponseEntity<ResponseWrapper<OrderResponse>> getAllOrdersByAdmin(int page, int size);
}
