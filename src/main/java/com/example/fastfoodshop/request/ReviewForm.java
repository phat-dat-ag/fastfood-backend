package com.example.fastfoodshop.request;

import jakarta.validation.Valid;

import java.util.List;

public record ReviewForm(
        @Valid
        List<ReviewCreateRequest> reviews
) {
}
