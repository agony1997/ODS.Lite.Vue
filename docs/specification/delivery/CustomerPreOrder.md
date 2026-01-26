客戶預訂單規格書 (CPO)
===
> 主要操作者：業務員
---

## 概述

客戶提前向業務員訂貨，記錄預訂內容。
業務員參考 CPO 建立 SPO（業務員訂貨單），確保有足夠庫存送貨。

---

## 作業流程

```
┌─────────────────────────────────────────────────────────────────────────┐
│  階段 1: 客戶預訂 (D-2)                                                  │
│  ─────────────────                                                      │
│  - 客戶向業務員預訂（電話/現場/線上）                                     │
│  - 業務員建立 CPO                                                        │
│  - 設定預計送貨日期 (deliveryDate)                                       │
└─────────────────────────────────────────────────────────────────────────┘
        │
        ▼ 業務員訂貨
┌─────────────────────────────────────────────────────────────────────────┐
│  階段 2: 業務員建立 SPO                                                  │
│  ─────────────────────                                                  │
│  - 參考 CPO 預訂總量                                                     │
│  - 可額外多訂備用                                                        │
│  - SPO.qty = CPO 總量 + 備貨                                             │
└─────────────────────────────────────────────────────────────────────────┘
        │
        ▼ D 日領貨完成
┌─────────────────────────────────────────────────────────────────────────┐
│  階段 3: 系統產生 SDO                                                    │
│  ───────────────────                                                    │
│  - 根據 CPO 自動產生 SDO                                                 │
│  - CPO.status = PENDING → SDO 待送貨                                    │
└─────────────────────────────────────────────────────────────────────────┘
        │
        ▼ 送貨完成
┌─────────────────────────────────────────────────────────────────────────┐
│  階段 4: 更新狀態                                                        │
│  ─────────────                                                          │
│  - SDO 簽收完成                                                          │
│  - CPO.status = DELIVERED                                               │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 資料結構

### CustomerPreOrder (CPO 客戶預訂單)

| 欄位 | 型別 | 說明 |
|------|------|------|
| preOrderNo | String | 預訂單號 (CPO + 流水號) |
| branchCode | String | 營業所代碼 |
| locationCode | String | 儲位代碼（業務員） |
| customerCode | String | 客戶代碼 |
| orderDate | LocalDate | 訂貨日期 |
| deliveryDate | LocalDate | 預計送貨日期 |
| status | String | 狀態 |
| createdAt | LocalDateTime | 建立時間 |
| createdBy | String | 建立人員 |

### CustomerPreOrderDetail (CPOD 客戶預訂單明細)

| 欄位 | 型別 | 說明 |
|------|------|------|
| preOrderNo | String | 預訂單號 |
| itemNo | Integer | 項次 |
| productCode | String | 產品代碼 |
| qty | Integer | 預訂數量 |
| unit | String | 單位 |

---

## CPO 狀態說明

| 狀態 | 說明 |
|------|------|
| PENDING | 待送貨（已預訂，尚未送達） |
| DELIVERED | 已送達（SDO 簽收完成） |
| CANCELLED | 已取消 |

---

## 單據關係

```
客戶 A 預訂 ─── CPO-A ──┐
客戶 B 預訂 ─── CPO-B ──┼──► 業務員 S 參考 ──► SPO
客戶 C 預訂 ─── CPO-C ──┘

D 日領貨完成後：
CPO-A ──► SDO-A (送給客戶 A)
CPO-B ──► SDO-B (送給客戶 B)
CPO-C ──► SDO-C (送給客戶 C)
```

---

## API 設計

| 方法 | 端點 | 說明 | 操作者 |
|------|------|------|--------|
| GET | /api/customer-pre-order | 查詢預訂單清單 | 業務員 |
| GET | /api/customer-pre-order/{preOrderNo} | 查詢單一預訂單 | 業務員 |
| POST | /api/customer-pre-order | 建立預訂單 | 業務員 |
| PUT | /api/customer-pre-order/{preOrderNo} | 更新預訂單 | 業務員 |
| DELETE | /api/customer-pre-order/{preOrderNo} | 取消預訂單 | 業務員 |
| GET | /api/customer-pre-order/summary | 查詢預訂彙總（供 SPO 參考） | 業務員 |

---

## 相關規格書

- [送貨單規格書](./SalesDeliveryOrder.md)
- [業務員訂貨規格書](../purchase/SalesPurchase.md)

---
