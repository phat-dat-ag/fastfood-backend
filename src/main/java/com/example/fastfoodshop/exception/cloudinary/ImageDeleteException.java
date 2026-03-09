package com.example.fastfoodshop.exception.cloudinary;

import com.example.fastfoodshop.exception.base.BusinessException;

public class ImageDeleteException extends BusinessException {
    public ImageDeleteException(String cloudinaryMessage) {
        super("IMAGE_DELETE_FAILED", "Lỗi xóa ảnh: " + cloudinaryMessage);
    }
}
