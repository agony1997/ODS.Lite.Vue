package com.example.mockodsvue.closing.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BranchReturnStatus {

    PENDING("PENDING", "待退貨"),
    RETURNED("RETURNED", "已退貨");

    private final String code;
    private final String name;
}
