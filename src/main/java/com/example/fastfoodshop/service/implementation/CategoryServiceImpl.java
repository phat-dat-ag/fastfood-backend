package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.CategoryDTO;
import com.example.fastfoodshop.dto.PromotionDTO;
import com.example.fastfoodshop.dto.PromotionResult;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.exception.category.CategoryNotFoundException;
import com.example.fastfoodshop.exception.category.DeletedCategoryException;
import com.example.fastfoodshop.repository.CategoryRepository;
import com.example.fastfoodshop.request.CategoryCreateRequest;
import com.example.fastfoodshop.request.CategoryUpdateRequest;
import com.example.fastfoodshop.response.CategoryResponse;
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
import java.util.ArrayList;
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

    public Category findUndeletedCategoryOrThrow(String categorySlug) {
        return categoryRepository.findBySlug(categorySlug).orElseThrow(
                () -> new CategoryNotFoundException(categorySlug)
        );
    }

    public Category findActivatedCategoryOrThrow(Long categoryId) {
        return categoryRepository.findByIdAndIsActivatedTrueAndIsDeletedFalse(categoryId).orElseThrow(
                () -> new CategoryNotFoundException(categoryId)
        );
    }

    public Category findDeactivatedCategoryOrThrow(Long categoryId) {
        return categoryRepository.findByIdAndIsActivatedFalseAndIsDeletedFalse(categoryId).orElseThrow(
                () -> new CategoryNotFoundException(categoryId)
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

    public PromotionResult applyPromotion(Product product, Category category) {
        PromotionDTO chosenPromotion = null;

        for (Promotion promotion : product.getPromotions()) {
            PromotionDTO promotionDTO = PromotionDTO.from(promotion);
            if (isValidPromotion(promotionDTO)) {
                chosenPromotion = promotionDTO;
                break;
            }
        }

        if (chosenPromotion == null) {
            for (Promotion promotion : category.getPromotions()) {
                PromotionDTO promotionDTO = PromotionDTO.from(promotion);
                if (isValidPromotion(promotionDTO)) {
                    chosenPromotion = promotionDTO;
                    break;
                }
            }
        }

        int originalPrice = product.getPrice();
        int discountedPrice = originalPrice;
        Long promotionId = null;

        if (chosenPromotion != null) {
            discountedPrice = PromotionUtils.calculateDiscountedPrice(originalPrice, chosenPromotion);
            promotionId = chosenPromotion.id();
        }
        return new PromotionResult(discountedPrice, promotionId);
    }

    public CategoryDTO createCategory(CategoryCreateRequest categoryCreateRequest) {
        String slug = generateUniqueSlug(categoryCreateRequest.getName());

        Category category = new Category();
        category.setSlug(slug);
        category.setName(categoryCreateRequest.getName());
        category.setDescription(categoryCreateRequest.getDescription());
        category.setActivated(categoryCreateRequest.isActivated());

        handleCategoryImage(category, categoryCreateRequest.getImageUrl());

        Category savedCategory = categoryRepository.save(category);
        return CategoryDTO.from(savedCategory);
    }

    public CategoryDTO updateCategory(CategoryUpdateRequest categoryUpdateRequest) {
        Category category = findCategoryOrThrow(categoryUpdateRequest.getId());
        category.setName(categoryUpdateRequest.getName());
        category.setDescription(categoryUpdateRequest.getDescription());
        category.setActivated(categoryUpdateRequest.isActivated());

        handleCategoryImage(category, categoryUpdateRequest.getImageUrl());

        Category savedCategory = categoryRepository.save(category);
        return CategoryDTO.from(savedCategory);
    }

    public CategoryResponse getCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryRepository.findByIsDeletedFalse(pageable);

        return new CategoryResponse(categoryPage);
    }

    public String activateCategory(Long id) {
        Category category = findDeactivatedCategoryOrThrow(id);
        category.setActivated(true);

        categoryRepository.save(category);
        return "Đã kích hoạt danh mục";
    }

    public String deactivateCategory(Long id) {
        Category category = findActivatedCategoryOrThrow(id);
        category.setActivated(false);

        categoryRepository.save(category);
        return "Đã hủy kích hoạt danh mục";
    }

    public CategoryDTO deleteCategory(Long categoryId) {
        Category category = findCategoryOrThrow(categoryId);
        if (category.isDeleted()) {
            throw new DeletedCategoryException(categoryId);
        }
        category.setDeleted(true);

        Category deletedCategory = categoryRepository.save(category);
        return CategoryDTO.from(deletedCategory);
    }

    public ArrayList<CategoryDTO> getDisplayableCategories() {
        List<Category> categories = categoryRepository.findByIsDeletedFalseAndIsActivatedTrue();
        ArrayList<CategoryDTO> categoryDTOs = new ArrayList<>();

        for (Category category : categories) {
            categoryDTOs.add(CategoryDTO.from(category));
        }

        return categoryDTOs;
    }
}

