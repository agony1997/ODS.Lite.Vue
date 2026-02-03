package com.example.mockodsvue.master.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductCategory {

    REFRIGERATED("REFRIGERATED", "冷藏"),
    NORMAL("NORMAL", "常溫");

    private final String code;
    private final String name;
}
