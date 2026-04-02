package com.optshop.security;

import org.springframework.stereotype.Component;


import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtil {
   
      @Value("${jwt.secret}")
    private String SECRET; 

    public String generateToken(String email) 
    {
        return Jwts.builder().setSubject(email)
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();
    }

    public String extractEmail(String token) 
    {
        return Jwts.parser().setSigningKey(SECRET)
                .parseClaimsJws(token).getBody().getSubject();
    }
}
