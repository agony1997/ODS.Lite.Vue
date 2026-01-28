package com.example.mockodsvue.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 更新營業所訂貨確認數量請求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchPurchaseSummaryUpdateDTO {

    private String branchCode;
    private LocalDate purchaseDate;
    private List<UpdateDetailDTO> details;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDetailDTO {
        private String productCode;
        private String unit;
        private int confirmedQty;
    }
}
