
package com.hirehub.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hirehub.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey signingKey;

    public JwtService(
            @Value("${HIREHUB_JWT_SECRET:${hirehub.jwt.secret:}}")
            String secret) {

        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "JWT signing secret must not be null or blank");
        }

        this.signingKey =
                Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(User user) {

        long now = System.currentTimeMillis();

        Date issuedAt = new Date(now);
        Date expiration = new Date(now + 30 * 60 * 1000L);

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(signingKey)
                .compact();
    }

    public Long extractUserId(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.valueOf(claims.getSubject());
    }
}
