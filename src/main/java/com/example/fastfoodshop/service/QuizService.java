package com.example.fastfoodshop.service;

import com.example.fastfoodshop.request.QuizAddFeedbackRequest;
import com.example.fastfoodshop.request.QuizSubmitRequest;
import com.example.fastfoodshop.response.QuizFeedbackResponse;
import com.example.fastfoodshop.response.QuizHistoryResponse;
import com.example.fastfoodshop.response.QuizResponse;

public interface QuizService {
    QuizResponse getQuiz(String phone, String topicDifficultySlug);

    QuizResponse checkQuizSubmission(String phone, QuizSubmitRequest quizSubmitRequest);

    String addFeedbackToCompletedQuiz(String phone, QuizAddFeedbackRequest quizAddFeedbackRequest);

    QuizHistoryResponse getAllHistoryQuizzesByUser(String phone, int page, int size);

    QuizResponse getQuizHistoryDetailByUser(String phone, Long quizId);

    QuizFeedbackResponse getAllFeedbacksByAdmin(int page, int size);
}
