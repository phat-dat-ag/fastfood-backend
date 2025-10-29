package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByDeliveredAtIsNullAndCancelledAtIsNull();

    List<Order> findByUserAndDeliveredAtIsNullAndCancelledAtIsNull(User user);

    @Query("SELECT o FROM Order o WHERE o.user = :user AND (o.deliveredAt IS NOT NULL OR o.cancelledAt IS NOT NULL)")
    List<Order> findCompletedOrCancelledOrdersByUser(@Param("user") User user);

}
