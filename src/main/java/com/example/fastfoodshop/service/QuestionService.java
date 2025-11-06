package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.QuestionDTO;
import com.example.fastfoodshop.entity.Answer;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.repository.QuestionRepository;
import com.example.fastfoodshop.request.QuestionCreateRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final CloudinaryService cloudinaryService;
    private final TopicDifficultyService topicDifficultyService;
    private final AnswerService answerService;
    private final QuestionRepository questionRepository;

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

    public ResponseEntity<ResponseWrapper<ArrayList<QuestionDTO>>> getAllQuestionsByTopicDifficulty(String topicDifficultySlug) {
        try {
            TopicDifficulty topicDifficulty = topicDifficultyService.findValidTopicDifficultyOrThrow(topicDifficultySlug);

            List<Question> questions = questionRepository.findByTopicDifficultyAndIsDeletedFalse(topicDifficulty);

            ArrayList<QuestionDTO> questionDTOs = new ArrayList<>();
            for (Question question : questions) {
                questionDTOs.add(new QuestionDTO(question));
            }

            return ResponseEntity.ok(ResponseWrapper.success(questionDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_ALL_QUESTIONS_FAILED",
                    "Lỗi lấy các câu hỏi của độ khó " + e.getMessage()
            ));
        }
    }
}
