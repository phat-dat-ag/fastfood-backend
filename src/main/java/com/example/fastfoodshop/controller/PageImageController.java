package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.response.image.ImageAboutUsResponse;
import com.example.fastfoodshop.response.image.ImageChallengeIntroductionResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.PageImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/pages")
@RequiredArgsConstructor
public class PageImageController extends BaseController {
    private final PageImageService pageImageService;

    @GetMapping("/about-us/images")
    public ResponseEntity<ResponseWrapper<ImageAboutUsResponse>> getAboutUsPageImage() {
        return okResponse(pageImageService.getAboutUsPageImages());
    }

    @GetMapping("/challenge-introduction/images")
    public ResponseEntity<ResponseWrapper<ImageChallengeIntroductionResponse>> getChallengeIntroductionImage() {
        return okResponse(pageImageService.getChallengeIntroductionImages());
    }
}
