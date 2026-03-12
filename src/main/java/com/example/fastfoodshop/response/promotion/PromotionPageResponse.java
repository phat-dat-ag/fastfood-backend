package com.example.fastfoodshop.response.promotion;

import com.example.fastfoodshop.dto.PromotionDTO;
import com.example.fastfoodshop.entity.Promotion;
import org.springframework.data.domain.Page;

import java.util.List;

public record PromotionPageResponse(
        List<PromotionDTO> promotions,
        int currentPage,
        int pageSize,
        long totalItems,
        int totalPages
) {
    public static PromotionPageResponse from(Page<Promotion> page) {
        List<PromotionDTO> promotions = page.getContent().stream().map(PromotionDTO::from).toList();

        return new PromotionPageResponse(
                promotions,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
