package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Award;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.exception.award.AwardNotFoundException;
import com.example.fastfoodshop.exception.award.DeletedAwardException;
import com.example.fastfoodshop.exception.award.InvalidAwardStatusException;
import com.example.fastfoodshop.exception.topic_difficulty.TopicDifficultyNotFoundException;
import com.example.fastfoodshop.factory.award.AwardCreateRequestFactory;
import com.example.fastfoodshop.factory.award.AwardFactory;
import com.example.fastfoodshop.factory.award.AwardPageFactory;
import com.example.fastfoodshop.factory.topic_difficulty.TopicDifficultyFactory;
import com.example.fastfoodshop.repository.AwardRepository;
import com.example.fastfoodshop.request.AwardCreateRequest;
import com.example.fastfoodshop.response.award.AwardPageResponse;
import com.example.fastfoodshop.response.award.AwardUpdateResponse;
import com.example.fastfoodshop.service.implementation.AwardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AwardServiceImplTest {
    @Mock
    TopicDifficultyService topicDifficultyService;

    @Mock
    AwardRepository awardRepository;

    @InjectMocks
    AwardServiceImpl awardService;

    private static final Long TOPIC_DIFFICULTY_ID = 109L;
    private static final String TOPIC_DIFFICULTY_SLUG = "hong-tra";

    private static final int PAGE = 5;
    private static final int SIZE = 5;

    private static final Long AWARD_ID = 888L;

    @Test
    void getRandomAwardByTopicDifficulty_availableAwards_shouldReturnAward() {
        List<Award> availableAwards = AwardFactory.createAvailableAwards();

        when(awardRepository.findAvailableByTopicDifficulty(any(Long.class)))
                .thenReturn(availableAwards);

        TopicDifficulty activatedDifficulty =
                TopicDifficultyFactory.createActivatedDifficulty(TOPIC_DIFFICULTY_ID);

        Award awardResponse = awardService.getRandomAwardByTopicDifficulty(activatedDifficulty);

        assertNotNull(awardResponse);
        assertNotNull(awardResponse.getId());

        assertFalse(awardResponse.isDeleted());

        assertTrue(awardResponse.isActivated());
        assertTrue(awardResponse.getUsedQuantity() < awardResponse.getQuantity());

        verify(awardRepository).findAvailableByTopicDifficulty(activatedDifficulty.getId());
    }

    @Test
    void getRandomAwardByTopicDifficulty_fallbackAwards_shouldReturnAward() {
        List<Award> availableAwards = List.of();

        when(awardRepository.findAvailableByTopicDifficulty(any(Long.class)))
                .thenReturn(availableAwards);

        TopicDifficulty activatedDifficulty =
                TopicDifficultyFactory.createActivatedDifficulty(TOPIC_DIFFICULTY_ID);

        Award deactivatedAward = AwardFactory.createDeactivatedAward(AWARD_ID);

        when(awardRepository.findAnyAvailableAwardAsFallback(activatedDifficulty.getId()))
                .thenReturn(Optional.of(deactivatedAward));

        Award awardResponse = awardService.getRandomAwardByTopicDifficulty(activatedDifficulty);

        assertNotNull(awardResponse);
        assertNotNull(awardResponse.getId());

        verify(awardRepository).findAvailableByTopicDifficulty(activatedDifficulty.getId());
    }

    @Test
    void getRandomAwardByTopicDifficulty_unavailableAwards_shouldThrowAwardNotFoundException() {
        List<Award> availableAwards = List.of();

        when(awardRepository.findAvailableByTopicDifficulty(any(Long.class)))
                .thenReturn(availableAwards);

        TopicDifficulty activatedDifficulty =
                TopicDifficultyFactory.createActivatedDifficulty(TOPIC_DIFFICULTY_ID);

        when(awardRepository.findAnyAvailableAwardAsFallback(activatedDifficulty.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                AwardNotFoundException.class,
                () -> awardService.getRandomAwardByTopicDifficulty(activatedDifficulty)
        );

        verify(awardRepository).findAvailableByTopicDifficulty(activatedDifficulty.getId());
    }

    @Test
    void createAward_validRequest_shouldReturnAwardUpdateResponse() {
        TopicDifficulty activatedDifficulty =
                TopicDifficultyFactory.createActivatedDifficulty(TOPIC_DIFFICULTY_ID);

        when(topicDifficultyService.findValidTopicDifficultyOrThrow(activatedDifficulty.getSlug()))
                .thenReturn(activatedDifficulty);

        Award deactivatedAward = AwardFactory.createDeactivatedAward(AWARD_ID);

        when(awardRepository.save(any(Award.class))).thenReturn(deactivatedAward);

        AwardCreateRequest validRequest = AwardCreateRequestFactory.createValid();

        AwardUpdateResponse awardUpdateResponse =
                awardService.createAward(activatedDifficulty.getSlug(), validRequest);

        assertNotNull(awardUpdateResponse);
        assertNotNull(awardUpdateResponse.message());

        verify(topicDifficultyService).findValidTopicDifficultyOrThrow(activatedDifficulty.getSlug());
        verify(awardRepository).save(any(Award.class));
    }

    @Test
    void createAward_notFoundDifficulty_shouldThrowTopicDifficultyNotFoundException() {
        when(topicDifficultyService.findValidTopicDifficultyOrThrow(TOPIC_DIFFICULTY_SLUG))
                .thenThrow(new TopicDifficultyNotFoundException(TOPIC_DIFFICULTY_SLUG));

        AwardCreateRequest validRequest = AwardCreateRequestFactory.createValid();

        assertThrows(
                TopicDifficultyNotFoundException.class,
                () -> awardService.createAward(TOPIC_DIFFICULTY_SLUG, validRequest)
        );

        verify(topicDifficultyService).findValidTopicDifficultyOrThrow(TOPIC_DIFFICULTY_SLUG);
    }

    @Test
    void getAllAwardsByTopicDifficulty_validDifficulty_shouldReturnAwardPageResponse() {
        TopicDifficulty activatedDifficulty =
                TopicDifficultyFactory.createActivatedDifficulty(TOPIC_DIFFICULTY_ID);

        when(topicDifficultyService.findValidTopicDifficultyOrThrow(activatedDifficulty.getSlug()))
                .thenReturn(activatedDifficulty);

        Pageable pageable = PageRequest.of(PAGE, SIZE);

        Page<Award> awardPage = AwardPageFactory.createAwardPage();

        when(awardRepository.findByTopicDifficultyAndIsDeletedFalse(activatedDifficulty, pageable))
                .thenReturn(awardPage);

        AwardPageResponse awardPageResponse =
                awardService.getAllAwardsByTopicDifficulty(activatedDifficulty.getSlug(), PAGE, SIZE);

        assertNotNull(awardPageResponse);
        assertNotNull(awardPageResponse.awards());

        verify(topicDifficultyService).findValidTopicDifficultyOrThrow(activatedDifficulty.getSlug());
        verify(awardRepository).findByTopicDifficultyAndIsDeletedFalse(activatedDifficulty, pageable);
    }

    @Test
    void getAllAwardsByTopicDifficulty_notFoundDifficulty_shouldThrowTopicDifficultyNotFoundException() {
        when(topicDifficultyService.findValidTopicDifficultyOrThrow(TOPIC_DIFFICULTY_SLUG))
                .thenThrow(new TopicDifficultyNotFoundException(TOPIC_DIFFICULTY_SLUG));

        assertThrows(
                TopicDifficultyNotFoundException.class,
                () -> awardService.getAllAwardsByTopicDifficulty(TOPIC_DIFFICULTY_SLUG, PAGE, SIZE)
        );

        verify(topicDifficultyService).findValidTopicDifficultyOrThrow(TOPIC_DIFFICULTY_SLUG);
    }

    @Test
    void updateAwardActivation_deactivated_shouldReturnAwardUpdateResponse() {
        Award activatedAward = AwardFactory.createActivatedAward(AWARD_ID);

        when(awardRepository.findById(activatedAward.getId())).thenReturn(Optional.of(activatedAward));

        when(awardRepository.save(any(Award.class))).thenReturn(activatedAward);

        boolean activated = false;

        AwardUpdateResponse awardUpdateResponse =
                awardService.updateAwardActivation(activatedAward.getId(), activated);

        assertNotNull(awardUpdateResponse);
        assertNotNull(awardUpdateResponse.message());

        assertFalse(activatedAward.isActivated());

        verify(awardRepository).findById(activatedAward.getId());
        verify(awardRepository).save(any(Award.class));
    }

    @Test
    void updateAwardActivation_activated_shouldReturnAwardUpdateResponse() {
        Award deactivatedAward = AwardFactory.createDeactivatedAward(AWARD_ID);

        when(awardRepository.findById(deactivatedAward.getId())).thenReturn(Optional.of(deactivatedAward));

        when(awardRepository.save(any(Award.class))).thenReturn(deactivatedAward);

        boolean activated = true;

        AwardUpdateResponse awardUpdateResponse =
                awardService.updateAwardActivation(deactivatedAward.getId(), activated);

        assertNotNull(awardUpdateResponse);
        assertNotNull(awardUpdateResponse.message());

        assertTrue(deactivatedAward.isActivated());

        verify(awardRepository).findById(deactivatedAward.getId());
        verify(awardRepository).save(any(Award.class));
    }

    @Test
    void updateAwardActivation_invalidStatus_shouldThrowInvalidAwardStatusException() {
        Award deactivatedAward = AwardFactory.createDeactivatedAward(AWARD_ID);

        when(awardRepository.findById(deactivatedAward.getId())).thenReturn(Optional.of(deactivatedAward));

        boolean activated = false;

        assertThrows(
                InvalidAwardStatusException.class,
                () -> awardService.updateAwardActivation(deactivatedAward.getId(), activated)
        );

        verify(awardRepository).findById(deactivatedAward.getId());
    }

    @Test
    void updateAwardActivation_notFoundAward_shouldThrowAwardNotFoundException() {
        when(awardRepository.findById(AWARD_ID)).thenReturn(Optional.empty());

        boolean activated = true;

        assertThrows(
                AwardNotFoundException.class,
                () -> awardService.updateAwardActivation(AWARD_ID, activated)
        );

        verify(awardRepository).findById(AWARD_ID);
    }

    @Test
    void deleteAward_validAwardId_shouldReturnAwardUpdateResponse() {
        Award deactivatedAward = AwardFactory.createDeactivatedAward(AWARD_ID);

        when(awardRepository.findById(deactivatedAward.getId()))
                .thenReturn(Optional.of(deactivatedAward));

        when(awardRepository.save(any(Award.class))).thenReturn(deactivatedAward);

        AwardUpdateResponse awardUpdateResponse = awardService.deleteAward(deactivatedAward.getId());

        assertNotNull(awardUpdateResponse);
        assertNotNull(awardUpdateResponse.message());

        assertTrue(deactivatedAward.isDeleted());

        verify(awardRepository).findById(deactivatedAward.getId());
        verify(awardRepository).save(any(Award.class));
    }

    @Test
    void deleteAward_notFoundAward_shouldThrowAwardNotFoundException() {
        when(awardRepository.findById(AWARD_ID)).thenReturn(Optional.empty());

        assertThrows(AwardNotFoundException.class, () -> awardService.deleteAward(AWARD_ID));

        verify(awardRepository).findById(AWARD_ID);
    }

    @Test
    void deleteAward_deletedAward_shouldThrowDeletedAwardException() {
        Award deletedAward = AwardFactory.createDeletedAward(AWARD_ID);

        when(awardRepository.findById(deletedAward.getId())).thenReturn(Optional.of(deletedAward));

        assertThrows(DeletedAwardException.class, () -> awardService.deleteAward(deletedAward.getId()));

        verify(awardRepository).findById(deletedAward.getId());
    }
}
