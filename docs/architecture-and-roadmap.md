# Mock ODS VUE - 系統架構分析與開發進度表

> 建立日期：2026-01-28
> 本文件為後續開發的主要依據，所有功能開發應依此進度表執行。

---

## 一、系統架構分析

### 1.1 技術棧

| 層級 | 技術 | 版本 |
|------|------|------|
| 前端框架 | Vue 3 + Quasar 2 | Vue 3.4 / Quasar 2.14 |
| 前端建置 | Vite | 5.x |
| 後端框架 | Spring Boot | 3.4.1 |
| 語言 | Java 17 / JavaScript (ES Modules) | - |
| 資料庫 | PostgreSQL | - |
| ORM | Spring Data JPA (Hibernate) | - |
| 安全性 | Spring Security + JWT (JJWT 0.12.6) | - |
| DTO 映射 | MapStruct 1.5.5 | - |
| 容器化 | Docker Compose (PostgreSQL + pgAdmin) | - |

### 1.2 架構模式

採用**模組化單體架構 (Modular Monolith)**，前後端分離：

```
┌─────────────────────────────────────────────────────┐
│  前端 (Vue 3 + Quasar 2)                             │
│  Port 5173 (dev) → 建置至 static/ (prod)             │
│  ┌─────────┐ ┌──────────┐ ┌──────────────┐          │
│  │ Views   │ │ API 模組 │ │ Vue Router   │          │
│  └─────────┘ └──────────┘ └──────────────┘          │
└────────────────────┬────────────────────────────────┘
                     │ REST API (/api/*)
                     │ (dev: Vite proxy → :8080)
┌────────────────────▼────────────────────────────────┐
│  後端 (Spring Boot 3.4.1)  Port 8080                 │
│                                                      │
│  ┌──────────────┐  Security Filter Chain             │
│  │ JWT Filter   │──► Authentication                  │
│  └──────────────┘                                    │
│         │                                            │
│  ┌──────▼───────┐                                    │
│  │ Controller   │  REST API 端點                      │
│  └──────┬───────┘                                    │
│         │                                            │
│  ┌──────▼───────┐                                    │
│  │ Service      │  業務邏輯                            │
│  └──────┬───────┘                                    │
│         │                                            │
│  ┌──────▼───────┐  ┌──────────────┐                  │
│  │ Repository   │  │ MapStruct    │                  │
│  │ (JPA)        │  │ Mapper       │                  │
│  └──────┬───────┘  └──────────────┘                  │
│         │                                            │
│  ┌──────▼───────┐                                    │
│  │ Entity       │  JPA 實體 (繼承 BaseEntity)         │
│  └──────────────┘                                    │
└────────────────────┬────────────────────────────────┘
                     │
              ┌──────▼──────┐
              │ PostgreSQL  │
              │ Port 5432   │
              └─────────────┘
```

### 1.3 業務領域模組

系統模擬 ODS（訂貨配送系統）的完整業務流程，共 **6 大業務領域**：

```
訂貨 (Purchase) → 收貨 (Receive) → 配貨 (Allocation) → 送貨 (Delivery) → 結帳 (Closing)
                                                                            │
                                                          主檔/營業所設定 (Master/Branch)
```

#### 領域模組清單

| # | 領域 | Entity 子目錄 | 核心單據 | 依賴 |
|---|------|--------------|---------|------|
| 1 | 權限管理 | `auth/` | AuthUser, AuthRole, AuthUserBranchRole | 無 |
| 2 | 主檔管理 | `master/` | Product, Factory, Customer, ProductFactory, ProductUnitConversion | 無 |
| 3 | 營業所設定 | `branch/` | Branch, Location, BranchProductList, SalesPriority | 主檔 |
| 4 | 訂貨作業 | `purchase/` | SPO, SPOD, BPF, SalesPurchaseList, BPO, BPOD | 營業所、主檔 |
| 5 | 收貨作業 | `receive/` | FDO, FDOD | 訂貨 (BPO) |
| 6 | 庫存管理 | `inventory/` | Inventory | 收貨、配貨、送貨 |
| 7 | 配貨/領貨 | `allocation/` | AO, AOD, SRO, SROD | 訂貨、庫存 |
| 8 | 送貨作業 | `delivery/` | CPO, CPOD, SDO, SDOD, AR | 配貨、庫存 |
| 9 | 結帳作業 | `closing/` | SKR, SKRD, SRR, SRRD, BRO, BROD | 庫存 |

