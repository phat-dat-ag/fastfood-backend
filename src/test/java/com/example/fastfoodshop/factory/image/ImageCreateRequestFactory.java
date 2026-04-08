package com.example.fastfoodshop.factory.image;

import com.example.fastfoodshop.enums.PageType;
import com.example.fastfoodshop.enums.SectionType;
import com.example.fastfoodshop.factory.file.MediaFileFactory;
import com.example.fastfoodshop.request.ImageCreateRequest;

public class ImageCreateRequestFactory {
    private static final String ALTERNATIVE_TEXT = "Ảnh sản phẩm";
    private static final PageType PAGE_TYPE = PageType.ABOUT_US;
    private static final SectionType SECTION_TYPE = SectionType.MISSION;

    public static ImageCreateRequest createValidWithImageFile() {
        return new ImageCreateRequest(
                MediaFileFactory.createValidFile(),
                ALTERNATIVE_TEXT,
                PAGE_TYPE,
                SECTION_TYPE
        );
    }

    public static ImageCreateRequest createValidWithEmptyFile() {
        return new ImageCreateRequest(
                MediaFileFactory.createEmptyFile(),
                ALTERNATIVE_TEXT,
                PAGE_TYPE,
                SECTION_TYPE
        );
    }

    public static ImageCreateRequest createValidWithoutFile() {
        return new ImageCreateRequest(
                null,
                ALTERNATIVE_TEXT,
                PAGE_TYPE,
                SECTION_TYPE
        );
    }
}
