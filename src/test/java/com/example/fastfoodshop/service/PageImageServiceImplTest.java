package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Image;
import com.example.fastfoodshop.enums.PageType;
import com.example.fastfoodshop.factory.page_image.PageImageFactory;
import com.example.fastfoodshop.repository.ImageRepository;
import com.example.fastfoodshop.response.image.ImageAboutUsResponse;
import com.example.fastfoodshop.response.image.ImageChallengeIntroductionResponse;
import com.example.fastfoodshop.service.implementation.PageImageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PageImageServiceImplTest {
    @Mock
    ImageRepository imageRepository;

    @InjectMocks
    PageImageServiceImpl pageImageService;

    @Test
    void getAboutUsPageImages_shouldReturnImageAboutUsResponse() {
        List<Image> images = PageImageFactory.createAboutUsPageImages();

        when(imageRepository.findByPageType(PageType.ABOUT_US)).thenReturn(images);

        ImageAboutUsResponse imageAboutUsResponse = pageImageService.getAboutUsPageImages();

        assertNotNull(imageAboutUsResponse);

        assertEquals(1, imageAboutUsResponse.carouselImages().size());
        assertEquals(1, imageAboutUsResponse.showcaseImages().size());
        assertEquals(1, imageAboutUsResponse.missionImages().size());

        verify(imageRepository).findByPageType(PageType.ABOUT_US);
    }

    @Test
    void getAboutUsPageImages_emptyList_shouldReturnImageAboutUsResponse() {
        when(imageRepository.findByPageType(PageType.ABOUT_US)).thenReturn(List.of());

        ImageAboutUsResponse imageAboutUsResponse = pageImageService.getAboutUsPageImages();

        assertNotNull(imageAboutUsResponse);

        assertEquals(0, imageAboutUsResponse.carouselImages().size());
        assertEquals(0, imageAboutUsResponse.showcaseImages().size());
        assertEquals(0, imageAboutUsResponse.missionImages().size());

        verify(imageRepository).findByPageType(PageType.ABOUT_US);
    }

    @Test
    void getChallengeIntroductionImages_shouldReturnImageAboutUsResponse() {
        List<Image> images = PageImageFactory.createChallengePageImages();

        when(imageRepository.findByPageType(PageType.CHALLENGE)).thenReturn(images);

        ImageChallengeIntroductionResponse imageAboutUsResponse
                = pageImageService.getChallengeIntroductionImages();

        assertNotNull(imageAboutUsResponse);

        assertEquals(1, imageAboutUsResponse.carouselImages().size());

        verify(imageRepository).findByPageType(PageType.CHALLENGE);
    }

    @Test
    void getChallengeIntroductionImages_emptyList_shouldReturnImageAboutUsResponse() {
        when(imageRepository.findByPageType(PageType.CHALLENGE)).thenReturn(List.of());

        ImageChallengeIntroductionResponse imageAboutUsResponse
                = pageImageService.getChallengeIntroductionImages();

        assertNotNull(imageAboutUsResponse);

        assertEquals(0, imageAboutUsResponse.carouselImages().size());

        verify(imageRepository).findByPageType(PageType.CHALLENGE);
    }
}