### 1.4 核心設計模式

| 設計模式 | 應用 |
|---------|------|
| **Audit Trail** | BaseEntity 自動記錄 createdAt/updatedAt/createdBy/updatedBy |
| **樂觀鎖** | DocumentSequence 使用 `@Version` 避免序號衝突 |
| **狀態機** | BPF (FROZEN→CONFIRMED)、SPO Detail (PENDING→AGGREGATED) |
| **序號產生器** | SequenceGenerator 依單據類型+日期自動產號 |
| **凍結控制** | BranchPurchaseFrozen 控制編輯權限，跨角色協作 |

---

## 二、現況盤點

### 2.1 已完成模組

| 模組 | 後端 (Entity/Repo/Service/Controller/Mapper) | 前端 (View/API) | 測試 | 完成度 |
|------|----------------------------------------------|-----------------|------|--------|
| 權限管理 (Auth) | Entity + Repo + Service + Controller | 無登入頁面 | 無 | 70% |
| 營業所查詢 (Branch) | Entity + Repo + Controller (僅 GET /api/branches) | API 模組 | 無 | 50% |
| 營業所產品清單 | Entity + Repo + Service + Controller | API 模組 | 無 | 80% |
| 業務員訂貨 (SPO) | Entity + Repo + Service + Controller + Mapper | 無前端頁面（PurchaseSalesView 為空殼） | 無 | 60% |
| 營業所訂貨彙總 (BPO) | Entity + Repo + Service + Controller + Mapper | 完整前端頁面 (PurchaseBranchView) | 有單元測試 | **90%** |
| 序號管理 | Entity + Repo + Service (SequenceGenerator) | - | 無 | 90% |

### 2.2 僅有 Entity + Repository（無 Service/Controller/前端）

| 模組 | Entity 已建立 | Repository 已建立 |
|------|--------------|-------------------|
| 主檔 (Product/Factory/Customer) | Product, Factory, Customer, ProductFactory, ProductUnitConversion | 全部 |
| 庫存 (Inventory) | Inventory | InventoryRepository |
| 收貨 (FDO) | FactoryDeliveryOrder, FactoryDeliveryOrderDetail | 全部 |
| 配貨 (AO) | AllocationOrder, AllocationOrderDetail | 全部 |
| 領貨 (SRO) | SalesReceiveOrder, SalesReceiveOrderDetail | 全部 |
| 送貨 (SDO/CPO) | SalesDeliveryOrder, SalesDeliveryOrderDetail, CustomerPreOrder, CustomerPreOrderDetail, AccountReceivable | 全部 |
| 結帳 (SKR/SRR/BRO) | SalesKeepRecord, SalesKeepRecordDetail, SalesReturnRecord, SalesReturnRecordDetail, BranchReturnOrder, BranchReturnOrderDetail | 全部 |

### 2.3 前端現況

```
已實作頁面：
├── IndexView.vue        → 首頁（空殼）
├── PurchaseBranchView.vue → 營業所訂貨彙總（完整功能）
└── PurchaseSalesView.vue  → 業務員訂貨（空殼，後端 API 已就緒）

缺失：
├── 登入頁面（後端 JWT 已實作，前端無對應）
├── 主檔管理頁面
├── 收貨頁面
├── 配貨頁面
├── 領貨頁面
├── 送貨頁面
├── 結帳頁面
└── 權限與角色管理頁面
```

---

## 三、開發進度表

### 3.1 開發階段總覽

```
Phase 0: 基礎建設          ← 登入、權限、前端架構
Phase 1: 訂貨流程完善       ← 業務員訂貨前端、整合測試
Phase 2: 收貨作業           ← FDO 模擬與收貨確認
Phase 3: 配貨與領貨         ← AO/SRO 配貨分配與領貨
Phase 4: 送貨作業           ← CPO/SDO/AR 送貨與簽收
Phase 5: 結帳作業           ← SKR/SRR/BRO 寄庫退庫
Phase 6: 主檔管理與系統完善  ← CRUD 管理頁面、報表
```

---

### Phase 0：基礎建設

> 目標：建立前端認證機制、全域導航、角色權限控制

