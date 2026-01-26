業務員訂貨規格書
===
> 主要操作者 : 業務員，庫務，組長
---

營業員向營業所訂貨，只可以建立兩天以後的訂單。
營業員的上司有權限可代為訂貨。

下訂表格內可預帶產品資料，並可以新增產品。
預帶產品資料來自於每個營業所管理的預設產品清單與排序。

此訂單每儲位每天只能有一筆。
不可自行刪除訂單。

在查詢時如果沒有訂單，系統自動建立該人訂單。

---

> ### 一般操作功能

1. 查詢
2. 新增產品
3. 編輯每行產品之數量
4. 儲存 (更新)

> ### 特殊功能

1. 帶入上次產品清單與數量至表格
2. 帶入自定義產品清單與數量至表格
3. 帶入營業所產品清單至表格
4. 建立自定義產品清單

---

> ### 表格欄位

- 產品代碼
- 產品名稱
- 單位
- 訂購數量 (qty)
- 確認數量 (confirmedQty) - 組長調整用
- 上次訂購數量

---

> ### 搜尋欄位

當選項有多筆時，預設值按代碼ASC排序取第一筆。

> 營業所

- 必填
- 業務員預設值: 所屬營業所。
- 高權限預設值: 有權限的營業所。

> 儲位

- 必填
- 業務員預設值: 所擁有儲位。
- 高權限預設值: 選擇之營業所下的所有儲位，不含營業所本身儲位。

> 日期

- 必填
- 預設值: 後天
- 不可選擇非法日期
- 可選區間為 7 天, 即 D+2 ~ D+9

---

> ### 編輯權限控制

編輯權限由 BPF (營業所凍結單) 控制：

| BPF 狀態 | 業務員 | 組長 |
|----------|--------|------|
| 不存在 | ✅ 可編輯 qty | - |
| FROZEN | ❌ 不可編輯 | ✅ 可編輯 confirmedQty |
| CONFIRMED | ❌ 不可編輯 | ❌ 不可編輯 |

---

> ### 表格

- SalesPurchaseOrder (業務員訂貨單)
- SalesPurchaseOrderDetail (業務員訂貨單明細)

---

> ### SalesPurchaseOrder 表格結構

| 欄位 | 型別 | 說明 |
|------|------|------|
| purchaseNo | String | 訂貨單號 (SPO + 流水號) |
| branchCode | String | 營業所代碼 |
| locationCode | String | 儲位代碼 |
| purchaseDate | LocalDate | 訂貨日期 |
| createdAt | LocalDateTime | 建立時間 |
| updatedAt | LocalDateTime | 更新時間 |

> 注意：SPO 本身無狀態欄位，狀態由 BPF 和 SPOD 控制。

---

> ### SalesPurchaseOrderDetail 表格結構

| 欄位 | 型別 | 說明 |
|------|------|------|
| id | Long | 主鍵 |
| purchaseNo | String | 訂貨單號 |
| itemNo | Integer | 項次 |
| productCode | String | 產品代碼 |
| productName | String | 產品名稱 |
| unit | String | 單位 |
| qty | Integer | 訂購數量 (業務員填寫) |
| confirmedQty | Integer | 確認數量 (組長調整，預設 = qty) |
| lastQty | Integer | 上次訂購數量 |
| status | String | 狀態 (PENDING / AGGREGATED) |
| sortOrder | Integer | 排序 |

---

> ### SPOD 狀態說明

| 狀態 | 說明 |
|------|------|
| PENDING | 待彙總（預設） |
| AGGREGATED | 已被庫務彙總進 BPO |

---

> ### 設計考量

#### 為何 SPO 無狀態欄位？

1. **狀態粒度正確**：彙總是發生在明細層級（SPOD → BPOD），同一 SPO 的明細可能分進不同 BPO（按工廠分）
2. **避免同步問題**：若 SPO 和 SPOD 都有狀態，需維護兩處且可能不一致
3. **設計簡潔**：SPO 整體狀態可透過聚合 SPOD 狀態得知

#### 為何用 BPF 控制編輯權限而非 SPO 狀態？

1. **凍結粒度為營業所+日期**：組長一次凍結整個營業所當天的所有訂單
2. **快速查詢**：判斷是否可編輯只需查 BPF 一筆記錄
3. **權責分離**：BPF 控制編輯權限，SPOD 狀態追蹤彙總進度

#### qty 與 confirmedQty 的關係

- `qty`：業務員原始訂購數量
- `confirmedQty`：組長確認後的數量，預設等於 qty
- 保留兩個欄位可供報表分析調整差異

---

> ### 相關規格書

- [營業所訂貨規格書](./BranchPurchase.md)
- [業務員自訂產品清單規格書](./SalesPurchaseList.md)
- [營業所產品排序規格書](../branch/BranchProductList.md)

---
