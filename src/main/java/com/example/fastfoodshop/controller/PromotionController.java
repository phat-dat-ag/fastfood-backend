package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.dto.PromotionDTO;
import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.request.PromotionCreateRequest;
import com.example.fastfoodshop.response.PromotionResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

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

    @PostMapping("/order")
    public ResponseEntity<ResponseWrapper<PromotionDTO>> createPromotionOrder
            (@RequestBody PromotionCreateRequest request) {
        return promotionService.createPromotionOrder(request);
    }

    @GetMapping("/category")
    public ResponseEntity<ResponseWrapper<PromotionResponse>> getPromotionCategory(
            @Valid @ModelAttribute PageRequest request
    ) {
        return promotionService.getPromotionCategory(request.getPage(), request.getSize());
    }

    @GetMapping("/product")
    public ResponseEntity<ResponseWrapper<PromotionResponse>> getPromotionProduct(
            @Valid @ModelAttribute PageRequest request
    ) {
        return promotionService.getPromotionProduct(request.getPage(), request.getSize());
    }

    @GetMapping("/order")
    public ResponseEntity<ResponseWrapper<PromotionResponse>> getPromotionOrder(
            @Valid @ModelAttribute PageRequest request
    ) {
        return promotionService.getPromotionOrder(request.getPage(), request.getSize());
    }

    @GetMapping("/order/valid")
    public ResponseEntity<ResponseWrapper<ArrayList<PromotionDTO>>> getValidPromotionOrder(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return promotionService.getValidPromotionOrder(userDetails.getUsername());
    }

    @PutMapping("/activate")
    public ResponseEntity<ResponseWrapper<String>> activatePromotion(@RequestParam("promotionId") Long promotionId) {
        return promotionService.activatePromotion(promotionId);
    }

    @PutMapping("/deactivate")
    public ResponseEntity<ResponseWrapper<String>> deactivatePromotion(@RequestParam("promotionId") Long promotionId) {
        return promotionService.deactivatePromotion(promotionId);
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<PromotionDTO>> deletePromotionCategory(
            @RequestParam("promotionId") Long promotionId
    ) {
        return promotionService.deletePromotion(promotionId);
    }
}
