package com.example.fastfoodshop.factory.answer;

import com.example.fastfoodshop.request.AnswerCreateRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

public class AnswerCreateRequestFactory {
    private static final String CORRECT_CONTENT = "Câu trả lời đúng";
    private static final String INCORRECT_CONTENT = "Câu trả lời sai";

    private static AnswerCreateRequest answer(String content, MockMultipartFile file, boolean correct) {
        return new AnswerCreateRequest(content, file, correct);
    }

    private static MockMultipartFile createEmptyFile() {
        return new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                new byte[]{}
        );
    }

    private static MockMultipartFile createValidFile() {
        return new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "data".getBytes()
        );
    }

    private static AnswerCreateRequest createCorrectTextAnswer() {
        return answer(CORRECT_CONTENT, null, true);
    }

    private static AnswerCreateRequest createIncorrectTextAnswer() {
        return answer(INCORRECT_CONTENT, null, false);
    }

    private static AnswerCreateRequest createIncorrectTextAnswerWithEmptyImage() {
        return answer(INCORRECT_CONTENT, createEmptyFile(), false);
    }

    private static AnswerCreateRequest createCorrectImageAnswer() {
        return answer(null, createValidFile(), true);
    }

    private static AnswerCreateRequest createIncorrectImageAnswer() {
        return answer(null, createValidFile(), false);
    }

    public static List<AnswerCreateRequest> createValidTextAnswers() {
        return List.of(
                createIncorrectTextAnswer(),
                createCorrectTextAnswer(),
                createIncorrectTextAnswerWithEmptyImage(),
                createIncorrectTextAnswer()
        );
    }

    public static List<AnswerCreateRequest> createValidImageAnswers() {
        return List.of(
                createCorrectImageAnswer(),
                createIncorrectImageAnswer(),
                createIncorrectImageAnswer(),
                createIncorrectImageAnswer()
        );
    }

    public static List<AnswerCreateRequest> createNotEnoughAnswers() {
        return List.of(
                createIncorrectTextAnswer(),
                createCorrectTextAnswer(),
                createIncorrectTextAnswer()
        );
    }

    public static List<AnswerCreateRequest> createInvalidCorrectAnswers() {
        return List.of(
                createIncorrectTextAnswer(),
                createCorrectTextAnswer(),
                createCorrectTextAnswer(),
                createIncorrectTextAnswer()
        );
    }
}