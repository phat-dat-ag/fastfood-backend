package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Answer;
import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.repository.QuestionRepository;
import com.example.fastfoodshop.request.QuestionCreateRequest;
import com.example.fastfoodshop.response.QuestionResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final CloudinaryService cloudinaryService;
    private final TopicDifficultyService topicDifficultyService;
    private final AnswerService answerService;
    private final QuestionRepository questionRepository;

    private Question findUndeletedQuestion(Long questionId) {
        return questionRepository.findByIdAndIsDeletedFalse(questionId).orElseThrow(
                () -> new RuntimeException("Không tìm thấy câu hỏi này tồn tại")
        );
    }

    private Question findActivatedQuestion(Long questionId) {
        return questionRepository.findByIdAndIsDeletedFalseAndIsActivatedTrue(questionId).orElseThrow(
                () -> new RuntimeException("Không tìm thấy câu hỏi này đang kích hoạt")
        );
    }

    private Question findDeactivatedQuestion(Long questionId) {
        return questionRepository.findByIdAndIsDeletedFalseAndIsActivatedFalse(questionId).orElseThrow(
                () -> new RuntimeException("Không tìm thấy câu hỏi này đang bị hủy kích hoạt")
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
            } catch (Exception e) {
                System.out.println("Ngoai lệ khi dọn ảnh câu hỏi cũ: " + e.getMessage());
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
            } catch (Exception e) {
                System.out.println("Ngoai lệ khi dọn âm thanh câu hỏi cũ: " + e.getMessage());
            }
        }
    }

    public List<Question> getAllValidQuestionsByTopicDifficulty(TopicDifficulty topicDifficulty) {
        return questionRepository.findByTopicDifficultyAndIsActivatedTrueAndIsDeletedFalse(topicDifficulty);
    }

    @Transactional
    public ResponseEntity<ResponseWrapper<String>> createQuestions(List<QuestionCreateRequest> questionCreateRequests, String topicDifficultySlug) {
        try {
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
            return ResponseEntity.ok(ResponseWrapper.success("Đã lưu các câu hỏi"));
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "CREATE_QUESTIONS_FAILED",
                    "Lỗi tạo các câu hỏi " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<QuestionResponse>> getAllQuestionsByTopicDifficulty(String topicDifficultySlug, int page, int size) {
        try {
            TopicDifficulty topicDifficulty = topicDifficultyService.findValidTopicDifficultyOrThrow(topicDifficultySlug);

            Pageable pageable = PageRequest.of(page, size);
            Page<Question> questionPage = questionRepository.findByTopicDifficultyAndIsDeletedFalse(topicDifficulty, pageable);

            return ResponseEntity.ok(ResponseWrapper.success(new QuestionResponse(questionPage)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_ALL_QUESTIONS_FAILED",
                    "Lỗi lấy các câu hỏi của độ khó " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<String>> activateQuestion(Long questionId) {
        try {
            Question question = findDeactivatedQuestion(questionId);
            question.setActivated(true);
            questionRepository.save(question);

            return ResponseEntity.ok(ResponseWrapper.success("Kích hoạt câu hỏi thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "ACTIVATE_QUESTION_FAILED",
                    "Lỗi kích hoạt câu hỏi " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<String>> deactivateQuestion(Long questionId) {
        try {
            Question question = findActivatedQuestion(questionId);
            question.setActivated(false);
            questionRepository.save(question);

            return ResponseEntity.ok(ResponseWrapper.success("Hủy kích hoạt câu hỏi thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "DEACTIVATE_QUESTION_FAILED",
                    "Lỗi hủy kích hoạt câu hỏi " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<String>> deleteQuestion(Long questionId) {
        try {
            Question question = findUndeletedQuestion(questionId);
            question.setDeleted(true);
            questionRepository.save(question);

            return ResponseEntity.ok(ResponseWrapper.success("Xóa câu hỏi thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "DELETE_QUESTION_FAILED",
                    "Lỗi xóa câu hỏi " + e.getMessage()
            ));
        }
    }
}
