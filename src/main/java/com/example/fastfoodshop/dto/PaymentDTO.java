package com.example.fastfoodshop.dto;

import lombok.Data;

@Data
public class PaymentDTO {
    private String clientSecret;

    public PaymentDTO(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
