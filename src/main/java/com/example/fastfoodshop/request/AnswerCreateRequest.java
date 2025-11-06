package com.example.fastfoodshop.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AnswerCreateRequest {
    private String content;
    private MultipartFile imageUrl;

    @NotNull(message = "Phải cho biết câu trả lời đúng hay sai")
    private Boolean isCorrect;

    @AssertTrue(message = "Phải có nội dung text hoặc hình ảnh, nhưng không được có cả hai")
    public boolean isValidContentOrImage() {
        boolean hasText = content != null && !content.trim().isEmpty();
        boolean hasImage = imageUrl != null && !imageUrl.isEmpty();

        return hasText ^ hasImage;
    }
}
