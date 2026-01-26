工廠主檔規格書
===
---

## 概述

工廠基本資訊，用於產品對應、訂貨、銷退等流程。

---

## 資料結構

### Factory（工廠）

| 欄位 | 型別 | 說明 |
|------|------|------|
| factoryCode | String | 工廠代碼 (PK) |
| factoryName | String | 工廠名稱 |
| address | String | 地址 |
| phone | String | 電話 |
| status | String | 狀態 (ACTIVE / INACTIVE) |

---

## 單據關係

```
Factory
├── ProductFactory (產品對應)
├── BPO (營業所訂貨單，按工廠分)
├── FDO (工廠出貨單)
└── BRO (營業所銷退單，按工廠分)
```

---

## API 設計

| 方法 | 端點 | 說明 |
|------|------|------|
| GET | /api/factory | 查詢工廠清單 |
| GET | /api/factory/{factoryCode} | 查詢單一工廠 |

---

## 相關規格書

- [產品主檔規格書](./Product.md)

---
