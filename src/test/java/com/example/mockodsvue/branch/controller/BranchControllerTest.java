package com.example.mockodsvue.branch.controller;

import com.example.mockodsvue.auth.model.entity.AuthUser;
import com.example.mockodsvue.branch.model.entity.Branch;
import com.example.mockodsvue.auth.repository.AuthUserRepository;
import com.example.mockodsvue.branch.repository.BranchRepository;
import com.example.mockodsvue.shared.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("BranchController 整合測試")
class BranchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String userToken;

    @BeforeEach
    void setUp() {
        // 建立測試使用者
        AuthUser user = new AuthUser();
        user.setUserCode("TEST001");
        user.setEmail("brtest@example.com");
        user.setUserName("測試使用者");
        user.setPassword(passwordEncoder.encode("password"));
        user.setStatus("ACTIVE");
        authUserRepository.save(user);

        userToken = jwtTokenProvider.generateToken("TEST001", List.of("SALES"));

        // 建立測試營業所
        Branch branch = new Branch();
        branch.setBranchCode("BRTEST");
        branch.setBranchName("測試營業所");
        branch.setStatus("ACTIVE");
        branchRepository.save(branch);
    }

    @Test
    @DisplayName("GET /api/branches - 已認證使用者取得啟用營業所列表")
    void getAllEnabled_Authenticated_Success() throws Exception {
        mockMvc.perform(get("/api/branches")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /api/branches - 未認證返回 401")
    void getAllEnabled_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/branches"))
                .andExpect(status().isForbidden());
    }
}
