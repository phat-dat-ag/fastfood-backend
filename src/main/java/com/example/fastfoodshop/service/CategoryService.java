package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.CategoryDTO;
import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.dto.PromotionDTO;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.repository.CategoryRepository;
import com.example.fastfoodshop.response.CategoryResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.util.PromotionUtils;
import com.example.fastfoodshop.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;

    public String generateUniqueSlug(String name) {
        String baseSlug = SlugUtils.toSlug(name);
        String uniqueSlug = baseSlug;
        int counter = 1;

        while (categoryRepository.existsBySlug((uniqueSlug))) {
            uniqueSlug = baseSlug + "-" + counter++;
        }
        return uniqueSlug;
    }

    public Category findCategoryOrThrow(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
    }

    public Category findCategoryOrThrow(String slug) {
        return categoryRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
    }

    public void handleCategoryImage(Category category, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty())
            return;

        String oldPublicId = category.getImagePublicId();
        Map<?, ?> result = cloudinaryService.uploadImage(imageFile, "category");

        category.setImageUrl((String) result.get("secure_url"));
        category.setImagePublicId((String) result.get("public_id"));

        if (oldPublicId != null && !oldPublicId.isEmpty()) {
            try {
                boolean deleted = cloudinaryService.deleteImage(oldPublicId);
            } catch (Exception e) {
                System.out.println("Ngoai lệ khi dọn ảnh danh mục cũ: " + e.getMessage());
            }
        }
    }

    private boolean isValidPromotion(PromotionDTO promotionDTO) {
        LocalDateTime now = LocalDateTime.now();
        return promotionDTO.isActivated()
                && !promotionDTO.isDeleted()
                && !promotionDTO.isGlobal()
                && promotionDTO.getStartAt().isBefore(now)
                && promotionDTO.getEndAt().isAfter(now)
                && promotionDTO.getQuantity() > promotionDTO.getUsedQuantity();
    }

    public void applyPromotion(ProductDTO productDTO, Category category) {
        PromotionDTO chosenPromotion = null;

        for (PromotionDTO promotionDTO : productDTO.getPromotions()) {
            if (isValidPromotion(promotionDTO)) {
                chosenPromotion = promotionDTO;
                break;
            }
        }

        if (chosenPromotion == null) {
            for (Promotion promotion : category.getPromotions()) {
                PromotionDTO promotionDTO = new PromotionDTO(promotion);
                if (isValidPromotion(promotionDTO)) {
                    chosenPromotion = promotionDTO;
                    break;
                }
            }
        }

        int originalPrice = productDTO.getPrice();
        int discountedPrice = originalPrice;

        if (chosenPromotion != null) {
            discountedPrice = PromotionUtils.calculateDiscountedPrice(originalPrice, chosenPromotion);
            productDTO.setPromotionId(chosenPromotion.getId());
        }

        productDTO.setDiscountedPrice(discountedPrice);
    }

    public ResponseEntity<ResponseWrapper<CategoryDTO>> createCategory(String name, String description, boolean activated, MultipartFile imageFile) {
        try {
            String slug = generateUniqueSlug(name);

            Category category = new Category();
            category.setSlug(slug);
            category.setName(name);
            category.setDescription(description);
            category.setActivated(activated);

            handleCategoryImage(category, imageFile);

            Category savedCategory = categoryRepository.save(category);
            return ResponseEntity.ok(ResponseWrapper.success(new CategoryDTO(savedCategory)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "CREATE_CATEGORY_FAILED",
                    "Lỗi tạo danh mục sản phẩm: " + e.getMessage())
            );
        }
    }

    public ResponseEntity<ResponseWrapper<CategoryDTO>> updateCategory(Long id, String name, String description, boolean activated, MultipartFile imageFile) {
        try {
            Category category = findCategoryOrThrow(id);
            category.setName(name);
            category.setDescription(description);
            category.setActivated(activated);

            handleCategoryImage(category, imageFile);

            Category savedCategory = categoryRepository.save(category);
            return ResponseEntity.ok(ResponseWrapper.success(new CategoryDTO(savedCategory)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "UPDATE_CATEGORY_FAILED",
                    "Lỗi cập nhật danh mục sản phẩm: " + e.getMessage())
            );
        }
    }

    public ResponseEntity<ResponseWrapper<CategoryResponse>> getCategories(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Category> categoryPage = categoryRepository.findByIsDeletedFalse(pageable);

            return ResponseEntity.ok(ResponseWrapper.success(new CategoryResponse(categoryPage)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_CATEGORY_FAILED",
                    "Lỗi lấy danh mục sản phẩm: " + e.getMessage())
            );
        }
    }

    public ResponseEntity<ResponseWrapper<CategoryDTO>> deleteCategory(Long id) {
        try {
            Category category = findCategoryOrThrow(id);
            category.setDeleted(true);

            Category deletedCategory = categoryRepository.save(category);
            return ResponseEntity.ok(ResponseWrapper.success(new CategoryDTO(deletedCategory)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "DELETE_CATEGORY_FAILED",
                    "Không thể xóa danh mục: " + e.getMessage())
            );
        }
    }

    public ResponseEntity<ResponseWrapper<ArrayList<CategoryDTO>>> getDisplayableCategories() {
        try {
            List<Category> categories = categoryRepository.findByIsDeletedFalseAndIsActivatedTrue();
            ArrayList<CategoryDTO> categoryDTOs = new ArrayList<>();

            for (Category category : categories) {
                categoryDTOs.add(new CategoryDTO(category));
            }

            return ResponseEntity.ok(ResponseWrapper.success(categoryDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_DISPLAYABLE_CATEGORY_FAILED",
                    "Lỗi lấy danh mục sản phẩm để trưng bày: " + e.getMessage())
            );
        }
    }
}
