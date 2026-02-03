package com.example.mockodsvue.purchase.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 業務員訂貨單明細 (SPOD) 狀態
 */
@Getter
@RequiredArgsConstructor
public enum SalesOrderDetailStatus {

    PENDING("PENDING", "待彙總"),
    AGGREGATED("AGGREGATED", "已彙總");

    private final String code;
    private final String name;
}
