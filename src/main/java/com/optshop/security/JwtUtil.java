package com.optshop.security;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import java.security.Key;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(String email) 
    {
        return Jwts.builder().setSubject(email)
                .signWith(getSigningKey()).compact();
    }

    public String extractEmail(String token) 
    {
        return Jwts.parserBuilder().setSigningKey(getSigningKey())
                .build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
