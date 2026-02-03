package com.example.mockodsvue.delivery.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryDetailType {

    SALES("SALES", "銷售"),
    RETURN("RETURN", "退貨"),
    DAMAGE("DAMAGE", "損耗");

    private final String code;
    private final String name;
}
