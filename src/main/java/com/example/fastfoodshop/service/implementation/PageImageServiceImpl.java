package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.ImageDTO;
import com.example.fastfoodshop.enums.PageType;
import com.example.fastfoodshop.enums.SectionType;
import com.example.fastfoodshop.repository.ImageRepository;
import com.example.fastfoodshop.response.image.ImageAboutUsResponse;
import com.example.fastfoodshop.response.image.ImageChallengeIntroductionResponse;
import com.example.fastfoodshop.service.PageImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PageImageServiceImpl implements PageImageService {
    private final ImageRepository imageRepository;

    private List<ImageDTO> getImagesByPageType(PageType pageType) {
        return imageRepository
                .findByPageType(pageType)
                .stream()
                .map(ImageDTO::from)
                .toList();
    }

    private Map<SectionType, List<ImageDTO>> groupImagesBySectionType(List<ImageDTO> imageDTOs) {
        return imageDTOs
                .stream()
                .collect(Collectors.groupingBy(ImageDTO::sectionType));
    }

    public ImageAboutUsResponse getAboutUsPageImages() {
        List<ImageDTO> aboutUsImages = getImagesByPageType(PageType.ABOUT_US);

        Map<SectionType, List<ImageDTO>> grouped = groupImagesBySectionType(aboutUsImages);

        log.info("[PageImageService] Retrieved images for about us page");

        return new ImageAboutUsResponse(
                grouped.getOrDefault(SectionType.CAROUSEL, List.of()),
                grouped.getOrDefault(SectionType.SHOWCASE, List.of()),
                grouped.getOrDefault(SectionType.MISSION, List.of())
        );
    }

    public ImageChallengeIntroductionResponse getChallengeIntroductionImages() {
        List<ImageDTO> challengeImages = getImagesByPageType(PageType.CHALLENGE);

        Map<SectionType, List<ImageDTO>> grouped = groupImagesBySectionType(challengeImages);

        log.info("[PageImageService] Retrieved images for challenge page");

        return new ImageChallengeIntroductionResponse(
                grouped.getOrDefault(SectionType.CAROUSEL, List.of())
        );
    }
}
