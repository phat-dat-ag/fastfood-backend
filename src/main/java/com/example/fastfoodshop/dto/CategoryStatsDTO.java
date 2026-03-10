package com.example.fastfoodshop.dto;

import java.math.BigDecimal;

public record CategoryStatsDTO(
        String name,
        BigDecimal totalRevenue,
        Long totalQuantitySold
) {
}
