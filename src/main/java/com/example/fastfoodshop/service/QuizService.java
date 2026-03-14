package com.example.fastfoodshop.service;

import com.example.fastfoodshop.request.QuizAddFeedbackRequest;
import com.example.fastfoodshop.request.QuizSubmitRequest;
import com.example.fastfoodshop.response.quiz.QuizFeedbackPageResponse;
import com.example.fastfoodshop.response.quiz.QuizHistoryPageResponse;
import com.example.fastfoodshop.response.quiz.QuizResponse;
import com.example.fastfoodshop.response.quiz.QuizUpdateResponse;

public interface QuizService {
    QuizResponse getQuiz(String phone, String topicDifficultySlug);

    QuizResponse checkQuizSubmission(String phone, QuizSubmitRequest quizSubmitRequest);

    QuizUpdateResponse addFeedbackToCompletedQuiz(String phone, QuizAddFeedbackRequest quizAddFeedbackRequest);

    QuizHistoryPageResponse getAllHistoryQuizzesByUser(String phone, int page, int size);

    QuizResponse getQuizHistoryDetailByUser(String phone, Long quizId);

    QuizFeedbackPageResponse getAllFeedbacksByAdmin(int page, int size);
}
