package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.ImageCreateRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.image.ImageUpdateResponse;
import com.example.fastfoodshop.service.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/images")
@RequiredArgsConstructor
public class AdminImageController extends BaseController {
    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<ImageUpdateResponse>> uploadImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute ImageCreateRequest imageCreateRequest
    ) {
        return okResponse(imageService.uploadImage(userDetails.getUsername(), imageCreateRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ImageUpdateResponse>> deleteImage(
            @PathVariable("id") Long imageId
    ) {
        return okResponse(imageService.deleteImage(imageId));
    }
}
