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

    @PostMapping("/product")
    public ResponseEntity<ResponseWrapper<PromotionDTO>> createPromotionProduct
            (@RequestBody PromotionCreateRequest request) {
        return promotionService.createPromotionProduct(request);
    }

    @GetMapping("/category")
    public ResponseEntity<ResponseWrapper<PromotionResponse>> getPromotionCategory() {
        return promotionService.getPromotionCategory();
    }

    @GetMapping("/product")
    public ResponseEntity<ResponseWrapper<PromotionResponse>> getPromotionProduct() {
        return promotionService.getPromotionProduct();
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<PromotionDTO>> deletePromotionCategory(
            @RequestParam("promotionId") Long promotionId
    ) {
        return promotionService.deletePromotion(promotionId);
    }
}
