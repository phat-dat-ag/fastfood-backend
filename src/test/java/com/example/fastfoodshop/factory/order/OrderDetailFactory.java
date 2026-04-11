package com.example.fastfoodshop.factory.order;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.OrderDetail;
import com.example.fastfoodshop.entity.Product;

public class OrderDetailFactory {
    public static OrderDetail createValid(Order order, Product product, int quantity, int discountedPrice) {
        OrderDetail orderDetail = new OrderDetail();

        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        orderDetail.setQuantity(quantity);
        orderDetail.setDiscountedPrice(discountedPrice);

        return orderDetail;
    }
}
