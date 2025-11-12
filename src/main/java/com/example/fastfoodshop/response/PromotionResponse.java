package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.PromotionDTO;
import com.example.fastfoodshop.entity.Promotion;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
public class PromotionResponse {
    List<PromotionDTO> promotions = new ArrayList<>();
    private int currentPage;
    private int pageSize;
    private long totalItems;
    private int totalPages;

    public PromotionResponse(Page<Promotion> page) {
        for (Promotion promotion : page.getContent()) {
            this.promotions.add(new PromotionDTO(promotion));
        }
        this.currentPage = page.getNumber();
        this.pageSize = page.getSize();
        this.totalItems = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}
