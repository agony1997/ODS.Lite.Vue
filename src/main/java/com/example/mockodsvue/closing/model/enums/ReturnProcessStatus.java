package com.example.mockodsvue.closing.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReturnProcessStatus {

    PENDING("PENDING", "待處理"),
    PROCESSED("PROCESSED", "已處理");

    private final String code;
    private final String name;
}
