package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.constant.QuizConstants;
import com.example.fastfoodshop.dto.PromotionDTO;
import com.example.fastfoodshop.entity.Award;
import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.entity.Quiz;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.enums.PromotionQueryType;
import com.example.fastfoodshop.enums.PromotionType;
import com.example.fastfoodshop.exception.promotion.DeletedPromotionException;
import com.example.fastfoodshop.exception.promotion.UncompletedQuizException;
import com.example.fastfoodshop.exception.promotion.CodeAlreadyExistsException;
import com.example.fastfoodshop.exception.promotion.InvalidPromotionStatusException;
import com.example.fastfoodshop.exception.promotion.PromotionNotFoundException;
import com.example.fastfoodshop.exception.promotion.UnavailablePromotionException;
import com.example.fastfoodshop.factory.PromotionFactory;
import com.example.fastfoodshop.projection.ItemPromotionProjection;
import com.example.fastfoodshop.repository.AwardRepository;
import com.example.fastfoodshop.repository.PromotionRepository;
import com.example.fastfoodshop.request.PromotionCreateRequest;
import com.example.fastfoodshop.response.promotion.ItemPromotionResponse;
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
import com.example.fastfoodshop.validator.PromotionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final AwardRepository awardRepository;
    private final AwardService awardService;
    private final UserService userService;
    private final PromotionFactory promotionFactory;
    private final PromotionValidator promotionValidator;

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

    @Transactional
    public void increasePromotionUsageCount(Long promotionId) {
        Promotion promotion = findPromotionOrThrow(promotionId);
        if (promotion.getUsedQuantity() >= promotion.getQuantity()) {
            throw new UnavailablePromotionException(promotionId);
        }

        promotion.setUsedQuantity(promotion.getUsedQuantity() + 1);
        promotionRepository.save(promotion);

        log.info("[Promotion Service] Increased usage count for promotion id={}", promotionId);
    }

    public Promotion checkPromotionCode(String promotionCode, int orderPrice) {
        Promotion promotion = findPromotionOrThrow(promotionCode);

        promotionValidator.validatePromotion(promotion, orderPrice, LocalDateTime.now());

        log.info(
                "[Promotion Service] Got valid promotion by code successfully id={}",
                promotion.getId()
        );

        return promotion;
    }

    private void validateUniqueCode(String code) {
        if (promotionRepository.findByCode(code).isPresent()) {
            throw new CodeAlreadyExistsException(code);
        }
    }

    public PromotionResponse createPromotion(PromotionCreateRequest promotionCreateRequest) {
        validateUniqueCode(promotionCreateRequest.code());

        Category category = promotionCreateRequest.categoryId() != null
                ? categoryService.findCategoryByIdOrThrow(promotionCreateRequest.categoryId())
                : null;

        Product product = promotionCreateRequest.productId() != null
                ? productService.findProductByIdOrThrow(promotionCreateRequest.productId())
                : null;

        Promotion promotion = promotionFactory.buildPromotionFromRequest(promotionCreateRequest, category, product);

        Promotion savedPromotion = promotionRepository.save(promotion);

        log.info("[Promotion Service] Successfully created promotion id={}", savedPromotion.getId());

        return new PromotionResponse(PromotionDTO.from(savedPromotion));
    }

    public PromotionPageResponse getPromotions(PromotionQueryType promotionQueryType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Promotion> promotionPage = switch (promotionQueryType) {
            case CATEGORY -> promotionRepository
                    .findByCategoryIsNotNullAndIsDeletedFalse(pageable);
            case PRODUCT -> promotionRepository
                    .findByProductIsNotNullAndIsDeletedFalse(pageable);
            case GLOBAL -> promotionRepository.findGlobalOrderPromotions(pageable);
        };

        log.info("[Promotion Service] Successfully got promotion page");

        return PromotionPageResponse.from(promotionPage);
    }

    public PromotionOrdersResponse getValidPromotions(String phone) {
        User user = userService.findUserOrThrow(phone);

        List<PromotionDTO> validOrderPromotions = promotionRepository
                .findGlobalOrderPromotionsByUser(user.getId(), LocalDateTime.now())
                .stream().map(PromotionDTO::from).toList();

        log.info("[Promotion Service] Successfully got valid promotions for user phone={}", phone);

        return new PromotionOrdersResponse(validOrderPromotions);
    }

    public PromotionUpdateResponse updatePromotionActivation(Long promotionId, boolean activated) {
        Promotion promotion = findPromotionOrThrow(promotionId);

        if (promotion.isDeleted()) {
            throw new DeletedPromotionException();
        }

        if (promotion.isActivated() == activated) {
            throw new InvalidPromotionStatusException();
        }

        promotion.setActivated(activated);
        promotionRepository.save(promotion);

        String message = activated ? "Đã kích hoạt mã khuyến mãi: " + promotionId
                : "Đã hủy kích hoạt mã khuyến mãi: " + promotionId;

        log.info(
                "[Promotion Service] Successfully updated activation for promotion id={}, new status: {}",
                promotionId, activated
        );

        return new PromotionUpdateResponse(message);
    }

    public PromotionUpdateResponse deletePromotion(Long promotionId) {
        Promotion promotion = findPromotionOrThrow(promotionId);
        if (promotion.isDeleted()) {
            throw new DeletedPromotionException();
        }

        promotion.setDeleted(true);
        promotionRepository.save(promotion);

        log.info("[Promotion Service] Successfully deleted promotion id={}", promotionId);

        return new PromotionUpdateResponse("Đã xóa mã khuyến mãi: " + promotionId);
    }

    public ItemPromotionResponse getItemPromotionItems() {
        LocalDateTime now = LocalDateTime.now();

        List<ItemPromotionProjection> categoryProjections = promotionRepository
                .getDisplayableCategoryPromotionsLimited4(now);

        List<ItemPromotionProjection> productProjections = promotionRepository
                .getDisplayableProductPromotionsLimited4(now);

        log.info("[Promotion Service] Successfully got item promotion");

        return ItemPromotionResponse.from(categoryProjections, productProjections);
    }

    private void validateQuizCompleted(Quiz quiz) {
        if (quiz.getCompletedAt() == null) {
            log.debug("[Promotion Service] Quiz id={} has not been completed", quiz.getId());
            throw new UncompletedQuizException();
        }
    }

    private Award getRandomAward(Quiz quiz) {
        log.debug("[Promotion Service] Successfully got random award for quiz id={}", quiz.getId());
        return awardService.getRandomAwardByTopicDifficulty(quiz.getTopicDifficulty());
    }

    private String generatePromotionCode(Long userId, Long quizId, LocalDateTime completedAt) {
        long uniqueNumber = completedAt
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        String timePart = Long.toString(uniqueNumber, 36).toUpperCase();

        String promotionCode = "PM-" + userId + "Q" + quizId + "-" + timePart;

        log.debug("[Promotion Service] Successfully generated promotion code: {}", promotionCode);

        return promotionCode;
    }

    private int calculatePromotionValue(Award award) {
        int value = NumberUtils.randomNumber(award.getMinValue(), award.getMaxValue());

        int promotionValue = award.getType() == PromotionType.PERCENTAGE
                ? value
                : NumberUtils.roundToThousand(value);

        log.debug("[Promotion Service] Calculated promotion value: {}", promotionValue);

        return promotionValue;
    }

    private LocalDateTime calculateEndTime(LocalDateTime startAt) {
        return startAt.plusDays(QuizConstants.PROMOTION_VALIDITY_DAYS);
    }

    private Promotion createPromotion(User user, Quiz quiz, Award award) {
        String code = generatePromotionCode(user.getId(), quiz.getId(), quiz.getCompletedAt());

        int value = calculatePromotionValue(award);

        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime endAt = calculateEndTime(startAt);

        log.info("[Promotion Service] Successfully created promotion with code={}", code);

        return promotionFactory.buildPromotionFromAward(user, award, code, value, startAt, endAt);
    }

    private void updateAwardUsage(Award award) {
        award.setUsedQuantity(award.getUsedQuantity() + 1);

        log.debug("[Promotion Service] Successfully updated award usage for award id={}", award.getId());

        awardRepository.save(award);
    }

    @Transactional
    public Promotion grantPromotion(User user, Quiz quiz) {
        validateQuizCompleted(quiz);

        Award award = getRandomAward(quiz);

        Promotion promotion = createPromotion(user, quiz, award);

        updateAwardUsage(award);

        log.info(
                "[Promotion Service] Successfully granted promotion id={} for user phone={}",
                promotion.getId(), award.getId()
        );

        return promotionRepository.save(promotion);
    }
}