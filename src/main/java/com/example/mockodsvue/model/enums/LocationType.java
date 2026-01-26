package com.example.mockodsvue.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LocationType {

    WAREHOUSE("WAREHOUSE", "倉庫"),
    CAR("CAR", "車輛");

    private final String code;
    private final String name;
}
