package com.example.fastfoodshop.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudinaryService {
    Map<?, ?> uploadImage(MultipartFile file, String folderName);

    Map<?, ?> uploadAudio(MultipartFile file, String folder);

    boolean deleteImage(String publicId);

    boolean deleteAudio(String publicId);
}
