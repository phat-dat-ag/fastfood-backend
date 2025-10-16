package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.PromotionDTO;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.repository.PromotionRepository;
import com.example.fastfoodshop.request.PromotionCreateRequest;
import com.example.fastfoodshop.response.PromotionResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;
    private final CategoryService categoryService;

    public boolean checkUniqueCode(String code) {
        return promotionRepository.findByCode(code).isPresent();
    }

    private Promotion buildPromotionCategoryFromRequest(PromotionCreateRequest request) {
        Promotion promotion = new Promotion();
        promotion.setType(request.getType());
        promotion.setValue(request.getValue());
        promotion.setStartAt(request.getStartAt());
        promotion.setEndAt(request.getEndAt());
        promotion.setQuantity(request.getQuantity());
        promotion.setUsedQuantity(0);
        promotion.setMaxDiscountAmount(request.getMaxDiscountAmount());
        promotion.setMinSpendAmount(request.getMinSpendAmount());
        promotion.setCode(request.getCode());
        promotion.setGlobal(request.getIsGlobal());
        promotion.setActivated(request.getIsActivated());
        promotion.setDeleted(false);
        return promotion;
    }

    public Promotion findPromotionOrThrow(Long id) {
        return promotionRepository.findById(id).orElseThrow(() -> new RuntimeException("Mã khuyến mãi không tồn tại"));
    }

    public ResponseEntity<ResponseWrapper<PromotionDTO>> createPromotionCategory(PromotionCreateRequest request) {
        try {
            if (checkUniqueCode(request.getCode())) {
                return ResponseEntity.badRequest().body(ResponseWrapper.error(
                                "CREATE_PROMOTION_FAILED",
                                "Mã khuyến mãi đã tồn tại"
                        )
                );
            }
            Category category = categoryService.findCategoryOrThrow(request.getCategoryId());

            Promotion promotion = buildPromotionCategoryFromRequest(request);
            promotion.setCategory(category);
            Promotion savedPromotion = promotionRepository.save(promotion);
            return ResponseEntity.ok(ResponseWrapper.success(new PromotionDTO(savedPromotion)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "CREATE_PROMOTION_FAILED",
                            "Lỗi khi tạo mã khuyến mãi " + e.getMessage()
                    )
            );
        }
    }

    public ResponseEntity<ResponseWrapper<PromotionResponse>> getPromotionCategory() {
        try {
            List<Promotion> categoryPromotions = promotionRepository.findByCategoryIsNotNullAndIsDeletedFalse();
            return ResponseEntity.ok(ResponseWrapper.success(new PromotionResponse(categoryPromotions)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "GET_PROMOTION_CATEGORY_FAILED",
                            "Lỗi khi lấy mã khuyến mãi theo danh mục " + e.getMessage()
                    )
            );
        }
    }

    public ResponseEntity<ResponseWrapper<PromotionDTO>> deletePromotionCategory(Long promotionId) {
        try {
            Promotion promotion = findPromotionOrThrow(promotionId);
            promotion.setDeleted(true);
            Promotion deletedPromotion = promotionRepository.save(promotion);
            return ResponseEntity.ok(ResponseWrapper.success(new PromotionDTO(deletedPromotion)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "DELETE_PROMOTION_CATEGORY_FAILED",
                            "Lỗi khi xóa mã khuyến mãi theo danh mục " + e.getMessage()
                    )
            );
        }
    }
}
