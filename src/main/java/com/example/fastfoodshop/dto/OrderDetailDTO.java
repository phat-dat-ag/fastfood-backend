package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.OrderDetail;

public record OrderDetailDTO(
        int quantity,
        int discountedPrice,
        ProductDTO product
) {
    public static OrderDetailDTO from(OrderDetail orderDetail) {
        return new OrderDetailDTO(
                orderDetail.getQuantity(),
                orderDetail.getDiscountedPrice(),
                ProductDTO.from(orderDetail.getProduct())
        );
    }
}
