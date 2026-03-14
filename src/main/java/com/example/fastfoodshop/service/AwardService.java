package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Award;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.request.AwardCreateRequest;
import com.example.fastfoodshop.request.AwardGetByTopicDifficultyRequest;
import com.example.fastfoodshop.response.award.AwardPageResponse;
import com.example.fastfoodshop.response.award.AwardUpdateResponse;

public interface AwardService {
    Award getRandomAwardByTopicDifficulty(TopicDifficulty topicDifficulty);

    AwardUpdateResponse createAward(String topicDifficultySlug, AwardCreateRequest request);

    AwardPageResponse getAllAwardsByTopicDifficulty(AwardGetByTopicDifficultyRequest awardGetByTopicDifficultyRequest);

    AwardUpdateResponse activateAward(Long awardId);

    AwardUpdateResponse deactivateAward(Long awardId);

    AwardUpdateResponse deleteAward(Long awardId);
}
