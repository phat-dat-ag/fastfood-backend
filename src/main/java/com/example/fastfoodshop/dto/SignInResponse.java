package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.User;
import lombok.Data;

@Data
public class SignInResponse {
    private String token;
    private UserDTO user;

    public SignInResponse(String token, User user) {
        this.token = token;
        this.user = new UserDTO(user);
    }
}