| # | 任務 | 層級 | 說明 | 前置任務 |
|---|------|------|------|---------|
| 0-1 | 登入頁面 | 前端 | 建立 LoginView.vue，呼叫 `/api/auth/login`，JWT 存入 localStorage | 無 |
| 0-2 | 前端路由守衛 | 前端 | Router beforeEach 驗證 token，未登入導向登入頁 | 0-1 |
| 0-3 | API 請求攔截器 | 前端 | 封裝 fetch 工具函式，自動帶入 Authorization header，處理 401 | 0-1 |
| 0-4 | 全域使用者狀態 | 前端 | 使用 reactive 或 Pinia 儲存登入使用者資訊 (userId, branchCode, roles) | 0-1 |
| 0-5 | 側邊導航選單 | 前端 | 改用 Quasar QLayout + QDrawer，依角色動態顯示功能選單 | 0-4 |
| 0-6 | 首頁儀表板 | 前端 | IndexView 改為簡易儀表板，顯示待辦事項摘要 | 0-5 |
| 0-7 | 後端權限註解 | 後端 | Controller 加上 `@PreAuthorize` 角色限制 | 無 |
| 0-8 | 現有 API 補 token | 前端 | 更新 branch.js、branchPurchase.js 等現有 API 模組，使用統一攔截器 | 0-3 |

**驗收條件：**
- [ ] 可正常登入/登出
- [ ] 未登入存取頁面自動導向登入頁
- [ ] 側邊選單依角色顯示不同功能
- [ ] 所有 API 請求自動帶 JWT token

---

### Phase 1：訂貨流程完善

> 目標：完成業務員訂貨前端頁面，讓訂貨→彙總流程可端到端運作

| # | 任務 | 層級 | 說明 | 前置任務 |
|---|------|------|------|---------|
| 1-1 | 業務員訂貨 API 模組 | 前端 | 建立 `api/salesPurchase.js`，封裝所有 SPO 相關 API 呼叫 | 0-3 |
| 1-2 | 業務員訂貨頁面 | 前端 | 實作 PurchaseSalesView.vue：日期選擇（D+2~D+9）、產品清單編輯、數量輸入、儲存 | 1-1 |
| 1-3 | 載入清單功能 | 前端 | 支援三種載入：昨日訂單、自訂清單、營業所清單 | 1-2 |
| 1-4 | 自訂清單管理 | 前端 | 業務員可編輯/儲存個人常用產品清單 | 1-2 |
| 1-5 | 凍結狀態提示 | 前端 | 已凍結時顯示唯讀模式，提示「組長已凍結，無法編輯」 | 1-2 |
| 1-6 | 訂貨流程整合測試 | 測試 | 端到端測試：業務員訂貨 → 組長凍結/調整 → 確認 → 彙總產生 BPO | 1-5 |
| 1-7 | BranchPurchaseView 補強 | 前端 | 新增/刪除產品功能（目前僅能調整確認數量） | 1-6 |

**驗收條件：**
- [ ] 業務員可選擇日期、載入清單、輸入數量、儲存訂貨單
- [ ] 已凍結日期顯示唯讀
- [ ] 端到端流程：訂貨 → 凍結 → 調整 → 確認 → 彙總 全部可操作

---

### Phase 2：收貨作業

> 目標：BPO 產生後模擬工廠出貨，庫務可確認收貨入庫

| # | 任務 | 層級 | 說明 | 前置任務 |
|---|------|------|------|---------|
| 2-1 | FDO Service | 後端 | 實作收貨業務邏輯：查詢待收貨清單、確認收貨、差異處理 | 無 |
| 2-2 | FDO Controller | 後端 | 實作 API：GET /api/receive/fdo, PUT /api/receive/fdo/{no}/confirm | 2-1 |
| 2-3 | Mock 工廠出貨 | 後端 | POST /api/mock/factory/delivery：根據 BPO 自動產生 FDO（含批次號、效期） | 2-1 |
| 2-4 | 庫存入庫邏輯 | 後端 | 收貨確認後，依實收數量新增/更新 Inventory 記錄（含 batchNo、expiryDate） | 2-1 |
| 2-5 | FDO Mapper + DTO | 後端 | 建立 FactoryDeliveryMapper、對應 DTO | 2-1 |
| 2-6 | 收貨頁面 | 前端 | ReceiveView.vue：待收貨清單、FDO 明細、輸入實收數量、確認收貨 | 2-2 |
| 2-7 | Mock 出貨頁面 | 前端 | MockFactoryView.vue：選擇 BPO 一鍵模擬出貨（開發輔助頁面） | 2-3 |
| 2-8 | 收貨單元測試 | 測試 | FDO Service 測試：正常收貨、差異處理、庫存更新 | 2-4 |

