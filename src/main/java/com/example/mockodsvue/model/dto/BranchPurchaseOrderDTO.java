package com.example.mockodsvue.model.dto;

import com.example.mockodsvue.model.enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 營業所訂貨單 (BPO) 清單結果 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchPurchaseOrderDTO {

    private String bpoNo;
    private String branchCode;
    private String factoryCode;
    private LocalDate purchaseDate;
    private DeliveryStatus status;
    private LocalDateTime createdAt;
    private String createdBy;
    private List<DetailDTO> details;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailDTO {
        private Integer itemNo;
        private String productCode;
        private String productName;
        private String unit;
        private Integer qty;
    }
}
