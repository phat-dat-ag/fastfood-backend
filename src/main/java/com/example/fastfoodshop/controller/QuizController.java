package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.request.QuizAddFeedbackRequest;
import com.example.fastfoodshop.request.QuizSubmitRequest;
import com.example.fastfoodshop.response.QuizFeedbackResponse;
import com.example.fastfoodshop.response.QuizResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {
    private final QuizService quizService;

    @GetMapping()
    public ResponseEntity<ResponseWrapper<QuizResponse>> getQuiz(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("topicDifficultySlug") String topicDifficultySlug
    ) {
        return quizService.getQuiz(userDetails.getUsername(), topicDifficultySlug);
    }

    @PostMapping()
    public ResponseEntity<ResponseWrapper<QuizResponse>> submitQuiz(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody QuizSubmitRequest quizSubmitRequest
    ) {
        return quizService.checkQuizSubmission(
                userDetails.getUsername(),
                quizSubmitRequest.getQuizId(),
                quizSubmitRequest.getTopicDifficultySlug(),
                quizSubmitRequest.getQuizQuestions()
        );
    }

    @PutMapping()
    public ResponseEntity<ResponseWrapper<String>> addFeedbackToQuiz(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody QuizAddFeedbackRequest request
    ) {
        return quizService.addFeedbackToCompletedQuiz(userDetails.getUsername(), request.getQuizId(), request.getFeedback());
    }

    @GetMapping("/by-user")
    public ResponseEntity<ResponseWrapper<ArrayList<QuizResponse>>> getAllHistoryQuizzesByUser(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return quizService.getAllHistoryQuizzesByUser(userDetails.getUsername());
    }

    @GetMapping("/by-user/detail")
    public ResponseEntity<ResponseWrapper<QuizResponse>> getQuizHistoryDetailByUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("quizId") Long quizId
    ) {
        return quizService.getQuizHistoryDetailByUser(userDetails.getUsername(), quizId);
    }

    @GetMapping("/manage")
    public ResponseEntity<ResponseWrapper<QuizFeedbackResponse>> getAllFeedbacksByAdmin(@Valid @ModelAttribute PageRequest request) {
        return quizService.getAllFeedbacksByAdmin(request.getPage(), request.getSize());
    }
}