**驗收條件：**
- [ ] BPO 彙總後可模擬工廠出貨產生 FDO
- [ ] 庫務可查看待收貨清單，逐項輸入實收數量
- [ ] 收貨確認後庫存正確入庫（含批次、效期）
- [ ] 差異情況可正確標記 DISCREPANCY

---

### Phase 3：配貨與領貨

> 目標：庫務可依 FIFO + 優先度配貨，業務員可確認領貨

| # | 任務 | 層級 | 說明 | 前置任務 |
|---|------|------|------|---------|
| 3-1 | 配貨演算法 Service | 後端 | 實作 FIFO + SalesPriority 配貨邏輯，從大庫庫存分配批次給各業務員 | Phase 2 |
| 3-2 | AO Controller | 後端 | POST /api/allocation (執行配貨), GET /api/allocation (清單), GET /api/allocation/pending | 3-1 |
| 3-3 | 配貨庫存扣減 | 後端 | 配貨時大庫 Inventory.qty 扣減（預留），AOD 記錄分配明細 | 3-1 |
| 3-4 | 領貨 Service | 後端 | 業務員查詢待領 AOD，確認領取產生 SRO，庫存移轉至業務員儲位 | 3-1 |
| 3-5 | SRO Controller | 後端 | GET /api/receive-order/pending, POST /api/receive-order/confirm | 3-4 |
| 3-6 | AO/SRO Mapper + DTO | 後端 | 建立對應 Mapper 和 DTO | 3-1 |
| 3-7 | 配貨頁面 | 前端 | AllocationView.vue：待配貨清單、一鍵配貨、配貨結果檢視 | 3-2 |
| 3-8 | 領貨頁面 | 前端 | SalesReceiveView.vue：待領明細、點貨確認、領貨完成 | 3-5 |
| 3-9 | 配貨/領貨測試 | 測試 | 配貨演算法測試：FIFO 分配、優先度排序、庫存不足處理 | 3-3 |

**驗收條件：**
- [ ] 庫務可對已收貨的品項執行配貨
- [ ] 配貨依 FIFO 分配批次，高優先度業務員取得較長效期
- [ ] 業務員可查看待領清單並確認領取
- [ ] 庫存正確從大庫移轉至業務員儲位

---

### Phase 4：送貨作業

> 目標：業務員可接受客戶預訂、送貨、簽收，產生應收帳款

| # | 任務 | 層級 | 說明 | 前置任務 |
|---|------|------|------|---------|
| 4-1 | CPO Service | 後端 | 客戶預訂單 CRUD：建立、查詢、取消 | Phase 3 |
| 4-2 | CPO Controller | 後端 | GET/POST /api/pre-order, PUT /api/pre-order/{no}/cancel | 4-1 |
| 4-3 | SDO 自動產生 | 後端 | 領貨完成後，系統根據 CPO 自動產生 SDO + 預帶品項 | 4-1 |
| 4-4 | SDO 交易處理 | 後端 | 送達確認、現場加購、退貨、毀損、簽收 | 4-3 |
| 4-5 | AR 應收帳款 | 後端 | 簽收完成後產生 AR (銷售金額 - 退貨金額) | 4-4 |
| 4-6 | SDO 庫存異動 | 後端 | 簽收後更新業務員儲位庫存（銷售扣減、退貨增加、毀損扣減） | 4-4 |
| 4-7 | SDO/CPO Mapper + DTO | 後端 | 建立對應 Mapper 和 DTO | 4-1 |
| 4-8 | 客戶預訂頁面 | 前端 | PreOrderView.vue：客戶清單、建立預訂、預訂歷史 | 4-2 |
| 4-9 | 送貨頁面 | 前端 | DeliveryView.vue：今日送貨清單、送達確認、現場交易、簽收 | 4-4 |
| 4-10 | 應收帳款頁面 | 前端 | AccountReceivableView.vue：應收帳款清單、收款狀態 | 4-5 |
| 4-11 | 送貨流程測試 | 測試 | 預訂 → 產生 SDO → 送達 → 交易 → 簽收 → AR 全流程測試 | 4-6 |

