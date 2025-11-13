package com.example.fastfoodshop.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderStatsDTO {
    private Long pendingOrderAmount;
    private Long confirmedOrderAmount;
    private Long deliveringOrderAmount;
    private Long deliveredOrderAmount;
    private Long cancelledOrderAmount;

    private Long cashOnDeliveryOrderAmount;
    private Long bankTransferOrderAmount;

    private Long discountedOrderAmount;

    private BigDecimal cashOnDeliveryRevenue;
    private BigDecimal bankTransferRevenue;
    private BigDecimal totalRevenue;

    public OrderStatsDTO(
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
        this.pendingOrderAmount = pendingOrderAmount;
        this.confirmedOrderAmount = confirmedOrderAmount;
        this.deliveringOrderAmount = deliveringOrderAmount;
        this.deliveredOrderAmount = deliveredOrderAmount;
        this.cancelledOrderAmount = cancelledOrderAmount;
        this.cashOnDeliveryOrderAmount = cashOnDeliveryOrderAmount;
        this.bankTransferOrderAmount = bankTransferOrderAmount;
        this.discountedOrderAmount = discountedOrderAmount;
        this.cashOnDeliveryRevenue = cashOnDeliveryRevenue;
        this.bankTransferRevenue = bankTransferRevenue;
        this.totalRevenue = totalRevenue;
    }
}
