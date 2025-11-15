package com.example.fastfoodshop.service;

import com.example.fastfoodshop.constant.QuizConstants;
import com.example.fastfoodshop.dto.PromotionCodeCheckResultDTO;
import com.example.fastfoodshop.dto.PromotionDTO;
import com.example.fastfoodshop.entity.*;
import com.example.fastfoodshop.enums.PromotionType;
import com.example.fastfoodshop.repository.AwardRepository;
import com.example.fastfoodshop.repository.PromotionRepository;
import com.example.fastfoodshop.request.PromotionCreateRequest;
import com.example.fastfoodshop.response.PromotionResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.util.NumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final AwardRepository awardRepository;
    private final AwardService awardService;
    private final UserService userService;

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

    private Promotion findUndeletedPromotionOrThrow(Long id) {
        return promotionRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new RuntimeException("Mã khuyến mãi không tồn tại hoặc đã bị xóa"));
    }

    public Promotion findPromotionOrThrow(String code) {
        return promotionRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Mã khuyến mãi không tồn tại"));
    }

    public void increasePromotionUsageCount(Long promotionId) {
        Promotion promotion = findPromotionOrThrow(promotionId);
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

    public ResponseEntity<ResponseWrapper<PromotionResponse>> getPromotionCategory(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Promotion> categoryPromotionPage = promotionRepository.findByCategoryIsNotNullAndIsDeletedFalse(pageable);
            return ResponseEntity.ok(ResponseWrapper.success(new PromotionResponse(categoryPromotionPage)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "GET_PROMOTION_CATEGORY_FAILED",
                            "Lỗi khi lấy mã khuyến mãi theo danh mục " + e.getMessage()
                    )
            );
        }
    }

    public ResponseEntity<ResponseWrapper<PromotionResponse>> getPromotionProduct(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Promotion> productPromotionPage = promotionRepository.findByProductIsNotNullAndIsDeletedFalse(pageable);
            return ResponseEntity.ok(ResponseWrapper.success(new PromotionResponse(productPromotionPage)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "GET_PROMOTION_PRODUCT_FAILED",
                            "Lỗi khi lấy mã khuyến mãi theo sản phẩm " + e.getMessage()
                    )
            );
        }
    }

    public ResponseEntity<ResponseWrapper<PromotionResponse>> getPromotionOrder(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Promotion> orderPromotionPage = promotionRepository.findGlobalOrderPromotions(pageable);
            return ResponseEntity.ok(ResponseWrapper.success(new PromotionResponse(orderPromotionPage)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "GET_PROMOTION_PRODUCT_FAILED",
                            "Lỗi khi lấy mã khuyến mãi cho đơn hàng " + e.getMessage()
                    )
            );
        }
    }

    public ResponseEntity<ResponseWrapper<ArrayList<PromotionDTO>>> getValidPromotionOrder(String phone) {
        try {
            User user = userService.findUserOrThrow(phone);
            List<Promotion> orderPromotions = promotionRepository.findGlobalOrderPromotionsByUser(user.getId(), LocalDateTime.now());
            ArrayList<PromotionDTO> validOrderPromotions = new ArrayList<>();

            for (Promotion promotion : orderPromotions) {
                validOrderPromotions.add(new PromotionDTO(promotion));
            }
            return ResponseEntity.ok(ResponseWrapper.success(validOrderPromotions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "GET_PROMOTION_PRODUCT_FAILED",
                            "Lỗi khi lấy mã khuyến mãi cho đơn hàng " + e.getMessage()
                    )
            );
        }
    }

    public ResponseEntity<ResponseWrapper<String>> activatePromotion(Long promotionId) {
        try {
            Promotion promotion = findUndeletedPromotionOrThrow(promotionId);
            if (promotion.isActivated()) {
                return ResponseEntity.badRequest().body(ResponseWrapper.error(
                        "ACTIVATE_PROMOTION_FAILED",
                        "Mã khuyến mãi này đã được kích hoạt"
                ));
            }
            promotion.setActivated(true);
            promotionRepository.save(promotion);
            return ResponseEntity.ok(ResponseWrapper.success("Đã kích hoạt mã khuyến mãi"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "ACTIVATE_PROMOTION_FAILED",
                    "Lỗi kích hoạt mã khuyến mãi " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<String>> deactivatePromotion(Long promotionId) {
        try {
            Promotion promotion = findUndeletedPromotionOrThrow(promotionId);
            if (!promotion.isActivated()) {
                return ResponseEntity.badRequest().body(ResponseWrapper.error(
                        "DEACTIVATE_PROMOTION_FAILED",
                        "Mã khuyến mãi này đã bị huủy kích hoạt"
                ));
            }
            promotion.setActivated(false);
            promotionRepository.save(promotion);
            return ResponseEntity.ok(ResponseWrapper.success("Đã hủy kích hoạt mã khuyến mãi"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "DEACTIVATE_PROMOTION_FAILED",
                    "Lỗi hủy kích hoạt mã khuyến mãi " + e.getMessage()
            ));
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

    private String generatePromotionCode(Long userId, Long quizId, LocalDateTime completedAt) {
        long uniqueNumber = completedAt
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        String timePart = Long.toString(uniqueNumber, 36).toUpperCase();

        return "PM-" + userId + "Q" + quizId + "-" + timePart;
    }

    public Promotion grantPromotion(User user, Quiz quiz) {
        if (quiz.getCompletedAt() == null)
            throw new RuntimeException("Lỗi tạo phần thưởng: Bài kiểm tra chưa hoàn thành");
        String promotionCode = generatePromotionCode(user.getId(), quiz.getId(), quiz.getCompletedAt());

        Award award = awardService.getRandomAwardByTopicDifficulty(quiz.getTopicDifficulty());
        int value = NumberUtils.randomNumber(award.getMinValue(), award.getMaxValue());
        value = award.getType() == PromotionType.PERCENTAGE ? value : NumberUtils.roundToThousand(value);

        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime endAt = startAt.plusDays(QuizConstants.PROMOTION_VALIDITY_DAYS);

        Promotion promotion = new Promotion();
        promotion.setUser(user);
        promotion.setType(award.getType());
        promotion.setValue(value);
        promotion.setStartAt(startAt);
        promotion.setEndAt(endAt);
        promotion.setUsedQuantity(0);
        promotion.setQuantity(1);
        promotion.setMaxDiscountAmount(award.getMaxDiscountAmount());
        promotion.setMinSpendAmount(award.getMinSpendAmount());
        promotion.setCode(promotionCode);
        promotion.setGlobal(true);
        promotion.setActivated(true);

        award.setUsedQuantity(award.getUsedQuantity() + 1);
        awardRepository.save(award);
        return promotionRepository.save(promotion);
    }
}
