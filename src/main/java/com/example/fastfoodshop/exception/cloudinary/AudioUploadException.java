package com.example.fastfoodshop.exception.cloudinary;

import com.example.fastfoodshop.exception.base.BusinessException;

public class AudioUploadException extends BusinessException {
    public AudioUploadException(String audioMessage) {
        super("AUDIO_UPLOAD_FAILED", "Lỗi tải video" + audioMessage);
    }
}
