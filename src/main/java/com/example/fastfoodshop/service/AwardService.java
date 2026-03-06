package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.AwardDTO;
import com.example.fastfoodshop.entity.Award;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.request.AwardCreateRequest;
import com.example.fastfoodshop.response.AwardResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import org.springframework.http.ResponseEntity;

public interface AwardService {
    Award getRandomAwardByTopicDifficulty(TopicDifficulty topicDifficulty);

    ResponseEntity<ResponseWrapper<AwardDTO>> createAward(String topicDifficultySlug, AwardCreateRequest request);

    ResponseEntity<ResponseWrapper<AwardResponse>> getAllAwardsByTopicDifficulty(String topicDifficultySlug, int page, int size);

    ResponseEntity<ResponseWrapper<String>> activateAward(Long awardId);

    ResponseEntity<ResponseWrapper<String>> deactivateAward(Long awardId);

    ResponseEntity<ResponseWrapper<AwardDTO>> deleteAward(Long awardId);
}
