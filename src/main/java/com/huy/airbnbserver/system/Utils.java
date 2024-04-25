package com.huy.airbnbserver.system;


import com.huy.airbnbserver.user.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class Utils {

    public static Integer extractAuthenticationId(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ((UserPrincipal)authentication.getPrincipal()).getUser().getId();
        } else {
            throw new AccessDeniedException("Unauthenticated User");
        }
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
