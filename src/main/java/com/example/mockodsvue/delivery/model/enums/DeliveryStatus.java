package com.example.mockodsvue.delivery.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 工廠出貨單 (FDO) 狀態
 */
@Getter
@RequiredArgsConstructor
public enum DeliveryStatus {

    PENDING("PENDING", "待收貨"),
    RECEIVED("RECEIVED", "已收貨"),
    DISCREPANCY("DISCREPANCY", "有差異");

    private final String code;
    private final String name;
}
