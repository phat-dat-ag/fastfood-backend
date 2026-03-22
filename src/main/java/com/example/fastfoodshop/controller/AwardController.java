package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.AwardCreateRequest;
import com.example.fastfoodshop.request.AwardGetByTopicDifficultyRequest;
import com.example.fastfoodshop.request.UpdateActivationRequest;
import com.example.fastfoodshop.response.award.AwardPageResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.award.AwardUpdateResponse;
import com.example.fastfoodshop.service.AwardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/awards")
@RequiredArgsConstructor
public class AwardController extends BaseController {
    private final AwardService awardService;

    @PostMapping("/{topicDifficultySlug}")
    public ResponseEntity<ResponseWrapper<AwardUpdateResponse>> createAward(
            @PathVariable("topicDifficultySlug") String topicDifficultySlug,
            @RequestBody @Valid AwardCreateRequest awardCreateRequest
    ) {
        return okResponse(awardService.createAward(topicDifficultySlug, awardCreateRequest));
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<AwardPageResponse>> getAllAwardsByTopicDifficulty(
            @Valid @ModelAttribute AwardGetByTopicDifficultyRequest awardGetByTopicDifficultyRequest
    ) {
        return okResponse(awardService.getAllAwardsByTopicDifficulty(awardGetByTopicDifficultyRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseWrapper<AwardUpdateResponse>> updateAwardActivation(
            @PathVariable("id") Long awardId,
            @RequestBody @Valid UpdateActivationRequest updateActivationRequest
    ) {
        return okResponse(awardService.updateAwardActivation(awardId, updateActivationRequest.activated()));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ResponseWrapper<AwardUpdateResponse>> deleteAward(
            @PathVariable("id") Long awardId
    ) {
        return okResponse(awardService.deleteAward(awardId));
    }
}
