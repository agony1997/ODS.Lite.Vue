package com.example.mockodsvue.controller;

import com.example.mockodsvue.model.dto.auth.AssignRoleRequest;
import com.example.mockodsvue.model.dto.auth.CreateRoleRequest;
import com.example.mockodsvue.model.entity.auth.AuthRole;
import com.example.mockodsvue.model.entity.auth.AuthUser;
import com.example.mockodsvue.repository.AuthRoleRepository;
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
@DisplayName("RoleController 整合測試")
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private AuthRoleRepository authRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        // 建立使用者
        AuthUser admin = new AuthUser();
        admin.setEmpNo("ADMIN001");
        admin.setEmail("admin@example.com");
        admin.setEmpName("管理員");
        admin.setPassword(passwordEncoder.encode("password"));
        authUserRepository.save(admin);

        AuthUser user = new AuthUser();
        user.setEmpNo("USER001");
        user.setEmail("user@example.com");
        user.setEmpName("一般使用者");
        user.setPassword(passwordEncoder.encode("password"));
        authUserRepository.save(user);

        // 建立角色
        AuthRole salesRole = new AuthRole();
        salesRole.setRoleCode("SALES");
        salesRole.setRoleName("業務員");
        authRoleRepository.save(salesRole);

        // 產生 Token
        adminToken = jwtTokenProvider.generateToken("ADMIN001", List.of("ADMIN"));
        userToken = jwtTokenProvider.generateToken("USER001", List.of("SALES"));
    }

    @Test
    @DisplayName("POST /api/roles - ADMIN 新增角色成功")
    void createRole_AsAdmin_Success() throws Exception {
        CreateRoleRequest request = new CreateRoleRequest();
        request.setRoleCode("MANAGER");
        request.setRoleName("主管");

        mockMvc.perform(post("/api/roles")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roleCode", is("MANAGER")))
                .andExpect(jsonPath("$.roleName", is("主管")));
    }

    @Test
    @DisplayName("POST /api/roles - 非 ADMIN 無權限")
    void createRole_AsUser_Returns403() throws Exception {
        CreateRoleRequest request = new CreateRoleRequest();
        request.setRoleCode("MANAGER");
        request.setRoleName("主管");

        mockMvc.perform(post("/api/roles")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/roles - ADMIN 查詢所有角色")
    void getAllRoles_AsAdmin_Success() throws Exception {
        mockMvc.perform(get("/api/roles")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("DELETE /api/roles/{roleCode} - ADMIN 刪除角色")
    void deleteRole_AsAdmin_Success() throws Exception {
        mockMvc.perform(delete("/api/roles/SALES")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/roles/assign - ADMIN 指派角色給使用者")
    void assignRole_AsAdmin_Success() throws Exception {
        AssignRoleRequest request = new AssignRoleRequest();
        request.setEmpNo("USER001");
        request.setRoleCode("SALES");

        mockMvc.perform(post("/api/roles/assign")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/roles/assign - 指派不存在的角色")
    void assignRole_RoleNotFound_Returns400() throws Exception {
        AssignRoleRequest request = new AssignRoleRequest();
        request.setEmpNo("USER001");
        request.setRoleCode("UNKNOWN");

        mockMvc.perform(post("/api/roles/assign")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/roles/assign/{empNo}/{roleCode} - 非 ADMIN 無權限")
    void removeRole_AsUser_Returns403() throws Exception {
        mockMvc.perform(delete("/api/roles/assign/USER001/SALES")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}
