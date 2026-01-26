業務員自訂產品清單規格書
===
> 主要操作者 : 業務員
---

業務員可為自己的儲位建立自訂產品清單，用於快速帶入常用產品與數量。
此清單與營業所產品清單 (BranchProductList) 為獨立的兩套系統。

---

> ### 與營業所產品清單的差異

| 項目 | 營業所產品清單 | 業務員自訂清單 |
|------|---------------|---------------|
| 層級 | 營業所 (branchCode) | 儲位 (locationCode) |
| 維護者 | 營業所管理員 | 業務員本人 |
| 包含數量 | 否 | 是 |
| 用途 | 新訂單預設範本 | 快速帶入常用品項與數量 |

---

> ### 一般操作功能

1. 查詢自訂產品清單
2. 儲存自訂產品清單 (整批覆蓋)

> ### 使用情境

1. 在訂貨畫面點選「帶入自訂清單」，將清單內容載入訂單
2. 在訂貨畫面點選「儲存為自訂清單」，將當前訂單內容存為自訂清單

---

> ### 表格欄位

- 排序 (sortOrder)
- 產品代碼 (productCode)
- 單位 (unit)
- 數量 (qty)

---

> ### 搜尋欄位

> 儲位

- 必填
- 由訂貨畫面帶入，不需另外選擇

---

> ### 驗證規則

1. 儲位必須存在
2. 同一儲位內，產品代碼 + 單位 不可重複

---

> ### API 端點

| Endpoint | Method | 功能 |
|----------|--------|------|
| `/api/purchase/sales/custom-list?locationCode=` | GET | 查詢自訂清單 |
| `/api/purchase/sales/custom-list?locationCode=` | PUT | 儲存自訂清單 |
| `/api/purchase/sales/load/custom?locationCode=&date=` | POST | 帶入自訂清單至訂單 |

---

> ### 表格

- SalesPurchaseList (業務員自訂產品清單)

---

> ### SalesPurchaseList 表格結構

| 欄位 | 型別 | 說明 |
|------|------|------|
| id | Integer | 主鍵 (自動產生) |
| locationCode | String(20) | 儲位代碼 |
| productCode | String(20) | 產品代碼 |
| unit | String(5) | 單位 |
| qty | Integer | 數量 |
| sortOrder | Integer | 排序順序 |

唯一鍵約束: (locationCode, productCode, unit)

---

> ### 相關規格書

- [業務員訂貨規格書](./SalesPurchase.md)
- [營業所產品排序規格書](../branch/BranchProductList.md)

---
