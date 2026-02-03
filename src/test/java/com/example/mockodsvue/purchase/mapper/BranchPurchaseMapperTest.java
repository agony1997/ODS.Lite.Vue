package com.example.mockodsvue.purchase.mapper;

import com.example.mockodsvue.purchase.model.dto.BranchPurchaseOrderDTO;
import com.example.mockodsvue.purchase.model.dto.BranchPurchaseSummaryDTO.LocationInfoDTO;
import com.example.mockodsvue.branch.model.entity.Location;
import com.example.mockodsvue.purchase.model.entity.BranchPurchaseOrder;
import com.example.mockodsvue.purchase.model.entity.BranchPurchaseOrderDetail;
import com.example.mockodsvue.delivery.model.enums.DeliveryStatus;
import com.example.mockodsvue.branch.model.enums.LocationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("BranchPurchaseMapper 測試")
class BranchPurchaseMapperTest {

    @Autowired
    private BranchPurchaseMapper mapper;

    @Test
    @DisplayName("toLocationInfoDTO 映射成功")
    void toLocationInfoDTO_Success() {
        // given
        Location location = new Location();
        location.setLocationCode("LOC01");
        location.setLocationName("倉庫1");
        location.setBranchCode("BR01");
        location.setLocationType(LocationType.WAREHOUSE);
        location.setStatus("ACTIVE");

        // when
        LocationInfoDTO dto = mapper.toLocationInfoDTO(location);

        // then
        assertEquals("LOC01", dto.getLocationCode());
        assertEquals("倉庫1", dto.getLocationName());
    }

    @Test
    @DisplayName("toBpoDTO 基本映射成功")
    void toBpoDTO_BasicMapping_Success() {
        // given
        BranchPurchaseOrder order = new BranchPurchaseOrder();
        order.setBpoNo("BPO-001");
        order.setBranchCode("BR01");
        order.setFactoryCode("F001");
        order.setPurchaseDate(LocalDate.of(2025, 1, 1));
        order.setStatus(DeliveryStatus.PENDING);

        // when
        BranchPurchaseOrderDTO dto = mapper.toBpoDTO(order);

        // then
        assertEquals("BPO-001", dto.getBpoNo());
        assertEquals("BR01", dto.getBranchCode());
        assertEquals("F001", dto.getFactoryCode());
        assertEquals(LocalDate.of(2025, 1, 1), dto.getPurchaseDate());
        assertEquals(DeliveryStatus.PENDING, dto.getStatus());
        assertNull(dto.getDetails());
    }

    @Test
    @DisplayName("toBpoDTO 含明細映射成功")
    void toBpoDTO_WithDetails_Success() {
        // given
        BranchPurchaseOrder order = new BranchPurchaseOrder();
        order.setBpoNo("BPO-001");
        order.setBranchCode("BR01");
        order.setFactoryCode("F001");
        order.setPurchaseDate(LocalDate.of(2025, 1, 1));
        order.setStatus(DeliveryStatus.PENDING);

        BranchPurchaseOrderDetail detail = new BranchPurchaseOrderDetail();
        detail.setBpoNo("BPO-001");
        detail.setItemNo(1);
        detail.setProductCode("P001");
        detail.setProductName("味全鮮乳");
        detail.setUnit("箱");
        detail.setQty(20);

        // when
        BranchPurchaseOrderDTO dto = mapper.toBpoDTO(order, List.of(detail));

        // then
        assertNotNull(dto.getDetails());
        assertEquals(1, dto.getDetails().size());
        assertEquals("P001", dto.getDetails().get(0).getProductCode());
        assertEquals("味全鮮乳", dto.getDetails().get(0).getProductName());
        assertEquals(20, dto.getDetails().get(0).getQty());
    }

    @Test
    @DisplayName("toBpoDetailDTO 映射成功")
    void toBpoDetailDTO_Success() {
        // given
        BranchPurchaseOrderDetail detail = new BranchPurchaseOrderDetail();
        detail.setBpoNo("BPO-001");
        detail.setItemNo(1);
        detail.setProductCode("P001");
        detail.setProductName("味全鮮乳");
        detail.setUnit("箱");
        detail.setQty(20);

        // when
        BranchPurchaseOrderDTO.DetailDTO dto = mapper.toBpoDetailDTO(detail);

        // then
        assertEquals(1, dto.getItemNo());
        assertEquals("P001", dto.getProductCode());
        assertEquals("味全鮮乳", dto.getProductName());
        assertEquals("箱", dto.getUnit());
        assertEquals(20, dto.getQty());
    }

    @Test
    @DisplayName("productKey 格式正確")
    void productKey_Format() {
        assertEquals("P001-箱", mapper.productKey("P001", "箱"));
        assertEquals("P002-瓶", mapper.productKey("P002", "瓶"));
    }
}
