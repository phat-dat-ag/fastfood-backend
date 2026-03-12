package com.example.fastfoodshop.response.auth;

import java.time.LocalDateTime;

public record OTPResponse(
        String phone,
        LocalDateTime expiredAt
) {
}
