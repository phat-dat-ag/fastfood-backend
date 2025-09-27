package com.example.fastfoodshop.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public Map<?, ?> uploadImage(MultipartFile file) {
        try {
            return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                            "folder", "avatar",
                            "resource_type", "auto"
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException("Upload image failed", e);
        }
    }

    public boolean deleteImage(String publicId) {
        try {
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return ("ok").equals(result.get("result"));
        } catch (IOException e) {
            throw new RuntimeException("Delete image failed", e);
        }
    }
}
