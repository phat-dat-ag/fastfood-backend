package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.AwardDTO;
import com.example.fastfoodshop.entity.Award;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.request.AwardCreateRequest;
import com.example.fastfoodshop.request.AwardGetByTopicDifficultyRequest;
import com.example.fastfoodshop.response.AwardResponse;

public interface AwardService {
    Award getRandomAwardByTopicDifficulty(TopicDifficulty topicDifficulty);

    AwardDTO createAward(String topicDifficultySlug, AwardCreateRequest request);

    AwardResponse getAllAwardsByTopicDifficulty(AwardGetByTopicDifficultyRequest awardGetByTopicDifficultyRequest);

    String activateAward(Long awardId);

    String deactivateAward(Long awardId);

    AwardDTO deleteAward(Long awardId);
}
