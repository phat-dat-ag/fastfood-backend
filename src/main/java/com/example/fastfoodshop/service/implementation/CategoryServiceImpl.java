package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.CategoryDTO;
import com.example.fastfoodshop.dto.CategorySelectionDTO;
import com.example.fastfoodshop.dto.PromotionDTO;
import com.example.fastfoodshop.dto.PromotionResult;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.exception.category.CategoryNotFoundException;
import com.example.fastfoodshop.exception.category.DeletedCategoryException;
import com.example.fastfoodshop.exception.category.InvalidCategoryStatusException;
import com.example.fastfoodshop.repository.CategoryRepository;
import com.example.fastfoodshop.request.CategoryCreateRequest;
import com.example.fastfoodshop.request.CategoryUpdateRequest;
import com.example.fastfoodshop.response.category.CategoryDisplayResponse;
import com.example.fastfoodshop.response.category.CategorySelectionResponse;
import com.example.fastfoodshop.response.category.CategoryPageResponse;
import com.example.fastfoodshop.response.category.CategoryResponse;
import com.example.fastfoodshop.response.category.CategoryUpdateResponse;
import com.example.fastfoodshop.service.CategoryService;
import com.example.fastfoodshop.service.CloudinaryService;
import com.example.fastfoodshop.util.PromotionUtils;
import com.example.fastfoodshop.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private String generateUniqueSlug(String name) {
        String baseSlug = SlugUtils.toSlug(name);
        String uniqueSlug = baseSlug;
        int counter = 1;

        while (categoryRepository.existsBySlug((uniqueSlug))) {
            uniqueSlug = baseSlug + "-" + counter++;
        }
        return uniqueSlug;
    }

    public Category findCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new CategoryNotFoundException(categoryId)
        );
    }

    public Category findCategoryOrThrow(String categorySlug) {
        return categoryRepository.findBySlug(categorySlug).orElseThrow(
                () -> new CategoryNotFoundException(categorySlug)
        );
    }

    private void handleCategoryImage(Category category, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty())
            return;

        String oldPublicId = category.getImagePublicId();
        Map<?, ?> result = cloudinaryService.uploadImage(imageFile, "category");

        category.setImageUrl((String) result.get("secure_url"));
        category.setImagePublicId((String) result.get("public_id"));

        if (oldPublicId != null && !oldPublicId.isEmpty()) {
            try {
                boolean deleted = cloudinaryService.deleteImage(oldPublicId);
                log.info("Old category image deleted successfully: {}", oldPublicId);
            } catch (Exception e) {
                log.warn("Failed to delete old category image: {}", oldPublicId, e);
            }
        }
    }

    private boolean isValidPromotion(PromotionDTO promotionDTO) {
        LocalDateTime now = LocalDateTime.now();
        return promotionDTO.activated()
                && !promotionDTO.deleted()
                && !promotionDTO.global()
                && promotionDTO.startAt().isBefore(now)
                && promotionDTO.endAt().isAfter(now)
                && promotionDTO.quantity() > promotionDTO.usedQuantity();
    }

    private PromotionDTO findValidPromotion(List<Promotion> promotions) {
        return promotions.stream()
                .map(PromotionDTO::from)
                .filter(this::isValidPromotion)
                .findFirst()
                .orElse(null);
    }

    public PromotionResult applyPromotion(Product product, Category category) {
        PromotionDTO promotion = findValidPromotion(product.getPromotions());

        if (promotion == null) {
            promotion = findValidPromotion(category.getPromotions());
        }

        int originalPrice = product.getPrice();

        if (promotion == null) {
            return new PromotionResult(originalPrice, null);
        }

        int discountedPrice = PromotionUtils.calculateDiscountedPrice(originalPrice, promotion);

        return new PromotionResult(discountedPrice, promotion.id());
    }

    private Category buildCategory(CategoryCreateRequest categoryCreateRequest, String slug) {
        Category category = new Category();
        category.setSlug(slug);
        category.setName(categoryCreateRequest.name());
        category.setDescription(categoryCreateRequest.description());
        category.setActivated(categoryCreateRequest.activated());

        return category;
    }

    public CategoryResponse createCategory(CategoryCreateRequest categoryCreateRequest) {
        String slug = generateUniqueSlug(categoryCreateRequest.name());

        Category category = buildCategory(categoryCreateRequest, slug);

        handleCategoryImage(category, categoryCreateRequest.imageUrl());

        Category savedCategory = categoryRepository.save(category);
        return new CategoryResponse(CategoryDTO.from(savedCategory));
    }

    private void updateCategoryFields(Category category, CategoryUpdateRequest categoryUpdateRequest) {
        category.setName(categoryUpdateRequest.name());
        category.setDescription(categoryUpdateRequest.description());
        category.setActivated(categoryUpdateRequest.activated());
    }

    public CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest categoryUpdateRequest) {
        Category category = findCategoryOrThrow(categoryId);

        updateCategoryFields(category, categoryUpdateRequest);

        handleCategoryImage(category, categoryUpdateRequest.imageUrl());

        Category savedCategory = categoryRepository.save(category);
        return new CategoryResponse(CategoryDTO.from(savedCategory));
    }

    public CategoryPageResponse getCategoryPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryRepository.findByIsDeletedFalse(pageable);

        return CategoryPageResponse.from(categoryPage);
    }

    public CategorySelectionResponse getCategorySelections() {
        List<CategorySelectionDTO> categoryDTOs = categoryRepository
                .findByIsDeletedFalseAndIsActivatedTrue()
                .stream()
                .map(category -> new CategorySelectionDTO(
                        category.getId(), category.getName()
                ))
                .toList();

        return new CategorySelectionResponse(categoryDTOs);
    }

    public CategoryUpdateResponse updateCategoryActivation(Long categoryId, boolean activated) {
        Category category = findCategoryOrThrow(categoryId);

        if (category.isActivated() == activated) {
            throw new InvalidCategoryStatusException();
        }

        category.setActivated(activated);
        categoryRepository.save(category);

        String message = activated
                ? "Đã kích hoạt danh mục: " + categoryId
                : "Đã hủy kích hoạt danh mục: " + categoryId;

        return new CategoryUpdateResponse(message);
    }

    public CategoryUpdateResponse deleteCategory(Long categoryId) {
        Category category = findCategoryOrThrow(categoryId);
        if (category.isDeleted()) {
            throw new DeletedCategoryException(categoryId);
        }
        category.setDeleted(true);

        categoryRepository.save(category);
        return new CategoryUpdateResponse("Đã xóa danh mục sản phẩm: " + categoryId);
    }

    public CategoryDisplayResponse getAllDisplayableCategories() {
        List<CategoryDTO> categoryDTOs = categoryRepository
                .findByIsDeletedFalseAndIsActivatedTrue()
                .stream()
                .map(CategoryDTO::from)
                .toList();

        return new CategoryDisplayResponse(categoryDTOs);
    }
}