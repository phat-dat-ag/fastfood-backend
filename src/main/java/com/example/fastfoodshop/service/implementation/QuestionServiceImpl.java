package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.entity.Answer;
import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.exception.question.DeletedQuestionException;
import com.example.fastfoodshop.exception.question.InvalidQuestionStatusException;
import com.example.fastfoodshop.exception.question.QuestionNotFoundException;
import com.example.fastfoodshop.repository.QuestionRepository;
import com.example.fastfoodshop.request.QuestionCreateRequest;
import com.example.fastfoodshop.request.QuestionGetByTopicDifficultyRequest;
import com.example.fastfoodshop.response.question.QuestionPageResponse;
import com.example.fastfoodshop.response.question.QuestionUpdateResponse;
import com.example.fastfoodshop.service.AnswerService;
import com.example.fastfoodshop.service.CloudinaryService;
import com.example.fastfoodshop.service.QuestionService;
import com.example.fastfoodshop.service.TopicDifficultyService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final CloudinaryService cloudinaryService;
    private final TopicDifficultyService topicDifficultyService;
    private final AnswerService answerService;
    private final QuestionRepository questionRepository;

    private static final Logger log = LoggerFactory.getLogger(QuestionServiceImpl.class);

    private Question findUndeletedQuestion(Long questionId) {
        return questionRepository.findByIdAndIsDeletedFalse(questionId).orElseThrow(
                () -> new QuestionNotFoundException(questionId)
        );
    }

    private Question findActivatedQuestion(Long questionId) {
        return questionRepository.findByIdAndIsDeletedFalseAndIsActivatedTrue(questionId).orElseThrow(
                () -> new InvalidQuestionStatusException(questionId)
        );
    }

    private Question findDeactivatedQuestion(Long questionId) {
        return questionRepository.findByIdAndIsDeletedFalseAndIsActivatedFalse(questionId).orElseThrow(
                () -> new InvalidQuestionStatusException(questionId)
        );
    }

    private void handleQuestionImage(Question question, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty())
            return;

        String oldPublicId = question.getImagePublicId();
        Map<?, ?> result = cloudinaryService.uploadImage(imageFile, "question-image");

        question.setImageUrl((String) result.get("secure_url"));
        question.setImagePublicId((String) result.get("public_id"));

        if (oldPublicId != null && !oldPublicId.isEmpty()) {
            try {
                boolean deleted = cloudinaryService.deleteImage(oldPublicId);
                log.info("Old question image deleted successfully: {}", oldPublicId);
            } catch (Exception e) {
                log.warn("Failed to delete old question image: {}", oldPublicId, e);
            }
        }
    }

    private void handleQuestionAudio(Question question, MultipartFile audioFile) {
        if (audioFile == null || audioFile.isEmpty())
            return;

        String oldAudioPublicId = question.getAudioPublicId();
        Map<?, ?> result = cloudinaryService.uploadAudio(audioFile, "question-audio");

        question.setAudioUrl((String) result.get("secure_url"));
        question.setAudioPublicId((String) result.get("public_id"));

        if (oldAudioPublicId != null && !oldAudioPublicId.isEmpty()) {
            try {
                boolean deleted = cloudinaryService.deleteAudio(oldAudioPublicId);
                log.info("Old question audio deleted successfully: {}", oldAudioPublicId);
            } catch (Exception e) {
                log.warn("Failed to delete old question audio: {}", oldAudioPublicId, e);
            }
        }
    }

    public List<Question> getAllValidQuestionsByTopicDifficulty(TopicDifficulty topicDifficulty) {
        return questionRepository.findByTopicDifficultyAndIsActivatedTrueAndIsDeletedFalse(topicDifficulty);
    }

    @Transactional
    public QuestionUpdateResponse createQuestions(List<QuestionCreateRequest> questionCreateRequests, String topicDifficultySlug) {
        TopicDifficulty topicDifficulty = topicDifficultyService.findValidTopicDifficultyOrThrow(topicDifficultySlug);

        for (QuestionCreateRequest questionCreateRequest : questionCreateRequests) {
            Question question = new Question();

            question.setTopicDifficulty(topicDifficulty);
            question.setContent(questionCreateRequest.content());
            question.setActivated(questionCreateRequest.activated());

            handleQuestionImage(question, questionCreateRequest.imageUrl());
            handleQuestionAudio(question, questionCreateRequest.audioUrl());

            Question savedQuestion = questionRepository.save(question);

            List<Answer> savedAnswers = answerService.createAnswers(questionCreateRequest.answers(), savedQuestion);
        }
        return new QuestionUpdateResponse("Đã lưu các câu hỏi");
    }

    public QuestionPageResponse getAllQuestionsByTopicDifficulty(
            QuestionGetByTopicDifficultyRequest questionGetByTopicDifficultyRequest
    ) {
        TopicDifficulty topicDifficulty = topicDifficultyService.findValidTopicDifficultyOrThrow(
                questionGetByTopicDifficultyRequest.getTopicDifficultySlug()
        );

        Pageable pageable = PageRequest.of(
                questionGetByTopicDifficultyRequest.getPage(), questionGetByTopicDifficultyRequest.getSize()
        );
        Page<Question> questionPage = questionRepository.findByTopicDifficultyAndIsDeletedFalse(topicDifficulty, pageable);

        return QuestionPageResponse.from(questionPage);
    }

    public QuestionUpdateResponse activateQuestion(Long questionId) {
        Question question = findDeactivatedQuestion(questionId);
        question.setActivated(true);
        questionRepository.save(question);

        return new QuestionUpdateResponse("Kích hoạt câu hỏi thành công: " + questionId);
    }

    public QuestionUpdateResponse deactivateQuestion(Long questionId) {
        Question question = findActivatedQuestion(questionId);
        question.setActivated(false);
        questionRepository.save(question);

        return new QuestionUpdateResponse("Hủy kích hoạt câu hỏi thành công: " + questionId);
    }

    public QuestionUpdateResponse deleteQuestion(Long questionId) {
        Question question = findUndeletedQuestion(questionId);
        if (question.isDeleted()) {
            throw new DeletedQuestionException(questionId);
        }
        question.setDeleted(true);
        questionRepository.save(question);

        return new QuestionUpdateResponse("Xóa câu hỏi thành công: " + questionId);
    }
}
