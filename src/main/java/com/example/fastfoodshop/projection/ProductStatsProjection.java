package com.example.fastfoodshop.projection;

import java.math.BigDecimal;

public interface ProductStatsProjection {
    String getName();

    BigDecimal getTotalRevenue();

    Long getTotalQuantitySold();
}
