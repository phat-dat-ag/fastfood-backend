package com.example.fastfoodshop.factory.order;

import com.example.fastfoodshop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

public class OrderPageFactory {
    public static Page<Order> createOrderPage(List<Order> orders) {
        return new PageImpl<>(orders);
    }
}