**驗收條件：**
- [ ] 業務員可建立客戶預訂單
- [ ] 領貨完成後自動產生送貨單（預帶預訂品項）
- [ ] 現場可操作：加購、退貨、毀損
- [ ] 簽收後自動產生應收帳款，庫存正確異動

---

### Phase 5：結帳作業

> 目標：業務員下班後可寄庫/退庫，庫務可彙整營業所銷退單

| # | 任務 | 層級 | 說明 | 前置任務 |
|---|------|------|------|---------|
| 5-1 | SKR 寄庫 Service | 後端 | 業務員寄庫：車上有效貨品暫存大庫，Inventory keepQty 增加 | Phase 4 |
| 5-2 | SRR 退庫 Service | 後端 | 業務員退庫：報廢品退回大庫，Inventory returnQty 增加，記錄退庫原因 | Phase 4 |
| 5-3 | BRO 彙整 Service | 後端 | 庫務彙整退庫單，依工廠分組產生營業所銷退單 | 5-2 |
| 5-4 | SKR/SRR/BRO Controller | 後端 | 各模組 CRUD API | 5-1, 5-2, 5-3 |
| 5-5 | 寄庫領回邏輯 | 後端 | 隔天領貨時自動併入 SRO，keepQty 歸零 | 5-1 |
| 5-6 | SKR/SRR/BRO Mapper + DTO | 後端 | 建立對應 Mapper 和 DTO | 5-1 |
| 5-7 | 寄庫/退庫頁面 | 前端 | ClosingView.vue：車存庫存清單、寄庫操作、退庫操作（選擇原因） | 5-4 |
| 5-8 | 營業所銷退頁面 | 前端 | BranchReturnView.vue：待處理退庫清單、一鍵彙整、BRO 清單 | 5-4 |
| 5-9 | 結帳流程測試 | 測試 | 寄庫/退庫 → 庫存異動 → BRO 彙整 全流程測試 | 5-5 |

**驗收條件：**
- [ ] 業務員可將車上有效貨品寄庫
- [ ] 業務員可將報廢品退庫（含原因：STALE/DAMAGED/RECALLED）
- [ ] 寄庫貨品隔天自動併入領貨清單
- [ ] 庫務可彙整退庫單產生營業所銷退單

---

### Phase 6：主檔管理與系統完善

> 目標：提供管理後台、報表、系統優化

| # | 任務 | 層級 | 說明 | 前置任務 |
|---|------|------|------|---------|
| 6-1 | 產品管理頁面 | 全端 | Product CRUD：新增/編輯/停用，產品分類篩選 | 無 |
| 6-2 | 工廠管理頁面 | 全端 | Factory CRUD + ProductFactory 對應管理 | 6-1 |
| 6-3 | 客戶管理頁面 | 全端 | Customer CRUD：依營業所篩選 | 無 |
| 6-4 | 營業所管理頁面 | 全端 | Branch CRUD + Location 管理 + SalesPriority 設定 | 無 |
| 6-5 | 使用者管理頁面 | 全端 | User CRUD + 角色指派 (AuthUserBranchRole) | 無 |
| 6-6 | 營業所產品清單頁面 | 全端 | BranchProductList 管理：排序、複製、批次操作 | 6-1 |
| 6-7 | 庫存查詢頁面 | 前端 | InventoryView.vue：依營業所/儲位/產品查詢庫存，含批次效期資訊 | Phase 2 |
| 6-8 | 單位轉換管理 | 全端 | ProductUnitConversion CRUD | 6-1 |
| 6-9 | 錯誤處理優化 | 全端 | 統一錯誤回應格式、前端 Toast 通知 | 無 |
| 6-10 | 資料驗證強化 | 後端 | 加入 Bean Validation (@NotNull, @Size 等) | 無 |

**驗收條件：**
- [ ] 管理員可透過介面管理所有主檔資料
- [ ] 庫存可即時查詢，含批次效期
- [ ] 錯誤訊息使用者友善

---

## 四、模組依賴關係圖

