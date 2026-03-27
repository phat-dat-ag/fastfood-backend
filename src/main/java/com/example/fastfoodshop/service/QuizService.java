package com.example.fastfoodshop.service;

import com.example.fastfoodshop.request.QuizAddFeedbackRequest;
import com.example.fastfoodshop.request.QuizSubmitRequest;
import com.example.fastfoodshop.response.quiz.QuizFeedbackPageResponse;
import com.example.fastfoodshop.response.quiz.QuizHistoryPageResponse;
import com.example.fastfoodshop.response.quiz.QuizResponse;
import com.example.fastfoodshop.response.quiz.QuizUpdateResponse;

public interface QuizService {
    QuizResponse getQuiz(String phone, String topicDifficultySlug);

    QuizResponse submitQuiz(String phone, QuizSubmitRequest quizSubmitRequest);

    QuizUpdateResponse addFeedbackToQuiz(
            String phone, Long quizId, QuizAddFeedbackRequest quizAddFeedbackRequest
    );

    QuizHistoryPageResponse getQuizHistories(String phone, int page, int size);

    QuizResponse getQuizHistory(String phone, Long quizId);

    QuizFeedbackPageResponse getAllQuizFeedbacks(int page, int size);
}
