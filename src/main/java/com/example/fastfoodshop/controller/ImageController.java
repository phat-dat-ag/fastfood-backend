package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.ImageCreateRequest;
import com.example.fastfoodshop.response.AboutUsImageResponse;
import com.example.fastfoodshop.response.ChallengeIntroductionImageResponse;
import com.example.fastfoodshop.response.ItemPromotionResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PostMapping()
    public ResponseEntity<ResponseWrapper<String>> uploadImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute ImageCreateRequest imageCreateRequest
    ) {
        return imageService.uploadImage(userDetails.getUsername(), imageCreateRequest);
    }

    @GetMapping("/about-us")
    public ResponseEntity<ResponseWrapper<AboutUsImageResponse>> getAboutUsPageImage() {
        return imageService.getAboutUsPageImages();
    }

    @GetMapping("/challenge-introduction")
    public ResponseEntity<ResponseWrapper<ChallengeIntroductionImageResponse>> getChallengeIntroductionImage() {
        return imageService.getChallengeIntroductionImages();
    }

    @GetMapping("/promotion")
    public ResponseEntity<ResponseWrapper<ItemPromotionResponse>> getItemPromotionImage() {
        return imageService.getItemPromotionImages();
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<String>> deleteImage(@RequestParam("imageId") Long imageId) {
        return imageService.deleteImage(imageId);
    }
}
