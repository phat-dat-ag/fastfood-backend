package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Cart;

public record CartDTO(
        UserDTO user,
        ProductDTO product,
        int quantity
) {
    public static CartDTO from(Cart cart) {
        return new CartDTO(
                new UserDTO(cart.getUser()),
                ProductDTO.from(cart.getProduct()),
                cart.getQuantity()
        );
    }
}
