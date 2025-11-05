package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.dto.AwardDTO;
import com.example.fastfoodshop.request.AwardCreateRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.AwardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

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
    public ResponseEntity<ResponseWrapper<ArrayList<AwardDTO>>> getAllAwardsByTopicDifficulty(
            @RequestParam("topicDifficultySlug") String topicDifficultySlug
    ) {
        return awardService.getAllAwardsByTopicDifficulty(topicDifficultySlug);
    }

    @DeleteMapping
    ResponseEntity<ResponseWrapper<AwardDTO>> deleteAward(@RequestParam("awardId") Long awardId) {
        return awardService.deleteAward(awardId);
    }
}
