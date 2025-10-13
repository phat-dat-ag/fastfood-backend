package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Cart;
import lombok.Data;

@Data
public class CartDTO {
    private UserDTO user;
    private ProductDTO product;
    private int quantity;

    public CartDTO(Cart cart) {
        this.user = new UserDTO(cart.getUser());
        this.product = new ProductDTO(cart.getProduct());
        this.quantity = cart.getQuantity();
    }
}
