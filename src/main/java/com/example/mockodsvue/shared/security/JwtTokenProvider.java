package com.example.mockodsvue.shared.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {
        // 使用 HMAC-SHA256 演算法，密鑰至少需要 32 bytes
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    /**
     * 產生 JWT Token
     * @param empNo 員工編號
     * @param roles 角色清單
     * @return JWT Token 字串
     */
    public String generateToken(String empNo, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(empNo)                          // 主體：員工編號
                .claim("roles", roles)                   // 自訂欄位：角色
                .issuedAt(now)                           // 簽發時間
                .expiration(expiryDate)                  // 過期時間
                .signWith(secretKey)                     // 簽章
                .compact();
    }

    /**
     * 從 Token 取得員工編號
     */
    public String getEmpNoFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 從 Token 取得角色清單
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("roles", List.class);
    }

    /**
     * 驗證 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Token 無效或過期
            return false;
        }
    }

    /**
     * 解析 Token 取得 Claims
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
