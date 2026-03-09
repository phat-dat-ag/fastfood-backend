package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.PromotionCodeCheckResultDTO;
import com.example.fastfoodshop.dto.PromotionDTO;
import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.entity.Quiz;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.request.PromotionCreateRequest;
import com.example.fastfoodshop.response.PromotionResponse;

import java.util.ArrayList;

public interface PromotionService {
    Promotion findPromotionOrThrow(Long promotionId);

    void increasePromotionUsageCount(Long promotionId);

    PromotionCodeCheckResultDTO checkPromotionCode(String promotionCode, int orderPrice);

    PromotionDTO createPromotionCategory(PromotionCreateRequest promotionCreateRequest);

    PromotionDTO createPromotionProduct(PromotionCreateRequest promotionCreateRequest);

    PromotionDTO createPromotionOrder(PromotionCreateRequest promotionCreateRequest);

    PromotionResponse getPromotionCategory(int page, int size);

    PromotionResponse getPromotionProduct(int page, int size);

    PromotionResponse getPromotionOrder(int page, int size);

    ArrayList<PromotionDTO> getValidPromotionOrder(String phone);

    String activatePromotion(Long promotionId);

    String deactivatePromotion(Long promotionId);

    PromotionDTO deletePromotion(Long promotionId);

    Promotion grantPromotion(User user, Quiz quiz);
}
