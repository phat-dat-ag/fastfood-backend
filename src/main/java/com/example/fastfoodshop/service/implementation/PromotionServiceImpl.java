package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.constant.QuizConstants;
import com.example.fastfoodshop.dto.PromotionCodeCheckResultDTO;
import com.example.fastfoodshop.dto.PromotionDTO;
import com.example.fastfoodshop.entity.Award;
import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.entity.Quiz;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.enums.PromotionType;
import com.example.fastfoodshop.exception.promotion.*;
import com.example.fastfoodshop.repository.AwardRepository;
import com.example.fastfoodshop.repository.PromotionRepository;
import com.example.fastfoodshop.request.PromotionCreateRequest;
import com.example.fastfoodshop.response.promotion.PromotionPageResponse;
import com.example.fastfoodshop.response.promotion.PromotionOrdersResponse;
import com.example.fastfoodshop.response.promotion.PromotionResponse;
import com.example.fastfoodshop.response.promotion.PromotionUpdateResponse;
import com.example.fastfoodshop.service.CategoryService;
import com.example.fastfoodshop.service.ProductService;
import com.example.fastfoodshop.service.PromotionService;
import com.example.fastfoodshop.service.AwardService;
import com.example.fastfoodshop.service.UserService;
import com.example.fastfoodshop.util.NumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final AwardRepository awardRepository;
    private final AwardService awardService;
    private final UserService userService;

    private boolean checkUniqueCode(String code) {
        return promotionRepository.findByCode(code).isPresent();
    }

    private Promotion buildPromotionCategoryFromRequest(PromotionCreateRequest promotionCreateRequest) {
        Promotion promotion = new Promotion();
        promotion.setType(promotionCreateRequest.type());
        promotion.setValue(promotionCreateRequest.value());
        promotion.setStartAt(promotionCreateRequest.startAt());
        promotion.setEndAt(promotionCreateRequest.endAt());
        promotion.setQuantity(promotionCreateRequest.quantity());
        promotion.setUsedQuantity(0);
        promotion.setMaxDiscountAmount(promotionCreateRequest.maxDiscountAmount());
        promotion.setMinSpendAmount(promotionCreateRequest.minSpendAmount());
        promotion.setCode(promotionCreateRequest.code());
        promotion.setGlobal(promotionCreateRequest.global());
        promotion.setActivated(promotionCreateRequest.activated());
        promotion.setDeleted(false);
        return promotion;
    }

    private Promotion findUndeletedPromotionOrThrow(Long promotionId) {
        return promotionRepository.findByIdAndIsDeletedFalse(promotionId).orElseThrow(
                () -> new PromotionNotFoundException(promotionId)
        );
    }

    private Promotion findPromotionOrThrow(String promotionCode) {
        return promotionRepository.findByCode(promotionCode).orElseThrow(
                () -> new PromotionNotFoundException(promotionCode)
        );
    }

    public Promotion findPromotionOrThrow(Long promotionId) {
        return promotionRepository.findById(promotionId).orElseThrow(
                () -> new PromotionNotFoundException(promotionId)
        );
    }

    public void increasePromotionUsageCount(Long promotionId) {
        Promotion promotion = findPromotionOrThrow(promotionId);
        if (promotion.getUsedQuantity() >= promotion.getQuantity()) {
            throw new UnavailablePromotionException(promotionId);
        }
        promotion.setUsedQuantity(promotion.getUsedQuantity() + 1);
        promotionRepository.save(promotion);
    }

    public PromotionCodeCheckResultDTO checkPromotionCode(String promotionCode, int orderPrice) {
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

        return PromotionCodeCheckResultDTO.success("Đã áp dụng khuyến mãi!", PromotionDTO.from(promotion));
    }

    public PromotionResponse createPromotionCategory(PromotionCreateRequest promotionCreateRequest) {
        if (checkUniqueCode(promotionCreateRequest.code())) {
            throw new CodeAlreadyExistsException(promotionCreateRequest.code());
        }
        Category category = categoryService.findCategoryOrThrow(promotionCreateRequest.categoryId());

        Promotion promotion = buildPromotionCategoryFromRequest(promotionCreateRequest);
        promotion.setCategory(category);
        Promotion savedPromotion = promotionRepository.save(promotion);
        return new PromotionResponse(PromotionDTO.from(savedPromotion));
    }

    public PromotionResponse createPromotionProduct(PromotionCreateRequest promotionCreateRequest) {
        if (checkUniqueCode(promotionCreateRequest.code())) {
            throw new CodeAlreadyExistsException(promotionCreateRequest.code());
        }
        Product product = productService.findProductOrThrow(promotionCreateRequest.productId());

        Promotion promotion = buildPromotionCategoryFromRequest(promotionCreateRequest);
        promotion.setProduct(product);
        Promotion savedPromotion = promotionRepository.save(promotion);
        return new PromotionResponse(PromotionDTO.from(savedPromotion));
    }

    public PromotionResponse createPromotionOrder(PromotionCreateRequest promotionCreateRequest) {
        if (checkUniqueCode(promotionCreateRequest.code())) {
            throw new CodeAlreadyExistsException(promotionCreateRequest.code());
        }

        Promotion promotion = buildPromotionCategoryFromRequest(promotionCreateRequest);
        Promotion savedPromotion = promotionRepository.save(promotion);
        return new PromotionResponse(PromotionDTO.from(savedPromotion));
    }

    public PromotionPageResponse getPromotionCategory(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Promotion> categoryPromotionPage = promotionRepository.findByCategoryIsNotNullAndIsDeletedFalse(pageable);
        return PromotionPageResponse.from(categoryPromotionPage);
    }

    public PromotionPageResponse getPromotionProduct(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Promotion> productPromotionPage = promotionRepository.findByProductIsNotNullAndIsDeletedFalse(pageable);
        return PromotionPageResponse.from(productPromotionPage);
    }

    public PromotionPageResponse getPromotionOrder(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Promotion> orderPromotionPage = promotionRepository.findGlobalOrderPromotions(pageable);
        return PromotionPageResponse.from(orderPromotionPage);
    }

    public PromotionOrdersResponse getValidPromotionOrder(String phone) {
        User user = userService.findUserOrThrow(phone);
        List<Promotion> orderPromotions = promotionRepository.findGlobalOrderPromotionsByUser(user.getId(), LocalDateTime.now());
        ArrayList<PromotionDTO> validOrderPromotions = new ArrayList<>();

        for (Promotion promotion : orderPromotions) {
            validOrderPromotions.add(PromotionDTO.from(promotion));
        }
        return new PromotionOrdersResponse(validOrderPromotions);
    }

    public PromotionUpdateResponse activatePromotion(Long promotionId) {
        Promotion promotion = findUndeletedPromotionOrThrow(promotionId);
        if (promotion.isActivated()) {
            throw new InvalidPromotionStatusException();
        }
        promotion.setActivated(true);
        promotionRepository.save(promotion);
        return new PromotionUpdateResponse("Đã kích hoạt mã khuyến mãi: " + promotionId);
    }

    public PromotionUpdateResponse deactivatePromotion(Long promotionId) {
        Promotion promotion = findUndeletedPromotionOrThrow(promotionId);
        if (!promotion.isActivated()) {
            throw new InvalidPromotionStatusException();
        }
        promotion.setActivated(false);
        promotionRepository.save(promotion);
        return new PromotionUpdateResponse("Đã hủy kích hoạt mã khuyến mãi: " + promotionId);
    }

    public PromotionUpdateResponse deletePromotion(Long promotionId) {
        Promotion promotion = findPromotionOrThrow(promotionId);
        if (promotion.isDeleted()) {
            throw new DeletedPromotionException();
        }
        promotion.setDeleted(true);
        Promotion deletedPromotion = promotionRepository.save(promotion);
        return new PromotionUpdateResponse("Đã xóa mã khuyến mãi: " + promotionId);
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
            throw new UncompletedQuizException();
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