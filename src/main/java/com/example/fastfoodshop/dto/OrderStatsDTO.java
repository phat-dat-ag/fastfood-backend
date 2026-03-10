package com.example.fastfoodshop.dto;

import java.math.BigDecimal;

public record OrderStatsDTO(
        Long pendingOrderAmount,
        Long confirmedOrderAmount,
        Long deliveringOrderAmount,
        Long deliveredOrderAmount,
        Long cancelledOrderAmount,

        Long cashOnDeliveryOrderAmount,
        Long bankTransferOrderAmount,

        Long discountedOrderAmount,

        BigDecimal cashOnDeliveryRevenue,
        BigDecimal bankTransferRevenue,
        BigDecimal totalRevenue
) {
}
