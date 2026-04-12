package com.example.fastfoodshop.factory.cart;

import com.example.fastfoodshop.dto.CartDTO;
import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.dto.UserDTO;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.factory.product.ProductDTOFactory;
import com.example.fastfoodshop.factory.product.ProductFactory;
import com.example.fastfoodshop.factory.user.UserFactory;

import java.util.List;

public class CartDTOFactory {
    private static final Long PRODUCT_ID = 11111L;
    private static final int QUANTITY = 10;

    private static CartDTO createValid() {
        User user = UserFactory.createActivatedUser();
        Product product = ProductFactory.createActivatedProduct(PRODUCT_ID);

        return new CartDTO(
                UserDTO.from(user), ProductDTO.from(product), QUANTITY
        );
    }

    public static List<CartDTO> createValidCartDTOs() {
        return List.of(
                createValid(),
                createValid(),
                createValid()
        );
    }

    private static CartDTO createValidWithProductPromotion() {
        User user = UserFactory.createActivatedUser();

        ProductDTO productDTO = ProductDTOFactory.createValidWithPromotionId(PRODUCT_ID);

        return new CartDTO(
                UserDTO.from(user), productDTO, QUANTITY
        );
    }

    public static List<CartDTO> createValidCartDTOsWithProductPromotions() {
        return List.of(
                createValidWithProductPromotion(),
                createValidWithProductPromotion(),
                createValidWithProductPromotion()
        );
    }
}