package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.request.QuestionCreateRequest;
import com.example.fastfoodshop.response.question.QuestionPageResponse;
import com.example.fastfoodshop.response.question.QuestionUpdateResponse;

import java.util.List;

public interface QuestionService {
    QuestionUpdateResponse createQuestions(
            List<QuestionCreateRequest> questionCreateRequests, String topicDifficultySlug
    );

    QuestionPageResponse getAllQuestionsByTopicDifficulty(
            String topicDifficultySlug, int page, int size
    );

    QuestionUpdateResponse updateQuestionActivation(Long questionId, boolean activated);

    QuestionUpdateResponse deleteQuestion(Long questionId);

    List<Question> getAllValidQuestionsByTopicDifficulty(TopicDifficulty topicDifficulty);
}
