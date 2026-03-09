package com.example.fastfoodshop.exception.image;

import com.example.fastfoodshop.exception.base.BusinessException;

public class ImageNotFoundException extends BusinessException {
    public ImageNotFoundException(Long imageId) {
        super("IMAGE_NOT_FOUND", "Ảnh không tồn tại: " + imageId);
    }
}
