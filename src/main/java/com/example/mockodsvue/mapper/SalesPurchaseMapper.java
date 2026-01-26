package com.example.mockodsvue.mapper;

import com.example.mockodsvue.model.dto.SalesPurchaseDTO;
import com.example.mockodsvue.model.dto.SalesPurchaseDTO.SalesPurchaseDetailDTO;
import com.example.mockodsvue.model.dto.SalesPurchaseListDTO;
import com.example.mockodsvue.model.entity.branch.BranchProductList;
import com.example.mockodsvue.model.entity.purchase.SalesPurchaseList;
import com.example.mockodsvue.model.entity.purchase.SalesPurchaseOrder;
import com.example.mockodsvue.model.entity.purchase.SalesPurchaseOrderDetail;
import com.example.mockodsvue.model.enums.SalesOrderDetailStatus;
import org.mapstruct.*;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface SalesPurchaseMapper {

    // ==================== Order 轉換 ====================

    @Mapping(target = "details", ignore = true)
    @Mapping(target = "frozenStatus", ignore = true)
    SalesPurchaseDTO toDTO(SalesPurchaseOrder order);

    default SalesPurchaseDTO toDTO(SalesPurchaseOrder order,
                                    List<SalesPurchaseOrderDetail> details,
                                    Map<String, BranchProductList> productMap,
                                    Map<String, Integer> lastQtyMap) {
        SalesPurchaseDTO dto = toDTO(order);
        dto.setDetails(toDetailDTOList(details, productMap, lastQtyMap));
        return dto;
    }

    // ==================== Detail 轉換 ====================

    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "lastQty", ignore = true)
    @Mapping(target = "sortOrder", source = "itemNo")
    SalesPurchaseDetailDTO toDetailDTO(SalesPurchaseOrderDetail detail);

    default SalesPurchaseDetailDTO toDetailDTO(SalesPurchaseOrderDetail detail,
                                                Map<String, BranchProductList> productMap,
                                                Map<String, Integer> lastQtyMap) {
        SalesPurchaseDetailDTO dto = toDetailDTO(detail);

        String key = productKey(detail.getProductCode(), detail.getUnit());
        BranchProductList product = productMap.get(key);

        dto.setProductName(product != null ? product.getProductName() : "");
        dto.setLastQty(lastQtyMap.getOrDefault(key, 0));

        return dto;
    }

    default List<SalesPurchaseDetailDTO> toDetailDTOList(List<SalesPurchaseOrderDetail> details,
                                                          Map<String, BranchProductList> productMap,
                                                          Map<String, Integer> lastQtyMap) {
        return details.stream()
                .map(d -> toDetailDTO(d, productMap, lastQtyMap))
                .toList();
    }

    // ==================== Detail Entity 建立 ====================

    default SalesPurchaseOrderDetail toDetailEntity(String purchaseNo, int itemNo,
                                                     String productCode, String unit,
                                                     int qty, int confirmedQty) {
        SalesPurchaseOrderDetail detail = new SalesPurchaseOrderDetail();
        detail.setPurchaseNo(purchaseNo);
        detail.setItemNo(itemNo);
        detail.setProductCode(productCode);
        detail.setUnit(unit);
        detail.setQty(qty);
        detail.setConfirmedQty(confirmedQty);
        detail.setStatus(SalesOrderDetailStatus.PENDING);
        return detail;
    }

    default List<SalesPurchaseOrderDetail> toDetailEntities(String purchaseNo,
                                                             List<SalesPurchaseDetailDTO> dtoList) {
        return java.util.stream.IntStream.range(0, dtoList.size())
                .mapToObj(i -> {
                    SalesPurchaseDetailDTO d = dtoList.get(i);
                    return toDetailEntity(purchaseNo, i + 1, d.getProductCode(), d.getUnit(), d.getQty(), d.getConfirmedQty());
                })
                .toList();
    }

    default List<SalesPurchaseOrderDetail> toDetailEntitiesFromBranch(String purchaseNo,
                                                                       List<BranchProductList> branchList) {
        return java.util.stream.IntStream.range(0, branchList.size())
                .mapToObj(i -> {
                    BranchProductList p = branchList.get(i);
                    return toDetailEntity(purchaseNo, i + 1, p.getProductCode(), p.getUnit(), 0, 0);
                })
                .toList();
    }

    // ==================== CustomList 轉換 ====================

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "locationCode", ignore = true)
    SalesPurchaseList toListEntity(SalesPurchaseListDTO dto);

    SalesPurchaseListDTO toListDTO(SalesPurchaseList entity);

    List<SalesPurchaseListDTO> toListDTOList(List<SalesPurchaseList> entities);

    // ==================== 工具方法 ====================

    default String productKey(String productCode, String unit) {
        return productCode + "-" + unit;
    }
}
