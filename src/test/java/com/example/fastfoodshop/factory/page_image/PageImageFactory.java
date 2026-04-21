package com.example.fastfoodshop.factory.page_image;

import com.example.fastfoodshop.entity.Image;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.PageType;
import com.example.fastfoodshop.enums.SectionType;
import com.example.fastfoodshop.factory.user.UserFactory;

import java.time.Instant;
import java.util.List;

public class PageImageFactory {
    private static final Long IMAGE_ID = 111L;
    private static final String URL = "URL";
    private static final String PUBLIC_ID = "PUBLIC_ID";
    private static final String ALTERNATIVE_TEXT = "ALTERNATIVE_TEXT";

    private static Image createImage(PageType pageType, SectionType sectionType) {
        User user = UserFactory.createActivatedUser();

        Image image = new Image();

        image.setUser(user);
        image.setId(IMAGE_ID);
        image.setPageType(pageType);
        image.setSectionType(sectionType);
        image.setUrl(URL);
        image.setPublicId(PUBLIC_ID);
        image.setAlternativeText(ALTERNATIVE_TEXT);
        image.setCreatedAt(Instant.now());
        image.setUpdatedAt(Instant.now());

        return image;
    }

    public static List<Image> createAboutUsPageImages() {
        return List.of(
                createImage(PageType.ABOUT_US, SectionType.CAROUSEL),
                createImage(PageType.ABOUT_US, SectionType.SHOWCASE),
                createImage(PageType.ABOUT_US, SectionType.MISSION)
        );
    }

    public static List<Image> createChallengePageImages() {
        return List.of(
                createImage(PageType.CHALLENGE, SectionType.CAROUSEL)
        );
    }
}
