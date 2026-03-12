package com.example.fastfoodshop.response.review;

import com.example.fastfoodshop.dto.ReviewDTO;

import java.util.List;

public record ReviewProductsResponse(
        List<ReviewDTO> reviewProducts
) {
}
