package com.TaiNguyen.ProjectManagementSystems.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Collection;
import java.util.Date;

public class JwtProvider {

//    static SecretKey Key = Keys.hmacShaKeyFor(JwtConstant.SECRETE_KEY.getBytes());
//
//
//    public static String generateToken(Authentication auth){
//
//        return Jwts.builder().setIssuedAt(new Date())
//                .setExpiration(new Date(new Date().getTime()+86400000))
//                .claim("email", auth.getName())
//                .signWith(Key)
//                .compact();
//
//
//    }
//
//    public static String getEmailFromToken(String jwt){
//            Claims claims = Jwts.parserBuilder().setSigningKey(Key).build().parseClaimsJws(jwt).getBody();
//
//        return String.valueOf(claims.get("email"));
//
//    }


    // Generate a secure key for HMAC-SHA256
    private static final SecretKey KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Generate JWT token
    public static String generateToken(Authentication auth) {
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 86400000)) // Token expires in 24 hours
                .claim("email", auth.getName()) // Set email claim from Authentication
                .signWith(KEY) // Sign the token with the secure key
                .compact();
    }

    // Extract email from JWT token
    public static String getEmailFromToken(String jwt) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY) // Set the signing key for parsing
                .build()
                .parseClaimsJws(jwt) // Parse the JWT string
                .getBody(); // Get the body of the JWT

        return String.valueOf(claims.get("email")); // Extract email claim from JWT
    }


}
