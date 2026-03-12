package com.example.fastfoodshop.response.auth;

import com.example.fastfoodshop.dto.UserDTO;

public record SignInResponse(
        String token,
        UserDTO user
) {
}
