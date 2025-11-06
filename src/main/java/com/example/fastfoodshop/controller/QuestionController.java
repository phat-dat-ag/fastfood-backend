package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.dto.QuestionDTO;
import com.example.fastfoodshop.request.QuestionForm;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

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
    public ResponseEntity<ResponseWrapper<ArrayList<QuestionDTO>>> getAllQuestionsByTopicDifficulty(
            @RequestParam("topicDifficultySlug") String topicDifficultySlug
    ) {
        return questionService.getAllQuestionsByTopicDifficulty(topicDifficultySlug);
    }
}
