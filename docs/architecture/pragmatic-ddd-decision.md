# Pragmatic DDD 重構決策報告

> 文件版本：1.0
> 建立日期：2026-02-03
> 適用專案：Mock-ODS-VUE

---

## 一、為何選擇 Pragmatic DDD？

### 1.1 傳統分層架構的問題

原本的架構採用**按技術層分類 (Package-by-Layer)**：

```
controller/    ← 7 個 Controller 混在一起
service/       ← 7 個 Service 混在一起
repository/    ← 48 個 Repository 全部混在一起
model/entity/  ← 按子領域分，但與 repository/service 斷開
```

這種架構在專案規模較小時運作良好，但隨著系統成長會產生以下問題：

| 問題 | 影響 |
|------|------|
| **認知負擔高** | 開發「訂貨」功能時，需要在 controller/service/repository/model 四個目錄間跳轉 |
| **隱性耦合** | 同一層的類別容易互相引用，導致領域邊界模糊 |
| **修改範圍難以預測** | 改動一個功能可能意外影響其他領域 |
| **團隊協作衝突** | 多人同時開發不同功能時，常修改同一個 package 下的檔案 |

### 1.2 為何不選擇「完整 DDD」？

完整的 Domain-Driven Design 包含：
- Aggregate Root、Value Object、Domain Event
- Anti-Corruption Layer (ACL)
- Bounded Context 之間用 Domain Event 或 API 通訊
- CQRS / Event Sourcing

**不適合本專案的原因**：

1. **專案規模**：~130 個 Java 檔案，屬於中小型系統
2. **團隊規模**：目前單人或小團隊開發
3. **領域複雜度**：訂貨配送系統的業務邏輯相對明確，沒有複雜的領域事件流
4. **成本效益**：完整 DDD 的學習曲線和開發成本過高

### 1.3 Pragmatic DDD 的定位

**Pragmatic DDD = Package-by-Domain + 簡化版領域邊界**

核心原則：
- ✅ 按領域（而非技術層）組織程式碼
- ✅ 每個領域自包含 controller/service/repository/model
- ✅ 允許跨領域直接 import（不需要 ACL）
- ❌ 不強制 Aggregate Root 模式
- ❌ 不需要 Domain Event

這是一種**務實的折衷方案**，獲得 80% 的 DDD 好處，只付出 20% 的成本。

---

## 二、領域劃分的考量

### 2.1 劃分原則

```
業務能力 (Business Capability) → 領域 (Domain)
```

每個領域回答一個核心業務問題：

| 領域 | 核心業務問題 |
|------|-------------|
| `auth` | 誰可以登入？誰有什麼權限？ |
| `branch` | 營業所有哪些？每個營業所賣什麼產品？ |
| `purchase` | 業務員訂了什麼？營業所怎麼彙總？ |
| `master` | 系統有哪些產品、工廠、客戶？ |
| `sequence` | 單據編號怎麼產生？ |
| `allocation` | 貨物怎麼分配給業務員？ |
| `delivery` | 業務員怎麼送貨給客戶？ |
| `closing` | 業務員怎麼退貨、寄庫？ |
| `inventory` | 庫存有多少？ |
| `receive` | 工廠送來的貨怎麼收？ |

### 2.2 領域分類

根據目前的開發狀態，領域分為三類：

```
┌─────────────────────────────────────────────────────┐
│  核心領域 (Core Domain)                              │
│  ┌─────────┐  ┌─────────┐                           │
│  │purchase │  │  auth   │  ← 有完整 CRUD + 業務邏輯  │
│  └─────────┘  └─────────┘                           │
├─────────────────────────────────────────────────────┤
│  支援領域 (Supporting Domain)                        │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐             │
│  │ branch  │  │ master  │  │sequence │  ← 提供資料  │
│  └─────────┘  └─────────┘  └─────────┘             │
├─────────────────────────────────────────────────────┤
│  休眠領域 (Dormant Domain)                           │
│  ┌──────────┐ ┌──────────┐ ┌─────────┐ ┌─────────┐ │
│  │allocation│ │ delivery │ │ closing │ │inventory│ │
│  └──────────┘ └──────────┘ └─────────┘ └─────────┘ │
│  ← 僅有 entity + repository，等待未來開發            │
└─────────────────────────────────────────────────────┘
```

### 2.3 `shared` 的特殊定位

`shared` **不是一個領域**，而是跨領域基礎設施：

```
shared/
├── config/      ← Spring 設定，被所有領域使用
├── security/    ← JWT 驗證，攔截所有請求
├── exception/   ← 共用例外類別
└── model/       ← AuditEntity（所有 entity 繼承）
                   Sortable（排序介面）
```

**為何不放在各領域？**
- `AuditEntity` 被 30+ 個 entity 繼承，放在任何單一領域都不合適
- `SecurityConfig` 是全域設定，不屬於任何業務領域
- 這些類別的變更頻率極低，不會造成領域間耦合

---

## 三、跨領域依賴策略

### 3.1 允許的依賴方向

```
purchase ──→ branch      (查詢儲位、營業所產品清單)
purchase ──→ master      (查詢產品工廠對應)
purchase ──→ sequence    (產生單據編號)
shared.security ──→ auth (載入使用者權限)
```

### 3.2 為何允許直接 import？

