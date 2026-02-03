package com.example.mockodsvue.delivery.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {

    PENDING("PENDING", "待付款"),
    PARTIAL("PARTIAL", "部分付款"),
    PAID("PAID", "已付款"),
    OVERDUE("OVERDUE", "逾期");

    private final String code;
    private final String name;
}
