package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.request.QuestionCreateRequest;
import com.example.fastfoodshop.request.QuestionGetByTopicDifficultyRequest;
import com.example.fastfoodshop.response.QuestionResponse;

import java.util.List;

public interface QuestionService {
    List<Question> getAllValidQuestionsByTopicDifficulty(TopicDifficulty topicDifficulty);

    String createQuestions(List<QuestionCreateRequest> questionCreateRequests, String topicDifficultySlug);

    QuestionResponse getAllQuestionsByTopicDifficulty(
            QuestionGetByTopicDifficultyRequest questionGetByTopicDifficultyRequest
    );

    String activateQuestion(Long questionId);

    String deactivateQuestion(Long questionId);

    String deleteQuestion(Long questionId);
}
