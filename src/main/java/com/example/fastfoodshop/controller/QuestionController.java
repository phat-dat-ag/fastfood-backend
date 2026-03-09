package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.QuestionForm;
import com.example.fastfoodshop.request.QuestionGetByTopicDifficultyRequest;
import com.example.fastfoodshop.response.QuestionResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
public class QuestionController extends BaseController {
    private final QuestionService questionService;

    @PostMapping()
    public ResponseEntity<ResponseWrapper<String>> createQuestions(
            @ModelAttribute QuestionForm request,
            @RequestParam("topicDifficultySlug") String topicDifficultySlug
    ) {
        return okResponse(questionService.createQuestions(request.getQuestions(), topicDifficultySlug));
    }

    @GetMapping()
    public ResponseEntity<ResponseWrapper<QuestionResponse>> getAllQuestionsByTopicDifficulty(
            @Valid @ModelAttribute QuestionGetByTopicDifficultyRequest questionGetByTopicDifficultyRequest
    ) {
        return okResponse(questionService.getAllQuestionsByTopicDifficulty(questionGetByTopicDifficultyRequest));
    }

    @PutMapping("/activate")
    public ResponseEntity<ResponseWrapper<String>> activateQuestion(@RequestParam("questionId") Long questionId) {
        return okResponse(questionService.activateQuestion(questionId));
    }

    @PutMapping("/deactivate")
    public ResponseEntity<ResponseWrapper<String>> deactivateQuestion(@RequestParam("questionId") Long questionId) {
        return okResponse(questionService.deactivateQuestion(questionId));
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<String>> deleteQuestion(@RequestParam("questionId") Long questionId) {
        return okResponse(questionService.deleteQuestion(questionId));
    }
}
