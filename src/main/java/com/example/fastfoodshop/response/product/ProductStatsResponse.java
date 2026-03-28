package com.example.fastfoodshop.response.product;

import com.example.fastfoodshop.dto.ProductStatsDTO;

import java.util.List;

public record ProductStatsResponse(
        List<ProductStatsDTO> productStats
) {
}
