package com.example.fastfoodshop.factory.file;

import com.example.fastfoodshop.constant.CloudinaryResult;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryUpdateResultFactory {
    public static Map<String, Object> createValidResult() {
        Map<String, Object> result = new HashMap<>();

        result.put(CloudinaryResult.SECURE_URL, "http://image-url");
        result.put(CloudinaryResult.PUBLIC_ID, "public-id");

        return result;
    }
}