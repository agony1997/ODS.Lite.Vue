package com.example.mockodsvue.model.dto;

import com.example.mockodsvue.model.enums.FrozenStatus;
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
    /**
     * 凍結狀態 (由 BPF 決定)
     * null: 未凍結 (開放)
     * FROZEN: 已凍結
     * CONFIRMED: 已確認
     */
    private FrozenStatus frozenStatus;
    private List<SalesPurchaseDetailDTO> details;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesPurchaseDetailDTO implements Sortable {
        private String purchaseNo;
        private int itemNo;
        private String productCode;
        private String productName;
        private String unit;
        private int qty;
        private int confirmedQty;
        private int lastQty;
        private int sortOrder;
    }
}
