配貨單規格書 (AO)
===
> 主要操作者：庫務
---

## 概述

庫務根據業務員訂貨明細 (SPOD) 進行配貨，從大庫庫存中分配產品給各業務員。
配貨時依 FIFO（先進先出）原則分配批次，並考慮業務員優先等級。

---

## 作業流程

```
┌─────────────────────────────────────────────────────────────────────────┐
│  階段 1: 收貨入庫                                                        │
│  ─────────────                                                          │
│  - FDO 確認收貨                                                          │
│  - 貨品入大庫（含批次、效期）                                             │
└─────────────────────────────────────────────────────────────────────────┘
        │
        ▼ 庫務執行配貨
┌─────────────────────────────────────────────────────────────────────────┐
│  階段 2: 配貨                                                            │
│  ─────────                                                              │
│  1. 查詢待配貨的 SPOD (BPF.status = CONFIRMED)                           │
│  2. 依 FIFO + 業務員優先度分配批次                                        │
│  3. 產生 AO + AOD                                                        │
│  4. AOD.status = PENDING                                                │
│  5. 大庫庫存扣減（預留）                                                  │
└─────────────────────────────────────────────────────────────────────────┘
        │
        ▼ 等待業務員領貨
┌─────────────────────────────────────────────────────────────────────────┐
│  階段 3: 領貨                                                            │
│  ─────────                                                              │
│  - 業務員確認領取                                                        │
│  - 更新 AOD.status = RECEIVED                                           │
│  - 詳見 SalesReceiveOrder.md                                            │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 配貨規則

### FIFO + 優先度

1. 優先等級高的業務員先分配到效期較長的批次
2. 相同優先等級的業務員依 FIFO 分配
3. 庫存不足時，低優先等級的業務員可能分配不到（allocatedQty = 0）

### 優先度設定

優先度透過 `SalesPriority` 資料表設定：

| 欄位 | 型別 | 說明 |
|------|------|------|
| id | Long | 主鍵 |
| branchCode | String | 營業所代碼 |
| locationCode | String | 儲位代碼 |
| priorityLevel | Integer | 優先等級 (1=最高) |

唯一鍵：(branchCode, locationCode)

---

## 資料結構

### AllocationOrder (AO 配貨單)

| 欄位 | 型別 | 說明 |
|------|------|------|
| allocationNo | String | 配貨單號 (AO + 流水號) |
| branchCode | String | 營業所代碼 |
| allocationDate | LocalDate | 配貨日期 |
| createdAt | LocalDateTime | 建立時間 |
| createdBy | String | 建立人員 |

> 粒度：營業所 + 日期，可多張（多次配貨產生多張）

### AllocationOrderDetail (AOD 配貨單明細)

| 欄位 | 型別 | 說明 |
|------|------|------|
| allocationNo | String | 配貨單號 |
| itemNo | Integer | 項次 |
| locationCode | String | 目標儲位（業務員） |
| productCode | String | 產品代碼 |
| batchNo | String | 批次號 |
| expiryDate | LocalDate | 效期 |
| requestedQty | Integer | 預定數量 (來自 SPOD.confirmedQty) |
| allocatedQty | Integer | 實際配貨數量 (可能為 0) |
| status | String | 狀態 (PENDING/RECEIVED) |

---

## AOD 狀態說明

| 狀態 | 說明 |
|------|------|
| PENDING | 待領取（已配貨，等待業務員領取） |
| RECEIVED | 已領取（業務員已確認領貨） |

---

## 單據關係

```
SPOD (業務員訂貨明細)
    │
    ▼ 庫務配貨
AO ─┬─ AOD (產品A, 批次B1, 給業務員S1)
    ├─ AOD (產品A, 批次B2, 給業務員S2)
    └─ AOD (產品B, 批次B3, 給業務員S1)
    │
    ▼ 業務員領貨
SRO (業務員 S1 領貨單)
├── SROD ← AOD (產品A, 批次B1)
└── SROD ← AOD (產品B, 批次B3)
```

---

## API 設計

| 方法 | 端點 | 說明 | 操作者 |
|------|------|------|--------|
| GET | /api/allocation | 查詢配貨單清單 | 庫務 |
| GET | /api/allocation/{allocationNo} | 查詢單一配貨單明細 | 庫務 |
| POST | /api/allocation | 執行配貨 | 庫務 |
| GET | /api/allocation/pending | 查詢待配貨的 SPOD | 庫務 |

---

## 相關規格書

- [業務領貨單規格書](./SalesReceiveOrder.md)
- [業務員訂貨規格書](../purchase/SalesPurchase.md)
- [工廠出貨單規格書](../receive/FactoryDeliveryOrder.md)

---
