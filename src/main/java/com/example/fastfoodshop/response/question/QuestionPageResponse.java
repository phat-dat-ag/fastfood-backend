package com.example.fastfoodshop.response.question;

import com.example.fastfoodshop.dto.QuestionDTO;
import com.example.fastfoodshop.entity.Question;
import org.springframework.data.domain.Page;

import java.util.List;

public record QuestionPageResponse(
        List<QuestionDTO> questions,
        int currentPage,
        int pageSize,
        long totalItems,
        int totalPages
) {
    public static QuestionPageResponse from(Page<Question> page) {
        List<QuestionDTO> questions = page.getContent()
                .stream().map(QuestionDTO::createAdminQuestion).toList();

        return new QuestionPageResponse(
                questions,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
