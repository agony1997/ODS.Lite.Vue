package com.example.mockodsvue.service;

import com.example.mockodsvue.mapper.BranchPurchaseMapper;
import com.example.mockodsvue.model.dto.BranchPurchaseFreezeDTO;
import com.example.mockodsvue.model.dto.BranchPurchaseOrderDTO;
import com.example.mockodsvue.model.dto.BranchPurchaseSummaryDTO;
import com.example.mockodsvue.model.dto.BranchPurchaseSummaryDTO.LocationInfoDTO;
import com.example.mockodsvue.model.dto.BranchPurchaseSummaryUpdateDTO;
import com.example.mockodsvue.model.entity.branch.Location;
import com.example.mockodsvue.model.entity.master.ProductFactory;
import com.example.mockodsvue.model.entity.purchase.*;
import com.example.mockodsvue.model.enums.DeliveryStatus;
import com.example.mockodsvue.model.enums.FrozenStatus;
import com.example.mockodsvue.model.enums.SalesOrderDetailStatus;
import com.example.mockodsvue.model.enums.SequenceType;
import com.example.mockodsvue.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BranchPurchaseService 測試")
class BranchPurchaseServiceTest {

    @Mock
    private SalesPurchaseOrderRepository spoRepository;

    @Mock
    private SalesPurchaseOrderDetailRepository spodRepository;

    @Mock
    private BranchPurchaseFrozenRepository bpfRepository;

    @Mock
    private BranchPurchaseOrderRepository bpoRepository;

    @Mock
    private BranchPurchaseOrderDetailRepository bpodRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private ProductFactoryRepository productFactoryRepository;

    @Mock
    private SequenceGenerator sequenceGenerator;

    @Mock
    private BranchPurchaseMapper mapper;

    @InjectMocks
    private BranchPurchaseService service;

    private static final String BRANCH_CODE = "BR001";
    private static final LocalDate PURCHASE_DATE = LocalDate.of(2026, 1, 28);
    private static final String CURRENT_USER = "testUser";

    // ==================== Helper Methods ====================

    private SalesPurchaseOrder createSpo(String purchaseNo, String locationCode) {
        SalesPurchaseOrder spo = new SalesPurchaseOrder();
        spo.setPurchaseNo(purchaseNo);
        spo.setBranchCode(BRANCH_CODE);
        spo.setLocationCode(locationCode);
        spo.setPurchaseDate(PURCHASE_DATE);
        spo.setPurchaseUser(CURRENT_USER);
        return spo;
    }

    private SalesPurchaseOrderDetail createSpod(String purchaseNo, String productCode, String unit, int qty, int confirmedQty) {
        SalesPurchaseOrderDetail spod = new SalesPurchaseOrderDetail();
        spod.setPurchaseNo(purchaseNo);
        spod.setProductCode(productCode);
        spod.setProductName(productCode + "名稱");
        spod.setUnit(unit);
        spod.setQty(qty);
        spod.setConfirmedQty(confirmedQty);
        spod.setStatus(SalesOrderDetailStatus.PENDING);
        spod.setItemNo(1);
        return spod;
    }

    private BranchPurchaseFrozen createBpf(FrozenStatus status) {
        BranchPurchaseFrozen bpf = new BranchPurchaseFrozen();
        bpf.setId(1);
        bpf.setBranchCode(BRANCH_CODE);
        bpf.setPurchaseDate(PURCHASE_DATE);
        bpf.setStatus(status);
        return bpf;
    }

    private Location createLocation(String locationCode) {
        Location loc = new Location();
        loc.setLocationCode(locationCode);
        loc.setBranchCode(BRANCH_CODE);
        return loc;
    }

    private BranchPurchaseFreezeDTO createFreezeDTO() {
        return BranchPurchaseFreezeDTO.builder()
                .branchCode(BRANCH_CODE)
                .purchaseDate(PURCHASE_DATE)
                .build();
    }

    /**
     * 設定 getSummary 所需的基本 mock，使其能順利回傳
     * (因為許多方法最後都會呼叫 getSummary)
     */
    private void stubGetSummary() {
        when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                .thenReturn(Optional.empty());
        when(locationRepository.findByBranchCode(BRANCH_CODE))
                .thenReturn(Collections.emptyList());
        when(mapper.toLocationInfoDTOList(anyList()))
                .thenReturn(Collections.emptyList());
        when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                .thenReturn(Collections.emptyList());
    }

    // ==================== GetSummary 測試 ====================

    @Nested
    @DisplayName("getSummary 測試")
    class GetSummaryTest {

