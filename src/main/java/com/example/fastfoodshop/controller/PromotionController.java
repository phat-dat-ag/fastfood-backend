package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.request.PromotionCreateRequest;
import com.example.fastfoodshop.response.promotion.PromotionPageResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.promotion.PromotionOrdersResponse;
import com.example.fastfoodshop.response.promotion.PromotionResponse;
import com.example.fastfoodshop.response.promotion.PromotionUpdateResponse;
import com.example.fastfoodshop.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ModelAttribute;

@RestController
@RequestMapping("/api/admin/promotion")
@RequiredArgsConstructor
public class PromotionController extends BaseController {
    private final PromotionService promotionService;

    @PostMapping("/category")
    public ResponseEntity<ResponseWrapper<PromotionResponse>> createPromotionCategory
            (@RequestBody PromotionCreateRequest promotionCreateRequest) {
        return okResponse(promotionService.createPromotionCategory(promotionCreateRequest));
    }

    @PostMapping("/product")
    public ResponseEntity<ResponseWrapper<PromotionResponse>> createPromotionProduct
            (@RequestBody PromotionCreateRequest promotionCreateRequest) {
        return okResponse(promotionService.createPromotionProduct(promotionCreateRequest));
    }

    @PostMapping("/order")
    public ResponseEntity<ResponseWrapper<PromotionResponse>> createPromotionOrder
            (@RequestBody PromotionCreateRequest promotionCreateRequest) {
        return okResponse(promotionService.createPromotionOrder(promotionCreateRequest));
    }

    @GetMapping("/category")
    public ResponseEntity<ResponseWrapper<PromotionPageResponse>> getPromotionCategory(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(promotionService.getPromotionCategory(request.getPage(), request.getSize()));
    }

    @GetMapping("/product")
    public ResponseEntity<ResponseWrapper<PromotionPageResponse>> getPromotionProduct(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(promotionService.getPromotionProduct(request.getPage(), request.getSize()));
    }

    @GetMapping("/order")
    public ResponseEntity<ResponseWrapper<PromotionPageResponse>> getPromotionOrder(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(promotionService.getPromotionOrder(request.getPage(), request.getSize()));
    }

    @GetMapping("/order/valid")
    public ResponseEntity<ResponseWrapper<PromotionOrdersResponse>> getValidPromotionOrder(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return okResponse(promotionService.getValidPromotionOrder(userDetails.getUsername()));
    }

    @PutMapping("/activate")
    public ResponseEntity<ResponseWrapper<PromotionUpdateResponse>> activatePromotion(
            @RequestParam("promotionId") Long promotionId
    ) {
        return okResponse(promotionService.activatePromotion(promotionId));
    }

    @PutMapping("/deactivate")
    public ResponseEntity<ResponseWrapper<PromotionUpdateResponse>> deactivatePromotion(
            @RequestParam("promotionId") Long promotionId
    ) {
        return okResponse(promotionService.deactivatePromotion(promotionId));
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<PromotionUpdateResponse>> deletePromotionCategory(
            @RequestParam("promotionId") Long promotionId
    ) {
        return okResponse(promotionService.deletePromotion(promotionId));
    }
}
