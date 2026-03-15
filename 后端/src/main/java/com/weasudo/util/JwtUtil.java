package com.weasudo.util;

import com.weasudo.exception.BusinessException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.weasudo.common.ResponseCodeEnum.*;

@Slf4j
public class JwtUtil {

    private static final String SECRET = System.getProperty(
            "jwt.secret",
            System.getenv().getOrDefault("JWT_SECRET", "weasudo-secret-key-weasudo-secret-key")
    );

    private static final long EXPIRE_TIME = Long.parseLong(System.getProperty(
            "jwt.expire.ms",
            System.getenv().getOrDefault("JWT_EXPIRE_MS", String.valueOf(1000L *60 *15))
    ));

    private static final SecretKey SECRET_KEY =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    private JwtUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String generateToken(String userId, String role) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId不能为空");
        }

        Date now = new Date();
        Date expireAt = new Date(now.getTime() + EXPIRE_TIME);

        return Jwts.builder()
                .setSubject("user")
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expireAt)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateToken(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId不能为空");
        }
        String role = "USER"; // 默认角色为"user"
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + EXPIRE_TIME);

        return Jwts.builder()
                .setSubject("user")
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expireAt)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String getUserId(String token) {
        Claims claims = getClaims(token);

        String userId = claims == null ? null : claims.get("userId", String.class);
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(TOKEN_INVALID);
        }
        return userId;
    }

    public static String getRole(String token) {
        Claims claims = getClaims(token);
        String role = claims == null ? null : claims.get("role", String.class);
        if(role == null || role.isBlank()) {
            throw new BusinessException(TOKEN_INVALID);
        }
        return role;
    }


    private static Claims getClaims(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // 明确抛出业务异常，表示 token 过期
            throw new BusinessException(TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            // 其他解析错误，视为 token 无效
            throw new BusinessException(TOKEN_INVALID);
        }
    }
}
