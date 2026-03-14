package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.ImageCreateRequest;
import com.example.fastfoodshop.response.image.ImageAboutUsResponse;
import com.example.fastfoodshop.response.image.ImageChallengeIntroductionResponse;
import com.example.fastfoodshop.response.image.ItemPromotionResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.image.ImageUpdateResponse;
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
public class ImageController extends BaseController {
    private final ImageService imageService;

    @PostMapping()
    public ResponseEntity<ResponseWrapper<ImageUpdateResponse>> uploadImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute ImageCreateRequest imageCreateRequest
    ) {
        return okResponse(imageService.uploadImage(userDetails.getUsername(), imageCreateRequest));
    }

    @GetMapping("/about-us")
    public ResponseEntity<ResponseWrapper<ImageAboutUsResponse>> getAboutUsPageImage() {
        return okResponse(imageService.getAboutUsPageImages());
    }

    @GetMapping("/challenge-introduction")
    public ResponseEntity<ResponseWrapper<ImageChallengeIntroductionResponse>> getChallengeIntroductionImage() {
        return okResponse(imageService.getChallengeIntroductionImages());
    }

    @GetMapping("/promotion")
    public ResponseEntity<ResponseWrapper<ItemPromotionResponse>> getItemPromotionImage() {
        return okResponse(imageService.getItemPromotionImages());
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<ImageUpdateResponse>> deleteImage(@RequestParam("imageId") Long imageId) {
        return okResponse(imageService.deleteImage(imageId));
    }
}
