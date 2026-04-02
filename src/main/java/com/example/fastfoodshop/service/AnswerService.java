package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.request.AnswerCreateRequest;

import java.util.List;

public interface AnswerService {
    void createAnswers(List<AnswerCreateRequest> answerCreateRequests, Question question);
}
