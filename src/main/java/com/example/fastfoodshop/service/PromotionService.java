package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.PromotionCodeCheckResultDTO;
import com.example.fastfoodshop.dto.PromotionDTO;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.repository.PromotionRepository;
import com.example.fastfoodshop.request.PromotionCreateRequest;
import com.example.fastfoodshop.response.PromotionResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;
    private final CategoryService categoryService;
    private final ProductService productService;

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

    public Promotion findPromotionOrThrow(String code) {
        return promotionRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Mã khuyến mãi không tồn tại"));
    }

    public void increasePromotionUsageCount(Promotion promotion) {
        if (promotion.getUsedQuantity() >= promotion.getQuantity()) {
            throw new RuntimeException("Mã khuyến mãi đã hết lượt sử dụng");
        }
        promotion.setUsedQuantity(promotion.getUsedQuantity() + 1);
        promotionRepository.save(promotion);
    }

    public PromotionCodeCheckResultDTO checkPromotionCode(String promotionCode, int orderPrice) {
        try {
            Promotion promotion = findPromotionOrThrow(promotionCode);
            if (!promotion.isGlobal())
                return PromotionCodeCheckResultDTO.error("Mã khuyến mãi này không áp dụng cho đơn hàng được");
            if (!promotion.isActivated())
                return PromotionCodeCheckResultDTO.error("Mã khuyến mãi này đã bị vô hiệu hóa");

            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(promotion.getStartAt()))
                return PromotionCodeCheckResultDTO.error("Mã khuyến mãi chưa có hiệu lực!");
            if (now.isAfter(promotion.getEndAt()))
                return PromotionCodeCheckResultDTO.error("Mã khuyến mãi đã hết hiệu lực");
            if (promotion.getUsedQuantity() >= promotion.getQuantity())
                return PromotionCodeCheckResultDTO.error("Đã hết lượt khuyến mãi!");
            if (promotion.getMinSpendAmount() > orderPrice)
                return PromotionCodeCheckResultDTO.error("Tổng đơn hàng chưa đủ điều kiện khuyến mãi!");

            return PromotionCodeCheckResultDTO.success("Đã áp dụng khuyến mãi!", new PromotionDTO(promotion));

        } catch (Exception e) {
            return PromotionCodeCheckResultDTO.error("Áp dụng khuyến mãi thất bại: " + e.getMessage());
        }
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

    public ResponseEntity<ResponseWrapper<PromotionDTO>> createPromotionProduct(PromotionCreateRequest request) {
        try {
            if (checkUniqueCode(request.getCode())) {
                return ResponseEntity.badRequest().body(ResponseWrapper.error(
                                "CREATE_PROMOTION_FAILED",
                                "Mã khuyến mãi đã tồn tại"
                        )
                );
            }
            Product product = productService.findProductOrThrow(request.getProductId());

            Promotion promotion = buildPromotionCategoryFromRequest(request);
            promotion.setProduct(product);
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

    public ResponseEntity<ResponseWrapper<PromotionDTO>> createPromotionOrder(PromotionCreateRequest request) {
        try {
            if (checkUniqueCode(request.getCode())) {
                return ResponseEntity.badRequest().body(ResponseWrapper.error(
                                "CREATE_PROMOTION_FAILED",
                                "Mã khuyến mãi đã tồn tại"
                        )
                );
            }

            Promotion promotion = buildPromotionCategoryFromRequest(request);
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

    public ResponseEntity<ResponseWrapper<PromotionResponse>> getPromotionProduct() {
        try {
            List<Promotion> productPromotions = promotionRepository.findByProductIsNotNullAndIsDeletedFalse();
            return ResponseEntity.ok(ResponseWrapper.success(new PromotionResponse(productPromotions)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "GET_PROMOTION_PRODUCT_FAILED",
                            "Lỗi khi lấy mã khuyến mãi theo sản phẩm " + e.getMessage()
                    )
            );
        }
    }

    public ResponseEntity<ResponseWrapper<PromotionResponse>> getPromotionOrder() {
        try {
            List<Promotion> orderPromotions = promotionRepository.findGlobalOrderPromotions();
            return ResponseEntity.ok(ResponseWrapper.success(new PromotionResponse(orderPromotions)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "GET_PROMOTION_PRODUCT_FAILED",
                            "Lỗi khi lấy mã khuyến mãi cho đơn hàng " + e.getMessage()
                    )
            );
        }
    }

    public ResponseEntity<ResponseWrapper<PromotionResponse>> getValidPromotionOrder() {
        try {
            List<Promotion> orderPromotions = promotionRepository.findGlobalOrderPromotions();
            List<Promotion> validOrderPromotions = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();

            for (Promotion promotion : orderPromotions) {
                if (!promotion.isDeleted() && promotion.isActivated() && now.isAfter(promotion.getStartAt()) && now.isBefore(promotion.getEndAt())) {
                    validOrderPromotions.add(promotion);
                }
            }
            return ResponseEntity.ok(ResponseWrapper.success(new PromotionResponse(validOrderPromotions)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "GET_PROMOTION_PRODUCT_FAILED",
                            "Lỗi khi lấy mã khuyến mãi cho đơn hàng " + e.getMessage()
                    )
            );
        }
    }

    public ResponseEntity<ResponseWrapper<PromotionDTO>> deletePromotion(Long promotionId) {
        try {
            Promotion promotion = findPromotionOrThrow(promotionId);
            promotion.setDeleted(true);
            Promotion deletedPromotion = promotionRepository.save(promotion);
            return ResponseEntity.ok(ResponseWrapper.success(new PromotionDTO(deletedPromotion)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "DELETE_PROMOTION_FAILED",
                            "Lỗi khi xóa mã khuyến mãi " + e.getMessage()
                    )
            );
        }
    }
}
