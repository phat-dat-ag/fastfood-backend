package com.example.fastfoodshop.response.promotion;

import com.example.fastfoodshop.dto.PromotionDTO;

import java.util.List;

public record PromotionOrdersResponse(
        List<PromotionDTO> promotionOrders
) {
}
