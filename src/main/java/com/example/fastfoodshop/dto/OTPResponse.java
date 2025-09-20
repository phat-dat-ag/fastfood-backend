package com.example.fastfoodshop.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OTPResponse {
    private String phone;
    private LocalDateTime expiredAt;

    public OTPResponse(String phone, LocalDateTime expiredAt) {
        this.phone = phone;
        this.expiredAt = expiredAt;
    }
}
