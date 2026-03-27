package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.TopicDifficultyUpdateRequest;
import com.example.fastfoodshop.request.UpdateActivationRequest;
import com.example.fastfoodshop.request.QuestionForm;
import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.request.AwardCreateRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.award.AwardPageResponse;
import com.example.fastfoodshop.response.award.AwardUpdateResponse;
import com.example.fastfoodshop.response.question.QuestionPageResponse;
import com.example.fastfoodshop.response.question.QuestionUpdateResponse;
import com.example.fastfoodshop.response.topic_difficulty.TopicDifficultyResponse;
import com.example.fastfoodshop.response.topic_difficulty.TopicDifficultyUpdateResponse;
import com.example.fastfoodshop.service.AwardService;
import com.example.fastfoodshop.service.QuestionService;
import com.example.fastfoodshop.service.TopicDifficultyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@RestController
@RequestMapping("/api/admin/topic-difficulties")
@RequiredArgsConstructor
public class AdminTopicDifficultyController extends BaseController {
    private final TopicDifficultyService topicDifficultyService;
    private final QuestionService questionService;
    private final AwardService awardService;

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<TopicDifficultyUpdateResponse>> updateTopicDifficulty(
            @PathVariable("id") Long topicDifficultyId,
            @Valid @RequestBody TopicDifficultyUpdateRequest topicDifficultyUpdateRequest
    ) {
        return okResponse(topicDifficultyService.updateTopicDifficulty(
                topicDifficultyId, topicDifficultyUpdateRequest
        ));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ResponseWrapper<TopicDifficultyResponse>> getTopicDifficultyBySlug(
            @PathVariable("slug") String topicDifficultySlug
    ) {
        return okResponse(topicDifficultyService.getTopicDifficultyBySlug(topicDifficultySlug));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseWrapper<TopicDifficultyUpdateResponse>> updateTopicDifficultyActivation(
            @PathVariable("id") Long topicDifficultyId,
            @Valid @RequestBody UpdateActivationRequest updateActivationRequest
    ) {
        return okResponse(topicDifficultyService.updateTopicDifficultyActivation(
                topicDifficultyId, updateActivationRequest.activated()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<TopicDifficultyUpdateResponse>> deleteTopicDifficulty(
            @PathVariable("id") Long topicDifficultyId
    ) {
        return okResponse(topicDifficultyService.deleteTopicDifficulty(topicDifficultyId));
    }

    @PostMapping("/{topicDifficultySlug}/questions")
    public ResponseEntity<ResponseWrapper<QuestionUpdateResponse>> createQuestions(
            @Valid @ModelAttribute QuestionForm questionForm,
            @PathVariable("topicDifficultySlug") String topicDifficultySlug
    ) {
        return okResponse(questionService.createQuestions(questionForm.questions(), topicDifficultySlug));
    }

    @GetMapping("/{topicDifficultySlug}/questions")
    public ResponseEntity<ResponseWrapper<QuestionPageResponse>> getAllQuestionsByTopicDifficulty(
            @Valid @ModelAttribute PageRequest pageRequest,
            @PathVariable("topicDifficultySlug") String topicDifficultySlug
    ) {
        return okResponse(questionService.getAllQuestionsByTopicDifficulty(
                topicDifficultySlug, pageRequest.getPage(), pageRequest.getSize()
        ));
    }

    @PostMapping("/{topicDifficultySlug}/awards")
    public ResponseEntity<ResponseWrapper<AwardUpdateResponse>> createAward(
            @PathVariable("topicDifficultySlug") String topicDifficultySlug,
            @RequestBody @Valid AwardCreateRequest awardCreateRequest
    ) {
        return okResponse(awardService.createAward(topicDifficultySlug, awardCreateRequest));
    }

    @GetMapping("/{topicDifficultySlug}/awards")
    public ResponseEntity<ResponseWrapper<AwardPageResponse>> getAllAwardsByTopicDifficulty(
            @PathVariable("topicDifficultySlug") String topicDifficultySlug,
            @Valid @ModelAttribute PageRequest pageRequest
    ) {
        return okResponse(awardService.getAllAwardsByTopicDifficulty(
                topicDifficultySlug, pageRequest.getPage(), pageRequest.getSize()
        ));
    }
}
