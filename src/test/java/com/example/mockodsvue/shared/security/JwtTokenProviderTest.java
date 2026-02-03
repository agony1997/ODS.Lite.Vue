package com.example.mockodsvue.shared.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtTokenProvider 測試")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        // 使用測試用的密鑰和過期時間 (1小時)
        String secret = "ThisIsA32ByteSecretKeyForJWT1234";
        long expiration = 3600000L;
        jwtTokenProvider = new JwtTokenProvider(secret, expiration);
    }

    @Test
    @DisplayName("產生 Token 成功")
    void generateToken_Success() {
        // given
        String empNo = "E001";
        List<String> roles = List.of("ADMIN", "SALES");

        // when
        String token = jwtTokenProvider.generateToken(empNo, roles);

        // then
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);  // JWT 格式: header.payload.signature
    }

    @Test
    @DisplayName("從 Token 取得員工編號")
    void getEmpNoFromToken_Success() {
        // given
        String empNo = "E001";
        String token = jwtTokenProvider.generateToken(empNo, List.of("ADMIN"));

        // when
        String result = jwtTokenProvider.getEmpNoFromToken(token);

        // then
        assertEquals(empNo, result);
    }

    @Test
    @DisplayName("從 Token 取得角色清單")
    void getRolesFromToken_Success() {
        // given
        String empNo = "E001";
        List<String> roles = List.of("ADMIN", "SALES");
        String token = jwtTokenProvider.generateToken(empNo, roles);

        // when
        List<String> result = jwtTokenProvider.getRolesFromToken(token);

        // then
        assertEquals(2, result.size());
        assertTrue(result.contains("ADMIN"));
        assertTrue(result.contains("SALES"));
    }

    @Test
    @DisplayName("驗證有效 Token")
    void validateToken_ValidToken_ReturnsTrue() {
        // given
        String token = jwtTokenProvider.generateToken("E001", List.of("ADMIN"));

        // when
        boolean result = jwtTokenProvider.validateToken(token);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("驗證無效 Token")
    void validateToken_InvalidToken_ReturnsFalse() {
        // given
        String invalidToken = "invalid.token.here";

        // when
        boolean result = jwtTokenProvider.validateToken(invalidToken);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("驗證過期 Token")
    void validateToken_ExpiredToken_ReturnsFalse() {
        // given - 建立一個過期時間為 0 的 provider
        JwtTokenProvider expiredProvider = new JwtTokenProvider(
                "ThisIsA32ByteSecretKeyForJWT1234", 0L);
        String token = expiredProvider.generateToken("E001", List.of("ADMIN"));

        // when
        boolean result = expiredProvider.validateToken(token);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("驗證空 Token")
    void validateToken_NullToken_ReturnsFalse() {
        // when
        boolean result = jwtTokenProvider.validateToken(null);

        // then
        assertFalse(result);
    }
}
