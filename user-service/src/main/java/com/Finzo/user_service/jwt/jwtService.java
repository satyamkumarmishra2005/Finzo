package com.Finzo.user_service.jwt;

import com.Finzo.user_service.constants.ApplicationConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class jwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;


    public String generateToken(Authentication authentication){
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .issuer("Finzo")
                .subject("JWT Token")
                .claim("email", authentication.getName())
                .claim("authorities", authorities)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ApplicationConstants.JWT_EXPIRATION_MS)) // Token valid for 1 hour
                .signWith(secretKey)
                .compact();
    }

    /**
     * Generate JWT token with Bearer prefix.
     */
    public String generateBearerToken(Authentication authentication) {
        return ApplicationConstants.JWT_PREFIX + generateToken(authentication);
    }
}
