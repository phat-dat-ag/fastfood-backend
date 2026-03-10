package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.CartDTO;
import com.example.fastfoodshop.dto.DeliveryDTO;
import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.dto.PromotionCodeCheckResultDTO;
import lombok.Data;

import java.util.ArrayList;

@Data
public class CartResponse {
    private ArrayList<CartDTO> carts = new ArrayList<>();
    private int originalPrice;
    private int subtotalPrice;
    private int totalPrice;
    private PromotionCodeCheckResultDTO applyPromotionResult;
    private DeliveryDTO deliveryInformation;
    private int deliveryFee;

    public CartResponse(ArrayList<CartDTO> cartDTOs) {
        for (CartDTO cartDTO : cartDTOs) {
            ProductDTO productDTO = cartDTO.product();
            this.carts.add(cartDTO);
            this.originalPrice += (cartDTO.quantity() * productDTO.price());
            this.subtotalPrice += (cartDTO.quantity() * productDTO.discountedPrice());
        }
        this.totalPrice = this.subtotalPrice;
    }
}
