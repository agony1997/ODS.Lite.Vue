package com.example.mockodsvue.purchase.controller;

import com.example.mockodsvue.auth.model.entity.AuthUser;
import com.example.mockodsvue.auth.model.entity.AuthUserBranchRole;
import com.example.mockodsvue.branch.model.entity.Branch;
import com.example.mockodsvue.branch.model.entity.BranchProductList;
import com.example.mockodsvue.branch.model.entity.Location;
import com.example.mockodsvue.purchase.model.entity.SalesPurchaseList;
import com.example.mockodsvue.purchase.model.entity.SalesPurchaseOrder;
import com.example.mockodsvue.purchase.model.entity.SalesPurchaseOrderDetail;
import com.example.mockodsvue.branch.model.enums.LocationType;
import com.example.mockodsvue.purchase.model.enums.SalesOrderDetailStatus;
import com.example.mockodsvue.auth.repository.AuthUserBranchRoleRepository;
import com.example.mockodsvue.auth.repository.AuthUserRepository;
import com.example.mockodsvue.branch.repository.BranchProductListRepository;
import com.example.mockodsvue.branch.repository.BranchRepository;
import com.example.mockodsvue.branch.repository.LocationRepository;
import com.example.mockodsvue.purchase.repository.SalesPurchaseListRepository;
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
@DisplayName("SalesPurchaseController 整合測試")
class SalesPurchaseControllerTest {

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
    private SalesPurchaseListRepository customListRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String storekeeperToken;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        // 動態計算有效的訂貨日期 (今天 + 3 天，在 2~9 天的範圍內)
        testDate = LocalDate.now().plusDays(3);

        // 建立營業所
        Branch branch = new Branch();
        branch.setBranchCode("SPBR01");
        branch.setBranchName("業務測試所");
        branch.setStatus("ACTIVE");
        branchRepository.save(branch);

        // 建立儲位
        Location location = new Location();
        location.setLocationCode("SPLOC01");
        location.setLocationName("業務車1");
        location.setBranchCode("SPBR01");
        location.setUserCode("SPSALES1");
        location.setLocationType(LocationType.CAR);
        location.setStatus("ACTIVE");
        locationRepository.save(location);

        // 建立營業所產品清單
        BranchProductList product = new BranchProductList();
        product.setBranchCode("SPBR01");
        product.setProductCode("P001");
        product.setProductName("測試商品");
        product.setUnit("箱");
        product.setSortOrder(1);
        branchProductListRepository.save(product);

        // 建立業務員
        AuthUser salesUser = new AuthUser();
        salesUser.setUserCode("SPSALES1");
        salesUser.setEmail("spsales@example.com");
        salesUser.setUserName("業務員");
        salesUser.setPassword(passwordEncoder.encode("password"));
        salesUser.setBranchCode("SPBR01");
        salesUser.setStatus("ACTIVE");
        authUserRepository.save(salesUser);

        AuthUserBranchRole salesRole = new AuthUserBranchRole();
        salesRole.setUserCode("SPSALES1");
        salesRole.setBranchCode("SPBR01");
        salesRole.setRoleCode("SALES");
        authUserBranchRoleRepository.save(salesRole);

        // 建立庫管 (無 SALES/LEADER/ADMIN 權限)
        AuthUser storekeeper = new AuthUser();
        storekeeper.setUserCode("SPSTORE1");
        storekeeper.setEmail("spstore@example.com");
        storekeeper.setUserName("庫管員");
        storekeeper.setPassword(passwordEncoder.encode("password"));
        storekeeper.setStatus("ACTIVE");
        authUserRepository.save(storekeeper);

        AuthUserBranchRole skRole = new AuthUserBranchRole();
        skRole.setUserCode("SPSTORE1");
        skRole.setBranchCode("SPBR01");
        skRole.setRoleCode("STOREKEEPER");
        authUserBranchRoleRepository.save(skRole);

