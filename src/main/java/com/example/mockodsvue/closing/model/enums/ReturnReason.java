package com.example.mockodsvue.closing.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReturnReason {

    STALE("STALE", "過期"),
    DAMAGED("DAMAGED", "損壞"),
    RECALLED("RECALLED", "召回");

    private final String code;
    private final String name;
}
