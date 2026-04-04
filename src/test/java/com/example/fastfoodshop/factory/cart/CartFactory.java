package com.example.fastfoodshop.factory.cart;

import com.example.fastfoodshop.constant.CartConstant;
import com.example.fastfoodshop.entity.Cart;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.factory.product.ProductFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartFactory {
    private static final int VALID_QUANTITY = 3;

    public static Cart createValidCart(User user, Product product) {
        Cart cart = new Cart();

        cart.setQuantity(VALID_QUANTITY);
        cart.setUser(user);
        cart.setProduct(product);

        return cart;
    }

    public static Cart createValidCartForUser(User user) {
        Product product = ProductFactory.createActivatedProduct(111L);

        Cart cart = new Cart();

        cart.setQuantity(VALID_QUANTITY);
        cart.setUser(user);
        cart.setProduct(product);

        return cart;
    }

    public static Optional<Cart> createValidOptionalCart(User user, Product product) {
        Cart cart = new Cart();

        cart.setQuantity(VALID_QUANTITY);
        cart.setUser(user);
        cart.setProduct(product);

        return Optional.of(cart);
    }

    public static List<Cart> createCartsForUser(User user) {
        return List.of(
                createValidCartForUser(user),
                createValidCartForUser(user),
                createValidCartForUser(user)
        );
    }

    public static List<Cart> createCartForUserWithProductAmountExceeded(User user) {
        List<Cart> carts = new ArrayList<>();

        for (int i = 1; i <= CartConstant.MAX_PRODUCT_TYPES_PER_CART; i++) {
            carts.add(createValidCartForUser(user));
        }

        return carts;
    }
}
