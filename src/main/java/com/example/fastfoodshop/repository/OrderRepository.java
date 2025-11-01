package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByDeliveredAtIsNullAndCancelledAtIsNull();

    List<Order> findByUserAndDeliveredAtIsNullAndCancelledAtIsNull(User user);

    @Query("SELECT o FROM Order o WHERE o.user = :user AND (o.deliveredAt IS NOT NULL OR o.cancelledAt IS NOT NULL)")
    List<Order> findCompletedOrCancelledOrdersByUser(@Param("user") User user);

    @Query("""
                SELECT o
                FROM Order o
                WHERE o.id = :orderId
                  AND o.user = :user
                  AND (o.deliveredAt IS NOT NULL OR o.cancelledAt IS NOT NULL)
            """)
    Optional<Order> findCompletedOrCancelledOrderByIdAndUser(@Param("orderId") Long orderId, @Param("user") User user);
}
