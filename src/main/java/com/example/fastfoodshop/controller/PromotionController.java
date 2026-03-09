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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/admin/promotion")
@RequiredArgsConstructor
public class PromotionController extends BaseController {
    private final PromotionService promotionService;

    @PostMapping("/category")
    public ResponseEntity<ResponseWrapper<PromotionDTO>> createPromotionCategory
            (@RequestBody PromotionCreateRequest promotionCreateRequest) {
        return okResponse(promotionService.createPromotionCategory(promotionCreateRequest));
    }

    @PostMapping("/product")
    public ResponseEntity<ResponseWrapper<PromotionDTO>> createPromotionProduct
            (@RequestBody PromotionCreateRequest promotionCreateRequest) {
        return okResponse(promotionService.createPromotionProduct(promotionCreateRequest));
    }

    @PostMapping("/order")
    public ResponseEntity<ResponseWrapper<PromotionDTO>> createPromotionOrder
            (@RequestBody PromotionCreateRequest promotionCreateRequest) {
        return okResponse(promotionService.createPromotionOrder(promotionCreateRequest));
    }

    @GetMapping("/category")
    public ResponseEntity<ResponseWrapper<PromotionResponse>> getPromotionCategory(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(promotionService.getPromotionCategory(request.getPage(), request.getSize()));
    }

    @GetMapping("/product")
    public ResponseEntity<ResponseWrapper<PromotionResponse>> getPromotionProduct(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(promotionService.getPromotionProduct(request.getPage(), request.getSize()));
    }

    @GetMapping("/order")
    public ResponseEntity<ResponseWrapper<PromotionResponse>> getPromotionOrder(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(promotionService.getPromotionOrder(request.getPage(), request.getSize()));
    }

    @GetMapping("/order/valid")
    public ResponseEntity<ResponseWrapper<ArrayList<PromotionDTO>>> getValidPromotionOrder(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return okResponse(promotionService.getValidPromotionOrder(userDetails.getUsername()));
    }

    @PutMapping("/activate")
    public ResponseEntity<ResponseWrapper<String>> activatePromotion(@RequestParam("promotionId") Long promotionId) {
        return okResponse(promotionService.activatePromotion(promotionId));
    }

    @PutMapping("/deactivate")
    public ResponseEntity<ResponseWrapper<String>> deactivatePromotion(@RequestParam("promotionId") Long promotionId) {
        return okResponse(promotionService.deactivatePromotion(promotionId));
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<PromotionDTO>> deletePromotionCategory(
            @RequestParam("promotionId") Long promotionId
    ) {
        return okResponse(promotionService.deletePromotion(promotionId));
    }
}
