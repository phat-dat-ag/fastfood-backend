package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Answer;
import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.request.AnswerCreateRequest;

import java.util.ArrayList;
import java.util.List;

public interface AnswerService {
    ArrayList<Answer> createAnswers(List<AnswerCreateRequest> answerCreateRequests, Question question);
}
