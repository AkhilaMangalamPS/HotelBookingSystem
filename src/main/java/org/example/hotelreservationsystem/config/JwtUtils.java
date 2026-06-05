package org.example.hotelreservationsystem.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utility class for JWT
 *
 * - Generate JWT tokens
 * - Extract information from tokens
 * - Validate token integrity and expiration
 */
@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;

    //Generate a cryptographic signing key from the cofigured secret
    private Key getSignKey(){
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Generate a JWT token for the authenticated user
     *
     * @param userDetails authenticated user details
     * @return signed JWT token as string
     */
    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(getSignKey() , SignatureAlgorithm.HS256)
                .compact();

    }

    /**
     * Extract username from JWT token
     *
     * @param token JWT token
     * @return username stored in token
     */
    public String getUsernameFromToken(String token){
        return Jwts.parserBuilder()
                //Set signing key to validate token integrity
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Validate JWT token
     * Checks:
     * - Signature validity
     * Token structure
     * Expiration
     *
     * @param token JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
            return true;
        }
        catch (JwtException | IllegalArgumentException e){

        }
        return false;
    }
}
