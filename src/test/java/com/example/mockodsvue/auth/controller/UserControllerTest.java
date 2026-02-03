package com.example.mockodsvue.controller;

import com.example.mockodsvue.model.dto.auth.CreateUserRequest;
import com.example.mockodsvue.model.entity.auth.AuthUser;
import com.example.mockodsvue.repository.AuthUserRepository;
import com.example.mockodsvue.security.JwtTokenProvider;
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

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("UserController 整合測試")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        // 建立 ADMIN 使用者
        AuthUser admin = new AuthUser();
        admin.setUserId("ADMIN001");
        admin.setEmail("admin@example.com");
        admin.setUserName("管理員");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setStatus("ACTIVE");
        authUserRepository.save(admin);

        // 建立一般使用者
        AuthUser user = new AuthUser();
        user.setUserId("USER001");
        user.setEmail("user@example.com");
        user.setUserName("一般使用者");
        user.setPassword(passwordEncoder.encode("password"));
        user.setStatus("ACTIVE");
        authUserRepository.save(user);

        // 產生 Token
        adminToken = jwtTokenProvider.generateToken("ADMIN001", List.of("ADMIN"));
        userToken = jwtTokenProvider.generateToken("USER001", List.of("SALES"));
    }

    @Test
    @DisplayName("POST /api/users - ADMIN 新增使用者成功")
    void createUser_AsAdmin_Success() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUserId("E001");
        request.setEmail("e001@example.com");
        request.setUserName("新使用者");
        request.setPassword("password123");

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", is("E001")))
                .andExpect(jsonPath("$.userName", is("新使用者")));
    }

    @Test
    @DisplayName("POST /api/users - 非 ADMIN 無權限")
    void createUser_AsUser_Returns403() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUserId("E001");
        request.setEmail("e001@example.com");
        request.setUserName("新使用者");
        request.setPassword("password123");

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/users - 未登入返回 401")
    void createUser_Unauthenticated_Returns401() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUserId("E001");
        request.setEmail("e001@example.com");
        request.setUserName("新使用者");
        request.setPassword("password123");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/users - ADMIN 查詢所有使用者")
    void getAllUsers_AsAdmin_Success() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @DisplayName("GET /api/users/{userId} - ADMIN 查詢單一使用者")
    void getUser_AsAdmin_Success() throws Exception {
        mockMvc.perform(get("/api/users/USER001")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is("USER001")))
                .andExpect(jsonPath("$.userName", is("一般使用者")));
    }

    @Test
    @DisplayName("GET /api/users/{userId} - 使用者查詢自己")
    void getUser_Self_Success() throws Exception {
        mockMvc.perform(get("/api/users/USER001")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is("USER001")));
    }

    @Test
    @DisplayName("GET /api/users/{userId} - 使用者查詢他人無權限")
    void getUser_OtherUser_Returns403() throws Exception {
        mockMvc.perform(get("/api/users/ADMIN001")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/users/{userId} - ADMIN 刪除使用者")
    void deleteUser_AsAdmin_Success() throws Exception {
        mockMvc.perform(delete("/api/users/USER001")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/users/{userId} - 非 ADMIN 無權限")
    void deleteUser_AsUser_Returns403() throws Exception {
        mockMvc.perform(delete("/api/users/ADMIN001")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}
