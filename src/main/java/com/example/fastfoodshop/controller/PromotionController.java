package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.image.ItemPromotionResponse;
import com.example.fastfoodshop.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController extends BaseController {
    private final PromotionService promotionService;

    @GetMapping("/items")
    public ResponseEntity<ResponseWrapper<ItemPromotionResponse>> getItemPromotionItems() {
        return okResponse(promotionService.getItemPromotionItems());
    }
}
