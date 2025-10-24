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
            ProductDTO productDTO = cartDTO.getProduct();
            this.carts.add(cartDTO);
            this.originalPrice += (cartDTO.getQuantity() * productDTO.getPrice());
            this.subtotalPrice += (cartDTO.getQuantity() * productDTO.getDiscountedPrice());
        }
        this.totalPrice = this.subtotalPrice;
    }
}
