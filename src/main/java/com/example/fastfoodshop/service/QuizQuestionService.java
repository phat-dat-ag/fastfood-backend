package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.entity.Quiz;

import java.util.List;

public interface QuizQuestionService {
    void createQuizQuestions(Quiz quiz, List<Question> questions);
}