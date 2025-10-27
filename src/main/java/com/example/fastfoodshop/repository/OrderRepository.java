package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
