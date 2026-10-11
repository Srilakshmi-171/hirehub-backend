package com.hirehub.security;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hirehub.entity.User;

import java.util.Date;

import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;

@Service
public class JwtService {
	
	private SecretKey signingKey;
	
	public JwtService(
		    @Value("${HIREHUB_JWT_SECRET:${hirehub.jwt.secret:}}") String secret
		) {
		    // 1. Check whether secret is null or blank
		    if (secret == null || secret.isBlank()) {
		        throw new IllegalStateException("JWT signing secret must not be null or blank");
		    }

		    // 2. Initialize signingKey using Keys.hmacShaKeyFor
		    this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
		}
	
	
	public String generateToken(User user) {
	    long now = System.currentTimeMillis();
	    Date issuedAt = new Date(now);
	    Date expiration = new Date(now + 30 * 60 * 1000L); // 30 minutes in milliseconds

	    return Jwts.builder()
	        .subject(String.valueOf(user.getId())) 
	        .issuedAt(issuedAt)                  
	        .expiration(expiration)
	        .signWith(signingKey) // 1. Secure the token with your initialized cryptographic key
	        .compact();           // 2. Build the token and serialize it to a compact, URL-safe string
	}
	
	public Long extractUserId(String token) {
	    Claims claims = Jwts.parser()
	        .verifyWith(signingKey)
	        .build()
	        .parseSignedClaims(token)
	        .getPayload();

	    String subject = claims.getSubject();
	    return Long.valueOf(subject);
	}




}
