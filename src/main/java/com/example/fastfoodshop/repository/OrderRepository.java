package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.projection.OrderStatsProjection;
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

    @Query(value = """
            SELECT
                COUNT(CASE WHEN order_status = 'PENDING' THEN 1 END) AS pendingOrderAmount,
                COUNT(CASE WHEN order_status = 'CONFIRMED' THEN 1 END) AS confirmedOrderAmount,
                COUNT(CASE WHEN order_status = 'DELEVERING' THEN 1 END) AS deliveringOrderAmount,
                COUNT(CASE WHEN order_status = 'DELIVERED' THEN 1 END) AS deliveredOrderAmount,
                COUNT(CASE WHEN order_status = 'CANCELLED' THEN 1 END) AS cancelledOrderAmount,
            
                COUNT(CASE WHEN payment_method = 'CASH_ON_DELIVERY' THEN 1 END) AS cashOnDeliveryOrderAmount,
                COUNT(CASE WHEN payment_method = 'BANK_TRANSFER' THEN 1 END) AS bankTransferOrderAmount,
            
                COUNT(CASE WHEN promotion_id IS NOT NULL THEN 1 END) AS discountedOrderAmount,
            
                SUM(CASE WHEN delivered_at IS NOT NULL AND payment_method = 'CASH_ON_DELIVERY' THEN total_price END) AS cashOnDeliveryRevenue,
                SUM(CASE WHEN delivered_at IS NOT NULL AND payment_method = 'BANK_TRANSFER' THEN total_price END) AS bankTransferRevenue,
                SUM(CASE WHEN delivered_at IS NOT NULL THEN total_price END) AS totalRevenue
            FROM orders;
            """, nativeQuery = true)
    OrderStatsProjection getStats();
}
