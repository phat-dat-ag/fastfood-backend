package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByDeliveredAtIsNullAndCancelledAtIsNull(Pageable pageable);

    Optional<Order> findByIdAndDeliveredAtIsNullAndCancelledAtIsNull(Long orderId);

    Page<Order> findByUserAndDeliveredAtIsNullAndCancelledAtIsNull(User user, Pageable pageable);

    Optional<Order> findByIdAndUserAndDeliveredAtIsNullAndCancelledAtIsNull(Long orderId, User user);

    @Query("SELECT o FROM Order o WHERE o.user = :user AND (o.deliveredAt IS NOT NULL OR o.cancelledAt IS NOT NULL)")
    Page<Order> findCompletedOrCancelledOrdersByUser(@Param("user") User user, Pageable pageable);

    @Query("""
                SELECT o
                FROM Order o
                WHERE o.id = :orderId
                  AND o.user = :user
                  AND (o.deliveredAt IS NOT NULL OR o.cancelledAt IS NOT NULL)
            """)
    Optional<Order> findCompletedOrCancelledOrderByIdAndUser(@Param("orderId") Long orderId, @Param("user") User user);
}
