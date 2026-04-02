package com.example.fastfoodshop.service;

import com.example.fastfoodshop.constant.FolderNameConstants;
import com.example.fastfoodshop.entity.Answer;
import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.exception.question.InvalidAnswerCountException;
import com.example.fastfoodshop.exception.question.InvalidCorrectAnswerCountException;
import com.example.fastfoodshop.factory.answer.AnswerCreateRequestFactory;
import com.example.fastfoodshop.factory.question.QuestionFactory;
import com.example.fastfoodshop.repository.AnswerRepository;
import com.example.fastfoodshop.request.AnswerCreateRequest;
import com.example.fastfoodshop.service.implementation.AnswerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AnswerServiceImplTest {
    @Mock
    CloudinaryService cloudinaryService;

    @Mock
    AnswerRepository answerRepository;

    @InjectMocks
    AnswerServiceImpl answerService;

    @Test
    void createAnswers_validRequestWithTextAnswer_shouldNotThrowException() {
        List<AnswerCreateRequest> validRequest = AnswerCreateRequestFactory.createValidTextAnswers();

        when(answerRepository.save(any(Answer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Question validQuestion = QuestionFactory.createValid();

        answerService.createAnswers(validRequest, validQuestion);

        verify(answerRepository, times(validRequest.size())).save(any(Answer.class));
    }

    @Test
    void createAnswers_validRequestWithImageAnswer_shouldNotThrowException() {
        List<AnswerCreateRequest> validRequest = AnswerCreateRequestFactory.createValidImageAnswers();

        when(cloudinaryService.uploadImage(
                any(MultipartFile.class), eq(FolderNameConstants.answerFolderName))
        ).thenReturn(new HashMap<>());

        when(answerRepository.save(any(Answer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Question validQuestion = QuestionFactory.createValid();

        answerService.createAnswers(validRequest, validQuestion);

        verify(cloudinaryService, times(validRequest.size()))
                .uploadImage(any(MultipartFile.class), eq(FolderNameConstants.answerFolderName));

        verify(answerRepository, times(validRequest.size())).save(any(Answer.class));
    }

    @Test
    void createAnswers_nullRequest_shouldThrowInvalidAnswerCountException() {
        Question validQuestion = QuestionFactory.createValid();

        assertThrows(InvalidAnswerCountException.class,
                () -> answerService.createAnswers(null, validQuestion)
        );
    }

    @Test
    void createAnswers_emptyRequest_shouldThrowInvalidAnswerCountException() {
        List<AnswerCreateRequest> emptyRequest = List.of();

        Question validQuestion = QuestionFactory.createValid();

        assertThrows(InvalidAnswerCountException.class,
                () -> answerService.createAnswers(emptyRequest, validQuestion)
        );
    }

    @Test
    void createAnswers_requestNotEnoughAnswerCount_shouldThrowInvalidAnswerCountException() {
        List<AnswerCreateRequest> validRequest = AnswerCreateRequestFactory.createNotEnoughAnswers();

        Question validQuestion = QuestionFactory.createValid();

        assertThrows(InvalidAnswerCountException.class,
                () -> answerService.createAnswers(validRequest, validQuestion)
        );
    }

    @Test
    void createAnswers_requestInvalidCorrectAnswerCount_shouldThrowInvalidCorrectAnswerCountException() {
        List<AnswerCreateRequest> validRequest = AnswerCreateRequestFactory.createInvalidCorrectAnswers();

        Question validQuestion = QuestionFactory.createValid();

        assertThrows(InvalidCorrectAnswerCountException.class,
                () -> answerService.createAnswers(validRequest, validQuestion)
        );
    }
}
