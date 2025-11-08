package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.entity.Quiz;
import com.example.fastfoodshop.entity.QuizQuestion;
import com.example.fastfoodshop.repository.QuizQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizQuestionService {
    private final QuizQuestionRepository questionRepository;

    public void createQuizQuestions(Quiz quiz, List<Question> questions) {
        for (Question question : questions) {
            QuizQuestion quizQuestion = new QuizQuestion();

            quizQuestion.setQuiz(quiz);
            quizQuestion.setQuestion(question);
            questionRepository.save(quizQuestion);
        }
    }
}