package com.example.fastfoodshop.exception.cloudinary;

import com.example.fastfoodshop.exception.base.BusinessException;

public class AudioDeleteException extends BusinessException {
    public AudioDeleteException(String cloudinaryMessage) {
        super("AUDIO_DELETE_FAILED", "Lỗi xóa video: " + cloudinaryMessage);
    }
}
