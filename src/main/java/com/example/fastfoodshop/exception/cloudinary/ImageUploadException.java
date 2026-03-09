package com.example.fastfoodshop.exception.cloudinary;

import com.example.fastfoodshop.exception.base.BusinessException;

public class ImageUploadException extends BusinessException {
    public ImageUploadException(String cloudinaryMessage) {
        super("IMAGE_UPLOAD_FAILED", "Lỗi tải ảnh" + cloudinaryMessage);
    }
}
