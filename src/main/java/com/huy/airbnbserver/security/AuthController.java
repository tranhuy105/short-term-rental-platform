package com.huy.airbnbserver.security;

import com.huy.airbnbserver.system.Result;
import com.huy.airbnbserver.system.StatusCode;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class AuthController {
    private final AuthService authService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("login")
    public Result getLoginInfo(Authentication authentication) {
        LOGGER.debug("Authenticated user: '{}'", authentication.getName());
        return new Result(true, StatusCode.SUCCESS, "User Info & Jwt", authService.createLoginInfo(authentication));
    }
}
