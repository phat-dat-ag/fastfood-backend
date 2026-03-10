package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.enums.OrderStatus;
import com.example.fastfoodshop.enums.PaymentMethod;
import com.example.fastfoodshop.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO(
        Long id,
        OrderStatus orderStatus,
        LocalDateTime placedAt,
        LocalDateTime confirmedAt,
        LocalDateTime deliveringAt,
        LocalDateTime deliveredAt,
        LocalDateTime cancelledAt,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        int originalPrice,
        int subtotalPrice,
        int deliveryFee,
        int totalPrice,
        AddressDTO address,
        UserDTO user,
        List<OrderNoteDTO> orderNotes,
        List<OrderDetailDTO> orderDetails,
        List<ReviewDTO> reviews,
        PromotionDTO promotion,
        String clientSecret
) {
    private static OrderDTO create(Order order, String clientSecret) {
        List<OrderNoteDTO> orderNotes =
                order.getOrderNotes().stream()
                        .map(OrderNoteDTO::from)
                        .toList();

        List<OrderDetailDTO> orderDetails =
                order.getOrderDetails().stream()
                        .map(OrderDetailDTO::from)
                        .toList();

        List<ReviewDTO> reviews =
                order.getReviews().stream()
                        .map(ReviewDTO::new)
                        .toList();

        return new OrderDTO(
                order.getId(),
                order.getOrderStatus(),
                order.getPlacedAt(),
                order.getConfirmedAt(),
                order.getDeliveringAt(),
                order.getDeliveredAt(),
                order.getCancelledAt(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                order.getOriginalPrice(),
                order.getSubtotalPrice(),
                order.getDeliveryFee(),
                order.getTotalPrice(),
                order.getAddress() != null ? AddressDTO.from(order.getAddress()) : null,
                order.getUser() != null ? UserDTO.from(order.getUser()) : null,
                orderNotes,
                orderDetails,
                reviews,
                order.getPromotion() != null ? PromotionDTO.from(order.getPromotion()) : null,
                clientSecret
        );
    }

    public static OrderDTO from(Order order) {
        return create(order, null);
    }

    public static OrderDTO from(Order order, String clientSecret) {
        return create(order, clientSecret);
    }
}
