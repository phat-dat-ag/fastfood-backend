package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.PromotionDTO;
import com.example.fastfoodshop.entity.Promotion;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PromotionResponse {
    ArrayList<PromotionDTO> promotions = new ArrayList<>();

    public PromotionResponse(List<Promotion> promotionList) {
        for (Promotion promotion : promotionList) {
            this.promotions.add(new PromotionDTO(promotion));
        }
    }
}
