package com.team25.event.planner.security.jwt;

import com.team25.event.planner.security.properties.JwtConfigurationProperties;
import com.team25.event.planner.security.user.UserDetailsImpl;
import com.team25.event.planner.user.model.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    private static final String AUTH_HEADER = "Authorization";
    private static final String HEADER_PREFIX = "Bearer ";

    private final JwtConfigurationProperties jwtConfiguration;

    private Key getSigningKey() {
        byte[] keyBytes = jwtConfiguration.getSecretKey().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetailsImpl userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("user_id", userDetails.getUserId())
                .claim("role", userDetails.getUserRole().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfiguration.getExpirationTimeMs()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            getAllClaimsFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    public String getToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader != null && authHeader.startsWith(HEADER_PREFIX)) {
            return authHeader.substring(HEADER_PREFIX.length());
        }
        return null;
    }

    public UserDetails getUserDetailsFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        return new UserDetailsImpl(
                claims.get("user_id", Long.class),
                claims.getSubject(),
                UserRole.valueOf(claims.get("role", String.class))
        );
    }
}