package com.example.fastfoodshop.factory.image;

import com.example.fastfoodshop.entity.Image;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.PageType;
import com.example.fastfoodshop.enums.SectionType;

public class ImageFactory {
    private static final String IMAGE_URL = "image_url";
    private static final String ALTERNATIVE_TEXT = "Ảnh sản phẩm";
    private static final PageType PAGE_TYPE = PageType.ABOUT_US;
    private static final SectionType SECTION_TYPE = SectionType.MISSION;

    public static Image createValidImage(User user, Long imageId) {
        Image image = new Image();

        image.setUser(user);
        image.setId(imageId);
        image.setUrl(IMAGE_URL);
        image.setAlternativeText(ALTERNATIVE_TEXT);
        image.setPageType(PAGE_TYPE);
        image.setSectionType(SECTION_TYPE);

        return image;
    }
}