**方案 A：Anti-Corruption Layer (ACL)**
```java
// purchase 領域
public interface BranchQueryPort {
    Location findByUserCode(String userCode);
}

// 需要額外的 adapter 實作
@Component
public class BranchQueryAdapter implements BranchQueryPort {
    private final LocationRepository locationRepository;
    // ...
}
```

**方案 B：直接 import（本專案採用）**
```java
// purchase 領域直接使用 branch 的 repository
@Service
public class BranchPurchaseService {
    private final LocationRepository locationRepository; // 來自 branch 領域
}
```

**選擇方案 B 的理由**：

| 考量 | ACL | 直接 import |
|------|-----|-------------|
| 程式碼量 | 多 50%+ | 最小化 |
| 重構難度 | 每個跨領域呼叫都要改 | 只改 import |
| 適用情境 | 微服務、團隊邊界明確 | 單體應用、小團隊 |
| 本專案 | ❌ 過度工程 | ✅ 務實選擇 |

### 3.3 依賴規則

為避免循環依賴，制定以下規則：

```
✅ 允許：
   purchase → branch, master, sequence
   delivery → branch, master
   任何領域 → shared

❌ 禁止：
   branch → purchase  (下游不可依賴上游)
   shared → 任何領域  (基礎設施不可依賴業務)
```

---

## 四、與原架構的比較

### 4.1 開發體驗改善

**情境：新增「訂貨單查詢 API」**

| 步驟 | 舊架構 | 新架構 |
|------|--------|--------|
| 1 | 開 `controller/` 找 SalesPurchaseController | 開 `purchase/controller/` |
| 2 | 開 `service/` 找 SalesPurchaseOrderService | 同目錄下 `../service/` |
| 3 | 開 `repository/` 找 6 個相關 repository | 同目錄下 `../repository/` |
| 4 | 開 `model/entity/purchase/` 找 entity | 同目錄下 `../model/entity/` |
| 5 | 開 `model/dto/` 找 DTO | 同目錄下 `../model/dto/` |

**新架構的優勢**：所有相關檔案都在 `purchase/` 下，IDE 的檔案樹一目了然。

### 4.2 程式碼導航

```
舊架構：理解「營業所凍結」功能需要打開的檔案
─────────────────────────────────────────────
controller/BranchPurchaseController.java
service/BranchPurchaseService.java
repository/BranchPurchaseFrozenRepository.java
repository/SalesPurchaseOrderRepository.java
repository/SalesPurchaseOrderDetailRepository.java
model/entity/purchase/BranchPurchaseFrozen.java
model/dto/BranchPurchaseFreezeDTO.java
model/enums/FrozenStatus.java

新架構：全部在 purchase/ 下
─────────────────────────────────────────────
purchase/
├── controller/BranchPurchaseController.java
├── service/BranchPurchaseService.java
├── repository/BranchPurchaseFrozenRepository.java
├── repository/SalesPurchaseOrderRepository.java
├── repository/SalesPurchaseOrderDetailRepository.java
├── model/entity/BranchPurchaseFrozen.java
├── model/dto/BranchPurchaseFreezeDTO.java
└── model/enums/FrozenStatus.java
```

### 4.3 未來擴展性

當系統成長到需要拆分微服務時：

```
單體 (現在)                    微服務 (未來)
─────────────                  ─────────────
purchase/  ──────────────────→  purchase-service/
branch/    ──────────────────→  branch-service/
auth/      ──────────────────→  auth-service/
```

因為領域邊界已經清晰，拆分成本大幅降低。

---

## 五、決策總結

| 決策點 | 選擇 | 理由 |
|--------|------|------|
| 架構風格 | Pragmatic DDD | 平衡複雜度與可維護性 |
| 領域數量 | 11 個 | 對應業務能力，不過度拆分 |
| 跨領域通訊 | 直接 import | 單體應用，避免過度抽象 |
| shared 的存在 | 保留 | 基礎設施需要集中管理 |
| 休眠領域 | 保留結構 | 預留未來開發空間 |

---

## 六、後續建議

1. **新功能開發**：優先在對應領域內完成，只有真正跨領域才 import 其他領域
2. **避免 shared 膨脹**：只有真正「跨所有領域」的類別才放 shared
3. **定期檢視依賴**：若發現循環依賴，考慮是否領域劃分需要調整
4. **文件同步**：領域變更時更新 CLAUDE.md 的架構說明

---

## 附錄：重構統計

### 檔案遷移統計

| 領域 | 檔案數 | 說明 |
|------|--------|------|
| shared | 11 | 跨領域基礎設施 |
| auth | 18 | 認證授權 |
| branch | 13 | 營業所 |
| purchase | 27 | 訂貨（核心業務） |
| master | 11 | 主檔資料 |
| sequence | 6 | 序號產生器 |
| allocation | 9 | 配貨（休眠） |
| delivery | 15 | 配送（休眠） |
| closing | 16 | 結帳退貨（休眠） |
| inventory | 2 | 庫存（休眠） |
| receive | 4 | 收貨（休眠） |
| **總計** | **133** | |

### 測試遷移統計

| 領域 | 測試檔案數 |
|------|-----------|
| shared | 5 |
| auth | 6 |
| branch | 3 |
| purchase | 8 |
| sequence | 1 |
| **總計** | **23** |

### 驗證結果

- ✅ `mvn compile` - 編譯成功
- ✅ `mvn clean test` - 187 個測試全部通過
