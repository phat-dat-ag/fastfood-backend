package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public Order findOrderOrThrow(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
    }
}
