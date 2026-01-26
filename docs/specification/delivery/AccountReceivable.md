應收帳款規格書 (AR)
===
> 主要操作者：業務員、財務
---

## 概述

送貨簽收完成後，系統自動產生應收帳款。
不收現金，全部記帳，由財務後續處理收款。

---

## 產生時機

```
SDO 簽收完成 (status = SIGNED)
        │
        ▼
系統自動產生 AR
├── 計算淨額 = 銷售金額 - 退貨金額
└── 毀損不計入（損失處理）
```

---

## 資料結構

### AccountReceivable (AR 應收帳款)

| 欄位 | 型別 | 說明 |
|------|------|------|
| arNo | String | 應收帳款單號 (AR + 流水號) |
| branchCode | String | 營業所代碼 |
| customerCode | String | 客戶代碼 |
| deliveryNo | String | 來源送貨單號 |
| amount | BigDecimal | 應收金額 |
| paidAmount | BigDecimal | 已收金額 |
| dueDate | LocalDate | 到期日 |
| status | String | 狀態 |
| createdAt | LocalDateTime | 建立時間 |
| paidAt | LocalDateTime | 付款時間 |

---

## AR 狀態說明

| 狀態 | 說明 |
|------|------|
| PENDING | 待收款 |
| PARTIAL | 部分收款 |
| PAID | 已收款 |
| OVERDUE | 逾期 |

---

## 金額計算

```
應收金額 = Σ(SDOD.amount where type = SALES)
         - Σ(SDOD.amount where type = RETURN)

注意：DAMAGE 不計入應收金額
```

---

## API 設計

| 方法 | 端點 | 說明 | 操作者 |
|------|------|------|--------|
| GET | /api/account-receivable | 查詢應收帳款清單 | 財務 |
| GET | /api/account-receivable/{arNo} | 查詢單一應收帳款 | 財務 |
| GET | /api/account-receivable/customer/{customerCode} | 查詢客戶應收帳款 | 業務員/財務 |
| PUT | /api/account-receivable/{arNo}/pay | 記錄收款 | 財務 |
| GET | /api/account-receivable/overdue | 查詢逾期帳款 | 財務 |

---

## 相關規格書

- [送貨單規格書](./SalesDeliveryOrder.md)

---
