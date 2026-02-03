package com.example.mockodsvue.closing.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KeepStatus {

    KEPT("KEPT", "寄庫中"),
    RETRIEVED("RETRIEVED", "已取回");

    private final String code;
    private final String name;
}
