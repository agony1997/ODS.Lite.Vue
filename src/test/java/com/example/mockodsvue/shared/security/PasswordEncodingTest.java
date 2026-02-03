package com.example.mockodsvue.shared.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("密碼編碼驗證測試")
class PasswordEncodingTest {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * data.sql 中所有使用者使用的 hash 值
     */
    private static final String DATA_SQL_HASH =
            "$2a$10$aRi0uOtcUAdiokog5Fpq3OT0VmN.eNMhqFmeIMb.buX7uZMA7Whxa";

    private static final String EXPECTED_PASSWORD = "password123";

    @Test
    @DisplayName("驗證 data.sql 中的 BCrypt hash 是否匹配 password123")
    void dataSqlHash_ShouldMatch_Password123() {
        boolean matches = encoder.matches(EXPECTED_PASSWORD, DATA_SQL_HASH);

        if (!matches) {
            // 產生正確的 hash 供修復使用
            String correctHash = encoder.encode(EXPECTED_PASSWORD);
            System.out.println("=== 密碼編碼問題 ===");
            System.out.println("data.sql 中的 hash 不匹配 'password123'");
            System.out.println("正確的 BCrypt hash: " + correctHash);
            System.out.println("請更新 data.sql 中的密碼欄位");
        }

        assertTrue(matches,
                "data.sql 中的 BCrypt hash 無法匹配密碼 'password123'，請更新 data.sql");
    }

    @Test
    @DisplayName("BCryptPasswordEncoder 可正確編碼與驗證 password123")
    void encoder_ShouldEncodeAndVerify_Password123() {
        String encoded = encoder.encode(EXPECTED_PASSWORD);

        assertNotNull(encoded);
        assertTrue(encoded.startsWith("$2a$"));
        assertTrue(encoder.matches(EXPECTED_PASSWORD, encoded));
        assertFalse(encoder.matches("wrongPassword", encoded));
    }

    @Test
    @DisplayName("產生 password123 的正確 BCrypt hash")
    void generateCorrectHash() {
        String hash = encoder.encode(EXPECTED_PASSWORD);
        System.out.println("password123 的 BCrypt hash: " + hash);
        assertTrue(encoder.matches(EXPECTED_PASSWORD, hash));
    }
}
