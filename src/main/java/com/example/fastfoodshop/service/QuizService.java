package com.example.fastfoodshop.service;

import com.example.fastfoodshop.request.QuizQuestionSubmitRequest;
import com.example.fastfoodshop.response.QuizFeedbackResponse;
import com.example.fastfoodshop.response.QuizHistoryResponse;
import com.example.fastfoodshop.response.QuizResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface QuizService {
    ResponseEntity<ResponseWrapper<QuizResponse>> getQuiz(String phone, String topicDifficultySlug);

    ResponseEntity<ResponseWrapper<QuizResponse>> checkQuizSubmission(
            String phone, Long quizId, String topicDifficultySlug, List<QuizQuestionSubmitRequest> quizQuestionSubmits
    );

    ResponseEntity<ResponseWrapper<String>> addFeedbackToCompletedQuiz(String phone, Long quizId, String feedback);

    ResponseEntity<ResponseWrapper<QuizHistoryResponse>> getAllHistoryQuizzesByUser(String phone, int page, int size);

    ResponseEntity<ResponseWrapper<QuizResponse>> getQuizHistoryDetailByUser(String phone, Long quizId);

    ResponseEntity<ResponseWrapper<QuizFeedbackResponse>> getAllFeedbacksByAdmin(int page, int size);
}
