package com.example.mockodsvue.purchase.mapper;

import com.example.mockodsvue.purchase.model.dto.SalesPurchaseDTO;
import com.example.mockodsvue.purchase.model.dto.SalesPurchaseDTO.SalesPurchaseDetailDTO;
import com.example.mockodsvue.purchase.model.dto.SalesPurchaseListDTO;
import com.example.mockodsvue.branch.model.entity.BranchProductList;
import com.example.mockodsvue.purchase.model.entity.SalesPurchaseList;
import com.example.mockodsvue.purchase.model.entity.SalesPurchaseOrder;
import com.example.mockodsvue.purchase.model.entity.SalesPurchaseOrderDetail;
import com.example.mockodsvue.purchase.model.enums.SalesOrderDetailStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("SalesPurchaseMapper 測試")
class SalesPurchaseMapperTest {

    @Autowired
    private SalesPurchaseMapper mapper;

    @Test
    @DisplayName("toDTO 基本映射成功")
    void toDTO_BasicMapping_Success() {
        // given
        SalesPurchaseOrder order = new SalesPurchaseOrder();
        order.setPurchaseNo("SPO-001");
        order.setBranchCode("BR01");
        order.setLocationCode("LOC01");
        order.setPurchaseDate(LocalDate.of(2025, 1, 1));
        order.setPurchaseUser("E001");

        // when
        SalesPurchaseDTO dto = mapper.toDTO(order);

        // then
        assertEquals("SPO-001", dto.getPurchaseNo());
        assertEquals("BR01", dto.getBranchCode());
        assertEquals("LOC01", dto.getLocationCode());
        assertEquals(LocalDate.of(2025, 1, 1), dto.getPurchaseDate());
        assertEquals("E001", dto.getPurchaseUser());
        assertNull(dto.getDetails());
    }

    @Test
    @DisplayName("toDTO 含明細與產品名稱填充")
    void toDTO_WithDetails_Success() {
        // given
        SalesPurchaseOrder order = new SalesPurchaseOrder();
        order.setPurchaseNo("SPO-001");
        order.setBranchCode("BR01");
        order.setLocationCode("LOC01");
        order.setPurchaseDate(LocalDate.of(2025, 1, 1));
        order.setPurchaseUser("E001");

        SalesPurchaseOrderDetail detail = new SalesPurchaseOrderDetail();
        detail.setPurchaseNo("SPO-001");
        detail.setItemNo(1);
        detail.setProductCode("P001");
        detail.setUnit("箱");
        detail.setQty(10);
        detail.setConfirmedQty(8);
        detail.setStatus(SalesOrderDetailStatus.PENDING);

        BranchProductList product = new BranchProductList();
        product.setProductCode("P001");
        product.setProductName("味全鮮乳");
        product.setUnit("箱");

        Map<String, BranchProductList> productMap = new HashMap<>();
        productMap.put("P001-箱", product);

        Map<String, Integer> lastQtyMap = new HashMap<>();
        lastQtyMap.put("P001-箱", 5);

        // when
        SalesPurchaseDTO dto = mapper.toDTO(order, List.of(detail), productMap, lastQtyMap);

        // then
        assertNotNull(dto.getDetails());
        assertEquals(1, dto.getDetails().size());
        assertEquals("味全鮮乳", dto.getDetails().get(0).getProductName());
        assertEquals(5, dto.getDetails().get(0).getLastQty());
    }

    @Test
    @DisplayName("toDetailDTO 基本映射成功")
    void toDetailDTO_Success() {
        // given
        SalesPurchaseOrderDetail detail = new SalesPurchaseOrderDetail();
        detail.setPurchaseNo("SPO-001");
        detail.setItemNo(3);
        detail.setProductCode("P001");
        detail.setUnit("箱");
        detail.setQty(10);
        detail.setConfirmedQty(8);

        // when
        SalesPurchaseDetailDTO dto = mapper.toDetailDTO(detail);

        // then
        assertEquals("SPO-001", dto.getPurchaseNo());
        assertEquals("P001", dto.getProductCode());
        assertEquals("箱", dto.getUnit());
        assertEquals(10, dto.getQty());
        assertEquals(8, dto.getConfirmedQty());
        assertEquals(3, dto.getSortOrder()); // itemNo -> sortOrder
    }

    @Test
    @DisplayName("toDetailEntities 批次建立實體成功")
    void toDetailEntities_Success() {
        // given
        SalesPurchaseDetailDTO dto1 = SalesPurchaseDetailDTO.builder()
                .productCode("P001").unit("箱").qty(10).confirmedQty(10).build();
        SalesPurchaseDetailDTO dto2 = SalesPurchaseDetailDTO.builder()
                .productCode("P002").unit("瓶").qty(5).confirmedQty(5).build();

        // when
        List<SalesPurchaseOrderDetail> entities = mapper.toDetailEntities("SPO-001", List.of(dto1, dto2));

        // then
        assertEquals(2, entities.size());
        assertEquals("SPO-001", entities.get(0).getPurchaseNo());
        assertEquals(1, entities.get(0).getItemNo());
        assertEquals("P001", entities.get(0).getProductCode());
        assertEquals(2, entities.get(1).getItemNo());
        assertEquals("P002", entities.get(1).getProductCode());
        assertEquals(SalesOrderDetailStatus.PENDING, entities.get(0).getStatus());
    }

    @Test
    @DisplayName("toListDTO 映射成功")
    void toListDTO_Success() {
        // given
        SalesPurchaseList entity = new SalesPurchaseList();
        entity.setLocationCode("LOC01");
        entity.setProductCode("P001");
        entity.setUnit("箱");
        entity.setQty(5);
        entity.setSortOrder(1);

        // when
        SalesPurchaseListDTO dto = mapper.toListDTO(entity);

        // then
        assertEquals("P001", dto.getProductCode());
        assertEquals("箱", dto.getUnit());
        assertEquals(5, dto.getQty());
        assertEquals(1, dto.getSortOrder());
    }

    @Test
    @DisplayName("productKey 格式正確")
    void productKey_Format() {
        assertEquals("P001-箱", mapper.productKey("P001", "箱"));
        assertEquals("P002-瓶", mapper.productKey("P002", "瓶"));
    }
}
