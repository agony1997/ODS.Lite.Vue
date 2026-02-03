# CLAUDE.md

This file provides guidance to Claude Code when working with this repository.

## 最高守則

- **禁止自動 push**：絕對不可自動執行 `git push`。所有推送操作必須由使用者明確指示後才能執行。

## 專案概述

Mock ODS VUE 是模擬 ODS（訂貨配送系統）的全端應用程式，前後端分離架構。

- **前端**: Vue 3 + Quasar 2 + Vite（位於 `frontend/`）
- **後端**: Spring Boot 3.4.1 + Java 17 + PostgreSQL + Spring Security (JWT)
- **建置工具**: Maven（後端）、npm + Vite（前端）

## 開發指令

### 前端（在 `frontend/` 目錄下）

```bash
npm install        # 安裝依賴
npm run dev        # 啟動開發伺服器 http://localhost:5173
npm run build      # 建置至 ../src/main/resources/static/
```

### 後端（在專案根目錄）

```bash
mvnw.cmd compile   # Windows 編譯
mvnw.cmd test      # 執行測試
mvn spring-boot:run  # 啟動應用 http://localhost:8080
```

### Docker

```bash
docker-compose up -d   # 啟動 PostgreSQL + pgAdmin
```

- PostgreSQL: localhost:5432, DB: `mock_ods_vue`, user: `postgres`, password: `password`
- pgAdmin: http://localhost:5050

## 架構

### 後端分層（Pragmatic DDD - Package-by-Domain）

```
src/main/java/com/example/mockodsvue/
├── MockOdsVueApplication.java     # 進入點
│
├── shared/                        # 跨領域基礎設施
│   ├── config/                    # SecurityConfig, JpaAuditingConfig, GlobalExceptionHandler
│   ├── security/                  # JWT (JwtTokenProvider, JwtAuthenticationFilter, UserDetailsServiceImpl)
│   ├── exception/                 # BusinessException
│   └── model/                     # AuditEntity, Sortable, BatchSortable
│
├── auth/                          # 認證授權領域
│   ├── controller/                # AuthController, UserController, RoleController
│   ├── service/                   # AuthService, UserService, RoleService
│   ├── repository/                # AuthUserRepository, AuthRoleRepository, AuthUserBranchRoleRepository
│   └── model/entity/ + dto/       # AuthUser, AuthRole, LoginRequest, LoginResponse...
│
├── branch/                        # 營業所領域
│   ├── controller/                # BranchController, BranchProductListController
│   ├── service/                   # BranchProductListService
│   ├── repository/                # BranchRepository, LocationRepository...
│   └── model/entity/ + dto/ + enums/
│
├── purchase/                      # 訂貨領域（核心業務）
│   ├── controller/                # SalesPurchaseController, BranchPurchaseController
│   ├── service/                   # SalesPurchaseOrderService, BranchPurchaseService
│   ├── mapper/                    # SalesPurchaseMapper, BranchPurchaseMapper
│   ├── repository/                # SPO/SPOD/BPO/BPOD/BPF 等 repository
│   └── model/entity/ + dto/ + enums/
│
├── master/                        # 主檔資料領域（僅 repository + entity）
│   ├── repository/                # ProductRepository, FactoryRepository...
│   └── model/entity/ + enums/     # Product, Factory, Customer...
│
├── sequence/                      # 序號產生器領域
│   ├── service/                   # SequenceGenerator interface + impl
│   ├── repository/                # DocumentSequenceRepository
│   └── model/entity/ + enums/     # DocumentSequence, SequenceType
│
├── allocation/                    # 配貨領域（休眠）
├── delivery/                      # 配送領域（休眠）
├── closing/                       # 結帳退貨領域（休眠）
├── inventory/                     # 庫存領域（休眠）
└── receive/                       # 收貨領域（休眠）
```

**跨領域依賴**：purchase → branch, master, sequence；shared.security → auth

### 前端結構

```
frontend/src/
├── main.js           # 進入點
├── App.vue           # 根組件
├── router/           # Vue Router
├── views/            # 頁面組件
├── api/              # API 呼叫模組（原生 fetch）
└── styles/           # 樣式
```

### 前後端整合

- **開發模式**: Vite proxy 將 `/api/*` 轉發至 localhost:8080
- **生產模式**: 前端建置輸出至 `src/main/resources/static/`
- **路徑別名**: `@` → `frontend/src/`

## 技術重點

- **JPA Auditing**: AuditEntity 自動記錄 createdAt/updatedAt/createdBy/updatedBy
- **MapStruct**: 編譯時生成 Entity ↔ DTO 轉換，需搭配 lombok-mapstruct-binding
- **Spring Security + JWT**: 密鑰設定於 application.properties
- **資料初始化**: ddl-auto=create + data.sql 每次啟動重建

## 慣例

- 所有 API 端點以 `/api/` 為前綴
- 前端 API 模組使用原生 `fetch`
- Entity 必須繼承 AuditEntity
- DTO 與 Entity 透過 MapStruct Mapper 轉換
- 回應語言使用繁體中文
