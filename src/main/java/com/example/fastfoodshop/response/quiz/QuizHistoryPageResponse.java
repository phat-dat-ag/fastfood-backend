package com.example.fastfoodshop.response.quiz;

import com.example.fastfoodshop.dto.QuizDTO;
import com.example.fastfoodshop.entity.Quiz;
import org.springframework.data.domain.Page;

import java.util.List;

public record QuizHistoryPageResponse(
        List<QuizDTO> quizzes,
        int currentPage,
        int pageSize,
        long totalItems,
        int totalPages
) {
    public static QuizHistoryPageResponse from(Page<Quiz> page) {
        List<QuizDTO> quizzes = page.getContent()
                .stream().map(QuizDTO::createReviewQuizResponse).toList();

        return new QuizHistoryPageResponse(
                quizzes,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
