package com.example.fastfoodshop.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductStatsDTO {
    private String name;
    private BigDecimal totalRevenue;
    private Long totalQuantitySold;

    public ProductStatsDTO(String name, BigDecimal totalRevenue, Long totalQuantitySold) {
        this.name = name;
        this.totalRevenue = totalRevenue;
        this.totalQuantitySold = totalQuantitySold;
    }
}
