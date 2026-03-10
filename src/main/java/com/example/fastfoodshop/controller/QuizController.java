package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.request.QuizAddFeedbackRequest;
import com.example.fastfoodshop.request.QuizSubmitRequest;
import com.example.fastfoodshop.response.QuizFeedbackResponse;
import com.example.fastfoodshop.response.QuizHistoryResponse;
import com.example.fastfoodshop.response.QuizResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ModelAttribute;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController extends BaseController {
    private final QuizService quizService;

    @GetMapping()
    public ResponseEntity<ResponseWrapper<QuizResponse>> getQuiz(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("topicDifficultySlug") String topicDifficultySlug
    ) {
        return okResponse(quizService.getQuiz(userDetails.getUsername(), topicDifficultySlug));
    }

    @PostMapping()
    public ResponseEntity<ResponseWrapper<QuizResponse>> submitQuiz(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody QuizSubmitRequest quizSubmitRequest
    ) {
        return okResponse(quizService.checkQuizSubmission(userDetails.getUsername(), quizSubmitRequest));
    }

    @PutMapping()
    public ResponseEntity<ResponseWrapper<String>> addFeedbackToQuiz(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody QuizAddFeedbackRequest quizAddFeedbackRequest
    ) {
        return okResponse(quizService.addFeedbackToCompletedQuiz(userDetails.getUsername(), quizAddFeedbackRequest));
    }

    @GetMapping("/by-user")
    public ResponseEntity<ResponseWrapper<QuizHistoryResponse>> getAllHistoryQuizzesByUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(quizService.getAllHistoryQuizzesByUser(
                userDetails.getUsername(), request.getPage(), request.getSize())
        );
    }

    @GetMapping("/by-user/detail")
    public ResponseEntity<ResponseWrapper<QuizResponse>> getQuizHistoryDetailByUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("quizId") Long quizId
    ) {
        return okResponse(quizService.getQuizHistoryDetailByUser(userDetails.getUsername(), quizId));
    }

    @GetMapping("/manage")
    public ResponseEntity<ResponseWrapper<QuizFeedbackResponse>> getAllFeedbacksByAdmin(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(quizService.getAllFeedbacksByAdmin(request.getPage(), request.getSize()));
    }
}
