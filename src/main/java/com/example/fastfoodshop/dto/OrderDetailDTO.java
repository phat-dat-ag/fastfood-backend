package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.OrderDetail;
import lombok.Data;

@Data
public class OrderDetailDTO {
    private int quantity;
    private int discountedPrice;
    private ProductDTO product;

    public OrderDetailDTO(OrderDetail orderDetail) {
        this.quantity = orderDetail.getQuantity();
        this.discountedPrice = orderDetail.getDiscountedPrice();
        this.product = new ProductDTO(orderDetail.getProduct());
    }
}
