package com.example.fastfoodshop.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CategoryStatsDTO {
    private String name;
    private BigDecimal totalRevenue;
    private Long totalQuantitySold;

    public CategoryStatsDTO(String name, BigDecimal totalRevenue, Long totalQuantitySold) {
        this.name = name;
        this.totalRevenue = totalRevenue;
        this.totalQuantitySold = totalQuantitySold;
    }
}
