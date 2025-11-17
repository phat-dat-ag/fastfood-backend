package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.QuestionForm;
import com.example.fastfoodshop.request.QuestionGetByTopicDifficultyRequest;
import com.example.fastfoodshop.response.QuestionResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/question")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping()
    public ResponseEntity<ResponseWrapper<String>> createQuestions(
            @ModelAttribute QuestionForm request,
            @RequestParam("topicDifficultySlug") String topicDifficultySlug
    ) {
        return questionService.createQuestions(request.getQuestions(), topicDifficultySlug);
    }

    @GetMapping()
    public ResponseEntity<ResponseWrapper<QuestionResponse>> getAllQuestionsByTopicDifficulty(
            @Valid @ModelAttribute QuestionGetByTopicDifficultyRequest request
    ) {
        return questionService.getAllQuestionsByTopicDifficulty(request.getTopicDifficultySlug(), request.getPage(), request.getSize());
    }

    @PutMapping("/activate")
    public ResponseEntity<ResponseWrapper<String>> activateQuestion(@RequestParam("questionId") Long questionId) {
        return questionService.activateQuestion(questionId);
    }

    @PutMapping("/deactivate")
    public ResponseEntity<ResponseWrapper<String>> deactivateQuestion(@RequestParam("questionId") Long questionId) {
        return questionService.deactivateQuestion(questionId);
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<String>> deleteQuestion(@RequestParam("questionId") Long questionId) {
        return questionService.deleteQuestion(questionId);
    }
}
