package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.dto.AwardDTO;
import com.example.fastfoodshop.request.AwardCreateRequest;
import com.example.fastfoodshop.request.AwardGetByTopicDifficultyRequest;
import com.example.fastfoodshop.response.AwardResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.AwardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/award")
@RequiredArgsConstructor
public class AwardController {
    private final AwardService awardService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<AwardDTO>> createAward(
            @RequestParam("topicDifficultySlug") String topicDifficultySlug,
            @RequestBody AwardCreateRequest request
    ) {
        return awardService.createAward(topicDifficultySlug, request);
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<AwardResponse>> getAllAwardsByTopicDifficulty(
            @Valid @ModelAttribute AwardGetByTopicDifficultyRequest request
    ) {
        return awardService.getAllAwardsByTopicDifficulty(request.getTopicDifficultySlug(), request.getPage(), request.getSize());
    }

    @PutMapping("/activate")
    public ResponseEntity<ResponseWrapper<String>> activateAward(@RequestParam("awardId") Long awardId) {
        return awardService.activateAward(awardId);
    }

    @PutMapping("/deactivate")
    public ResponseEntity<ResponseWrapper<String>> deactivateAward(@RequestParam("awardId") Long awardId) {
        return awardService.deactivateAward(awardId);
    }

    @DeleteMapping
    ResponseEntity<ResponseWrapper<AwardDTO>> deleteAward(@RequestParam("awardId") Long awardId) {
        return awardService.deleteAward(awardId);
    }
}
