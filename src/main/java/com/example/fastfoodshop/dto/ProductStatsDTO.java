package com.example.fastfoodshop.dto;

import java.math.BigDecimal;

public record ProductStatsDTO(
        String name,
        BigDecimal totalRevenue,
        Long totalQuantitySold
) {
}
