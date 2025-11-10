package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.QuizSubmitRequest;
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
}
