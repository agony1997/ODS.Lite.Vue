產品主檔規格書
===
---

## 概述

產品主檔及相關資料表，包含產品基本資訊、工廠對應、單位轉換。

---

## 資料結構

### Product（產品）

| 欄位 | 型別 | 說明 |
|------|------|------|
| productCode | String | 產品代碼 (PK) |
| productName | String | 產品名稱 |
| category | String | 分類 (REFRIGERATED / NORMAL) |
| baseUnit | String | 基本單位 |
| basePrice | BigDecimal | 基本單價 |
| status | String | 狀態 (ACTIVE / INACTIVE) |

### 產品分類

| 代碼 | 說明 |
|------|------|
| REFRIGERATED | 冷藏 |
| NORMAL | 常溫 |

---

### ProductFactory（產品工廠對應）

產品可能來自多個工廠，採用獨立對應表：

| 欄位 | 型別 | 說明 |
|------|------|------|
| id | Long | 主鍵 |
| productCode | String | 產品代碼 |
| factoryCode | String | 工廠代碼 |
| isDefault | Boolean | 是否為預設工廠 |

唯一鍵：(productCode, factoryCode)

```
Product ──< ProductFactory >── Factory
```

---

### ProductUnitConversion（產品單位轉換）

| 欄位 | 型別 | 說明 |
|------|------|------|
| id | Long | 主鍵 |
| productCode | String | 產品代碼 |
| fromUnit | String | 來源單位 |
| toUnit | String | 目標單位 |
| conversionRate | BigDecimal | 轉換率 |

唯一鍵：(productCode, fromUnit, toUnit)

#### 範例

| productCode | fromUnit | toUnit | conversionRate |
|-------------|----------|--------|----------------|
| P001 | 箱 | 個 | 12 |
| P001 | 個 | 箱 | 0.0833 |

---

## API 設計

| 方法 | 端點 | 說明 |
|------|------|------|
| GET | /api/product | 查詢產品清單 |
| GET | /api/product/{productCode} | 查詢單一產品 |
| GET | /api/product/{productCode}/factories | 查詢產品對應工廠 |
| GET | /api/product/{productCode}/units | 查詢產品單位轉換 |

---

## 相關規格書

- [工廠主檔規格書](./Factory.md)
- [庫存規格書](./Inventory.md)

---
