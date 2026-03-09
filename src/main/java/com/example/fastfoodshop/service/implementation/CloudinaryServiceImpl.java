package com.example.fastfoodshop.service.implementation;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.fastfoodshop.exception.cloudinary.AudioDeleteException;
import com.example.fastfoodshop.exception.cloudinary.AudioUploadException;
import com.example.fastfoodshop.exception.cloudinary.ImageDeleteException;
import com.example.fastfoodshop.exception.cloudinary.ImageUploadException;
import com.example.fastfoodshop.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;

    public Map<?, ?> uploadImage(MultipartFile file, String folderName) {
        try {
            return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                            "folder", folderName,
                            "resource_type", "auto"
                    )
            );
        } catch (IOException e) {
            throw new ImageUploadException(e.getMessage());
        }
    }

    public Map<?, ?> uploadAudio(MultipartFile file, String folder) {
        try {
            return cloudinary.uploader().upload(file.getBytes(),
                    Map.of("folder", folder, "resource_type", "video"));
        } catch (IOException e) {
            throw new AudioUploadException(e.getMessage());
        }
    }

    public boolean deleteImage(String publicId) {
        try {
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return ("ok").equals(result.get("result"));
        } catch (IOException e) {
            throw new ImageDeleteException(e.getMessage());
        }
    }

    public boolean deleteAudio(String publicId) {
        try {
            Map<?, ?> result = cloudinary.uploader().destroy(publicId,
                    Map.of("resource_type", "video"));
            return "ok".equals(result.get("result"));
        } catch (IOException e) {
            throw new AudioDeleteException(e.getMessage());
        }
    }
}
