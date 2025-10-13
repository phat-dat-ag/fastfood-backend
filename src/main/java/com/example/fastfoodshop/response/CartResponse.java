package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.CartDTO;
import com.example.fastfoodshop.entity.Cart;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CartResponse {
    private ArrayList<CartDTO> carts = new ArrayList<>();

    public CartResponse(List<Cart> cartList) {
        for (Cart cart : cartList) {
            this.carts.add(new CartDTO(cart));
        }
    }
}
