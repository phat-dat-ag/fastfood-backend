package com.example.fastfoodshop.response.promotion;

import com.example.fastfoodshop.dto.ItemPromotionDTO;
import com.example.fastfoodshop.projection.ItemPromotionProjection;

import java.util.ArrayList;
import java.util.List;

public record ItemPromotionResponse(
        List<ItemPromotionDTO> categoryPromotions,
        List<ItemPromotionDTO> productPromotions
) {
    public static ItemPromotionResponse from(List<ItemPromotionProjection> categoryPromotionProjections,
                                             List<ItemPromotionProjection> productPromotionProjections) {

        List<ItemPromotionDTO> categoryPromotions = new ArrayList<>();
        List<ItemPromotionDTO> productPromotions = new ArrayList<>();

        for (ItemPromotionProjection categoryProjection : categoryPromotionProjections) {
            categoryPromotions.add(ItemPromotionDTO.from(categoryProjection));
        }
        for (ItemPromotionProjection productProjection : productPromotionProjections) {
            productPromotions.add(ItemPromotionDTO.from(productProjection));
        }

        return new ItemPromotionResponse(categoryPromotions, productPromotions);
    }
}
