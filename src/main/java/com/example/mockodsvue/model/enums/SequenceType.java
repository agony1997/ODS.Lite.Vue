package com.example.mockodsvue.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SequenceType {

    SPO("SPO", "業務員訂貨單"),
    BPO("BPO", "營業所訂貨單"),
    FSO("FSO", "工廠出貨單"),
    DSO("DSO", "配送單"),
    ADJ("ADJ", "庫存調整單");

    private final String code;
    private final String name;
}
