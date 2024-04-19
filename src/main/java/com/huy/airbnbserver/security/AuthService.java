package com.huy.airbnbserver.security;

import com.huy.airbnbserver.user.UserPrincipal;
import com.huy.airbnbserver.user.converter.UserToUserDtoConverter;
import com.huy.airbnbserver.user.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    
    public Map<String, Object> createLoginInfo(Authentication authentication) {
        // create user info
        var userPrincipal = (UserPrincipal) authentication.getPrincipal();
        var user = userPrincipal.getUser();
        Map<String, Object> userDto = new HashMap<>();
        userDto.put("id", user.getId());
        userDto.put("username", user.getUsername());
        userDto.put("email", user.getEmail());
        userDto.put("role", user.getRoles());
        var imageUrl = user.getAvatar() != null ? "/api/v1/images/"+user.getAvatar().getName() : null;
        userDto.put("image_url", imageUrl);

        // create jwt
        String token = jwtProvider.createToken(authentication);

        Map<String, Object> loginResolvedMap = new HashMap<>();
        loginResolvedMap.put("userInfo", userDto);
        loginResolvedMap.put("token", token);

        return loginResolvedMap;
    }
}
