package com.example.mockodsvue.mapper;

import com.example.mockodsvue.model.dto.BranchPurchaseOrderDTO;
import com.example.mockodsvue.model.dto.BranchPurchaseSummaryDTO.LocationInfoDTO;
import com.example.mockodsvue.model.entity.branch.Location;
import com.example.mockodsvue.model.entity.purchase.BranchPurchaseOrder;
import com.example.mockodsvue.model.entity.purchase.BranchPurchaseOrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BranchPurchaseMapper {

    // ==================== Location 轉換 ====================

    LocationInfoDTO toLocationInfoDTO(Location location);

    List<LocationInfoDTO> toLocationInfoDTOList(List<Location> locations);

    // ==================== BPO 轉換 ====================

    @Mapping(target = "details", ignore = true)
    BranchPurchaseOrderDTO toBpoDTO(BranchPurchaseOrder order);

    default BranchPurchaseOrderDTO toBpoDTO(BranchPurchaseOrder order, List<BranchPurchaseOrderDetail> details) {
        BranchPurchaseOrderDTO dto = toBpoDTO(order);
        dto.setDetails(toBpoDetailDTOList(details));
        return dto;
    }

    BranchPurchaseOrderDTO.DetailDTO toBpoDetailDTO(BranchPurchaseOrderDetail detail);

    List<BranchPurchaseOrderDTO.DetailDTO> toBpoDetailDTOList(List<BranchPurchaseOrderDetail> details);

    // ==================== 工具方法 ====================

    /**
     * 產生產品唯一鍵 (productCode + unit)
     */
    default String productKey(String productCode, String unit) {
        return productCode + "-" + unit;
    }
}
