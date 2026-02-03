package com.example.mockodsvue.delivery.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryOrderStatus {

    PENDING("PENDING", "待送達"),
    DELIVERED("DELIVERED", "已送達"),
    SIGNED("SIGNED", "已簽收");

    private final String code;
    private final String name;
}
