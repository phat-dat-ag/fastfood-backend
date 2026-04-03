package com.example.fastfoodshop.factory.topic_difficulty;

import com.example.fastfoodshop.entity.TopicDifficulty;

public class TopicDifficultyFactory {
    private static TopicDifficulty createDifficulty(Long topicDifficultyId) {
        TopicDifficulty topicDifficulty = new TopicDifficulty();

        topicDifficulty.setId(topicDifficultyId);
        topicDifficulty.setSlug("slug" + topicDifficultyId);

        return topicDifficulty;
    }

    public static TopicDifficulty createActivatedDifficulty(Long topicDifficulty) {
        TopicDifficulty activatedDifficulty = createDifficulty(topicDifficulty);

        activatedDifficulty.setActivated(true);
        activatedDifficulty.setDeleted(false);

        return activatedDifficulty;
    }
}
