package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotNull;

public record UpdateActivationRequest(
        @NotNull(message = "Không được để trống trạng thái")
        Boolean activated
) {
}
