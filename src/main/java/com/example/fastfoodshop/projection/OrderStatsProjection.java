package com.example.fastfoodshop.projection;

import java.math.BigDecimal;

public interface OrderStatsProjection {
    Long getPendingOrderAmount();

    Long getConfirmedOrderAmount();

    Long getDeliveringOrderAmount();

    Long getDeliveredOrderAmount();

    Long getCancelledOrderAmount();

    Long getCashOnDeliveryOrderAmount();

    Long getBankTransferOrderAmount();

    Long getDiscountedOrderAmount();

    BigDecimal getCashOnDeliveryRevenue();

    BigDecimal getBankTransferRevenue();

    BigDecimal getTotalRevenue();
}
