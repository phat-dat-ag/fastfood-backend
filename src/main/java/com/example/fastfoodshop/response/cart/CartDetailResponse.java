package com.example.fastfoodshop.response.cart;

import com.example.fastfoodshop.dto.CartDTO;
import com.example.fastfoodshop.dto.DeliveryDTO;
import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.dto.PromotionDTO;

import java.util.ArrayList;
import java.util.List;

public record CartDetailResponse(
        List<CartDTO> carts,
        int originalPrice,
        int subtotalPrice,
        int totalPrice,
        PromotionDTO promotion,
        DeliveryDTO deliveryInformation,
        int deliveryFee
) {
    private static CartDetailResponse create(
            List<CartDTO> cartDTOs,
            PromotionDTO promotionDTO, DeliveryDTO deliveryInformation, int totalPrice
    ) {
        List<CartDTO> carts = new ArrayList<>();
        int originalPrice = 0;
        int subtotalPrice = 0;

        int deliveryFee = deliveryInformation == null ? 0 : deliveryInformation.fee();

        for (CartDTO cartDTO : cartDTOs) {
            ProductDTO productDTO = cartDTO.product();
            carts.add(cartDTO);
            originalPrice += (cartDTO.quantity() * productDTO.price());
            subtotalPrice += (cartDTO.quantity() * productDTO.discountedPrice());
        }

        return new CartDetailResponse(
                carts,
                originalPrice,
                subtotalPrice,
                totalPrice,
                promotionDTO,
                deliveryInformation,
                deliveryFee
        );
    }

    public static CartDetailResponse from(List<CartDTO> cartDTOs) {
        return create(cartDTOs, null, null, 0);
    }

    public static CartDetailResponse from(
            List<CartDTO> cartDTOs,
            PromotionDTO promotionDTO, DeliveryDTO deliveryInformation, int totalPrice
    ) {
        return create(cartDTOs, promotionDTO, deliveryInformation, totalPrice);
    }
}
