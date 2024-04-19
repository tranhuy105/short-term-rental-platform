package com.huy.airbnbserver.system;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

public class AuthorizeToken {
    public static boolean isUserIdMatch(Jwt jwt, Integer userIdFromRequestParam) {
            String userIdFromToken = jwt.getClaimAsString("userId");
            return userIdFromToken != null && userIdFromToken.equals(String.valueOf(userIdFromRequestParam));
    }
}
