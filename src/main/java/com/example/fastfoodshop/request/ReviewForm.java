package com.example.fastfoodshop.request;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class ReviewForm {
    @Valid
    private List<ReviewCreateRequest> reviews;
}
