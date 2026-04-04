package com.example.fastfoodshop.factory.category;

import com.example.fastfoodshop.entity.Category;

import java.time.Instant;

public class CategoryFactory {
    private static Category createCategory() {
        Category category = new Category();

        category.setCreatedAt(Instant.now());
        category.setUpdatedAt(Instant.now());

        return category;
    }

    public static Category createActivatedCategory(Long categoryId) {
        Category category = createCategory();

        category.setId(categoryId);
        category.setSlug("Trai-cay" + categoryId);
        category.setActivated(true);
        category.setDeleted(false);

        return category;
    }
}
