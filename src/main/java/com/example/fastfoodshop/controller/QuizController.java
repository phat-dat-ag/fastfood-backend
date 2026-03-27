package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.request.QuizAddFeedbackRequest;
import com.example.fastfoodshop.request.QuizSubmitRequest;
import com.example.fastfoodshop.response.quiz.QuizHistoryPageResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.quiz.QuizResponse;
import com.example.fastfoodshop.response.quiz.QuizUpdateResponse;
import com.example.fastfoodshop.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController extends BaseController {
    private final QuizService quizService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<QuizResponse>> submitQuiz(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody QuizSubmitRequest quizSubmitRequest
    ) {
        return okResponse(quizService.submitQuiz(userDetails.getUsername(), quizSubmitRequest));
    }

    @PutMapping("/{id}/feedback")
    public ResponseEntity<ResponseWrapper<QuizUpdateResponse>> addFeedbackToQuiz(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long quizId,
            @Valid @RequestBody QuizAddFeedbackRequest quizAddFeedbackRequest
    ) {
        return okResponse(quizService.addFeedbackToQuiz(userDetails.getUsername(), quizId, quizAddFeedbackRequest));
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<QuizHistoryPageResponse>> getQuizHistories(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(quizService.getQuizHistories(
                userDetails.getUsername(), request.getPage(), request.getSize())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<QuizResponse>> getQuizHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long quizId
    ) {
        return okResponse(quizService.getQuizHistory(userDetails.getUsername(), quizId));
    }
}
