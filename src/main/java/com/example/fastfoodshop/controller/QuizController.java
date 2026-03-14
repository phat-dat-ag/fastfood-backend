package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.request.QuizAddFeedbackRequest;
import com.example.fastfoodshop.request.QuizSubmitRequest;
import com.example.fastfoodshop.response.quiz.QuizFeedbackPageResponse;
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
    public ResponseEntity<ResponseWrapper<QuizUpdateResponse>> addFeedbackToQuiz(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody QuizAddFeedbackRequest quizAddFeedbackRequest
    ) {
        return okResponse(quizService.addFeedbackToCompletedQuiz(userDetails.getUsername(), quizAddFeedbackRequest));
    }

    @GetMapping("/by-user")
    public ResponseEntity<ResponseWrapper<QuizHistoryPageResponse>> getAllHistoryQuizzesByUser(
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
    public ResponseEntity<ResponseWrapper<QuizFeedbackPageResponse>> getAllFeedbacksByAdmin(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(quizService.getAllFeedbacksByAdmin(request.getPage(), request.getSize()));
    }
}
