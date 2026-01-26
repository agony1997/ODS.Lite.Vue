業務員優先度規格書
===
---

## 概述

業務員優先度用於配貨時決定批次分配順序。
優先等級高的業務員可先分配到效期較長（品質較好）的批次。

---

## 資料結構

### SalesPriority（業務員優先度）

| 欄位 | 型別 | 說明 |
|------|------|------|
| id | Long | 主鍵 |
| branchCode | String | 營業所代碼 |
| locationCode | String | 儲位代碼（業務員） |
| priorityLevel | Integer | 優先等級 (1=最高) |

唯一鍵：(branchCode, locationCode)

---

## 優先等級說明

| 等級 | 說明 | 配貨優先 |
|------|------|----------|
| 1 | 最高優先 | 先分配到效期較長的批次 |
| 2 | 次優先 | |
| 3 | 一般 | |
| ... | ... | |
| N | 最低優先 | 效期較短的批次，可能分不到 |

---

## 配貨規則

### FIFO + 優先度

```
配貨順序：
1. 按業務員優先等級排序（priorityLevel ASC）
2. 依產品效期分配（expiryDate ASC = FIFO）
3. 優先等級高者先分配到效期較長的批次
```

### 範例

假設有：
- 業務員 A (priorityLevel = 1)
- 業務員 B (priorityLevel = 2)
- 批次 B1 (效期 2024/01/20)
- 批次 B2 (效期 2024/01/15)

分配結果：
- A 分到 B1（效期較長）
- B 分到 B2（效期較短）

### 庫存不足時

當庫存不足以分配給所有業務員時：
- 優先等級高的業務員先分配
- 低優先等級的業務員 allocatedQty 可能為 0

---

## 預設值

若業務員未設定優先度：
- 視為最低優先（或設定一個預設等級）
- 建議新增業務員時自動建立預設優先度

---

## API 設計

| 方法 | 端點 | 說明 |
|------|------|------|
| GET | /api/sales-priority | 查詢優先度清單 |
| GET | /api/sales-priority/branch/{branchCode} | 查詢營業所下的優先度設定 |
| PUT | /api/sales-priority/{id} | 更新優先度 |
| POST | /api/sales-priority | 新增優先度設定 |

---

## 相關規格書

- [配貨單規格書](../allocation/AllocationOrder.md)
- [儲位主檔規格書](../master/Location.md)

---
