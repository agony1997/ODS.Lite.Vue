庫存規格書
===
---

## 概述

庫存採用餘額表設計，記錄各儲位的產品庫存數量。
支援批次、效期管理，配貨時依 FIFO（先進先出）原則分配。

---

## 儲位結構

```
營業所 (Branch)
├── 大庫 (locationCode = branchCode)
│   ├── qty: 可用數量
│   ├── keepQty: 寄庫數量
│   └── returnQty: 待退庫數量
│
└── 業務員儲位 (locationCode = S001, S002...)
    └── qty: 車存數量
```

### 儲位類型

| 類型 | 說明 | locationCode |
|------|------|--------------|
| WAREHOUSE | 大庫 | = branchCode（例如 1000） |
| CAR | 業務員車存 | 業務員儲位代碼（例如 S001） |

---

## 資料結構

### Inventory（庫存）

| 欄位 | 型別 | 說明 |
|------|------|------|
| id | Long | 主鍵 |
| branchCode | String | 營業所代碼 |
| locationCode | String | 儲位代碼 |
| locationType | String | 儲位類型 (WAREHOUSE / CAR) |
| productCode | String | 產品代碼 |
| batchNo | String | 批次號 |
| expiryDate | LocalDate | 效期 |
| qty | Integer | 可用數量 |
| keepQty | Integer | 寄庫數量（僅 WAREHOUSE） |
| returnQty | Integer | 待退庫數量（僅 WAREHOUSE） |

唯一鍵：(branchCode, locationCode, productCode, batchNo)

### 欄位說明

| 欄位 | WAREHOUSE | CAR |
|------|-----------|-----|
| qty | 可用庫存 | 車存數量 |
| keepQty | 業務員寄庫的貨 | 固定 0 |
| returnQty | 待退回工廠的貨 | 固定 0 |

---

## 庫存異動

### 異動時機

| 作業 | 單據 | 來源 | 目標 | 變化 |
|------|------|------|------|------|
| 收貨 | FDO | - | 大庫 | qty + |
| 配貨 | AO | 大庫 | - | qty - |
| 領貨 | SRO | - | 業務員 | qty + |
| 銷售 | SDO (SALES) | 業務員 | - | qty - |
| 客戶退貨 | SDO (RETURN) | - | 業務員 | qty + |
| 寄庫 | SKR | 業務員 | 大庫 | 業務員 qty -, 大庫 keepQty + |
| 領回寄庫 | SRO | 大庫 | 業務員 | 大庫 keepQty -, 業務員 qty + |
| 退庫 | SRR | 業務員 | 大庫 | 業務員 qty -, 大庫 returnQty + |
| 銷退送出 | BRO | 大庫 | - | returnQty - |

### 異動流程圖

```
FDO 收貨
    │
    ▼
大庫 (qty)
    │
    ├── AO 配貨 ──► SRO 領貨 ──► 業務員 (qty)
    │                               │
    │                               ├── SDO 銷售 ──► 客戶
    │                               │
    │                               ├── SDO 退貨 ◄── 客戶
    │                               │
    │                               ├── SKR 寄庫 ──► 大庫 (keepQty)
    │                               │                    │
    │                               │               SRO 領回 ◄─┘
    │                               │
    │                               └── SRR 退庫 ──► 大庫 (returnQty)
    │                                                    │
    │                                               BRO 送出 ──► 工廠
```

---

## FIFO 配貨規則

配貨時依以下順序分配：

1. **效期優先**：效期近的先出
2. **業務員優先度**：等級高的先分配到效期較長的批次
3. **批次號**：相同效期時，批號小的先出

```sql
ORDER BY expiryDate ASC, batchNo ASC
```

---

## API 設計

| 方法 | 端點 | 說明 |
|------|------|------|
| GET | /api/inventory | 查詢庫存清單 |
| GET | /api/inventory/warehouse/{branchCode} | 查詢大庫庫存 |
| GET | /api/inventory/location/{locationCode} | 查詢儲位庫存 |
| GET | /api/inventory/product/{productCode} | 查詢產品庫存分布 |

---

## 設計考量

### 為何採用餘額表？

| 方案 | 優點 | 缺點 |
|------|------|------|
| 餘額表 ✓ | 查詢快、結構簡單 | 需維護同步 |
| 交易記錄 | 資料正確性高 | 查詢慢 |

採用餘額表，但每筆異動都有對應單據（FDO/SRO/SDO/SKR/SRR/BRO），可追蹤來源。

### 為何分 qty / keepQty / returnQty？

- **qty**：可配貨、可銷售的正常庫存
- **keepQty**：業務員寄庫的貨，隔天要領回，不能配給別人
- **returnQty**：待退回工廠的報廢品，不能使用

---

## 相關規格書

- [產品主檔規格書](./Product.md)
- [配貨單規格書](../allocation/AllocationOrder.md)
- [業務員寄庫單規格書](../closing/SalesKeepRecord.md)
- [業務員退庫單規格書](../closing/SalesReturnRecord.md)

---
