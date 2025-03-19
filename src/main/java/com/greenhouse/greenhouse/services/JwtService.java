package com.greenhouse.greenhouse.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.expiration.time}")
    private long expirationTime;
    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    public String generateToken (String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, new SecretKeySpec(jwtSecretKey.getBytes(), "HmacSHA256"))
                .compact();
    }
}
