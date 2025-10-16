package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.dto.PromotionDTO;
import com.example.fastfoodshop.request.PromotionCreateRequest;
import com.example.fastfoodshop.response.PromotionResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/admin/promotion")
@RequiredArgsConstructor
public class PromotionController {
    private final PromotionService promotionService;

    @PostMapping("/category")
    public ResponseEntity<ResponseWrapper<PromotionDTO>> createPromotionCategory
            (@RequestBody PromotionCreateRequest request) {
        return promotionService.createPromotionCategory(request);
    }

    @GetMapping("/category")
    public ResponseEntity<ResponseWrapper<PromotionResponse>> getPromotionCategory() {
        return promotionService.getPromotionCategory();
    }

    @DeleteMapping("/category")
    public ResponseEntity<ResponseWrapper<PromotionDTO>> deletePromotionCategory(
            @RequestParam("promotionId") Long promotionId
    ) {
        return promotionService.deletePromotionCategory(promotionId);
    }
}