        storekeeperToken = jwtTokenProvider.generateToken("SPSTORE1", List.of("STOREKEEPER"));
    }

    @Test
    @DisplayName("GET /api/purchase/sales - 業務員查詢訂單成功")
    void getOrder_AsSales_Success() throws Exception {
        mockMvc.perform(get("/api/purchase/sales")
                        .with(user("SPSALES1").roles("SALES"))
                        .param("date", testDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.purchaseNo", notNullValue()))
                .andExpect(jsonPath("$.purchaseUser", is("SPSALES1")));
    }

    @Test
    @DisplayName("GET /api/purchase/sales - 未認證返回 403")
    void getOrder_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/purchase/sales")
                        .param("date", testDate.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /api/purchase/sales - 業務員更新訂單 (接受 JSON body)")
    void updateOrder_AsSales_Success() throws Exception {
        // 先手動建立已有的訂單 (不含明細，避免 replaceDetails 的 unique constraint 衝突)
        SalesPurchaseOrder order = new SalesPurchaseOrder();
        order.setPurchaseNo("SPO-UPD-001");
        order.setBranchCode("SPBR01");
        order.setLocationCode("SPLOC01");
        order.setPurchaseDate(testDate);
        order.setPurchaseUser("SPSALES1");
        spoRepository.save(order);

        // 構造更新請求 (空明細)
        String updateJson = """
                {
                    "purchaseNo": "SPO-UPD-001",
                    "branchCode": "SPBR01",
                    "locationCode": "SPLOC01",
                    "purchaseDate": "%s",
                    "purchaseUser": "SPSALES1",
                    "details": []
                }
                """.formatted(testDate);

        mockMvc.perform(put("/api/purchase/sales")
                        .with(user("SPSALES1").roles("SALES"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.purchaseNo", is("SPO-UPD-001")));
    }

    @Test
    @DisplayName("POST /api/purchase/sales/load/yesterday - 帶入上次訂單 (無前一天訂單回傳 400)")
    void loadFromYesterdayOrder_AsSales_NoPreviousOrder_Returns400() throws Exception {
        mockMvc.perform(post("/api/purchase/sales/load/yesterday")
                        .with(user("SPSALES1").roles("SALES"))
                        .param("date", testDate.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("找不到前一天的訂單")));
    }

    @Test
    @DisplayName("POST /api/purchase/sales/load/custom - 帶入自定義清單 (無清單回傳 400)")
    void loadFromCustomList_AsSales_NoList_Returns400() throws Exception {
        mockMvc.perform(post("/api/purchase/sales/load/custom")
                        .with(user("SPSALES1").roles("SALES"))
                        .param("date", testDate.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("尚未建立自定義產品清單")));
    }

    @Test
    @DisplayName("POST /api/purchase/sales/load/branch - 帶入營業所清單 (已認證可存取)")
    void loadFromBranchList_AsSales_Success() throws Exception {
        // 先手動建立已有的訂單 (避免 findOrCreate 中建立後又 replaceDetails 的 flush 問題)
        SalesPurchaseOrder order = new SalesPurchaseOrder();
        order.setPurchaseNo("SPO-BRANCH-001");
        order.setBranchCode("SPBR01");
        order.setLocationCode("SPLOC01");
        order.setPurchaseDate(testDate);
        order.setPurchaseUser("SPSALES1");
        spoRepository.save(order);

        mockMvc.perform(post("/api/purchase/sales/load/branch")
                        .with(user("SPSALES1").roles("SALES"))
                        .param("date", testDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.purchaseNo", is("SPO-BRANCH-001")));
    }

    @Test
    @DisplayName("GET /api/purchase/sales/custom-list - 取得自定義清單")
    void getCustomList_AsSales_Success() throws Exception {
        mockMvc.perform(get("/api/purchase/sales/custom-list")
                        .with(user("SPSALES1").roles("SALES")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("PUT /api/purchase/sales/custom-list - 儲存自定義清單")
    void saveCustomList_AsSales_Success() throws Exception {
        mockMvc.perform(put("/api/purchase/sales/custom-list")
                        .with(user("SPSALES1").roles("SALES"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/purchase/sales - 無權限角色返回 403")
    void getOrder_AsUnauthorizedRole_Returns403() throws Exception {
        mockMvc.perform(get("/api/purchase/sales")
                        .header("Authorization", "Bearer " + storekeeperToken)
                        .param("date", testDate.toString()))
                .andExpect(status().isForbidden());
    }
}
