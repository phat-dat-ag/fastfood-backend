package com.example.fastfoodshop.factory.category;

import com.example.fastfoodshop.factory.file.MediaFileFactory;
import com.example.fastfoodshop.request.CategoryUpdateRequest;

public class CategoryUpdateRequestFactory {
    private static final String CATEGORY_NAME = "Tra Thao Moc";
    private static final String CATEGORY_DESCRIPTION = "Thuc pham chuc nang";
    private static final boolean IS_ACTIVATED_CATEGORY = true;

    public static CategoryUpdateRequest createValidWithFile() {
        return new CategoryUpdateRequest(
                CATEGORY_NAME,
                CATEGORY_DESCRIPTION,
                IS_ACTIVATED_CATEGORY,
                MediaFileFactory.createValidFile()
        );
    }

    public static CategoryUpdateRequest createValidWithEmptyFile() {
        return new CategoryUpdateRequest(
                CATEGORY_NAME,
                CATEGORY_DESCRIPTION,
                IS_ACTIVATED_CATEGORY,
                MediaFileFactory.createEmptyFile()
        );
    }

    public static CategoryUpdateRequest createValidWithNullFile() {
        return new CategoryUpdateRequest(
                CATEGORY_NAME,
                CATEGORY_DESCRIPTION,
                IS_ACTIVATED_CATEGORY,
                null
        );
    }
}
