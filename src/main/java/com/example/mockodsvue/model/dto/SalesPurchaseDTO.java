package com.example.mockodsvue.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SalesPurchaseDTO {
    private LocalDate purchaseDate;
    private String purchaseNo;
    private String branchCode;
    private String locationCode;
    private boolean isFreeze;

    @Data
    public static class SalesPurchaseDetailDTO {
        private String purchaseNo;
        private int itemNo;
        private String productCode;
        private String unit;
        private int qty;
        private int confirmQty;
        private int sortOrder;
    }
}
