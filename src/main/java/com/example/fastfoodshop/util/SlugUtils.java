package com.example.fastfoodshop.util;

import java.text.Normalizer;
import java.util.Locale;

public class SlugUtils {
    public static String toSlug(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutAccents = normalized.replace("\\p{InCombiningDiacriticalMarks}+", "");

        String slug = withoutAccents.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
        return slug;
    }
}
