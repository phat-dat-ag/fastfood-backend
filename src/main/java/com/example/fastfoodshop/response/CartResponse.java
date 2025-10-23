package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.CartDTO;
import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.dto.PromotionCodeCheckResultDTO;
import com.example.fastfoodshop.entity.Cart;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CartResponse {
    private ArrayList<CartDTO> carts = new ArrayList<>();
    private int originalPrice;
    private int subtotalPrice;
    private int totalPrice;
    private PromotionCodeCheckResultDTO applyPromotionResult;

    public CartResponse(List<Cart> cartList) {
        for (Cart cart : cartList) {
            ProductDTO productDTO = new ProductDTO(cart.getProduct());
            this.carts.add(new CartDTO(cart));
            this.originalPrice += (cart.getQuantity() * productDTO.getPrice());
            this.subtotalPrice += (cart.getQuantity() * productDTO.getDiscountedPrice());
        }
        this.totalPrice = this.subtotalPrice;
    }

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
