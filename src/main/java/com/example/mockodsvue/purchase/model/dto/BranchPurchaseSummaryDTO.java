package com.example.mockodsvue.purchase.model.dto;

import com.example.mockodsvue.purchase.model.enums.FrozenStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 營業所訂貨彙總查詢結果 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchPurchaseSummaryDTO {

    private String branchCode;
    private LocalDate purchaseDate;

    /**
     * 凍結狀態
     * null: 開放中
     * FROZEN: 已凍結
     * CONFIRMED: 已確認
     */
    private FrozenStatus frozenStatus;

    /**
     * 儲位資訊清單 (用於表頭顯示)
     */
    private List<LocationInfoDTO> locations;

    /**
     * 彙總明細 (每個產品一行)
     */
    private List<SummaryDetailDTO> details;

    /**
     * 儲位資訊
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationInfoDTO {
        private String locationCode;
        private String locationName;
    }

    /**
     * 彙總明細
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryDetailDTO {
        private String productCode;
        private String productName;
        private String unit;

        /**
         * 確認數量 (組長可調整)
         */
        private int confirmedQty;

        /**
         * 原始訂購數量總和
         */
        private int totalQty;

        /**
         * 增減數量 (confirmedQty - totalQty)
         */
        private int diffQty;

        /**
         * 各儲位訂購數量 (橫向展開)
         * key: locationCode
         * value: 該儲位的訂購數量
         */
        private Map<String, Integer> locationQtyMap;
    }
}