        @Test
        @DisplayName("TC-S01: 有2儲位3產品 → 正確彙總")
        void twoLocationsThreeProducts_correctSummary() {
            // given
            SalesPurchaseOrder spo1 = createSpo("SPO-001", "LOC-A");
            SalesPurchaseOrder spo2 = createSpo("SPO-002", "LOC-B");
            List<SalesPurchaseOrder> spoList = List.of(spo1, spo2);

            SalesPurchaseOrderDetail spod1 = createSpod("SPO-001", "P001", "箱", 10, 10);
            SalesPurchaseOrderDetail spod2 = createSpod("SPO-001", "P002", "箱", 5, 5);
            SalesPurchaseOrderDetail spod3 = createSpod("SPO-002", "P003", "箱", 8, 8);

            Location locA = createLocation("LOC-A");
            Location locB = createLocation("LOC-B");

            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.empty());
            when(locationRepository.findByBranchCode(BRANCH_CODE))
                    .thenReturn(List.of(locA, locB));
            when(mapper.toLocationInfoDTOList(anyList()))
                    .thenReturn(List.of(
                            LocationInfoDTO.builder().locationCode("LOC-A").locationName("儲位A").build(),
                            LocationInfoDTO.builder().locationCode("LOC-B").locationName("儲位B").build()
                    ));
            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(spoList);
            when(spodRepository.findByPurchaseNoIn(List.of("SPO-001", "SPO-002")))
                    .thenReturn(List.of(spod1, spod2, spod3));
            when(mapper.productKey(anyString(), anyString()))
                    .thenAnswer(inv -> inv.getArgument(0) + "-" + inv.getArgument(1));

            // when
            BranchPurchaseSummaryDTO result = service.getSummary(BRANCH_CODE, PURCHASE_DATE);

            // then
            assertNotNull(result);
            assertEquals(BRANCH_CODE, result.getBranchCode());
            assertEquals(PURCHASE_DATE, result.getPurchaseDate());
            assertNull(result.getFrozenStatus());
            assertEquals(2, result.getLocations().size());
            assertEquals(3, result.getDetails().size());
        }

