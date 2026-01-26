取號機制規格書
===

> 設計系統內所有表格共用的取號方式

---

## 概述

系統中多種單據需要產生唯一的流水號，為確保併發安全與一致性，採用統一的取號機制。

---

## 需取號的單據類型

| 類型代碼 | 單據名稱 | 編號前綴 | 編號格式 |
|---------|---------|---------|---------|
| SPO | 業務員訂貨單 | SPO | SPO-{yyyyMMdd}-{3位序號} |
| BPO | 營業所訂貨單 | BPO | BPO-{yyyyMMdd}-{3位序號} |
| FSO | 工廠出貨單 | FSO | FSO-{yyyyMMdd}-{3位序號} |
| DSO | 配送單 | DSO | DSO-{yyyyMMdd}-{3位序號} |
| ADJ | 庫存調整單 | ADJ | ADJ-{yyyyMMdd}-{3位序號} |

---

## 取號表設計

### 表格結構

```
document_sequence (單據序號表)
├── sequence_type   : VARCHAR(10)  -- 類型代碼 (PK)
├── sequence_date   : DATE         -- 日期 (PK)
├── current_no      : INT          -- 當前序號
└── version         : BIGINT       -- 樂觀鎖版本號
```

### 複合主鍵

- `sequence_type` + `sequence_date` 組成複合主鍵
- 每種單據類型每天獨立計號
- 序號每日重置從 1 開始

---

## 併發控制機制

### 雙重鎖定策略

```
┌─────────────────────────────────────────────────────┐
│                 取號流程                             │
├─────────────────────────────────────────────────────┤
│  1. @Transactional(REQUIRES_NEW)  ← 獨立事務        │
│     避免外層事務回滾導致序號丟失                      │
│                                                     │
│  2. SELECT ... FOR UPDATE         ← 悲觀鎖 (行級)   │
│     鎖定該類型+日期的記錄                            │
│                                                     │
│  3. current_no + 1                                  │
│     遞增序號                                        │
│                                                     │
│  4. @Version                      ← 樂觀鎖 (額外保護)│
│     防止非預期的並發更新                             │
└─────────────────────────────────────────────────────┘
```

### 為何使用獨立事務？

| 情境 | 無獨立事務 | 有獨立事務 |
|-----|-----------|-----------|
| 外層事務成功 | 序號正常遞增 | 序號正常遞增 |
| 外層事務回滾 | 序號一併回滾，產生重複 | 序號已提交，不會重複 |

---

## 編號格式規則

### 格式組成

```
{前綴}-{日期}-{序號}
  │      │      │
  │      │      └── 3位數字，不足補零 (001-999)
  │      └────────── yyyyMMdd 格式
  └───────────────── 單據類型前綴
```

### 範例

| 類型 | 日期 | 序號 | 完整編號 |
|-----|------|-----|---------|
| SPO | 2024-01-22 | 1 | SPO-20240122-001 |
| SPO | 2024-01-22 | 15 | SPO-20240122-015 |
| BPO | 2024-01-22 | 1 | BPO-20240122-001 |

---

## API 設計

### 取號服務介面

```java
public interface SequenceGenerator {

    /**
     * 產生單據編號
     * @param type 單據類型
     * @param date 單據日期
     * @return 完整單據編號
     */
    String generate(SequenceType type, LocalDate date);
}
```

### 單據類型列舉

```java
public enum SequenceType {
    // SalesPurchaseOrder
    SPO("SPO", "業務員訂貨單"),
    // BranchPurchaseFrozen
    BPF("BPF", "營業所凍結單"),
    // BranchPurchaseOrder
    BPO("BPO", "營業所訂貨單"),
    // FactoryDeliveryOrder
    FDO("FDO", "工廠出貨單"),
    // CustomerOrder
    CO("CO", "客戶配送單"),
    // SalesKeepRecord
    SKR("SKR", "業務員寄庫單"),
    // SalesReturnRecord
    SRR("SRR", "業務員退庫單"),
    // BranchReturnOrder
    BRO("BRO", "營業所銷退單"),
    // InventoryAdjustmentOrder
    IAO("IAO", "庫存調整單");

    private final String code;
    private final String name;
}
```

---

## 使用方式

### 在 Service 中注入使用

```java
@Service
@RequiredArgsConstructor
public class SalesPurchaseOrderService {

    private final SequenceGenerator sequenceGenerator;

    public SalesPurchaseOrder createOrder(...) {
        String orderNo = sequenceGenerator.generate(
            SequenceType.SPO,
            purchaseDate
        );
        // ...
    }
}
```

---

## 異常處理

| 異常情況 | 處理方式 |
|---------|---------|
| 序號超過 999 | 拋出 BusinessException |
| 資料庫鎖定超時 | 重試機制 (最多 3 次) |
| 樂觀鎖衝突 | 自動重試取號 |

---

## 擴展考量

### 序號位數不足

當單日序號可能超過 999 時：
1. 調整格式為 4 位數：`{前綴}-{yyyyMMdd}-{4位序號}`
2. 或改用時間戳：`{前綴}-{yyyyMMddHHmmss}-{3位序號}`

### 分散式環境

若未來需要分散式部署：
1. 可改用 Redis 的 INCR 命令
2. 或使用 Snowflake 演算法
3. 目前的悲觀鎖方案在單一資料庫下已足夠

---

## 相關表格

- [Table.md](./Table.md) - 系統表格總覽
- [SalesPurchase.md](./purchase/SalesPurchase.md) - 業務員訂貨規格
