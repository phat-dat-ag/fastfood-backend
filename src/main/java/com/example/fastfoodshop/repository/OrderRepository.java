package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByDeliveredAtIsNull();
}
