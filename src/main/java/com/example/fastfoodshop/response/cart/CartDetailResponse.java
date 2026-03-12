package com.example.fastfoodshop.response.cart;

import com.example.fastfoodshop.dto.CartDTO;
import com.example.fastfoodshop.dto.DeliveryDTO;
import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.dto.PromotionCodeCheckResultDTO;

import java.util.ArrayList;
import java.util.List;

public record CartDetailResponse(
        List<CartDTO> carts,
        int originalPrice,
        int subtotalPrice,
        int totalPrice,
        PromotionCodeCheckResultDTO applyPromotionResult,
        DeliveryDTO deliveryInformation,
        int deliveryFee
) {
    private static CartDetailResponse create(
            ArrayList<CartDTO> cartDTOs,
            PromotionCodeCheckResultDTO applyPromotionResult, DeliveryDTO deliveryInformation, int deliveryFee
    ) {
        List<CartDTO> carts = new ArrayList<>();
        int originalPrice = 0;
        int subtotalPrice = 0;

        for (CartDTO cartDTO : cartDTOs) {
            ProductDTO productDTO = cartDTO.product();
            carts.add(cartDTO);
            originalPrice += (cartDTO.quantity() * productDTO.price());
            subtotalPrice += (cartDTO.quantity() * productDTO.discountedPrice());
        }
        int totalPrice = subtotalPrice;

        return new CartDetailResponse(
                carts,
                originalPrice,
                subtotalPrice,
                totalPrice + deliveryFee,
                applyPromotionResult,
                deliveryInformation,
                deliveryFee
        );
    }

    public static CartDetailResponse from(ArrayList<CartDTO> cartDTOs) {
        return create(cartDTOs, null, null, 0);
    }

    public static CartDetailResponse from(
            ArrayList<CartDTO> cartDTOs,
            PromotionCodeCheckResultDTO applyPromotionResult, DeliveryDTO deliveryInformation, int deliveryFee
    ) {
        return create(cartDTOs, applyPromotionResult, deliveryInformation, deliveryFee);
    }
}
