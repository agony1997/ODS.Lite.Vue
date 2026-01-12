---
name: postgres-expert
description: 專精於 PostgreSQL 資料庫建模、效能調優與 SQL 撰寫。當涉及 .sql 文件、Entity 類別設計、或是資料庫遷移 (Flyway/Liquibase) 時觸發。
---

# PostgreSQL 專家技能

## 核心審查原則 (Scientific Approach)
1. **正規化檢核**：預設遵循 3NF。對於高頻讀取的彙總數據，評估是否需要適度「反正規化」並記錄決策。
2. **索引策略**：
    - 每個表必須有 Primary Key。
    - 檢查外鍵 (Foreign Key) 是否有對應索引以優化 Join 效能。
    - 避免在低基數 (Low Cardinality) 欄位建立 B-tree 索引。
3. **資料型別優化**：
    - 精確選擇資料型別（如 `UUID` vs `BIGSERIAL`, `JSONB` vs `TEXT`）。
    - 時間欄位統一使用 `TIMESTAMP WITH TIME ZONE`。

## 執行指令
- 在修改任何 Table 結構前，必須先評估對現有資料的影響。
- **效能檢查**：當用戶提供複雜查詢時，主動模擬執行計畫 (EXPLAIN ANALYZE) 分析。
- **Java 整合**：確保 Hibernate/JPA 的 `@Column` 定義與資料庫定義完全一致。