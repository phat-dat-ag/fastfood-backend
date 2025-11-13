package com.example.fastfoodshop.projection;

import java.math.BigDecimal;

public interface CategoryStatsProjection {
    String getName();

    BigDecimal getTotalRevenue();

    Long getTotalQuantitySold();
}
