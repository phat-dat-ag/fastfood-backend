package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.request.QuestionCreateRequest;
import com.example.fastfoodshop.request.QuestionGetByTopicDifficultyRequest;
import com.example.fastfoodshop.response.question.QuestionPageResponse;
import com.example.fastfoodshop.response.question.QuestionUpdateResponse;

import java.util.List;

public interface QuestionService {
    List<Question> getAllValidQuestionsByTopicDifficulty(TopicDifficulty topicDifficulty);

    QuestionUpdateResponse createQuestions(List<QuestionCreateRequest> questionCreateRequests, String topicDifficultySlug);

    QuestionPageResponse getAllQuestionsByTopicDifficulty(
            QuestionGetByTopicDifficultyRequest questionGetByTopicDifficultyRequest
    );

    QuestionUpdateResponse activateQuestion(Long questionId);

    QuestionUpdateResponse deactivateQuestion(Long questionId);

    QuestionUpdateResponse deleteQuestion(Long questionId);
}
