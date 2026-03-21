package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.QuestionForm;
import com.example.fastfoodshop.request.QuestionGetByTopicDifficultyRequest;
import com.example.fastfoodshop.response.question.QuestionPageResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.question.QuestionUpdateResponse;
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
    public ResponseEntity<ResponseWrapper<QuestionUpdateResponse>> createQuestions(
            @ModelAttribute QuestionForm request,
            @RequestParam("topicDifficultySlug") String topicDifficultySlug
    ) {
        return okResponse(questionService.createQuestions(request.questions(), topicDifficultySlug));
    }

    @GetMapping()
    public ResponseEntity<ResponseWrapper<QuestionPageResponse>> getAllQuestionsByTopicDifficulty(
            @Valid @ModelAttribute QuestionGetByTopicDifficultyRequest questionGetByTopicDifficultyRequest
    ) {
        return okResponse(questionService.getAllQuestionsByTopicDifficulty(questionGetByTopicDifficultyRequest));
    }

    @PutMapping("/activate")
    public ResponseEntity<ResponseWrapper<QuestionUpdateResponse>> activateQuestion(
            @RequestParam("questionId") Long questionId
    ) {
        return okResponse(questionService.activateQuestion(questionId));
    }

    @PutMapping("/deactivate")
    public ResponseEntity<ResponseWrapper<QuestionUpdateResponse>> deactivateQuestion(
            @RequestParam("questionId") Long questionId
    ) {
        return okResponse(questionService.deactivateQuestion(questionId));
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<QuestionUpdateResponse>> deleteQuestion(
            @RequestParam("questionId") Long questionId
    ) {
        return okResponse(questionService.deleteQuestion(questionId));
    }
}
