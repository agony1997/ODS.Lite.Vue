package com.example.mockodsvue.branch.controller;

import com.example.mockodsvue.branch.model.dto.BranchProductListDTO;
import com.example.mockodsvue.auth.model.entity.AuthUser;
import com.example.mockodsvue.branch.model.entity.Branch;
import com.example.mockodsvue.branch.model.entity.BranchProductList;
import com.example.mockodsvue.auth.repository.AuthUserRepository;
import com.example.mockodsvue.branch.repository.BranchProductListRepository;
import com.example.mockodsvue.branch.repository.BranchRepository;
import com.example.mockodsvue.shared.security.JwtTokenProvider;
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
@DisplayName("BranchProductListController 整合測試")
class BranchProductListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private BranchProductListRepository branchProductListRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;
    private String leaderToken;
    private String salesToken;

    @BeforeEach
    void setUp() {
        // 建立使用者
        AuthUser admin = new AuthUser();
        admin.setUserCode("BPLADMIN");
        admin.setEmail("bpladmin@example.com");
        admin.setUserName("管理員");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setStatus("ACTIVE");
        authUserRepository.save(admin);

        AuthUser leader = new AuthUser();
        leader.setUserCode("BPLLEADER");
        leader.setEmail("bplleader@example.com");
        leader.setUserName("組長");
        leader.setPassword(passwordEncoder.encode("password"));
        leader.setStatus("ACTIVE");
        authUserRepository.save(leader);

        AuthUser sales = new AuthUser();
        sales.setUserCode("BPLSALES");
        sales.setEmail("bplsales@example.com");
        sales.setUserName("業務員");
        sales.setPassword(passwordEncoder.encode("password"));
        sales.setStatus("ACTIVE");
        authUserRepository.save(sales);

        adminToken = jwtTokenProvider.generateToken("BPLADMIN", List.of("ADMIN"));
        leaderToken = jwtTokenProvider.generateToken("BPLLEADER", List.of("LEADER"));
        salesToken = jwtTokenProvider.generateToken("BPLSALES", List.of("SALES"));

        // 建立測試營業所
        Branch branch = new Branch();
        branch.setBranchCode("BPLBR01");
        branch.setBranchName("測試營業所");
        branch.setStatus("ACTIVE");
        branchRepository.save(branch);

        Branch branch2 = new Branch();
        branch2.setBranchCode("BPLBR02");
        branch2.setBranchName("目標營業所");
        branch2.setStatus("ACTIVE");
        branchRepository.save(branch2);

        // 建立測試產品清單
        BranchProductList product = new BranchProductList();
        product.setBranchCode("BPLBR01");
        product.setProductCode("P001");
        product.setProductName("測試商品");
        product.setUnit("箱");
        product.setSortOrder(1);
        branchProductListRepository.save(product);
    }

    @Test
    @DisplayName("GET /api/branch-product-list/{branchCode} - 已認證查詢成功")
    void getByBranchCode_AsAuthenticated_Success() throws Exception {
        mockMvc.perform(get("/api/branch-product-list/BPLBR01")
                        .header("Authorization", "Bearer " + salesToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].productCode", is("P001")));
    }

    @Test
    @DisplayName("PUT /api/branch-product-list/{branchCode} - ADMIN 儲存成功")
    void save_AsAdmin_Success() throws Exception {
        BranchProductListDTO dto = new BranchProductListDTO();
        dto.setProductCode("P002");
        dto.setProductName("新商品");
        dto.setUnit("瓶");

        mockMvc.perform(put("/api/branch-product-list/BPLBR01")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(dto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].productCode", is("P002")));
    }

    @Test
    @DisplayName("PUT /api/branch-product-list/{branchCode} - SALES 權限不足返回 403")
    void save_AsSales_Returns403() throws Exception {
        BranchProductListDTO dto = new BranchProductListDTO();
        dto.setProductCode("P002");
        dto.setProductName("新商品");
        dto.setUnit("瓶");

        mockMvc.perform(put("/api/branch-product-list/BPLBR01")
                        .header("Authorization", "Bearer " + salesToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(dto))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/branch-product-list/copy - LEADER 複製成功")
    void copy_AsLeader_Success() throws Exception {
        mockMvc.perform(post("/api/branch-product-list/copy")
                        .header("Authorization", "Bearer " + leaderToken)
                        .param("from", "BPLBR01")
                        .param("to", "BPLBR02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].branchCode", is("BPLBR02")));
    }

    @Test
    @DisplayName("POST /api/branch-product-list/copy - 未認證返回 403")
    void copy_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(post("/api/branch-product-list/copy")
                        .param("from", "BPLBR01")
                        .param("to", "BPLBR02"))
                .andExpect(status().isForbidden());
    }
}
