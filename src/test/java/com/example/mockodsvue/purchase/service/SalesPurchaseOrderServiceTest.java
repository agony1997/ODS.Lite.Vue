package com.example.mockodsvue.service;

import com.example.mockodsvue.exception.BusinessException;
import com.example.mockodsvue.mapper.SalesPurchaseMapper;
import com.example.mockodsvue.model.dto.SalesPurchaseDTO;
import com.example.mockodsvue.model.dto.SalesPurchaseDTO.SalesPurchaseDetailDTO;
import com.example.mockodsvue.model.dto.SalesPurchaseListDTO;
import com.example.mockodsvue.model.entity.branch.BranchProductList;
import com.example.mockodsvue.model.entity.branch.Location;
import com.example.mockodsvue.model.entity.purchase.SalesPurchaseList;
import com.example.mockodsvue.model.entity.purchase.SalesPurchaseOrder;
import com.example.mockodsvue.model.entity.purchase.SalesPurchaseOrderDetail;
import com.example.mockodsvue.model.enums.LocationType;
import com.example.mockodsvue.model.enums.SalesOrderDetailStatus;
import com.example.mockodsvue.model.enums.SequenceType;
import com.example.mockodsvue.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SalesPurchaseOrderService 測試")
class SalesPurchaseOrderServiceTest {

    @Mock
    private SalesPurchaseOrderRepository orderRepository;

    @Mock
    private SalesPurchaseOrderDetailRepository detailRepository;

    @Mock
    private SalesPurchaseListRepository customListRepository;

    @Mock
    private BranchProductListRepository branchProductRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private SequenceGenerator sequenceGenerator;

    @Mock
    private SalesPurchaseMapper mapper;

    @Mock
    private BranchPurchaseFrozenRepository frozenRepository;

    @InjectMocks
    private SalesPurchaseOrderService service;

    private Location testLocation;
    private SalesPurchaseOrder testOrder;
    private LocalDate validPurchaseDate;
    private static final String LOCATION_CODE = "LOC001";
    private static final String BRANCH_CODE = "BR001";
    private static final String CURRENT_USER = "testUser";

    @BeforeEach
    void setUp() {
        // 設定有效的訂貨日期 (今天 + 3 天)
        validPurchaseDate = LocalDate.now().plusDays(3);

        // 建立測試儲位
        testLocation = new Location();
        testLocation.setLocationCode(LOCATION_CODE);
        testLocation.setBranchCode(BRANCH_CODE);
        testLocation.setLocationType(LocationType.CAR);
        testLocation.setStatus("ACTIVE");

        // 建立測試訂單
        testOrder = new SalesPurchaseOrder();
        testOrder.setId(1);
        testOrder.setPurchaseNo("SPO-20260125-001");
        testOrder.setBranchCode(BRANCH_CODE);
        testOrder.setLocationCode(LOCATION_CODE);
        testOrder.setPurchaseDate(validPurchaseDate);
        testOrder.setPurchaseUser(CURRENT_USER);
    }

    // ==================== findOrCreateByCondition 測試 ====================

    @Nested
    @DisplayName("findOrCreateByCondition 測試")
    class FindOrCreateByConditionTest {

        @Test
        @DisplayName("查詢已存在的訂單 - 成功")
        void findExistingOrder_Success() {
            // given
            when(locationRepository.findByLocationCode(LOCATION_CODE))
                    .thenReturn(Optional.of(testLocation));
            when(orderRepository.findByLocationCodeAndPurchaseDate(LOCATION_CODE, validPurchaseDate))
                    .thenReturn(Optional.of(testOrder));
            when(detailRepository.findByPurchaseNoOrderByItemNo(testOrder.getPurchaseNo()))
                    .thenReturn(List.of());
            when(branchProductRepository.findByBranchCodeOrderBySortOrder(BRANCH_CODE))
                    .thenReturn(List.of());
            when(orderRepository.findByLocationCodeAndPurchaseDate(LOCATION_CODE, validPurchaseDate.minusDays(1)))
                    .thenReturn(Optional.empty());

            SalesPurchaseDTO expectedDto = createTestDTO();
            when(mapper.toDTO(eq(testOrder), anyList(), anyMap(), anyMap()))
                    .thenReturn(expectedDto);

            // when
            SalesPurchaseDTO result = service.findOrCreateByCondition(LOCATION_CODE, validPurchaseDate, CURRENT_USER);

            // then
            assertNotNull(result);
            assertEquals(testOrder.getPurchaseNo(), result.getPurchaseNo());
            verify(sequenceGenerator, never()).generate(any(), any());
        }

