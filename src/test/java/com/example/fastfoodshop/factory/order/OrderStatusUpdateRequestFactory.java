package com.example.fastfoodshop.factory.order;

import com.example.fastfoodshop.enums.OrderStatus;
import com.example.fastfoodshop.request.OrderStatusUpdateRequest;

public class OrderStatusUpdateRequestFactory {
    private static final String cancelReason = "Ly do huy don";

    public static OrderStatusUpdateRequest createConfirmRequest() {
        return new OrderStatusUpdateRequest(OrderStatus.CONFIRMED, null);
    }

    public static OrderStatusUpdateRequest createMarkAsDeliveringRequest() {
        return new OrderStatusUpdateRequest(OrderStatus.DELIVERING, null);
    }

    public static OrderStatusUpdateRequest createMarkAsDeliveredRequest() {
        return new OrderStatusUpdateRequest(OrderStatus.DELIVERED, null);
    }

    public static OrderStatusUpdateRequest createCancelRequest() {
        return new OrderStatusUpdateRequest(OrderStatus.CANCELLED, cancelReason);
    }
}