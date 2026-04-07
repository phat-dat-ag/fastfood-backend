package com.example.fastfoodshop.factory.category;

import com.example.fastfoodshop.entity.Category;

import java.time.Instant;
import java.util.List;

public class CategoryFactory {
    private static final String IMAGE_PUBLIC_ID = "public-id";

    private static Category createCategory(Long categoryId) {
        Category category = new Category();

        category.setId(categoryId);
        category.setSlug("Trai-cay" + categoryId);

        category.setCreatedAt(Instant.now());
        category.setUpdatedAt(Instant.now());

        return category;
    }

    public static Category createDeactivatedCategory(Long categoryId) {
        Category category = createCategory(categoryId);

        category.setActivated(false);
        category.setDeleted(false);

        return category;
    }

    public static Category createActivatedCategory(Long categoryId) {
        Category category = createCategory(categoryId);

        category.setActivated(true);
        category.setDeleted(false);

        return category;
    }

    public static Category createDeletedCategory(Long categoryId) {
        Category category = createCategory(categoryId);

        category.setActivated(true);
        category.setDeleted(true);

        return category;
    }

    public static List<Category> createDisplayableCategories() {
        return List.of(
                createActivatedCategory(100L),
                createActivatedCategory(200L),
                createActivatedCategory(300L)
        );
    }

    public static Category createWithPublicIdField(Long categoryId) {
        Category category = createActivatedCategory(categoryId);

        category.setImagePublicId(IMAGE_PUBLIC_ID);

        return category;
    }

    public static Category createWithoutPublicIdField(Long categoryId) {
        Category category = createActivatedCategory(categoryId);

        category.setImagePublicId(null);

        return category;
    }

    public static Category createWithEmptyPublicIdField(Long categoryId) {
        Category category = createActivatedCategory(categoryId);

        category.setImagePublicId("");

        return category;
    }
}