        @Test
        @DisplayName("TC-S02: 無訂單 → 空 details")
        void noOrders_emptyDetails() {
            // given
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.empty());
            when(locationRepository.findByBranchCode(BRANCH_CODE))
                    .thenReturn(Collections.emptyList());
            when(mapper.toLocationInfoDTOList(anyList()))
                    .thenReturn(Collections.emptyList());
            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Collections.emptyList());

            // when
            BranchPurchaseSummaryDTO result = service.getSummary(BRANCH_CODE, PURCHASE_DATE);

            // then
            assertNotNull(result);
            assertTrue(result.getDetails().isEmpty());
        }

        @Test
        @DisplayName("TC-S03: 無凍結記錄 → frozenStatus=null")
        void noFrozenRecord_frozenStatusNull() {
            // given
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.empty());
            when(locationRepository.findByBranchCode(BRANCH_CODE))
                    .thenReturn(Collections.emptyList());
            when(mapper.toLocationInfoDTOList(anyList()))
                    .thenReturn(Collections.emptyList());
            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Collections.emptyList());

            // when
            BranchPurchaseSummaryDTO result = service.getSummary(BRANCH_CODE, PURCHASE_DATE);

            // then
            assertNull(result.getFrozenStatus());
        }

        @Test
        @DisplayName("TC-S04: 已凍結 → frozenStatus=FROZEN")
        void frozenRecord_frozenStatusFrozen() {
            // given
            BranchPurchaseFrozen bpf = createBpf(FrozenStatus.FROZEN);
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.of(bpf));
            when(locationRepository.findByBranchCode(BRANCH_CODE))
                    .thenReturn(Collections.emptyList());
            when(mapper.toLocationInfoDTOList(anyList()))
                    .thenReturn(Collections.emptyList());
            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Collections.emptyList());

            // when
            BranchPurchaseSummaryDTO result = service.getSummary(BRANCH_CODE, PURCHASE_DATE);

            // then
            assertEquals(FrozenStatus.FROZEN, result.getFrozenStatus());
        }

        @Test
        @DisplayName("TC-S05: 同產品跨儲位 → totalQty 加總正確")
        void sameProductAcrossLocations_totalQtySummed() {
            // given
            SalesPurchaseOrder spo1 = createSpo("SPO-001", "LOC-A");
            SalesPurchaseOrder spo2 = createSpo("SPO-002", "LOC-B");

            SalesPurchaseOrderDetail spod1 = createSpod("SPO-001", "P001", "箱", 6, 6);
            SalesPurchaseOrderDetail spod2 = createSpod("SPO-002", "P001", "箱", 4, 4);

            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.empty());
            when(locationRepository.findByBranchCode(BRANCH_CODE))
                    .thenReturn(List.of(createLocation("LOC-A"), createLocation("LOC-B")));
            when(mapper.toLocationInfoDTOList(anyList()))
                    .thenReturn(Collections.emptyList());
            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(List.of(spo1, spo2));
            when(spodRepository.findByPurchaseNoIn(List.of("SPO-001", "SPO-002")))
                    .thenReturn(List.of(spod1, spod2));
            when(mapper.productKey(anyString(), anyString()))
                    .thenAnswer(inv -> inv.getArgument(0) + "-" + inv.getArgument(1));

            // when
            BranchPurchaseSummaryDTO result = service.getSummary(BRANCH_CODE, PURCHASE_DATE);

            // then
            assertEquals(1, result.getDetails().size());
            assertEquals(10, result.getDetails().get(0).getTotalQty());
        }

        @Test
        @DisplayName("TC-S06: diffQty = confirmedQty - totalQty")
        void diffQtyCalculation() {
            // given
            SalesPurchaseOrder spo1 = createSpo("SPO-001", "LOC-A");

            SalesPurchaseOrderDetail spod1 = createSpod("SPO-001", "P001", "箱", 10, 8);

            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.empty());
            when(locationRepository.findByBranchCode(BRANCH_CODE))
                    .thenReturn(List.of(createLocation("LOC-A")));
            when(mapper.toLocationInfoDTOList(anyList()))
                    .thenReturn(Collections.emptyList());
            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(List.of(spo1));
            when(spodRepository.findByPurchaseNoIn(List.of("SPO-001")))
                    .thenReturn(List.of(spod1));
            when(mapper.productKey(anyString(), anyString()))
                    .thenAnswer(inv -> inv.getArgument(0) + "-" + inv.getArgument(1));

            // when
            BranchPurchaseSummaryDTO result = service.getSummary(BRANCH_CODE, PURCHASE_DATE);

            // then
            BranchPurchaseSummaryDTO.SummaryDetailDTO detail = result.getDetails().get(0);
            assertEquals(8, detail.getConfirmedQty());
            assertEquals(10, detail.getTotalQty());
            assertEquals(-2, detail.getDiffQty());
        }

        @Test
        @DisplayName("TC-S07: 結果依 productCode 排序")
        void resultsSortedByProductCode() {
            // given
            SalesPurchaseOrder spo1 = createSpo("SPO-001", "LOC-A");

            SalesPurchaseOrderDetail spodC = createSpod("SPO-001", "P003", "箱", 1, 1);
            SalesPurchaseOrderDetail spodA = createSpod("SPO-001", "P001", "箱", 2, 2);
            SalesPurchaseOrderDetail spodB = createSpod("SPO-001", "P002", "箱", 3, 3);

            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.empty());
            when(locationRepository.findByBranchCode(BRANCH_CODE))
                    .thenReturn(List.of(createLocation("LOC-A")));
            when(mapper.toLocationInfoDTOList(anyList()))
                    .thenReturn(Collections.emptyList());
            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(List.of(spo1));
            when(spodRepository.findByPurchaseNoIn(List.of("SPO-001")))
                    .thenReturn(List.of(spodC, spodA, spodB));
            when(mapper.productKey(anyString(), anyString()))
                    .thenAnswer(inv -> inv.getArgument(0) + "-" + inv.getArgument(1));

            // when
            BranchPurchaseSummaryDTO result = service.getSummary(BRANCH_CODE, PURCHASE_DATE);

            // then
            assertEquals(3, result.getDetails().size());
            assertEquals("P001", result.getDetails().get(0).getProductCode());
            assertEquals("P002", result.getDetails().get(1).getProductCode());
            assertEquals("P003", result.getDetails().get(2).getProductCode());
        }
    }

    // ==================== UpdateConfirmedQty 測試 ====================

    @Nested
    @DisplayName("updateConfirmedQty 測試")
    class UpdateConfirmedQtyTest {

        private BranchPurchaseSummaryUpdateDTO createUpdateDTO(List<BranchPurchaseSummaryUpdateDTO.UpdateDetailDTO> details) {
            return BranchPurchaseSummaryUpdateDTO.builder()
                    .branchCode(BRANCH_CODE)
                    .purchaseDate(PURCHASE_DATE)
                    .details(details)
                    .build();
        }

        private BranchPurchaseSummaryUpdateDTO.UpdateDetailDTO createUpdateDetail(String productCode, String unit, int confirmedQty) {
            return BranchPurchaseSummaryUpdateDTO.UpdateDetailDTO.builder()
                    .productCode(productCode)
                    .unit(unit)
                    .confirmedQty(confirmedQty)
                    .build();
        }

        @Test
        @DisplayName("TC-U01: 無凍結記錄 → throws")
        void noFrozenRecord_throws() {
            // given
            BranchPurchaseSummaryUpdateDTO dto = createUpdateDTO(Collections.emptyList());
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.empty());

            // when & then
            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> service.updateConfirmedQty(dto, CURRENT_USER));
            assertTrue(ex.getMessage().contains("尚未凍結"));
        }

        @Test
        @DisplayName("TC-U02: CONFIRMED → throws")
        void confirmedStatus_throws() {
            // given
            BranchPurchaseSummaryUpdateDTO dto = createUpdateDTO(Collections.emptyList());
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.of(createBpf(FrozenStatus.CONFIRMED)));

            // when & then
            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> service.updateConfirmedQty(dto, CURRENT_USER));
            assertTrue(ex.getMessage().contains("已確認"));
        }

        @Test
        @DisplayName("TC-U03: FROZEN → 正常更新")
        void frozenStatus_updatesSuccessfully() {
            // given
            BranchPurchaseFrozen bpf = createBpf(FrozenStatus.FROZEN);
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.of(bpf))       // updateConfirmedQty 查詢
                    .thenReturn(Optional.of(bpf));       // getSummary 查詢

            SalesPurchaseOrder spo = createSpo("SPO-001", "LOC-A");
            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(List.of(spo));

            SalesPurchaseOrderDetail spod = createSpod("SPO-001", "P001", "箱", 10, 10);
            when(spodRepository.findByPurchaseNoIn(List.of("SPO-001")))
                    .thenReturn(List.of(spod));
            when(mapper.productKey("P001", "箱")).thenReturn("P001-箱");

            BranchPurchaseSummaryUpdateDTO dto = createUpdateDTO(
                    List.of(createUpdateDetail("P001", "箱", 8))
            );

            // getSummary 的 mock
            when(locationRepository.findByBranchCode(BRANCH_CODE))
                    .thenReturn(Collections.emptyList());
            when(mapper.toLocationInfoDTOList(anyList()))
                    .thenReturn(Collections.emptyList());

            // when
            BranchPurchaseSummaryDTO result = service.updateConfirmedQty(dto, CURRENT_USER);

            // then
            assertNotNull(result);
            verify(spodRepository).saveAll(anyList());
            assertEquals(8, spod.getConfirmedQty());
        }

        @Test
        @DisplayName("TC-U04: 比例分配 qty=[6,4] confirmed=5 → 按比例分配")
        void proportionalDistribution() {
            // given
            BranchPurchaseFrozen bpf = createBpf(FrozenStatus.FROZEN);
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.of(bpf));

            SalesPurchaseOrder spo1 = createSpo("SPO-001", "LOC-A");
            SalesPurchaseOrder spo2 = createSpo("SPO-002", "LOC-B");
            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(List.of(spo1, spo2));

            SalesPurchaseOrderDetail spodA = createSpod("SPO-001", "P001", "箱", 6, 6);
            SalesPurchaseOrderDetail spodB = createSpod("SPO-002", "P001", "箱", 4, 4);
            when(spodRepository.findByPurchaseNoIn(List.of("SPO-001", "SPO-002")))
                    .thenReturn(List.of(spodA, spodB));
            when(mapper.productKey("P001", "箱")).thenReturn("P001-箱");

            BranchPurchaseSummaryUpdateDTO dto = createUpdateDTO(
                    List.of(createUpdateDetail("P001", "箱", 5))
            );

            // getSummary mock
            when(locationRepository.findByBranchCode(BRANCH_CODE))
                    .thenReturn(Collections.emptyList());
            when(mapper.toLocationInfoDTOList(anyList()))
                    .thenReturn(Collections.emptyList());

            // when
            service.updateConfirmedQty(dto, CURRENT_USER);

            // then - 6/10*5=3, 剩餘=5-3=2
            assertEquals(3, spodA.getConfirmedQty());
            assertEquals(2, spodB.getConfirmedQty());
        }

        @Test
        @DisplayName("TC-U05: totalOriginalQty=0 → 平均分配")
        void zeroOriginalQty_evenDistribution() {
            // given
            BranchPurchaseFrozen bpf = createBpf(FrozenStatus.FROZEN);
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.of(bpf));

            SalesPurchaseOrder spo1 = createSpo("SPO-001", "LOC-A");
            SalesPurchaseOrder spo2 = createSpo("SPO-002", "LOC-B");
            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(List.of(spo1, spo2));

            SalesPurchaseOrderDetail spodA = createSpod("SPO-001", "P001", "箱", 0, 0);
            SalesPurchaseOrderDetail spodB = createSpod("SPO-002", "P001", "箱", 0, 0);
            when(spodRepository.findByPurchaseNoIn(List.of("SPO-001", "SPO-002")))
                    .thenReturn(List.of(spodA, spodB));
            when(mapper.productKey("P001", "箱")).thenReturn("P001-箱");

            BranchPurchaseSummaryUpdateDTO dto = createUpdateDTO(
                    List.of(createUpdateDetail("P001", "箱", 5))
            );

            // getSummary mock
            when(locationRepository.findByBranchCode(BRANCH_CODE))
                    .thenReturn(Collections.emptyList());
            when(mapper.toLocationInfoDTOList(anyList()))
                    .thenReturn(Collections.emptyList());

            // when
            service.updateConfirmedQty(dto, CURRENT_USER);

            // then - 5/2=2 餘1, 第一個拿3, 第二個拿2
            assertEquals(3, spodA.getConfirmedQty());
            assertEquals(2, spodB.getConfirmedQty());
        }

        @Test
        @DisplayName("TC-U06: confirmedQty=0 → 全部為0")
        void zeroConfirmedQty_allZero() {
            // given
            BranchPurchaseFrozen bpf = createBpf(FrozenStatus.FROZEN);
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.of(bpf));

            SalesPurchaseOrder spo1 = createSpo("SPO-001", "LOC-A");
            SalesPurchaseOrder spo2 = createSpo("SPO-002", "LOC-B");
            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(List.of(spo1, spo2));

            SalesPurchaseOrderDetail spodA = createSpod("SPO-001", "P001", "箱", 6, 6);
            SalesPurchaseOrderDetail spodB = createSpod("SPO-002", "P001", "箱", 4, 4);
            when(spodRepository.findByPurchaseNoIn(List.of("SPO-001", "SPO-002")))
                    .thenReturn(List.of(spodA, spodB));
            when(mapper.productKey("P001", "箱")).thenReturn("P001-箱");

            BranchPurchaseSummaryUpdateDTO dto = createUpdateDTO(
                    List.of(createUpdateDetail("P001", "箱", 0))
            );

            // getSummary mock
            when(locationRepository.findByBranchCode(BRANCH_CODE))
                    .thenReturn(Collections.emptyList());
            when(mapper.toLocationInfoDTOList(anyList()))
                    .thenReturn(Collections.emptyList());

            // when
            service.updateConfirmedQty(dto, CURRENT_USER);

            // then
            assertEquals(0, spodA.getConfirmedQty());
            assertEquals(0, spodB.getConfirmedQty());
        }

        @Test
        @DisplayName("TC-U07: 單一儲位 → 全分配")
        void singleLocation_fullAllocation() {
            // given
            BranchPurchaseFrozen bpf = createBpf(FrozenStatus.FROZEN);
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.of(bpf));

            SalesPurchaseOrder spo = createSpo("SPO-001", "LOC-A");
            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(List.of(spo));

            SalesPurchaseOrderDetail spod = createSpod("SPO-001", "P001", "箱", 10, 10);
            when(spodRepository.findByPurchaseNoIn(List.of("SPO-001")))
                    .thenReturn(List.of(spod));
            when(mapper.productKey("P001", "箱")).thenReturn("P001-箱");

            BranchPurchaseSummaryUpdateDTO dto = createUpdateDTO(
                    List.of(createUpdateDetail("P001", "箱", 7))
            );

            // getSummary mock
            when(locationRepository.findByBranchCode(BRANCH_CODE))
                    .thenReturn(Collections.emptyList());
            when(mapper.toLocationInfoDTOList(anyList()))
                    .thenReturn(Collections.emptyList());

            // when
            service.updateConfirmedQty(dto, CURRENT_USER);

            // then
            assertEquals(7, spod.getConfirmedQty());
        }

        @Test
        @DisplayName("TC-U08: productKey 不存在 → skip")
        void productKeyNotFound_skip() {
            // given
            BranchPurchaseFrozen bpf = createBpf(FrozenStatus.FROZEN);
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.of(bpf));

            SalesPurchaseOrder spo = createSpo("SPO-001", "LOC-A");
            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(List.of(spo));

            SalesPurchaseOrderDetail spod = createSpod("SPO-001", "P001", "箱", 10, 10);
            when(spodRepository.findByPurchaseNoIn(List.of("SPO-001")))
                    .thenReturn(List.of(spod));
            when(mapper.productKey(anyString(), anyString()))
                    .thenAnswer(inv -> inv.getArgument(0) + "-" + inv.getArgument(1));

            // 更新一個不存在的產品
            BranchPurchaseSummaryUpdateDTO dto = createUpdateDTO(
                    List.of(createUpdateDetail("P999", "箱", 5))
            );

            // getSummary mock
            when(locationRepository.findByBranchCode(BRANCH_CODE))
                    .thenReturn(Collections.emptyList());
            when(mapper.toLocationInfoDTOList(anyList()))
                    .thenReturn(Collections.emptyList());

            // when
            service.updateConfirmedQty(dto, CURRENT_USER);

            // then - 原本的 spod 不應該被改動 confirmedQty
            assertEquals(10, spod.getConfirmedQty());
            verify(spodRepository).saveAll(anyList());
        }
    }

    // ==================== Freeze 測試 ====================

    @Nested
    @DisplayName("freeze 測試")
    class FreezeTest {

        @Test
        @DisplayName("TC-F01: 正常凍結 → BPF 建立 + confirmedQty=qty")
        void normalFreeze_bpfCreatedAndConfirmedQtySet() {
            // given
            BranchPurchaseFreezeDTO dto = createFreezeDTO();

            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.empty())        // freeze 檢查
                    .thenReturn(Optional.of(createBpf(FrozenStatus.FROZEN))); // getSummary 查詢

            SalesPurchaseOrder spo = createSpo("SPO-001", "LOC-A");
            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(List.of(spo));

            SalesPurchaseOrderDetail spod = createSpod("SPO-001", "P001", "箱", 10, 0);
            when(spodRepository.findByPurchaseNoIn(List.of("SPO-001")))
                    .thenReturn(List.of(spod));

            // getSummary mock
            when(locationRepository.findByBranchCode(BRANCH_CODE))
                    .thenReturn(Collections.emptyList());
            when(mapper.toLocationInfoDTOList(anyList()))
                    .thenReturn(Collections.emptyList());
            when(mapper.productKey(anyString(), anyString()))
                    .thenAnswer(inv -> inv.getArgument(0) + "-" + inv.getArgument(1));

            // when
            BranchPurchaseSummaryDTO result = service.freeze(dto, CURRENT_USER);

            // then
            verify(bpfRepository).save(argThat(bpf ->
                    bpf.getStatus() == FrozenStatus.FROZEN &&
                    bpf.getBranchCode().equals(BRANCH_CODE) &&
                    bpf.getFrozenBy().equals(CURRENT_USER)
            ));
            assertEquals(10, spod.getConfirmedQty());
            verify(spodRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("TC-F02: 已凍結 → throws")
        void alreadyFrozen_throws() {
            // given
            BranchPurchaseFreezeDTO dto = createFreezeDTO();
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.of(createBpf(FrozenStatus.FROZEN)));

            // when & then
            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> service.freeze(dto, CURRENT_USER));
            assertTrue(ex.getMessage().contains("已經凍結"));
        }

        @Test
        @DisplayName("TC-F03: 無 SPOD → BPF 正常建立")
        void noSpod_bpfStillCreated() {
            // given
            BranchPurchaseFreezeDTO dto = createFreezeDTO();

            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.empty())        // freeze 檢查
                    .thenReturn(Optional.of(createBpf(FrozenStatus.FROZEN))); // getSummary 查詢

            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Collections.emptyList());
            when(spodRepository.findByPurchaseNoIn(Collections.emptyList()))
                    .thenReturn(Collections.emptyList());

            // getSummary mock
            when(locationRepository.findByBranchCode(BRANCH_CODE))
                    .thenReturn(Collections.emptyList());
            when(mapper.toLocationInfoDTOList(anyList()))
                    .thenReturn(Collections.emptyList());

            // when
            BranchPurchaseSummaryDTO result = service.freeze(dto, CURRENT_USER);

            // then
            verify(bpfRepository).save(argThat(bpf -> bpf.getStatus() == FrozenStatus.FROZEN));
            assertNotNull(result);
        }
    }

    // ==================== Unfreeze 測試 ====================

    @Nested
    @DisplayName("unfreeze 測試")
    class UnfreezeTest {

        @Test
        @DisplayName("TC-UF01: FROZEN → 正常刪除")
        void frozenStatus_deletesSuccessfully() {
            // given
            BranchPurchaseFreezeDTO dto = createFreezeDTO();
            BranchPurchaseFrozen bpf = createBpf(FrozenStatus.FROZEN);

            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.of(bpf))        // unfreeze 查詢
                    .thenReturn(Optional.empty());        // getSummary 查詢

            // getSummary mock
            when(locationRepository.findByBranchCode(BRANCH_CODE))
                    .thenReturn(Collections.emptyList());
            when(mapper.toLocationInfoDTOList(anyList()))
                    .thenReturn(Collections.emptyList());
            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Collections.emptyList());

            // when
            BranchPurchaseSummaryDTO result = service.unfreeze(dto, CURRENT_USER);

            // then
            verify(bpfRepository).delete(bpf);
            assertNotNull(result);
        }

        @Test
        @DisplayName("TC-UF02: 無凍結記錄 → throws")
        void noFrozenRecord_throws() {
            // given
            BranchPurchaseFreezeDTO dto = createFreezeDTO();
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.empty());

            // when & then
            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> service.unfreeze(dto, CURRENT_USER));
            assertTrue(ex.getMessage().contains("尚未凍結"));
        }

        @Test
        @DisplayName("TC-UF03: CONFIRMED → throws")
        void confirmedStatus_throws() {
            // given
            BranchPurchaseFreezeDTO dto = createFreezeDTO();
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.of(createBpf(FrozenStatus.CONFIRMED)));

            // when & then
            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> service.unfreeze(dto, CURRENT_USER));
            assertTrue(ex.getMessage().contains("已確認"));
        }
    }

    // ==================== Confirm 測試 ====================

    @Nested
    @DisplayName("confirm 測試")
    class ConfirmTest {

        @Test
        @DisplayName("TC-C01: FROZEN → 更新為 CONFIRMED")
        void frozenToConfirmed() {
            // given
            BranchPurchaseFreezeDTO dto = createFreezeDTO();
            BranchPurchaseFrozen bpf = createBpf(FrozenStatus.FROZEN);

            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.of(bpf));

            // getSummary mock
            when(locationRepository.findByBranchCode(BRANCH_CODE))
                    .thenReturn(Collections.emptyList());
            when(mapper.toLocationInfoDTOList(anyList()))
                    .thenReturn(Collections.emptyList());
            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Collections.emptyList());

            // when
            BranchPurchaseSummaryDTO result = service.confirm(dto, CURRENT_USER);

            // then
            assertEquals(FrozenStatus.CONFIRMED, bpf.getStatus());
            assertEquals(CURRENT_USER, bpf.getConfirmedBy());
            assertNotNull(bpf.getConfirmedAt());
            verify(bpfRepository).save(bpf);
        }

        @Test
        @DisplayName("TC-C02: 無凍結記錄 → throws")
        void noFrozenRecord_throws() {
            // given
            BranchPurchaseFreezeDTO dto = createFreezeDTO();
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.empty());

            // when & then
            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> service.confirm(dto, CURRENT_USER));
            assertTrue(ex.getMessage().contains("尚未凍結"));
        }

        @Test
        @DisplayName("TC-C03: CONFIRMED → throws")
        void alreadyConfirmed_throws() {
            // given
            BranchPurchaseFreezeDTO dto = createFreezeDTO();
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.of(createBpf(FrozenStatus.CONFIRMED)));

            // when & then
            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> service.confirm(dto, CURRENT_USER));
            assertTrue(ex.getMessage().contains("已經確認"));
        }
    }

    // ==================== Aggregate 測試 ====================

    @Nested
    @DisplayName("aggregate 測試")
    class AggregateTest {

        @Test
        @DisplayName("TC-A01: 無凍結記錄 → throws")
        void noFrozenRecord_throws() {
            // given
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.empty());

            // when & then
            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> service.aggregate(BRANCH_CODE, PURCHASE_DATE, CURRENT_USER));
            assertTrue(ex.getMessage().contains("尚未凍結"));
        }

        @Test
        @DisplayName("TC-A02: 非 CONFIRMED → throws")
        void notConfirmed_throws() {
            // given
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.of(createBpf(FrozenStatus.FROZEN)));

            // when & then
            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> service.aggregate(BRANCH_CODE, PURCHASE_DATE, CURRENT_USER));
            assertTrue(ex.getMessage().contains("尚未確認"));
        }

        @Test
        @DisplayName("TC-A03: 無 PENDING SPOD → throws")
        void noPendingSpod_throws() {
            // given
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.of(createBpf(FrozenStatus.CONFIRMED)));

            SalesPurchaseOrder spo = createSpo("SPO-001", "LOC-A");
            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(List.of(spo));
            when(spodRepository.findByPurchaseNoInAndStatus(List.of("SPO-001"), SalesOrderDetailStatus.PENDING))
                    .thenReturn(Collections.emptyList());

            // when & then
            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> service.aggregate(BRANCH_CODE, PURCHASE_DATE, CURRENT_USER));
            assertTrue(ex.getMessage().contains("沒有待彙總"));
        }

        @Test
        @DisplayName("TC-A04: 2產品不同工廠 → 2張 BPO")
        void twoProductsDifferentFactories_twoBpos() {
            // given
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.of(createBpf(FrozenStatus.CONFIRMED)));

            SalesPurchaseOrder spo = createSpo("SPO-001", "LOC-A");
            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(List.of(spo));

            SalesPurchaseOrderDetail spodA = createSpod("SPO-001", "P001", "箱", 10, 8);
            SalesPurchaseOrderDetail spodB = createSpod("SPO-001", "P002", "箱", 5, 5);
            when(spodRepository.findByPurchaseNoInAndStatus(List.of("SPO-001"), SalesOrderDetailStatus.PENDING))
                    .thenReturn(List.of(spodA, spodB));

            ProductFactory pfA = new ProductFactory();
            pfA.setProductCode("P001");
            pfA.setFactoryCode("F001");
            ProductFactory pfB = new ProductFactory();
            pfB.setProductCode("P002");
            pfB.setFactoryCode("F002");

            when(productFactoryRepository.findByProductCodeAndIsDefaultTrue("P001"))
                    .thenReturn(Optional.of(pfA));
            when(productFactoryRepository.findByProductCodeAndIsDefaultTrue("P002"))
                    .thenReturn(Optional.of(pfB));

            when(sequenceGenerator.generate(eq(SequenceType.BPO), eq(PURCHASE_DATE)))
                    .thenReturn("BPO-001", "BPO-002");

            when(mapper.productKey(anyString(), anyString()))
                    .thenAnswer(inv -> inv.getArgument(0) + "-" + inv.getArgument(1));

            // getBpoList mock
            BranchPurchaseOrder bpo1 = new BranchPurchaseOrder();
            bpo1.setBpoNo("BPO-001");
            bpo1.setFactoryCode("F001");
            BranchPurchaseOrder bpo2 = new BranchPurchaseOrder();
            bpo2.setBpoNo("BPO-002");
            bpo2.setFactoryCode("F002");
            when(bpoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(List.of(bpo1, bpo2));
            when(bpodRepository.findByBpoNoOrderByItemNo(anyString()))
                    .thenReturn(Collections.emptyList());
            when(mapper.toBpoDTO(any(BranchPurchaseOrder.class), anyList()))
                    .thenReturn(BranchPurchaseOrderDTO.builder().bpoNo("BPO-001").build(),
                                BranchPurchaseOrderDTO.builder().bpoNo("BPO-002").build());

            // when
            List<BranchPurchaseOrderDTO> result = service.aggregate(BRANCH_CODE, PURCHASE_DATE, CURRENT_USER);

            // then
            assertEquals(2, result.size());
            verify(bpoRepository, times(2)).save(any(BranchPurchaseOrder.class));
            verify(bpodRepository, times(2)).save(any(BranchPurchaseOrderDetail.class));
        }

        @Test
        @DisplayName("TC-A05: 產品無預設工廠 → factoryCode=DEFAULT")
        void noDefaultFactory_usesDefault() {
            // given
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.of(createBpf(FrozenStatus.CONFIRMED)));

            SalesPurchaseOrder spo = createSpo("SPO-001", "LOC-A");
            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(List.of(spo));

            SalesPurchaseOrderDetail spod = createSpod("SPO-001", "P001", "箱", 10, 8);
            when(spodRepository.findByPurchaseNoInAndStatus(List.of("SPO-001"), SalesOrderDetailStatus.PENDING))
                    .thenReturn(List.of(spod));

            when(productFactoryRepository.findByProductCodeAndIsDefaultTrue("P001"))
                    .thenReturn(Optional.empty());

            when(sequenceGenerator.generate(eq(SequenceType.BPO), eq(PURCHASE_DATE)))
                    .thenReturn("BPO-001");

            when(mapper.productKey(anyString(), anyString()))
                    .thenAnswer(inv -> inv.getArgument(0) + "-" + inv.getArgument(1));

            // getBpoList mock
            when(bpoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Collections.emptyList());

            // when
            service.aggregate(BRANCH_CODE, PURCHASE_DATE, CURRENT_USER);

            // then
            verify(bpoRepository).save(argThat(bpo ->
                    "DEFAULT".equals(bpo.getFactoryCode())
            ));
        }

        @Test
        @DisplayName("TC-A06: 同工廠同產品跨儲位 → BPOD.qty=confirmedQty 加總 + SPOD 狀態更新")
        void sameFactorySameProductAcrossLocations_aggregatedCorrectly() {
            // given
            when(bpfRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Optional.of(createBpf(FrozenStatus.CONFIRMED)));

            SalesPurchaseOrder spo1 = createSpo("SPO-001", "LOC-A");
            SalesPurchaseOrder spo2 = createSpo("SPO-002", "LOC-B");
            when(spoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(List.of(spo1, spo2));

            SalesPurchaseOrderDetail spodA = createSpod("SPO-001", "P001", "箱", 6, 4);
            SalesPurchaseOrderDetail spodB = createSpod("SPO-002", "P001", "箱", 4, 3);
            when(spodRepository.findByPurchaseNoInAndStatus(List.of("SPO-001", "SPO-002"), SalesOrderDetailStatus.PENDING))
                    .thenReturn(List.of(spodA, spodB));

            ProductFactory pf = new ProductFactory();
            pf.setProductCode("P001");
            pf.setFactoryCode("F001");
            when(productFactoryRepository.findByProductCodeAndIsDefaultTrue("P001"))
                    .thenReturn(Optional.of(pf));

            when(sequenceGenerator.generate(eq(SequenceType.BPO), eq(PURCHASE_DATE)))
                    .thenReturn("BPO-001");

            when(mapper.productKey(anyString(), anyString()))
                    .thenAnswer(inv -> inv.getArgument(0) + "-" + inv.getArgument(1));

            // getBpoList mock
            when(bpoRepository.findByBranchCodeAndPurchaseDate(BRANCH_CODE, PURCHASE_DATE))
                    .thenReturn(Collections.emptyList());

            // when
            service.aggregate(BRANCH_CODE, PURCHASE_DATE, CURRENT_USER);

            // then - BPOD qty = 4 + 3 = 7
            verify(bpodRepository).save(argThat(bpod ->
                    bpod.getQty() == 7 &&
                    "P001".equals(bpod.getProductCode()) &&
                    "BPO-001".equals(bpod.getBpoNo())
            ));

            // SPOD 狀態全部更新為 AGGREGATED
            assertEquals(SalesOrderDetailStatus.AGGREGATED, spodA.getStatus());
            assertEquals(SalesOrderDetailStatus.AGGREGATED, spodB.getStatus());
            verify(spodRepository).saveAll(List.of(spodA, spodB));
        }
    }
}
