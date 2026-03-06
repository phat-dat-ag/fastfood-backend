package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.PromotionCodeCheckResultDTO;
import com.example.fastfoodshop.dto.PromotionDTO;
import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.entity.Quiz;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.request.PromotionCreateRequest;
import com.example.fastfoodshop.response.PromotionResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

public interface PromotionService {
    Promotion findPromotionOrThrow(Long id);

    void increasePromotionUsageCount(Long promotionId);

    PromotionCodeCheckResultDTO checkPromotionCode(String promotionCode, int orderPrice);

    ResponseEntity<ResponseWrapper<PromotionDTO>> createPromotionCategory(PromotionCreateRequest request);

    ResponseEntity<ResponseWrapper<PromotionDTO>> createPromotionProduct(PromotionCreateRequest request);

    ResponseEntity<ResponseWrapper<PromotionDTO>> createPromotionOrder(PromotionCreateRequest request);

    ResponseEntity<ResponseWrapper<PromotionResponse>> getPromotionCategory(int page, int size);

    ResponseEntity<ResponseWrapper<PromotionResponse>> getPromotionProduct(int page, int size);

    ResponseEntity<ResponseWrapper<PromotionResponse>> getPromotionOrder(int page, int size);

    ResponseEntity<ResponseWrapper<ArrayList<PromotionDTO>>> getValidPromotionOrder(String phone);

    ResponseEntity<ResponseWrapper<String>> activatePromotion(Long promotionId);

    ResponseEntity<ResponseWrapper<String>> deactivatePromotion(Long promotionId);

    ResponseEntity<ResponseWrapper<PromotionDTO>> deletePromotion(Long promotionId);

    Promotion grantPromotion(User user, Quiz quiz);
}