```
Phase 0: 基礎建設
    │
    ▼
Phase 1: 訂貨流程完善
    │
    ▼
Phase 2: 收貨作業 ──────────────────────┐
    │                                   │
    ▼                                   │
Phase 3: 配貨與領貨                      │
    │                                   │
    ▼                                   │
Phase 4: 送貨作業                        │
    │                                   │
    ▼                                   │
Phase 5: 結帳作業                        │
                                        │
Phase 6: 主檔管理（可與 Phase 2+ 並行）──┘
```

> Phase 6 的主檔管理與庫存查詢可在 Phase 2 之後任意時間點穿插進行，不影響主流程。

---

## 五、單據流程全景圖

```
業務員 (SALES)                組長 (LEADER)              庫務 (WAREHOUSE)
─────────────                ─────────────              ───────────────

[Phase 1]                    [Phase 1]
建立 SPO ──────────────────► 凍結 (BPF)
(D+2~D+9)                   調整 confirmedQty
                             確認完成
                                    │
                                    ▼                    [Phase 1]
                             彙總產生 BPO ─────────────► 發送給工廠
                                                              │
                                                        [Phase 2]
                                                        模擬工廠出貨 (FDO)
                                                        收貨確認 → 入庫
                                                              │
                                                        [Phase 3]
                                                        配貨 (AO)
                                                        FIFO + 優先度
                                                              │
[Phase 3]                                                     │
領貨 (SRO) ◄──────────────────────────────────────────────────┘
    │
[Phase 4]
建立 CPO (客戶預訂)
系統產生 SDO (送貨單)
送達 → 交易 → 簽收
    │
    ├── AR (應收帳款)
    │
[Phase 5]
下班: 寄庫 (SKR) / 退庫 (SRR)
                                                        [Phase 5]
                                                        彙整退庫 → BRO
```

---

## 六、每 Phase 建議開發順序

### 開發每個功能的標準步驟

```
1. Entity 檢查/補強    ← 確認 Entity 欄位完整
2. DTO 定義            ← 定義 Request/Response DTO
3. Mapper 建立         ← MapStruct Entity ↔ DTO
4. Service 實作        ← 業務邏輯
5. Controller 實作     ← REST API
6. 單元測試            ← Service 層測試
7. 前端 API 模組       ← fetch 封裝
8. 前端頁面            ← Vue 元件
9. 整合測試            ← 端到端驗證
```

---

## 七、技術債務與改進事項

| 項目 | 現況 | 建議改進 | 優先級 |
|------|------|---------|--------|
| 前端無認證 | API 模組未帶 token | Phase 0 實作 | 高 |
| 無統一 fetch 工具 | 每個 API 模組各自處理錯誤 | Phase 0 封裝 httpClient | 高 |
| data.sql 每次重建 | ddl-auto=create | 生產環境改用 Flyway migration | 中 |
| 無前端狀態管理 | 各頁面各自管理 | 評估引入 Pinia（至少管理 auth 狀態）| 中 |
| Controller 無權限註解 | 所有已認證用戶皆可存取 | Phase 0 加上 @PreAuthorize | 高 |
| 無 API 文件 | 無 Swagger/OpenAPI | 評估加入 springdoc-openapi | 低 |
| 前端無 TypeScript | 使用純 JavaScript | 長期可考慮遷移 | 低 |

---

## 八、附錄：核心檔案索引

### 後端

| 分類 | 路徑 |
|------|------|
| Entity 基類 | `src/main/java/.../model/entity/BaseEntity.java` |
| 安全設定 | `src/main/java/.../config/SecurityConfig.java` |
| JWT 處理 | `src/main/java/.../security/JwtTokenProvider.java` |
| 序號產生器 | `src/main/java/.../service/impl/SequenceGeneratorImpl.java` |
| 全域例外處理 | `src/main/java/.../config/GlobalExceptionHandler.java` |
| 測試資料 | `src/main/resources/data.sql` |

### 前端

| 分類 | 路徑 |
|------|------|
| 進入點 | `frontend/src/main.js` |
| 路由 | `frontend/src/router/index.js` |
| 根元件 | `frontend/src/App.vue` |
| 主功能頁面 | `frontend/src/views/PurchaseBranchView.vue` |
| API 模組 | `frontend/src/api/*.js` |
| Vite 設定 | `frontend/vite.config.js` |

---
