package com.example.fastfoodshop.exception.image;

import com.example.fastfoodshop.exception.base.BusinessException;

public class FileNotFoundException extends BusinessException {
    public FileNotFoundException() {
        super("FILE_NOT_FOUND", "Không tìm thấy tập tin hình ảnh");
    }
}