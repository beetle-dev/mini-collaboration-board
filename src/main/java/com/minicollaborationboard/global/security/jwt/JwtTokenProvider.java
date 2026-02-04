package com.minicollaborationboard.global.security.jwt;

import com.minicollaborationboard.global.security.dto.JwtClaims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final Long accessExpiration;

    public JwtTokenProvider(@Value("${spring.jwt.secret}")String secret,
                            @Value("${spring.jwt.access-expiration}")Long accessExpiration) {

        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.accessExpiration = accessExpiration;
    }

    public JwtClaims validateToken(String token) {

        var payload = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

        return JwtClaims.builder()
                .uuid(payload.get("uuid", String.class))
                .username(payload.get("username", String.class))
                .role(payload.get("role", String.class))
                .build();
    }

    public String createJwt(String uuid, String username, String role) {

        return Jwts.builder()
                .claim("uuid", uuid)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(secretKey)
                .compact();
    }
}
