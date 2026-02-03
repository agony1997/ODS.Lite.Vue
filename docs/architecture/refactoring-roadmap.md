# 後續重構計畫

> 文件版本：1.0
> 建立日期：2026-02-03
> 前置文件：pragmatic-ddd-decision.md

---

## 一、當前狀態總結

### 1.1 Pragmatic DDD 重構完成

| 項目 | 狀態 |
|------|------|
| 後端 Package-by-Domain 重構 | ✅ 完成 |
| 133 個 Java 檔案遷移 | ✅ 完成 |
| 24 個測試檔案遷移 | ✅ 完成 |
| 187 個測試全部通過 | ✅ 驗證 |
| CLAUDE.md 架構說明更新 | ✅ 完成 |

### 1.2 待提交的變更

目前工作區有大量重構變更尚未提交，包括：
- 後端：所有領域目錄重組
- 測試：測試檔案遷移 + 新增測試
- 文件：架構決策文件

---

## 二、後續重構階段

### Phase R1：重構收尾（立即執行）

> 目標：確保重構變更完整提交並驗證

| # | 任務 | 說明 | 優先級 |
|---|------|------|--------|
| R1-1 | 提交後端重構 | 提交所有 src/main/java 的遷移變更 | 高 |
| R1-2 | 提交測試遷移 | 提交所有 src/test/java 的遷移和新增測試 | 高 |
| R1-3 | 提交架構文件 | 提交 docs/architecture/*.md | 高 |
| R1-4 | 驗證 CI/CD | 確保所有測試在 CI 環境通過（如有） | 中 |

**驗收條件：**
- [ ] Git 工作區乾淨（無未提交變更）
- [ ] 所有測試在 clean build 後通過

---

### Phase R2：測試覆蓋強化

> 目標：提升各領域的測試覆蓋率

#### 2.1 測試現況分析

| 領域 | Controller | Service | Repository | 完整度 |
|------|------------|---------|------------|--------|
| auth | ✅ 3 個 | ✅ 3 個 | - | 良好 |
| branch | ✅ 2 個 | ✅ 1 個 | - | 良好 |
| purchase | ✅ 2 個 | ✅ 2 個 | - | 良好 |
| sequence | - | ✅ 1 個 | - | 基本 |
| shared | ✅ 1 個 | - | - | 良好 |
| master | ❌ | ❌ | ❌ | 無 |
| allocation | ❌ | ❌ | ❌ | 無（休眠） |
| delivery | ❌ | ❌ | ❌ | 無（休眠） |
| closing | ❌ | ❌ | ❌ | 無（休眠） |
| inventory | ❌ | ❌ | ❌ | 無（休眠） |
| receive | ❌ | ❌ | ❌ | 無（休眠） |

#### 2.2 測試強化任務

| # | 任務 | 說明 | 優先級 |
|---|------|------|--------|
| R2-1 | master Repository 測試 | Product、Factory、Customer 的 Repository 整合測試 | 中 |
| R2-2 | sequence Service 完整測試 | 增加邊界條件、並發場景測試 | 中 |
| R2-3 | branch Service 測試 | BranchProductListService 更多場景測試 | 低 |

**注意：** 休眠領域（allocation, delivery, closing, inventory, receive）在功能開發時再補測試。

---

### Phase R3：程式碼品質提升

> 目標：建立程式碼品質基準線

| # | 任務 | 說明 | 優先級 |
|---|------|------|--------|
| R3-1 | ArchUnit 架構測試 | 驗證領域依賴規則（禁止循環依賴） | 高 |
| R3-2 | IDE 警告清理 | 處理未使用的 import、未使用的變數 | 中 |
| R3-3 | Checkstyle 規則 | 統一程式碼風格 | 低 |
| R3-4 | JaCoCo 覆蓋率報告 | 建立測試覆蓋率基準線 | 低 |

#### R3-1 ArchUnit 規則範例

```java
@AnalyzeClasses(packages = "com.example.mockodsvue")
public class ArchitectureTest {

    @ArchTest
    static final ArchRule purchase_should_not_depend_on_delivery =
        noClasses().that().resideInAPackage("..purchase..")
            .should().dependOnClassesThat().resideInAPackage("..delivery..");

    @ArchTest
    static final ArchRule shared_should_not_depend_on_domains =
        noClasses().that().resideInAPackage("..shared..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..purchase..", "..branch..", "..master..", "..auth.."
            );
}
```

---

### Phase R4：前端對應重構（選擇性）

> 目標：評估前端是否需要對應重構

#### 4.1 現況評估

前端目前採用**按功能分目錄**的結構：

```
frontend/src/
├── api/           ← 各功能 API 模組
├── views/         ← 頁面組件
├── stores/        ← Pinia 狀態管理
├── composables/   ← Vue 3 Composables
└── router/        ← 路由設定
```

**評估結論：** 前端目前的結構已經相對合理，不需要大規模重構。但可考慮：

| # | 任務 | 說明 | 優先級 |
|---|------|------|--------|
| R4-1 | API 模組重命名 | 考慮是否將 API 模組按領域命名（如 purchaseApi.js） | 低 |
| R4-2 | 共用組件抽取 | 抽取可複用的 Quasar 組件封裝 | 低 |

---

## 三、與功能開發的協調

### 3.1 重構與功能開發的關係

```
Phase R1 (重構收尾)
    │
    ├──────────────────────────────────────┐
    │                                      │
    ▼                                      ▼
Phase R2-R3 (測試/品質)              Phase 1-5 (功能開發)
(可並行進行)                         (依 architecture-and-roadmap.md)
```

### 3.2 建議的開發順序

1. **立即**：完成 Phase R1（提交重構變更）
2. **短期**：繼續 Phase 1（訂貨流程完善），同時穿插 R2-1
3. **中期**：Phase 2-3 功能開發，搭配 R3-1 架構測試
4. **長期**：Phase 4-5 功能開發

---

## 四、技術債務追蹤

| 項目 | 來源 | 影響 | 建議處理時機 |
|------|------|------|-------------|
| 休眠領域無測試 | 設計決策 | 開發時需補測試 | 各領域功能開發時 |
| 無 ArchUnit 測試 | 新架構 | 依賴規則可能被破壞 | Phase R3 |
| 前端無 TypeScript | 歷史 | 重構複雜度較高 | 長期計畫 |
| 無 API 文件 | 歷史 | 前後端溝通成本 | Phase 6 |

---

## 五、檢查清單

### 提交前檢查

- [ ] `mvn clean test` 全部通過
- [ ] 無編譯警告（或已知可忽略）
- [ ] CLAUDE.md 架構說明與程式碼一致
- [ ] 新領域遵循既有目錄結構

### 新功能開發檢查

- [ ] 確認功能屬於哪個領域
- [ ] 新類別放在正確的領域目錄下
- [ ] 跨領域依賴遵循允許的方向
- [ ] 補充對應的單元測試

---

## 附錄：領域目錄結構範本

新功能開發時，請參考以下結構：

```
{domain}/
├── controller/           ← REST API 端點
│   └── XxxController.java
├── service/              ← 業務邏輯
│   ├── XxxService.java (interface, 如需要)
│   └── impl/
│       └── XxxServiceImpl.java
├── repository/           ← 資料存取
│   └── XxxRepository.java
├── mapper/               ← MapStruct 轉換器
│   └── XxxMapper.java
└── model/
    ├── entity/           ← JPA Entity
    │   └── Xxx.java
    ├── dto/              ← Request/Response DTO
    │   ├── XxxRequest.java
    │   └── XxxResponse.java
    └── enums/            ← 領域專用列舉
        └── XxxStatus.java
```
