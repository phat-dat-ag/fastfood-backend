package com.example.fastfoodshop.response.category;

import com.example.fastfoodshop.dto.CategoryStatsDTO;

import java.util.List;

public record CategoryStatsResponse(
        List<CategoryStatsDTO> categoryStats
) {
}
