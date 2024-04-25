package com.huy.airbnbserver.auth;


import com.huy.airbnbserver.system.Result;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Result register(
            @RequestBody @Valid RegistrationRequest request
    ) throws MessagingException {
        authenticationService.registerUser(request);
        return new Result(true, 202, "Register Success");
    }

    @PostMapping("/login")
    public Result login(
            @RequestBody @Valid LoginRequest request
    ) {
        return new Result(true, 200, "Login Success", authenticationService.authenticate(request));
    }


    @GetMapping("/activate")
    public Result activate(
            @RequestParam String token
    ) throws MessagingException {
        return new Result(true, 200, "Transaction done", authenticationService.activate(token));
    }
}
