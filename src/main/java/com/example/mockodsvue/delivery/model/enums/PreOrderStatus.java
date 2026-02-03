package com.example.mockodsvue.delivery.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PreOrderStatus {

    PENDING("PENDING", "待出貨"),
    DELIVERED("DELIVERED", "已出貨"),
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String name;
}
