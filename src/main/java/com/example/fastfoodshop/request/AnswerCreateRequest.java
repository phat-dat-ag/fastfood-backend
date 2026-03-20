package com.example.fastfoodshop.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record AnswerCreateRequest(
        String content,

        MultipartFile imageUrl,

        @NotNull(message = "Phải cho biết câu trả lời đúng hay sai")
        Boolean correct
) {
    @AssertTrue(message = "Phải có nội dung text hoặc hình ảnh, nhưng không được có cả hai")
    public boolean isValidContentOrImage() {
        boolean hasText = content != null && !content.isBlank();
        boolean hasImage = imageUrl != null && !imageUrl.isEmpty();

        return hasText ^ hasImage;
    }
}
