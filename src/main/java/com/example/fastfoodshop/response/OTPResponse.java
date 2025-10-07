package com.example.fastfoodshop.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OTPResponse {
    private String phone;
    private LocalDateTime expiredAt;

    public OTPResponse(String phone, LocalDateTime expiredAt) {
        this.phone = phone;
        this.expiredAt = expiredAt;
    }
}
