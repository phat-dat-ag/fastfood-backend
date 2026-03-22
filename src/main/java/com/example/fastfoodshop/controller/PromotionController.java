package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.enums.PromotionQueryType;
import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.request.PromotionCreateRequest;
import com.example.fastfoodshop.request.UpdateActivationRequest;
import com.example.fastfoodshop.response.promotion.PromotionPageResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.promotion.PromotionResponse;
import com.example.fastfoodshop.response.promotion.PromotionUpdateResponse;
import com.example.fastfoodshop.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController
@RequestMapping("/api/admin/promotions")
@RequiredArgsConstructor
public class PromotionController extends BaseController {
    private final PromotionService promotionService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<PromotionResponse>> createPromotion(
            @RequestBody @Valid PromotionCreateRequest promotionCreateRequest
    ) {
        return okResponse(promotionService.createPromotion(promotionCreateRequest));
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<PromotionPageResponse>> getPromotions(
            @Valid @ModelAttribute PageRequest pageRequest,
            @RequestParam("promotionQueryType") PromotionQueryType promotionQueryType
    ) {
        return okResponse(promotionService.getPromotions(
                promotionQueryType, pageRequest.getPage(), pageRequest.getSize()
        ));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseWrapper<PromotionUpdateResponse>> updatePromotionActivation(
            @PathVariable("id") Long promotionId,
            @RequestBody @Valid UpdateActivationRequest updateActivationRequest
    ) {
        return okResponse(promotionService.updatePromotionActivation(
                promotionId, updateActivationRequest.activated()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<PromotionUpdateResponse>> deletePromotion(
            @PathVariable("id") Long promotionId
    ) {
        return okResponse(promotionService.deletePromotion(promotionId));
    }
}
