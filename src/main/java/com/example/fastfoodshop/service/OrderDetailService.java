package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.CartDTO;
import com.example.fastfoodshop.entity.Order;

public interface OrderDetailService {
    void createOrderDetail(CartDTO cartDTO, Order order);
}
