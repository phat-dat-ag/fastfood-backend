package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.CategoryDTO;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.repository.CategoryRepository;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public void handleCategoryImage(Category category, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty())
            return;

        String oldPublicId = category.getCategoryImagePublicId();
        Map<?, ?> result = cloudinaryService.uploadImage(imageFile, "category");

        category.setCategoryImageUrl((String) result.get("secure_url"));
        category.setCategoryImagePublicId((String) result.get("public_id"));

        if (oldPublicId != null && !oldPublicId.isEmpty()) {
            try {
                boolean deleted = cloudinaryService.deleteImage(oldPublicId);
            } catch (Exception e) {
                System.out.println("Ngoai lệ khi dọn ảnh danh mục cũ: " + e.getMessage());
            }
        }
    }

    public ResponseEntity<ResponseWrapper<CategoryDTO>> createCategory(String name, String description, MultipartFile imageFile) {
        try {
            String slug = generateUniqueSlug(name);

            Category category = new Category();
            category.setSlug(slug);
            category.setName(name);
            category.setDescription(description);

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

    public ResponseEntity<ResponseWrapper<CategoryDTO>> updateCategory(Long id, String name, String description, MultipartFile imageFile) {
        try {
            Category category = findCategoryOrThrow(id);
            category.setName(name);
            category.setDescription(description);

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

    public ResponseEntity<ResponseWrapper<ArrayList<CategoryDTO>>> getCategories() {
        try {
            List<Category> categories = categoryRepository.findByIsDeletedFalse();
            ArrayList<CategoryDTO> categoryDTOs = new ArrayList<>();
            for (Category category : categories) {
                categoryDTOs.add(new CategoryDTO(category));
            }
            return ResponseEntity.ok(ResponseWrapper.success(categoryDTOs));
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
}
