package com.example.mockodsvue.allocation.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AllocationStatus {

    PENDING("PENDING", "待領取"),
    RECEIVED("RECEIVED", "已領取");

    private final String code;
    private final String name;
}
