package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.AwardDTO;
import com.example.fastfoodshop.entity.Award;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.repository.AwardRepository;
import com.example.fastfoodshop.request.AwardCreateRequest;
import com.example.fastfoodshop.response.AwardResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.util.NumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AwardService {
    private final TopicDifficultyService topicDifficultyService;
    private final AwardRepository awardRepository;

    private Award findAwardOrThrow(Long id) {
        return awardRepository.findById(id).orElseThrow(() -> new RuntimeException("Phần thưởng không tồn tại hoặc đã bị xóa"));
    }

    private Award findActivatedAward(Long awardId) {
        return awardRepository.findByIdAndIsDeletedFalseAndIsActivatedTrue(awardId).orElseThrow(
                () -> new RuntimeException("Không tìm thấy phần thưởng này đang kích hoạt")
        );
    }

    private Award findDeactivatedAward(Long awardId) {
        return awardRepository.findByIdAndIsDeletedFalseAndIsActivatedFalse(awardId).orElseThrow(
                () -> new RuntimeException("Không tìm thấy phần thưởng này đang bị hủy kích hoạt")
        );
    }

    private void buildAward(Award award, AwardCreateRequest request) {
        award.setType(request.getType());
        award.setMinValue(request.getMinValue());
        award.setMaxValue(request.getMaxValue());
        award.setUsedQuantity(0);
        award.setQuantity(request.getQuantity());
        award.setMaxDiscountAmount(request.getMaxDiscountAmount());
        award.setMinSpendAmount(request.getMinSpendAmount());
        award.setActivated(request.getIsActivated());
        award.setDeleted(false);
    }

    public Award getRandomAwardByTopicDifficulty(TopicDifficulty topicDifficulty) {
        List<Award> availableAwards = awardRepository.findAvailableByTopicDifficulty(topicDifficulty.getId());

        if (!availableAwards.isEmpty()) {
            int index = NumberUtils.randomNumber(0, availableAwards.size() - 1);
            return availableAwards.get(index);
        } else {
            Optional<Award> optionalAward = awardRepository.findAnyAvailableAwardAsFallback(topicDifficulty.getId());
            return optionalAward.orElseThrow(() -> new RuntimeException("Lỗi: không có phần thưởng nào tồn tại"));
        }
    }

    public ResponseEntity<ResponseWrapper<AwardDTO>> createAward(String topicDifficultySlug, AwardCreateRequest request) {
        try {
            TopicDifficulty topicDifficulty = topicDifficultyService.findValidTopicDifficultyOrThrow(topicDifficultySlug);
            Award award = new Award();
            award.setTopicDifficulty(topicDifficulty);
            buildAward(award, request);

            Award savedAward = awardRepository.save(award);
            return ResponseEntity.ok(ResponseWrapper.success(new AwardDTO(savedAward)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "CREATE_AWARD_FAILED",
                    "Lỗi tạo phần thưởng cho độ khó " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<AwardResponse>> getAllAwardsByTopicDifficulty(String topicDifficultySlug, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            TopicDifficulty topicDifficulty = topicDifficultyService.findValidTopicDifficultyOrThrow(topicDifficultySlug);
            Page<Award> awardPage = awardRepository.findByTopicDifficultyAndIsDeletedFalse(topicDifficulty, pageable);

            return ResponseEntity.ok(ResponseWrapper.success(new AwardResponse(awardPage)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_ALL_AWARDS_FAILED",
                    "Lỗi lấy các phần thưởng của độ khó " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<String>> activateAward(Long awardId) {
        try {
            Award award = findDeactivatedAward(awardId);
            award.setActivated(true);
            awardRepository.save(award);

            return ResponseEntity.ok(ResponseWrapper.success("Kích hoạt phần thưởng thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "ACTIVATE_AWARD_FAILED",
                    "Lỗi kích hoạt phần thưởng " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<String>> deactivateAward(Long awardId) {
        try {
            Award award = findActivatedAward(awardId);
            award.setActivated(false);
            awardRepository.save(award);

            return ResponseEntity.ok(ResponseWrapper.success("Hủy kích hoạt phần thưởng thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "DEACTIVATE_AWARD_FAILED",
                    "Lỗi hủy kích hoạt phần thưởng " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<AwardDTO>> deleteAward(Long awardId) {
        try {
            Award award = findAwardOrThrow(awardId);
            award.setDeleted(true);

            Award deletedAward = awardRepository.save(award);
            return ResponseEntity.ok(ResponseWrapper.success(new AwardDTO(deletedAward)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "DELETE_AWARD_FAILED",
                    "Lỗi xóa phần thưởng của độ khó " + e.getMessage()
            ));
        }
    }
}
