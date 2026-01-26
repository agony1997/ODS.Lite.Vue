營業所訂貨規格書
===
> 主要操作者 : 庫務，組長
---

營業所彙總所屬營業員的訂單後，統一向工廠端訂貨。
可在此時強迫調整各營務員的實際訂購數量。

只可以編輯、凍結和彙總後天的訂單。

---

## 作業流程

```
階段 1: 開放 (BPF 不存在)
├── 業務員可編輯 SPO
│
▼ 組長執行凍結
階段 2: 凍結 (BPF.status = FROZEN)
├── 業務員不可編輯
├── 組長可調整 confirmedQty
│
▼ 組長確認完成
階段 3: 確認 (BPF.status = CONFIRMED)
├── 組長不可再編輯
├── 等待庫務彙總
│
▼ 庫務建立 BPO
階段 4: 彙總完成
├── SPOD.status = AGGREGATED
└── BPO 送出給工廠
```

---

> ### 組長彙總頁面功能

1. 查詢（列出營業所下所有儲位的訂購）
2. 新增產品
3. 編輯每行產品之確認數量 (confirmedQty)
4. 儲存 (更新)

> ### 特殊功能

1. 凍結營業所訂單（業務員不可再修改）
2. 確認完成（組長也不可再修改）
3. 解除凍結（在確認前可解除）

---

> ### 表格欄位

- 產品代碼
- 產品名稱
- 單位
- 確認數量 (confirmedQty)
- 原始訂購數量 (qty)
- 增減數量 (confirmedQty - qty)
- 橫向動態展開的儲位欄位

---

> ### 搜尋欄位

當選項有多筆時，預設值按代碼ASC排序取第一筆。

> 營業所
- 必填
- 預設值: 有權限的營業所。

> 日期
- 必填
- 預設值: 後天

---

> ### 表格

- SalesPurchaseOrder (業務員訂貨單)
- SalesPurchaseOrderDetail (業務員訂貨單明細)
- BranchPurchaseFrozen (營業所凍結單)
- BranchPurchaseOrder (營業所訂貨單)
- BranchPurchaseOrderDetail (營業所訂貨單明細)

---

> ### BranchPurchaseFrozen (BPF) 表格結構

| 欄位 | 型別 | 說明 |
|------|------|------|
| id | Long | 主鍵 |
| branchCode | String | 營業所代碼 |
| purchaseDate | LocalDate | 訂貨日期 |
| status | String | 狀態 (FROZEN / CONFIRMED) |
| frozenAt | LocalDateTime | 凍結時間 |
| frozenBy | String | 凍結人員工編 |
| confirmedAt | LocalDateTime | 確認時間 |
| confirmedBy | String | 確認人員工編 |

唯一鍵約束: (branchCode, purchaseDate)

---

> ### BPF 狀態說明

| 狀態 | 說明 | 業務員 | 組長 |
|------|------|--------|------|
| (不存在) | 開放中 | ✅ 可編輯 | - |
| FROZEN | 已凍結 | ❌ 不可編輯 | ✅ 可調整 confirmedQty |
| CONFIRMED | 已確認 | ❌ 不可編輯 | ❌ 不可編輯 |

---

> ### BranchPurchaseOrder (BPO) 表格結構

| 欄位 | 型別 | 說明 |
|------|------|------|
| purchaseNo | String | 訂貨單號 (BPO + 流水號) |
| branchCode | String | 營業所代碼 |
| factoryCode | String | 工廠代碼 |
| purchaseDate | LocalDate | 訂貨日期 |
| status | String | 狀態 |
| createdAt | LocalDateTime | 建立時間 |
| createdBy | String | 建立人員工編 |

唯一鍵約束: (branchCode, factoryCode, purchaseDate)

> 注意：BPO 按工廠分，同一營業所同一天可能產生多張 BPO。

---

> ### BranchPurchaseOrderDetail (BPOD) 表格結構

| 欄位 | 型別 | 說明 |
|------|------|------|
| id | Long | 主鍵 |
| purchaseNo | String | 訂貨單號 (BPO) |
| itemNo | Integer | 項次 |
| productCode | String | 產品代碼 |
| productName | String | 產品名稱 |
| unit | String | 單位 |
| qty | Integer | 訂購數量 (各 SPOD.confirmedQty 加總) |

---

> ### 庫務彙總作業

庫務根據已確認 (BPF.status = CONFIRMED) 的 SPO 明細建立 BPO：

1. 查詢該營業所當天所有 SPOD (status = PENDING)
2. 依產品對應的工廠分組
3. 每個工廠產生一張 BPO
4. BPOD.qty = 各 SPOD.confirmedQty 加總
5. 更新 SPOD.status = AGGREGATED

```
SPO-1 ─┬─ SPOD (產品A, 工廠1) ──┐
       └─ SPOD (產品B, 工廠1) ──┼──► BPO-1 (工廠1)
SPO-2 ─┬─ SPOD (產品A, 工廠1) ──┘
       └─ SPOD (產品C, 工廠2) ──────► BPO-2 (工廠2)
```

---

> ### API 端點

| Endpoint | Method | 功能 | 操作者 |
|----------|--------|------|--------|
| `/api/purchase/branch/summary` | GET | 查詢營業所彙總資料 | 組長 |
| `/api/purchase/branch/summary` | PUT | 更新確認數量 | 組長 |
| `/api/purchase/branch/freeze` | POST | 凍結營業所 | 組長 |
| `/api/purchase/branch/unfreeze` | POST | 解除凍結 | 組長 |
| `/api/purchase/branch/confirm` | POST | 確認完成 | 組長 |
| `/api/purchase/branch/aggregate` | POST | 執行彙總建立 BPO | 庫務 |
| `/api/purchase/branch/bpo` | GET | 查詢 BPO 清單 | 庫務 |

---

> ### 設計考量

#### 為何需要 BPF？

1. **凍結粒度為營業所+日期**：一次凍結整個營業所當天的所有 SPO
2. **快速查詢**：判斷是否可編輯只需查 BPF 一筆記錄，而非遍歷所有 SPO
3. **審計軌跡**：記錄誰在何時執行凍結/確認

#### 為何 BPO 按工廠分？

1. **業務事實**：不同產品來自不同工廠，需分別向各工廠訂貨
2. **後續流程**：工廠出貨 (FDO) 也是按工廠產生
3. **追蹤方便**：可獨立追蹤各工廠的訂貨狀態

#### 為何 SPOD 有狀態而非 SPO？

1. **狀態粒度正確**：同一 SPO 的明細可能分進不同 BPO（按工廠分）
2. **避免同步問題**：只維護一處狀態，不會不一致
3. **可追蹤進度**：知道哪些明細已彙總、哪些還沒

---

> ### 相關規格書

- [業務員訂貨規格書](./SalesPurchase.md)
- [工廠出貨單規格書](../receive/FactoryDeliveryOrder.md)
- [作業流程](../WorkFlow.md)

---
