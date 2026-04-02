package com.example.fastfoodshop.factory.question;

import com.example.fastfoodshop.entity.Question;

public class QuestionFactory {
    public static Question createValid() {
        Question question = new Question();

        question.setId(123L);

        return question;
    }
}
