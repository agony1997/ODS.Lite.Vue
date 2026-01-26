工廠出貨單 (FDO)
===
---

## 概述

工廠根據營業所訂貨單 (BPO) 出貨後產生的單據。
營業所庫務人員收貨時，需核對數量、批次、效期，確認後貨品入庫。

---

## 單據來源

### 實際情境
- 工廠系統根據 BPO 產生 FDO
- FDO 隨貨送達營業所

### Mock 模擬方式
- **自動產生**：BPO 確認後，系統自動產生對應的 FDO（模擬工廠出貨）
- **手動觸發**：提供「模擬工廠出貨」功能，手動產生 FDO
- **模擬頁面**：提供隱藏的工廠模擬頁面，可操作出貨流程

---

## 單據狀態

| 狀態 | 說明 |
|------|------|
| PENDING | 待收貨（工廠已出貨，營業所尚未收貨） |
| RECEIVED | 已收貨（庫務已確認收貨） |
| DISCREPANCY | 有差異（收貨數量與出貨數量不符） |

---

## 資料結構

### 工廠出貨單 (FactoryDeliveryOrder)

| 欄位 | 型別 | 說明 |
|------|------|------|
| deliveryNo | String | 出貨單號 (FDO + 流水號) |
| branchCode | String | 營業所代碼 |
| purchaseNo | String | 對應的營業所訂貨單號 (BPO) |
| deliveryDate | LocalDate | 出貨日期 |
| receiveDate | LocalDate | 收貨日期 (nullable) |
| status | String | 狀態 (PENDING/RECEIVED/DISCREPANCY) |
| receivedBy | String | 收貨人員工編 (nullable) |
| remark | String | 備註 |

### 工廠出貨單明細 (FactoryDeliveryOrderDetail)

| 欄位 | 型別 | 說明 |
|------|------|------|
| deliveryNo | String | 出貨單號 |
| itemNo | Integer | 項次 |
| productCode | String | 產品代碼 |
| batchNo | String | 批次號 |
| expiryDate | LocalDate | 效期 |
| deliveryQty | Integer | 出貨數量 |
| receivedQty | Integer | 實收數量 (nullable) |
| unit | String | 單位 |

---

## 作業流程

### 1. 收貨作業

```
1. 庫務進入收貨頁面
2. 選擇/掃描 FDO 單號
3. 系統顯示 FDO 明細（產品、批次、效期、出貨數量）
4. 庫務逐項輸入實收數量
5. 系統比對出貨數量與實收數量
   - 相符：狀態設為 RECEIVED
   - 不符：狀態設為 DISCREPANCY，記錄差異
6. 確認收貨，貨品入庫
```

### 2. 差異處理

當實收數量與出貨數量不符時：
- 記錄差異數量（deliveryQty - receivedQty）
- FDO 狀態標記為 DISCREPANCY
- 差異原因可填寫於備註
- 後續可透過庫存調整單 (IAO) 處理

---

## 庫存影響

### 入庫規則
- 收貨確認後，依**實收數量**增加庫存
- 庫存記錄需包含：產品代碼、批次號、效期、數量
- 後續配貨時依 FIFO（先進先出）原則取貨

### 庫存結構建議
```
庫存 = f(產品代碼, 批次號, 效期)
```

---

## API 設計

| 方法 | 端點 | 說明 |
|------|------|------|
| GET | /api/receive/fdo | 查詢待收貨清單 |
| GET | /api/receive/fdo/{deliveryNo} | 查詢單一 FDO 明細 |
| PUT | /api/receive/fdo/{deliveryNo}/confirm | 確認收貨 |
| POST | /api/mock/factory/delivery | [Mock] 模擬工廠出貨 |

---

## 權限

| 角色 | 權限 |
|------|------|
| 庫務人員 | 收貨確認 |
| 營業所管理員 | 查詢收貨記錄 |
| 系統管理員 | Mock 模擬操作 |

---

## 相關單據

- 上游：營業所訂貨單 (BPO)
- 下游：庫存、配貨作業 (CO)

---
