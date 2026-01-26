營業所主檔規格書
===
---

## 概述

營業所基本資訊，是系統中的主要組織單位。

---

## 資料結構

### Branch（營業所）

| 欄位 | 型別 | 說明 |
|------|------|------|
| branchCode | String | 營業所代碼 (PK) |
| branchName | String | 營業所名稱 |
| address | String | 地址 |
| phone | String | 電話 |
| status | String | 狀態 (ACTIVE / INACTIVE) |

---

## 關聯結構

```
Branch
├── Location (儲位，含大庫和業務員車存)
├── User (所屬人員)
├── Customer (負責客戶)
├── Inventory (庫存)
└── 各類單據 (SPO, BPO, FDO, AO, SDO, SKR, SRR, BRO...)
```

---

## API 設計

| 方法 | 端點 | 說明 |
|------|------|------|
| GET | /api/branch | 查詢營業所清單 |
| GET | /api/branch/{branchCode} | 查詢單一營業所 |

---

## 相關規格書

- [儲位主檔規格書](./Location.md)
- [使用者規格書](./User.md)

---
