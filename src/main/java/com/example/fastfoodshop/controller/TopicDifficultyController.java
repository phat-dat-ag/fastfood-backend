package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.quiz.QuizResponse;
import com.example.fastfoodshop.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/topic-difficulties")
@RequiredArgsConstructor
public class TopicDifficultyController extends BaseController {
    private final QuizService quizService;

    @GetMapping("/{topicDifficultySlug}/quizzes")
    public ResponseEntity<ResponseWrapper<QuizResponse>> getQuiz(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("topicDifficultySlug") String topicDifficultySlug
    ) {
        return okResponse(quizService.getQuiz(userDetails.getUsername(), topicDifficultySlug));
    }
}
