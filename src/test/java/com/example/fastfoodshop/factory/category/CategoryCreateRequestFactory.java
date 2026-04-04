package com.example.fastfoodshop.factory.category;

import com.example.fastfoodshop.request.CategoryCreateRequest;
import org.springframework.mock.web.MockMultipartFile;

public class CategoryCreateRequestFactory {
    private static final String CATEGORY_NAME = "Sữa Bắp";
    private static final String CATEGORY_DESCRIPTION = "100 phần trăm hóa chất";

    private static MockMultipartFile createEmptyFile() {
        return new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                new byte[]{}
        );
    }

    private static MockMultipartFile createValidFile() {
        return new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "data".getBytes()
        );
    }

    public static CategoryCreateRequest createValidWithImageFile() {
        return new CategoryCreateRequest(
                CATEGORY_NAME,
                CATEGORY_DESCRIPTION,
                true,
                createValidFile()
        );
    }

    public static CategoryCreateRequest createValidWithEmptyImageFile() {
        return new CategoryCreateRequest(
                CATEGORY_NAME,
                CATEGORY_DESCRIPTION,
                true,
                createEmptyFile()
        );
    }

    public static CategoryCreateRequest createValidWithNullImageFile() {
        return new CategoryCreateRequest(
                CATEGORY_NAME,
                CATEGORY_DESCRIPTION,
                true,
                null
        );
    }
}
