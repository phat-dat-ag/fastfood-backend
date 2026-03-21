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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/award")
@RequiredArgsConstructor
public class AwardController extends BaseController {
    private final AwardService awardService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<AwardUpdateResponse>> createAward(
            @RequestParam("topicDifficultySlug") String topicDifficultySlug,
            @RequestBody AwardCreateRequest awardCreateRequest
    ) {
        return okResponse(awardService.createAward(topicDifficultySlug, awardCreateRequest));
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<AwardPageResponse>> getAllAwardsByTopicDifficulty(
            @Valid @ModelAttribute AwardGetByTopicDifficultyRequest awardGetByTopicDifficultyRequest
    ) {
        return okResponse(awardService.getAllAwardsByTopicDifficulty(awardGetByTopicDifficultyRequest));
    }

    @PatchMapping("/{id}/activation")
    public ResponseEntity<ResponseWrapper<AwardUpdateResponse>> updateAwardActivation(
            @PathVariable("id") Long awardId,
            @RequestBody UpdateActivationRequest updateActivationRequest
    ) {
        return okResponse(awardService.updateAwardActivation(awardId, updateActivationRequest.activated()));
    }

    @DeleteMapping
    ResponseEntity<ResponseWrapper<AwardUpdateResponse>> deleteAward(@RequestParam("awardId") Long awardId) {
        return okResponse(awardService.deleteAward(awardId));
    }
}
