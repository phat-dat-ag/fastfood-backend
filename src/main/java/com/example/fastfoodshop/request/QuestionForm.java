package com.example.fastfoodshop.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class QuestionForm {
    @Valid
    @NotEmpty(message = "Danh sách câu hỏi không được để trống")
    @Size(min = 1, message = "Phải có ít nhất 1 câu hỏi")
    private List<QuestionCreateRequest> questions;
}