        @Test
        @DisplayName("建立新訂單 - 成功")
        void createNewOrder_Success() {
            // given
            String newPurchaseNo = "SPO-20260125-001";
            when(locationRepository.findByLocationCode(LOCATION_CODE))
                    .thenReturn(Optional.of(testLocation));
            when(orderRepository.findByLocationCodeAndPurchaseDate(LOCATION_CODE, validPurchaseDate))
                    .thenReturn(Optional.empty());
            when(sequenceGenerator.generate(SequenceType.SPO, validPurchaseDate))
                    .thenReturn(newPurchaseNo);
            when(orderRepository.save(any(SalesPurchaseOrder.class)))
                    .thenAnswer(invocation -> {
                        SalesPurchaseOrder order = invocation.getArgument(0);
                        order.setId(1);
                        return order;
                    });
            when(branchProductRepository.findByBranchCodeOrderBySortOrder(BRANCH_CODE))
                    .thenReturn(List.of());
            when(mapper.toDetailEntitiesFromBranch(eq(newPurchaseNo), anyList()))
                    .thenReturn(List.of());
            when(detailRepository.findByPurchaseNoOrderByItemNo(newPurchaseNo))
                    .thenReturn(List.of());
            when(orderRepository.findByLocationCodeAndPurchaseDate(LOCATION_CODE, validPurchaseDate.minusDays(1)))
                    .thenReturn(Optional.empty());

            SalesPurchaseDTO expectedDto = createTestDTO();
            when(mapper.toDTO(any(SalesPurchaseOrder.class), anyList(), anyMap(), anyMap()))
                    .thenReturn(expectedDto);

            // when
            SalesPurchaseDTO result = service.findOrCreateByCondition(LOCATION_CODE, validPurchaseDate, CURRENT_USER);

            // then
            assertNotNull(result);
            verify(sequenceGenerator).generate(SequenceType.SPO, validPurchaseDate);
            verify(orderRepository).save(any(SalesPurchaseOrder.class));
        }

        @Test
        @DisplayName("訂貨日期過早 - 拋出例外")
        void purchaseDateTooEarly_ThrowsException() {
            // given
            LocalDate tooEarlyDate = LocalDate.now().plusDays(1);

            // when & then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.findOrCreateByCondition(LOCATION_CODE, tooEarlyDate, CURRENT_USER)
            );

            assertTrue(exception.getMessage().contains("訂貨日期必須在"));
            verify(orderRepository, never()).findByLocationCodeAndPurchaseDate(any(), any());
        }

        @Test
        @DisplayName("訂貨日期過晚 - 拋出例外")
        void purchaseDateTooLate_ThrowsException() {
            // given
            LocalDate tooLateDate = LocalDate.now().plusDays(10);

            // when & then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.findOrCreateByCondition(LOCATION_CODE, tooLateDate, CURRENT_USER)
            );

