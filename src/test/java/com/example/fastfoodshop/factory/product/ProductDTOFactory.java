package com.example.fastfoodshop.factory.product;

import com.example.fastfoodshop.dto.ProductDTO;

import java.time.LocalDateTime;
import java.util.List;

public class ProductDTOFactory {
    private static final Long CATEGORY_ID = 100L;
    private static final String CATEGORY_NAME = "Fast Food";

    private static final String NAME = "Burger";
    private static final String SLUG = "burger";
    private static final int PRICE = 50000;
    private static final String DESCRIPTION = "Delicious burger";
    private static final String IMAGE_URL = "image.jpg";
    private static final String MODEL_URL = "model.glb";

    private static final boolean ACTIVATED = true;
    private static final boolean DELETED = false;

    private static final int DISCOUNTED_PRICE = 40000;

    private static final double AVERAGE_RATING = 4.5;
    private static final long REVIEW_COUNT = 10L;
    private static final long SOLD_COUNT = 100L;

    private static final Long PROMOTION_ID = 9090L;

    public static ProductDTO createValidWithPromotionId(Long productId) {
        return new ProductDTO(
                CATEGORY_ID,
                CATEGORY_NAME,
                productId,
                NAME,
                SLUG,
                PRICE,
                DESCRIPTION,
                IMAGE_URL,
                MODEL_URL,
                LocalDateTime.now(),
                LocalDateTime.now(),
                ACTIVATED,
                DELETED,
                List.of(),
                List.of(),
                DISCOUNTED_PRICE,
                PROMOTION_ID,
                AVERAGE_RATING,
                REVIEW_COUNT,
                SOLD_COUNT
        );
    }
}