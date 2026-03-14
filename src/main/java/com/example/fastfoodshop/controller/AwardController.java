package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.AwardCreateRequest;
import com.example.fastfoodshop.request.AwardGetByTopicDifficultyRequest;
import com.example.fastfoodshop.response.award.AwardPageResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.award.AwardUpdateResponse;
import com.example.fastfoodshop.service.AwardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

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

    @PutMapping("/activate")
    public ResponseEntity<ResponseWrapper<AwardUpdateResponse>> activateAward(@RequestParam("awardId") Long awardId) {
        return okResponse(awardService.activateAward(awardId));
    }

    @PutMapping("/deactivate")
    public ResponseEntity<ResponseWrapper<AwardUpdateResponse>> deactivateAward(@RequestParam("awardId") Long awardId) {
        return okResponse(awardService.deactivateAward(awardId));
    }

    @DeleteMapping
    ResponseEntity<ResponseWrapper<AwardUpdateResponse>> deleteAward(@RequestParam("awardId") Long awardId) {
        return okResponse(awardService.deleteAward(awardId));
    }
}
