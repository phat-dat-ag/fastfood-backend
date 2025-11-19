package com.example.fastfoodshop.response;

import com.example.fastfoodshop.entity.Quiz;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuizHistoryResponse {
    List<QuizResponse> quizzes = new ArrayList<>();
    private int currentPage;
    private int pageSize;
    private long totalItems;
    private int totalPages;

    public QuizHistoryResponse(Page<Quiz> page) {
        for (Quiz quiz : page.getContent()) {
            this.quizzes.add(QuizResponse.createReviewQuizResponse(quiz));
        }
        this.currentPage = page.getNumber();
        this.pageSize = page.getSize();
        this.totalItems = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}
