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

    public static Product createDeletedProduct(Long productId) {
        Product product = createProduct();

        product.setId(productId);
        product.setSlug("Trai-cay" + productId);
        product.setActivated(true);
        product.setDeleted(true);

        return product;
    }

    public static Product createDeactivatedProduct(Long productId) {
        Product product = createProduct();

        product.setId(productId);
        product.setSlug("Trai-cay" + productId);
        product.setActivated(false);
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

    public static Product createActivatedProductWithDeletedCategory(Long productId, Long categoryId) {
        Category deletedCategory = CategoryFactory.createDeletedCategory(categoryId);

        Product activatedProduct = createActivatedProduct(productId);

        activatedProduct.setCategory(deletedCategory);

        return activatedProduct;
    }

    public static Product createActivatedProductWithDeactivatedCategory(Long productId, Long categoryId) {
        Category deactivatedCategory = CategoryFactory.createDeactivatedCategory(categoryId);

        Product activatedProduct = createActivatedProduct(productId);

        activatedProduct.setCategory(deactivatedCategory);

        return activatedProduct;
    }

    public static Product createActivatedProductWithActivatedCategory(Long productId, Long categoryId) {
        Category activatedCategory = CategoryFactory.createActivatedCategory(categoryId);

        Product activatedProduct = createActivatedProduct(productId);

        activatedProduct.setCategory(activatedCategory);

        return activatedProduct;
    }
}
