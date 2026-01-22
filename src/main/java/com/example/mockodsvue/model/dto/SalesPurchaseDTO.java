package com.example.mockodsvue.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesPurchaseDTO {
    private String purchaseNo;
    private String branchCode;
    private String locationCode;
    private LocalDate purchaseDate;
    private String purchaseUser;
    private boolean isFrozen;
    private List<SalesPurchaseDetailDTO> details;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesPurchaseDetailDTO {
        private String purchaseNo;
        private int itemNo;
        private String productCode;
        private String productName;
        private String unit;
        private int qty;
        private int confirmQty;
        private int lastQty;
        private int sortOrder;
    }
}
