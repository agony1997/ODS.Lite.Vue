營業所產品排序規格書
===
> 主要操作者 : 營業所管理員、系統管理員
---

用於各營業所指定常用產品之排序，此功能會帶入在此系統內所有與營業所有關的地方。
預計會擴充至業務員也能使用類似功能(訂貨處)，但還不確定要放在此表格內還是獨立出去。

當業務員建立新訂單時，會自動帶入該營業所的產品排序清單作為預設產品。

---

> ### 一般操作功能

1. 查詢營業所產品清單
2. 新增產品到清單
3. 編輯產品排序 (拖曳排序)
4. 刪除產品
5. 批次刪除產品

> ### 特殊功能

1. 複製其他營業所的產品清單

---

> ### 表格欄位

- 排序 (sortOrder)
- 產品代碼 (productCode)
- 產品名稱 (productName)
- 單位 (unit)

---

> ### 搜尋欄位

> 營業所

- 必填
- 預設值: 有權限的營業所 (按代碼 ASC 排序取第一筆)
- 僅顯示啟用的營業所

---

> ### 驗證規則

1. 營業所必須存在且啟用
2. 同一營業所內，產品代碼 + 單位 不可重複
3. 新增/更新/刪除時，產品清單不可為空

---

> ### API 端點

| Endpoint | Method | 功能 |
|----------|--------|------|
| `/api/branch-product-list/{branchCode}` | GET | 查詢產品清單 |
| `/api/branch-product-list/{branchCode}` | PUT | 儲存產品清單 (全量替換) |
| `/api/branch-product-list/copy?from=&to=` | POST | 複製到其他營業所 |
| `/api/branches` | GET | 取得啟用的營業所清單 |

> 注意：採用全量替換模式，前端操作完成後一次送出整個清單，後端整批儲存。

---

> ### 表格

- Branch (營業所)
- BranchProductList (營業所產品排序)
- Location (儲位)

---

> ### BranchProductList 表格結構

| 欄位 | 型別 | 說明 |
|------|------|------|
| id | Integer | 主鍵 (自動產生) |
| branchCode | String(20) | 營業所代碼 |
| productCode | String(20) | 產品代碼 |
| productName | String(40) | 產品名稱 |
| unit | String(5) | 單位 |
| sortOrder | Integer | 排序順序 |

唯一鍵約束: (branchCode, productCode, unit)

---

> ### 相關規格書

- [業務員訂貨規格書](../purchase/SalesPurchase.md)
- [業務員自訂產品清單規格書](../purchase/SalesPurchaseList.md)

---
