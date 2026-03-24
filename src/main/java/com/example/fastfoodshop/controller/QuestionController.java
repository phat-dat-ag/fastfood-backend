package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.UpdateActivationRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.question.QuestionUpdateResponse;
import com.example.fastfoodshop.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/admin/questions")
@RequiredArgsConstructor
public class QuestionController extends BaseController {
    private final QuestionService questionService;

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseWrapper<QuestionUpdateResponse>> updateQuestionActivation(
            @PathVariable("id") Long questionId,
            @Valid @RequestBody UpdateActivationRequest updateActivationRequest
    ) {
        return okResponse(questionService.updateQuestionActivation(
                questionId, updateActivationRequest.activated()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<QuestionUpdateResponse>> deleteQuestion(
            @PathVariable("id") Long questionId
    ) {
        return okResponse(questionService.deleteQuestion(questionId));
    }
}
