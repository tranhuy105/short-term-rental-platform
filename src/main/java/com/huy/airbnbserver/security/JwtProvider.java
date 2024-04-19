package com.huy.airbnbserver.security;

import com.huy.airbnbserver.user.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class JwtProvider {
    private final JwtEncoder jwtEncoder;
    public String createToken(Authentication authentication) {
        var now = Instant.now();
        long expiredIn = 1; // 1 hour
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claim = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(expiredIn, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("authorities", authorities)
                .claim("userId", ((UserPrincipal) authentication.getPrincipal()).getUser().getId())
                .build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claim)).getTokenValue();
    }
}
