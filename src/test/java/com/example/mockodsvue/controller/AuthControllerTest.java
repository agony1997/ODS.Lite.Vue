package com.example.mockodsvue.controller;

import com.example.mockodsvue.model.dto.auth.LoginRequest;
import com.example.mockodsvue.model.entity.auth.AuthUser;
import com.example.mockodsvue.model.entity.auth.AuthUserRole;
import com.example.mockodsvue.repository.AuthUserRepository;
import com.example.mockodsvue.repository.AuthUserRoleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("AuthController 整合測試")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private AuthUserRoleRepository authUserRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // 建立測試使用者
        AuthUser user = new AuthUser();
        user.setEmpNo("E001");
        user.setEmail("test@example.com");
        user.setEmpName("測試使用者");
        user.setPassword(passwordEncoder.encode("password123"));
        authUserRepository.save(user);

        // 建立角色關聯
        AuthUserRole userRole = new AuthUserRole();
        userRole.setEmpNo("E001");
        userRole.setRoleCode("ADMIN");
        authUserRoleRepository.save(userRole);
    }

    @Test
    @DisplayName("POST /api/auth/login - 登入成功")
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmpNo("E001");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.empNo", is("E001")))
                .andExpect(jsonPath("$.empName", is("測試使用者")))
                .andExpect(jsonPath("$.roles", hasItem("ADMIN")));
    }

    @Test
    @DisplayName("POST /api/auth/login - 密碼錯誤")
    void login_WrongPassword_Returns401() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmpNo("E001");
        request.setPassword("wrongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login - 使用者不存在")
    void login_UserNotFound_Returns401() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmpNo("E999");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login - 缺少必填欄位")
    void login_MissingFields_Returns400() throws Exception {
        LoginRequest request = new LoginRequest();
        // 不設定 empNo 和 password

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
