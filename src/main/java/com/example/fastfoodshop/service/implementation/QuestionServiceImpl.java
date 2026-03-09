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
import com.example.fastfoodshop.response.QuestionResponse;
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
    public String createQuestions(List<QuestionCreateRequest> questionCreateRequests, String topicDifficultySlug) {
        TopicDifficulty topicDifficulty = topicDifficultyService.findValidTopicDifficultyOrThrow(topicDifficultySlug);

        for (QuestionCreateRequest questionCreateRequest : questionCreateRequests) {
            Question question = new Question();

            question.setTopicDifficulty(topicDifficulty);
            question.setContent(questionCreateRequest.getContent());
            question.setActivated(questionCreateRequest.getIsActivated());

            handleQuestionImage(question, questionCreateRequest.getImageUrl());
            handleQuestionAudio(question, questionCreateRequest.getAudioUrl());

            Question savedQuestion = questionRepository.save(question);

            List<Answer> savedAnswers = answerService.createAnswers(questionCreateRequest.getAnswers(), savedQuestion);
        }
        return "Đã lưu các câu hỏi";
    }

    public QuestionResponse getAllQuestionsByTopicDifficulty(
            QuestionGetByTopicDifficultyRequest questionGetByTopicDifficultyRequest
    ) {
        TopicDifficulty topicDifficulty = topicDifficultyService.findValidTopicDifficultyOrThrow(
                questionGetByTopicDifficultyRequest.getTopicDifficultySlug()
        );

        Pageable pageable = PageRequest.of(
                questionGetByTopicDifficultyRequest.getPage(), questionGetByTopicDifficultyRequest.getSize()
        );
        Page<Question> questionPage = questionRepository.findByTopicDifficultyAndIsDeletedFalse(topicDifficulty, pageable);

        return new QuestionResponse(questionPage);
    }

    public String activateQuestion(Long questionId) {
        Question question = findDeactivatedQuestion(questionId);
        question.setActivated(true);
        questionRepository.save(question);

        return "Kích hoạt câu hỏi thành công";
    }

    public String deactivateQuestion(Long questionId) {
        Question question = findActivatedQuestion(questionId);
        question.setActivated(false);
        questionRepository.save(question);

        return "Hủy kích hoạt câu hỏi thành công";
    }

    public String deleteQuestion(Long questionId) {
        Question question = findUndeletedQuestion(questionId);
        if (question.isDeleted()) {
            throw new DeletedQuestionException(questionId);
        }
        question.setDeleted(true);
        questionRepository.save(question);

        return "Xóa câu hỏi thành công";
    }
}
