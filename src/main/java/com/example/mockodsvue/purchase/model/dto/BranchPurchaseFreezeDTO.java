package com.example.mockodsvue.purchase.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 凍結/解凍/確認請求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchPurchaseFreezeDTO {

    private String branchCode;
    private LocalDate purchaseDate;
}
