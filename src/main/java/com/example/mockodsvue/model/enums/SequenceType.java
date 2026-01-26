package com.example.mockodsvue.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SequenceType {

    // SalesPurchaseOrder
    SPO("SPO", "業務員訂貨單"),
    // BranchPurchaseFrozen
    BPF("BPF", "營業所凍結單"),
    // BranchPurchaseOrder
    BPO("BPO", "營業所訂貨單"),
    // FactoryDeliveryOrder
    FDO("FDO", "工廠出貨單"),
    // AllocationOrder
    AO("AO", "配貨單"),
    // SalesReceiveOrder
    SRO("SRO", "業務領貨單"),
    // CustomerPreOrder
    CPO("CPO", "客戶預訂單"),
    // SalesDeliveryOrder
    SDO("SDO", "送貨單"),
    // AccountReceivable
    AR("AR", "應收帳款"),
    // SalesKeepRecord
    SKR("SKR", "業務員寄庫單"),
    // SalesReturnRecord
    SRR("SRR", "業務員退庫單"),
    // BranchReturnOrder
    BRO("BRO", "營業所銷退單");

    private final String code;
    private final String name;
}
