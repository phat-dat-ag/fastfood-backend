package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Review;
import com.example.fastfoodshop.entity.ReviewImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public interface ReviewImageService {
    ArrayList<ReviewImage> createReviewImages(List<MultipartFile> images, Review review);
}
