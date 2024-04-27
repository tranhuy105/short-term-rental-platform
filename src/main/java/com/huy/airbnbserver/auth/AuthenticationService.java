package com.huy.airbnbserver.auth;

import com.huy.airbnbserver.email.EmailService;
import com.huy.airbnbserver.email.EmailTemplateName;
import com.huy.airbnbserver.security.JwtService;
import com.huy.airbnbserver.security.token.Token;
import com.huy.airbnbserver.security.token.TokenRepository;
import com.huy.airbnbserver.system.exception.EntityAlreadyExistException;
import com.huy.airbnbserver.system.exception.ObjectNotFoundException;
import com.huy.airbnbserver.user.User;
import com.huy.airbnbserver.user.UserPrincipal;
import com.huy.airbnbserver.user.UserRepository;
import com.huy.airbnbserver.user.converter.UserToUserDtoConverter;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserToUserDtoConverter userToUserDtoConverter;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Transactional
    public void registerUser(RegistrationRequest request) throws MessagingException {
        var userExist = userRepository.findByEmail(request.getEmail());
        if (userExist.isPresent()) {
            throw new EntityAlreadyExistException("user");
        }

        var user = new User();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(false);
        user.setRoles("user");

        userRepository.save(user);
        sendValidationEmail(user);
    }


    public HashMap<Object, Object> authenticate(LoginRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var claims = new HashMap<String, Object>();
        var userPrincipal = ((UserPrincipal) auth.getPrincipal());
        claims.put("fullName", userPrincipal.getUser().getFullname());
        claims.put("userId", userPrincipal.getUser().getId());

        var jwtToken = jwtService.generateToken(claims, (UserPrincipal) auth.getPrincipal());

        var response = new HashMap<>();
        response.put("access_token", jwtToken);
        response.put("user_info", userToUserDtoConverter
                .convert(userPrincipal.getUser()));
        return response;
    }


    @Transactional
    public String activate(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token).orElseThrow(
                () -> new ObjectNotFoundException("token", "token")
        );

        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            tokenRepository.delete(savedToken);
            return "Token has expired, a new token has been sent for this email!";
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(
                        () -> new ObjectNotFoundException("user", savedToken.getUser().getId())
                );
        user.setEnabled(true);
        userRepository.save(user);
        tokenRepository.delete(savedToken);
        return "active success";
    }


    // UTILS
    @Transactional
    public void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getFullname(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateAndSaveActivationToken(User user) {
        // generate a token
        String generatedToken = generateActivationToken(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationToken(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length()); // 0 -> 9
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }
}
