送貨單規格書 (SDO)
===
> 主要操作者：業務員
---

## 概述

業務員送貨給客戶的單據，記錄實際銷售、退貨、毀損。
系統於業務員領貨完成後，根據 CPO（客戶預訂單）自動產生 SDO。

---

## 作業流程

```
┌─────────────────────────────────────────────────────────────────────────┐
│  階段 1: 系統產生 SDO                                                    │
│  ───────────────────                                                    │
│  - 業務員領貨完成 (SRO.status = RECEIVED)                                │
│  - 系統根據 CPO 自動產生 SDO                                             │
│  - 預帶 CPO 的預訂品項                                                   │
│  - SDO.status = PENDING                                                 │
└─────────────────────────────────────────────────────────────────────────┘
        │
        ▼ 業務員出發送貨
┌─────────────────────────────────────────────────────────────────────────┐
│  階段 2: 送達客戶                                                        │
│  ─────────────                                                          │
│  - 業務員抵達客戶處                                                      │
│  - 確認送達，SDO.status = DELIVERED                                      │
│  - 記錄送達時間                                                          │
└─────────────────────────────────────────────────────────────────────────┘
        │
        ▼ 現場作業
┌─────────────────────────────────────────────────────────────────────────┐
│  階段 3: 交易處理                                                        │
│  ─────────────                                                          │
│  - 銷售：預訂品項 + 現場加購                                              │
│  - 退貨：客戶退回商品                                                    │
│  - 毀損：送貨過程中損壞                                                  │
│  - 調整 SDOD 明細                                                        │
└─────────────────────────────────────────────────────────────────────────┘
        │
        ▼ 客戶簽收
┌─────────────────────────────────────────────────────────────────────────┐
│  階段 4: 簽收完成                                                        │
│  ─────────────                                                          │
│  - 客戶確認簽收                                                          │
│  - SDO.status = SIGNED                                                  │
│  - 更新 CPO.status = DELIVERED                                          │
│  - 產生應收帳款 (AR)                                                     │
│  - 更新業務員儲位庫存                                                    │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 資料結構

### SalesDeliveryOrder (SDO 送貨單)

| 欄位 | 型別 | 說明 |
|------|------|------|
| deliveryNo | String | 送貨單號 (SDO + 流水號) |
| branchCode | String | 營業所代碼 |
| locationCode | String | 儲位代碼（業務員） |
| customerCode | String | 客戶代碼 |
| deliveryDate | LocalDate | 送貨日期 |
| status | String | 狀態 |
| deliveredAt | LocalDateTime | 送達時間 |
| signedAt | LocalDateTime | 簽收時間 |
| signedBy | String | 簽收人 |
| totalAmount | BigDecimal | 總金額 |
| createdAt | LocalDateTime | 建立時間 |

### SalesDeliveryOrderDetail (SDOD 送貨單明細)

| 欄位 | 型別 | 說明 |
|------|------|------|
| deliveryNo | String | 送貨單號 |
| itemNo | Integer | 項次 |
| preOrderNo | String | 來源預訂單 (nullable，現場加購為空) |
| productCode | String | 產品代碼 |
| batchNo | String | 批次號 |
| expiryDate | LocalDate | 效期 |
| qty | Integer | 數量 |
| type | String | 類型 (SALES/RETURN/DAMAGE) |
| unitPrice | BigDecimal | 單價 |
| amount | BigDecimal | 金額 |
| unit | String | 單位 |

---

## SDO 狀態說明

| 狀態 | 說明 |
|------|------|
| PENDING | 待送貨（已產生，尚未出發） |
| DELIVERED | 已送達（抵達客戶處） |
| SIGNED | 已簽收（客戶確認完成） |

---

## SDOD 類型說明

| 類型 | 說明 | 庫存影響 | 金額影響 |
|------|------|----------|----------|
| SALES | 銷售 | 業務員儲位 -qty | +金額 |
| RETURN | 退貨 | 業務員儲位 +qty | -金額 |
| DAMAGE | 毀損 | 業務員儲位 -qty | 無（損失） |

---

## 單據關係

```
SRO (領貨完成)
    │
    ▼ 系統自動產生
SDO ─┬─ SDOD (SALES, 來自 CPO 預訂)
     ├─ SDOD (SALES, 現場加購)
     ├─ SDOD (RETURN, 客戶退貨)
     └─ SDOD (DAMAGE, 毀損)
    │
    ▼ 簽收完成
AR (應收帳款) ─ 淨額 = 銷售 - 退貨
```

---

## 現場加購處理

業務員車上有多餘商品時，可現場加購：

1. 新增 SDOD (type = SALES, preOrderNo = null)
2. 選擇批次（從業務員儲位庫存）
3. 輸入數量、單價
4. 簽收時一併計入

---

## 臨時補貨處理

客戶臨時要更多，業務員車上不夠：

1. 業務員回營業所
2. 產生新的 SRO（補領貨）
3. 回到客戶處，新增 SDOD

---

## API 設計

| 方法 | 端點 | 說明 | 操作者 |
|------|------|------|--------|
| GET | /api/sales-delivery | 查詢送貨單清單 | 業務員 |
| GET | /api/sales-delivery/{deliveryNo} | 查詢單一送貨單 | 業務員 |
| GET | /api/sales-delivery/today | 查詢今日送貨清單 | 業務員 |
| PUT | /api/sales-delivery/{deliveryNo}/deliver | 確認送達 | 業務員 |
| PUT | /api/sales-delivery/{deliveryNo}/sign | 客戶簽收 | 業務員 |
| POST | /api/sales-delivery/{deliveryNo}/detail | 新增明細（加購/退貨/毀損） | 業務員 |
| PUT | /api/sales-delivery/{deliveryNo}/detail/{itemNo} | 更新明細 | 業務員 |

---

## 庫存影響

### 簽收完成後

| 類型 | 業務員儲位庫存 |
|------|----------------|
| SALES | 減少 |
| RETURN | 增加（需後續處理） |
| DAMAGE | 減少（記錄損失） |

---

## 相關規格書

- [客戶預訂單規格書](./CustomerPreOrder.md)
- [業務領貨單規格書](../allocation/SalesReceiveOrder.md)
- [應收帳款規格書](./AccountReceivable.md)

---
