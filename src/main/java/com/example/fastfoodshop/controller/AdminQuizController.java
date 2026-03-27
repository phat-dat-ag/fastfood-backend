package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.quiz.QuizFeedbackPageResponse;
import com.example.fastfoodshop.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/quizzes")
@RequiredArgsConstructor
public class AdminQuizController extends BaseController {
    private final QuizService quizService;

    @GetMapping("feedback")
    public ResponseEntity<ResponseWrapper<QuizFeedbackPageResponse>> getAllQuizFeedbacks(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(quizService.getAllQuizFeedbacks(request.getPage(), request.getSize()));
    }
}
