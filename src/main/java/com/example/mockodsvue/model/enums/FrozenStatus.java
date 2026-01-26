package com.example.mockodsvue.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 營業所凍結單 (BPF) 狀態
 */
@Getter
@RequiredArgsConstructor
public enum FrozenStatus {

    FROZEN("FROZEN", "已凍結"),
    CONFIRMED("CONFIRMED", "已確認");

    private final String code;
    private final String name;
}
