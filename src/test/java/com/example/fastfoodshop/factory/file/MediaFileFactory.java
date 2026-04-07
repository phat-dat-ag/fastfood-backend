package com.example.fastfoodshop.factory.file;

import org.springframework.mock.web.MockMultipartFile;

public class MediaFileFactory {
    public static MockMultipartFile createEmptyFile() {
        return new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                new byte[]{}
        );
    }

    public static MockMultipartFile createValidFile() {
        return new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "data".getBytes()
        );
    }
}
