package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.ItemPromotionDTO;
import com.example.fastfoodshop.projection.ItemPromotionProjection;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemPromotionResponse {
    private List<ItemPromotionDTO> categoryPromotions = new ArrayList<>();
    private List<ItemPromotionDTO> productPromotions = new ArrayList<>();

    public ItemPromotionResponse(List<ItemPromotionProjection> categoryPromotionProjections,
                                 List<ItemPromotionProjection> productPromotionProjections) {
        for (ItemPromotionProjection categoryProjection : categoryPromotionProjections) {
            this.categoryPromotions.add(new ItemPromotionDTO(categoryProjection));
        }
        for (ItemPromotionProjection productProjection : productPromotionProjections) {
            this.productPromotions.add(new ItemPromotionDTO(productProjection));
        }
    }
}