            assertTrue(exception.getMessage().contains("訂貨日期必須在"));
        }

        @Test
        @DisplayName("儲位不存在 - 拋出例外")
        void locationNotFound_ThrowsException() {
            // given
            when(locationRepository.findByLocationCode(LOCATION_CODE))
                    .thenReturn(Optional.empty());

            // when & then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.findOrCreateByCondition(LOCATION_CODE, validPurchaseDate, CURRENT_USER)
            );

            assertEquals("儲位不存在: " + LOCATION_CODE, exception.getMessage());
        }
    }

    // ==================== updateOrder 測試 ====================

    @Nested
    @DisplayName("updateOrder 測試")
    class UpdateOrderTest {

        @Test
        @DisplayName("更新訂單明細 - 成功")
        void updateOrder_Success() {
            // given
            SalesPurchaseDTO inputDto = createTestDTO();
            inputDto.setDetails(List.of(createTestDetailDTO("P001", 10)));

            when(orderRepository.findByPurchaseNo(inputDto.getPurchaseNo()))
                    .thenReturn(Optional.of(testOrder));
            when(mapper.toDetailEntities(eq(inputDto.getPurchaseNo()), anyList()))
                    .thenReturn(List.of(createTestDetail("P001", 10)));
            when(locationRepository.findByLocationCode(LOCATION_CODE))
                    .thenReturn(Optional.of(testLocation));
            when(branchProductRepository.findByBranchCodeOrderBySortOrder(BRANCH_CODE))
                    .thenReturn(List.of());
            when(orderRepository.findByLocationCodeAndPurchaseDate(LOCATION_CODE, validPurchaseDate.minusDays(1)))
                    .thenReturn(Optional.empty());
            when(mapper.toDTO(eq(testOrder), anyList(), anyMap(), anyMap()))
                    .thenReturn(inputDto);

            // when
            SalesPurchaseDTO result = service.updateOrder(inputDto);

            // then
            assertNotNull(result);
            verify(detailRepository).deleteByPurchaseNo(inputDto.getPurchaseNo());
            verify(detailRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("訂單不存在 - 拋出例外")
        void orderNotFound_ThrowsException() {
            // given
            SalesPurchaseDTO inputDto = createTestDTO();
            when(orderRepository.findByPurchaseNo(inputDto.getPurchaseNo()))
                    .thenReturn(Optional.empty());

            // when & then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.updateOrder(inputDto)
            );

            assertEquals("訂單不存在: " + inputDto.getPurchaseNo(), exception.getMessage());
        }
    }

    // ==================== loadFromYesterdayOrder 測試 ====================

    @Nested
    @DisplayName("loadFromYesterdayOrder 測試")
    class LoadFromYesterdayOrderTest {

        @Test
        @DisplayName("載入前一天訂單 - 成功")
        void loadFromYesterday_Success() {
            // given
            LocalDate yesterday = validPurchaseDate.minusDays(1);
            SalesPurchaseOrder yesterdayOrder = new SalesPurchaseOrder();
            yesterdayOrder.setPurchaseNo("SPO-YESTERDAY-001");

            when(orderRepository.findByLocationCodeAndPurchaseDate(LOCATION_CODE, yesterday))
                    .thenReturn(Optional.of(yesterdayOrder));
            when(detailRepository.findByPurchaseNoOrderByItemNo(yesterdayOrder.getPurchaseNo()))
                    .thenReturn(List.of(createTestDetail("P001", 5)));
            when(locationRepository.findByLocationCode(LOCATION_CODE))
                    .thenReturn(Optional.of(testLocation));
            when(orderRepository.findByLocationCodeAndPurchaseDate(LOCATION_CODE, validPurchaseDate))
                    .thenReturn(Optional.of(testOrder));
            when(mapper.toDetailEntity(isNull(), eq(0), anyString(), anyString(), anyInt(), eq(0)))
                    .thenReturn(createTestDetail("P001", 5));
            when(branchProductRepository.findByBranchCodeOrderBySortOrder(BRANCH_CODE))
                    .thenReturn(List.of());
            when(orderRepository.findByLocationCodeAndPurchaseDate(LOCATION_CODE, validPurchaseDate.minusDays(1)))
                    .thenReturn(Optional.of(yesterdayOrder));
            when(detailRepository.findByPurchaseNoOrderByItemNo(yesterdayOrder.getPurchaseNo()))
                    .thenReturn(List.of(createTestDetail("P001", 5)));

            SalesPurchaseDTO expectedDto = createTestDTO();
            when(mapper.toDTO(eq(testOrder), anyList(), anyMap(), anyMap()))
                    .thenReturn(expectedDto);

            // when
            SalesPurchaseDTO result = service.loadFromYesterdayOrder(LOCATION_CODE, validPurchaseDate, CURRENT_USER);

            // then
            assertNotNull(result);
            verify(detailRepository).deleteByPurchaseNo(testOrder.getPurchaseNo());
        }

        @Test
        @DisplayName("找不到前一天訂單 - 拋出例外")
        void yesterdayOrderNotFound_ThrowsException() {
            // given
            LocalDate yesterday = validPurchaseDate.minusDays(1);
            when(orderRepository.findByLocationCodeAndPurchaseDate(LOCATION_CODE, yesterday))
                    .thenReturn(Optional.empty());

            // when & then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.loadFromYesterdayOrder(LOCATION_CODE, validPurchaseDate, CURRENT_USER)
            );

            assertEquals("找不到前一天的訂單", exception.getMessage());
        }
    }

    // ==================== loadFromCustomList 測試 ====================

    @Nested
    @DisplayName("loadFromCustomList 測試")
    class LoadFromCustomListTest {

        @Test
        @DisplayName("載入自定義清單 - 成功")
        void loadFromCustomList_Success() {
            // given
            SalesPurchaseList customItem = new SalesPurchaseList();
            customItem.setProductCode("P001");
            customItem.setUnit("箱");
            customItem.setQty(10);

            when(customListRepository.findByLocationCodeOrderBySortOrder(LOCATION_CODE))
                    .thenReturn(List.of(customItem));
            when(locationRepository.findByLocationCode(LOCATION_CODE))
                    .thenReturn(Optional.of(testLocation));
            when(orderRepository.findByLocationCodeAndPurchaseDate(LOCATION_CODE, validPurchaseDate))
                    .thenReturn(Optional.of(testOrder));
            when(mapper.toDetailEntity(isNull(), eq(0), anyString(), anyString(), anyInt(), eq(0)))
                    .thenReturn(createTestDetail("P001", 10));
            when(branchProductRepository.findByBranchCodeOrderBySortOrder(BRANCH_CODE))
                    .thenReturn(List.of());
            when(orderRepository.findByLocationCodeAndPurchaseDate(LOCATION_CODE, validPurchaseDate.minusDays(1)))
                    .thenReturn(Optional.empty());

            SalesPurchaseDTO expectedDto = createTestDTO();
            when(mapper.toDTO(eq(testOrder), anyList(), anyMap(), anyMap()))
                    .thenReturn(expectedDto);

            // when
            SalesPurchaseDTO result = service.loadFromCustomList(LOCATION_CODE, validPurchaseDate, CURRENT_USER);

            // then
            assertNotNull(result);
        }

        @Test
        @DisplayName("自定義清單為空 - 拋出例外")
        void customListEmpty_ThrowsException() {
            // given
            when(customListRepository.findByLocationCodeOrderBySortOrder(LOCATION_CODE))
                    .thenReturn(List.of());

            // when & then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.loadFromCustomList(LOCATION_CODE, validPurchaseDate, CURRENT_USER)
            );

            assertEquals("尚未建立自定義產品清單", exception.getMessage());
        }
    }

    // ==================== CustomList 測試 ====================

    @Nested
    @DisplayName("CustomList 管理測試")
    class CustomListTest {

        @Test
        @DisplayName("儲存自定義清單 - 成功")
        void saveCustomList_Success() {
            // given
            SalesPurchaseListDTO item = new SalesPurchaseListDTO();
            item.setProductCode("P001");
            item.setUnit("箱");
            item.setQty(10);

            SalesPurchaseList entity = new SalesPurchaseList();
            entity.setProductCode("P001");

            when(mapper.toListEntity(any(SalesPurchaseListDTO.class))).thenReturn(entity);
            when(customListRepository.saveAll(anyList())).thenReturn(List.of(entity));
            when(mapper.toListDTOList(anyList())).thenReturn(List.of(item));

            // when
            List<SalesPurchaseListDTO> result = service.saveCustomList(LOCATION_CODE, List.of(item));

            // then
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(customListRepository).deleteByLocationCode(LOCATION_CODE);
            verify(customListRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("查詢自定義清單 - 成功")
        void getCustomList_Success() {
            // given
            SalesPurchaseList entity = new SalesPurchaseList();
            entity.setProductCode("P001");

            SalesPurchaseListDTO dto = new SalesPurchaseListDTO();
            dto.setProductCode("P001");

            when(customListRepository.findByLocationCodeOrderBySortOrder(LOCATION_CODE))
                    .thenReturn(List.of(entity));
            when(mapper.toListDTOList(anyList())).thenReturn(List.of(dto));

            // when
            List<SalesPurchaseListDTO> result = service.getCustomList(LOCATION_CODE);

            // then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("P001", result.get(0).getProductCode());
        }
    }

    // ==================== Helper Methods ====================

    private SalesPurchaseDTO createTestDTO() {
        return SalesPurchaseDTO.builder()
                .purchaseNo(testOrder.getPurchaseNo())
                .branchCode(BRANCH_CODE)
                .locationCode(LOCATION_CODE)
                .purchaseDate(validPurchaseDate)
                .purchaseUser(CURRENT_USER)
                .frozenStatus(null)
                .details(List.of())
                .build();
    }

    private SalesPurchaseDetailDTO createTestDetailDTO(String productCode, int qty) {
        return SalesPurchaseDetailDTO.builder()
                .productCode(productCode)
                .unit("箱")
                .qty(qty)
                .confirmedQty(0)
                .build();
    }

    private SalesPurchaseOrderDetail createTestDetail(String productCode, int qty) {
        SalesPurchaseOrderDetail detail = new SalesPurchaseOrderDetail();
        detail.setPurchaseNo(testOrder.getPurchaseNo());
        detail.setItemNo(1);
        detail.setProductCode(productCode);
        detail.setUnit("箱");
        detail.setQty(qty);
        detail.setConfirmedQty(0);
        detail.setStatus(SalesOrderDetailStatus.PENDING);
        return detail;
    }
}
