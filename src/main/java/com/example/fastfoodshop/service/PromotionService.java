package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.PromotionCodeCheckResultDTO;
import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.entity.Quiz;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.PromotionQueryType;
import com.example.fastfoodshop.request.PromotionCreateRequest;
import com.example.fastfoodshop.response.promotion.PromotionPageResponse;
import com.example.fastfoodshop.response.promotion.PromotionOrdersResponse;
import com.example.fastfoodshop.response.promotion.PromotionResponse;
import com.example.fastfoodshop.response.promotion.PromotionUpdateResponse;

public interface PromotionService {
    Promotion findPromotionOrThrow(Long promotionId);

    void increasePromotionUsageCount(Long promotionId);

    PromotionCodeCheckResultDTO checkPromotionCode(String promotionCode, int orderPrice);

    PromotionResponse createPromotion(PromotionCreateRequest promotionCreateRequest);

    PromotionPageResponse getPromotions(PromotionQueryType promotionQueryType, int page, int size);

    PromotionOrdersResponse getValidPromotions(String phone);

    PromotionUpdateResponse updatePromotionActivation(Long promotionId, boolean activated);

    PromotionUpdateResponse deletePromotion(Long promotionId);

    Promotion grantPromotion(User user, Quiz quiz);
}
