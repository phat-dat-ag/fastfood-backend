package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.request.QuestionCreateRequest;
import com.example.fastfoodshop.response.QuestionResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface QuestionService {
    List<Question> getAllValidQuestionsByTopicDifficulty(TopicDifficulty topicDifficulty);

    ResponseEntity<ResponseWrapper<String>> createQuestions(List<QuestionCreateRequest> questionCreateRequests, String topicDifficultySlug);

    ResponseEntity<ResponseWrapper<QuestionResponse>> getAllQuestionsByTopicDifficulty(String topicDifficultySlug, int page, int size);

    ResponseEntity<ResponseWrapper<String>> activateQuestion(Long questionId);

    ResponseEntity<ResponseWrapper<String>> deactivateQuestion(Long questionId);

    ResponseEntity<ResponseWrapper<String>> deleteQuestion(Long questionId);
}
