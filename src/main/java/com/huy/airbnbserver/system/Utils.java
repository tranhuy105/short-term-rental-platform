package com.huy.airbnbserver.system;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class Utils {
    public static boolean userIdNotMatch(Jwt jwt, Integer userIdFromRequestParam) {
            String userIdFromToken = jwt.getClaimAsString("userId");
            return userIdFromToken == null || !userIdFromToken.equals(String.valueOf(userIdFromRequestParam));
    }

    public static boolean imageValidationFailed(List<MultipartFile> images) {
        if (images.isEmpty()) {
            return true;
        }

        return !images.stream()
                .allMatch(file -> {
                    String originalName = file.getOriginalFilename();
                    String contentType = file.getContentType();
                    boolean emptyPostmanRequest = originalName != null && originalName.isEmpty();
                    boolean wrongContentType = contentType == null || !contentType.startsWith("image/");
                    return !emptyPostmanRequest && !wrongContentType;
                });
    }
}
