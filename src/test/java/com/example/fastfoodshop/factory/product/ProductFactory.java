package com.example.fastfoodshop.factory.product;

import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.factory.category.CategoryFactory;

import java.time.Instant;
import java.util.List;

public class ProductFactory {
    private static Product createProduct() {
        Category category = CategoryFactory.createActivatedCategory(111L);

        Product product = new Product();

        product.setCategory(category);

        product.setCreatedAt(Instant.now());
        product.setUpdatedAt(Instant.now());

        return product;
    }

    public static Product createActivatedProduct(Long productId) {
        Product product = createProduct();

        product.setId(productId);
        product.setSlug("Trai-cay" + productId);
        product.setActivated(true);
        product.setDeleted(false);

        return product;
    }

    public static Product createActivatedProductWithPromotions(
            Long productId, List<Promotion> promotions
    ) {
        Product product = createActivatedProduct(productId);

        product.setPromotions(promotions);

        return product;
    }
}
