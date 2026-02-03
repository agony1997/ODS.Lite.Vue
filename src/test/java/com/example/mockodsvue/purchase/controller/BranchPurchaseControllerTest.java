package com.example.mockodsvue.purchase.controller;

import com.example.mockodsvue.purchase.model.dto.AggregateRequestDTO;
import com.example.mockodsvue.purchase.model.dto.BranchPurchaseFreezeDTO;
import com.example.mockodsvue.purchase.model.dto.BranchPurchaseSummaryUpdateDTO;
import com.example.mockodsvue.auth.model.entity.AuthUser;
import com.example.mockodsvue.auth.model.entity.AuthUserBranchRole;
import com.example.mockodsvue.branch.model.entity.Branch;
import com.example.mockodsvue.branch.model.entity.BranchProductList;
import com.example.mockodsvue.branch.model.entity.Location;
import com.example.mockodsvue.purchase.model.entity.SalesPurchaseOrder;
import com.example.mockodsvue.purchase.model.entity.SalesPurchaseOrderDetail;
import com.example.mockodsvue.branch.model.enums.LocationType;
import com.example.mockodsvue.purchase.model.enums.SalesOrderDetailStatus;
import com.example.mockodsvue.auth.repository.AuthUserBranchRoleRepository;
import com.example.mockodsvue.auth.repository.AuthUserRepository;
import com.example.mockodsvue.branch.repository.BranchProductListRepository;
import com.example.mockodsvue.branch.repository.BranchRepository;
import com.example.mockodsvue.branch.repository.LocationRepository;
import com.example.mockodsvue.purchase.repository.SalesPurchaseOrderDetailRepository;
import com.example.mockodsvue.purchase.repository.SalesPurchaseOrderRepository;
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

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("BranchPurchaseController 整合測試")
class BranchPurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private AuthUserBranchRoleRepository authUserBranchRoleRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private BranchProductListRepository branchProductListRepository;

    @Autowired
    private SalesPurchaseOrderRepository spoRepository;

    @Autowired
    private SalesPurchaseOrderDetailRepository spodRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String salesToken;
    private final LocalDate testDate = LocalDate.of(2025, 7, 1);

    @BeforeEach
    void setUp() {
        // 建立營業所
        Branch branch = new Branch();
        branch.setBranchCode("BPBR01");
        branch.setBranchName("彙總測試所");
        branch.setStatus("ACTIVE");
        branchRepository.save(branch);

        // 建立儲位
        Location location = new Location();
        location.setLocationCode("BPLOC01");
        location.setLocationName("車1");
        location.setBranchCode("BPBR01");
        location.setUserCode("BPSALES1");
        location.setLocationType(LocationType.CAR);
        location.setStatus("ACTIVE");
        locationRepository.save(location);

        // 建立營業所產品清單
        BranchProductList product = new BranchProductList();
        product.setBranchCode("BPBR01");
        product.setProductCode("P001");
        product.setProductName("測試商品");
        product.setUnit("箱");
        product.setSortOrder(1);
        branchProductListRepository.save(product);

        // 建立 SPO + SPOD 測試資料
        SalesPurchaseOrder spo = new SalesPurchaseOrder();
        spo.setPurchaseNo("SPO-BP-001");
        spo.setBranchCode("BPBR01");
        spo.setLocationCode("BPLOC01");
        spo.setPurchaseDate(testDate);
        spo.setPurchaseUser("BPSALES1");
        spoRepository.save(spo);

        SalesPurchaseOrderDetail spod = new SalesPurchaseOrderDetail();
        spod.setPurchaseNo("SPO-BP-001");
        spod.setItemNo(1);
        spod.setProductCode("P001");
        spod.setProductName("測試商品");
        spod.setUnit("箱");
        spod.setQty(10);
        spod.setConfirmedQty(10);
        spod.setStatus(SalesOrderDetailStatus.PENDING);
        spodRepository.save(spod);

        // 建立使用者
        AuthUser admin = new AuthUser();
        admin.setUserCode("BPADMIN1");
        admin.setEmail("bpadmin@example.com");
        admin.setUserName("管理員");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setStatus("ACTIVE");
        authUserRepository.save(admin);

        AuthUser leader = new AuthUser();
        leader.setUserCode("BPLEADER1");
        leader.setEmail("bpleader@example.com");
        leader.setUserName("組長");
        leader.setPassword(passwordEncoder.encode("password"));
        leader.setStatus("ACTIVE");
        authUserRepository.save(leader);

        AuthUser sales = new AuthUser();
        sales.setUserCode("BPSALES1");
        sales.setEmail("bpsales@example.com");
        sales.setUserName("業務員");
        sales.setPassword(passwordEncoder.encode("password"));
        sales.setBranchCode("BPBR01");
        sales.setStatus("ACTIVE");
        authUserRepository.save(sales);

        AuthUserBranchRole salesRole = new AuthUserBranchRole();
        salesRole.setUserCode("BPSALES1");
        salesRole.setBranchCode("BPBR01");
        salesRole.setRoleCode("SALES");
        authUserBranchRoleRepository.save(salesRole);

        salesToken = jwtTokenProvider.generateToken("BPSALES1", List.of("SALES"));
    }

    @Test
    @DisplayName("GET /api/purchase/branch/summary - LEADER 查詢彙總成功")
    void getSummary_AsLeader_Success() throws Exception {
        mockMvc.perform(get("/api/purchase/branch/summary")
                        .with(user("BPLEADER1").roles("LEADER"))
                        .param("branchCode", "BPBR01")
                        .param("date", testDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.branchCode", is("BPBR01")));
    }

    @Test
    @DisplayName("GET /api/purchase/branch/summary - SALES 權限不足返回 403")
    void getSummary_AsSales_Returns403() throws Exception {
        mockMvc.perform(get("/api/purchase/branch/summary")
                        .with(user("BPSALES1").roles("SALES"))
                        .param("branchCode", "BPBR01")
                        .param("date", testDate.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/purchase/branch/freeze - LEADER 凍結成功")
    void freeze_AsLeader_Success() throws Exception {
        BranchPurchaseFreezeDTO dto = BranchPurchaseFreezeDTO.builder()
                .branchCode("BPBR01")
                .purchaseDate(testDate)
                .build();

        mockMvc.perform(post("/api/purchase/branch/freeze")
                        .with(user("BPLEADER1").roles("LEADER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.frozenStatus", is("FROZEN")));
    }

    @Test
    @DisplayName("POST /api/purchase/branch/unfreeze - ADMIN 解除凍結成功")
    void unfreeze_AsAdmin_Success() throws Exception {
        BranchPurchaseFreezeDTO dto = BranchPurchaseFreezeDTO.builder()
                .branchCode("BPBR01")
                .purchaseDate(testDate)
                .build();

        // 先凍結
        mockMvc.perform(post("/api/purchase/branch/freeze")
                .with(user("BPADMIN1").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // 解除凍結
        mockMvc.perform(post("/api/purchase/branch/unfreeze")
                        .with(user("BPADMIN1").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.frozenStatus").doesNotExist());
    }

    @Test
    @DisplayName("PUT /api/purchase/branch/summary - ADMIN 更新確認數量成功")
    void updateConfirmedQty_AsAdmin_Success() throws Exception {
        BranchPurchaseFreezeDTO freezeDto = BranchPurchaseFreezeDTO.builder()
                .branchCode("BPBR01")
                .purchaseDate(testDate)
                .build();

        // 先凍結
        mockMvc.perform(post("/api/purchase/branch/freeze")
                .with(user("BPADMIN1").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(freezeDto)));

        // 更新確認數量
        BranchPurchaseSummaryUpdateDTO updateDto = BranchPurchaseSummaryUpdateDTO.builder()
                .branchCode("BPBR01")
                .purchaseDate(testDate)
                .details(List.of(
                        BranchPurchaseSummaryUpdateDTO.UpdateDetailDTO.builder()
                                .productCode("P001")
                                .unit("箱")
                                .confirmedQty(8)
                                .build()
                ))
                .build();

        mockMvc.perform(put("/api/purchase/branch/summary")
                        .with(user("BPADMIN1").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.branchCode", is("BPBR01")));
    }

    @Test
    @DisplayName("POST /api/purchase/branch/confirm - LEADER 確認成功")
    void confirm_AsLeader_Success() throws Exception {
        BranchPurchaseFreezeDTO dto = BranchPurchaseFreezeDTO.builder()
                .branchCode("BPBR01")
                .purchaseDate(testDate)
                .build();

        // 先凍結
        mockMvc.perform(post("/api/purchase/branch/freeze")
                .with(user("BPLEADER1").roles("LEADER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // 確認
        mockMvc.perform(post("/api/purchase/branch/confirm")
                        .with(user("BPLEADER1").roles("LEADER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.frozenStatus", is("CONFIRMED")));
    }

    @Test
    @DisplayName("POST /api/purchase/branch/aggregate - ADMIN 彙總成功")
    void aggregate_AsAdmin_Success() throws Exception {
        BranchPurchaseFreezeDTO freezeDto = BranchPurchaseFreezeDTO.builder()
                .branchCode("BPBR01")
                .purchaseDate(testDate)
                .build();

        // 凍結 -> 確認
        mockMvc.perform(post("/api/purchase/branch/freeze")
                .with(user("BPADMIN1").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(freezeDto)));

        mockMvc.perform(post("/api/purchase/branch/confirm")
                .with(user("BPADMIN1").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(freezeDto)));

        // 彙總
        AggregateRequestDTO aggDto = AggregateRequestDTO.builder()
                .branchCode("BPBR01")
                .purchaseDate(testDate)
                .build();

        mockMvc.perform(post("/api/purchase/branch/aggregate")
                        .with(user("BPADMIN1").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aggDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /api/purchase/branch/bpo - LEADER 查詢 BPO 清單成功")
    void getBpoList_AsLeader_Success() throws Exception {
        mockMvc.perform(get("/api/purchase/branch/bpo")
                        .with(user("BPLEADER1").roles("LEADER"))
                        .param("branchCode", "BPBR01")
                        .param("date", testDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/purchase/branch/summary - 未認證返回 403")
    void getSummary_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/purchase/branch/summary")
                        .param("branchCode", "BPBR01")
                        .param("date", testDate.toString()))
                .andExpect(status().isForbidden());
    }
}
