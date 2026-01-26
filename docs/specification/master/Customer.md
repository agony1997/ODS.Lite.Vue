客戶主檔規格書
===
---

## 概述

客戶基本資訊，用於預訂、送貨、應收帳款等流程。

---

## 資料結構

### Customer（客戶）

| 欄位 | 型別 | 說明 |
|------|------|------|
| customerCode | String | 客戶代碼 (PK) |
| customerName | String | 客戶名稱 |
| branchCode | String | 所屬營業所 |
| locationCode | String | 負責業務員儲位 |
| address | String | 地址 |
| phone | String | 電話 |
| contactPerson | String | 聯絡人 |
| status | String | 狀態 (ACTIVE / INACTIVE) |

---

## 單據關係

```
Customer
├── CPO (客戶預訂單)
├── SDO (送貨單)
└── AR (應收帳款)
```

---

## API 設計

| 方法 | 端點 | 說明 |
|------|------|------|
| GET | /api/customer | 查詢客戶清單 |
| GET | /api/customer/{customerCode} | 查詢單一客戶 |
| GET | /api/customer/by-location/{locationCode} | 查詢業務員負責的客戶 |

---

## 相關規格書

- [客戶預訂單規格書](../delivery/CustomerPreOrder.md)
- [送貨單規格書](../delivery/SalesDeliveryOrder.md)

---
