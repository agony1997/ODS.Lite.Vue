# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 專案概述

Mock ODS VUE 是一個模擬 ODS (訂貨配送系統) 的全端應用程式，採用前後端分離架構：
- **前端**: Vue 3 + Quasar 2 + Vite
- **後端**: Spring Boot 3.4.1 + PostgreSQL + Spring Security (JWT)

## 開發指令

### 前端開發

```bash
# 在 frontend/ 目錄下執行
npm install           # 首次安裝依賴
npm run dev           # 啟動開發伺服器 (http://localhost:5173)
npm run build         # 建置生產版本至 ../src/main/resources/static/
npm run preview       # 預覽建置結果
```

### 後端開發

```bash
# 在專案根目錄執行
mvn spring-boot:run   # 啟動 Spring Boot 應用 (http://localhost:8080)
mvn clean install     # 清理並建置專案
mvn test              # 執行測試
```

**資料庫需求**: 確保 PostgreSQL 運行於 localhost:5432，資料庫名稱為 `mock_ods_vue`

## 架構設計

### 前後端整合

- **開發模式**: 前端 Vite dev server (5173) 透過 proxy 將 `/api/*` 請求轉發至後端 (8080)
- **生產模式**: 前端建置產物輸出至 `src/main/resources/static/`，由 Spring Boot 統一提供服務
- **路徑別名**: `@` 指向 `frontend/src/`

### 後端架構

採用標準的分層架構：

```
controller/          # REST API 端點
  ├─ @RequestMapping 定義路由
  └─ 注入 Service 層處理業務邏輯

service/             # 業務邏輯層
  ├─ 處理業務規則與流程
  └─ 注入 Repository 進行資料存取

repository/          # 資料存取層 (Spring Data JPA)
  └─ 繼承 JpaRepository 提供 CRUD 操作

mapper/              # MapStruct DTO 轉換
  └─ Entity ↔ DTO 自動映射

model/
  ├─ entity/         # JPA 實體類別
  │   ├─ AuditEntity  # 所有 Entity 的基礎類別，包含 Audit 欄位
  │   ├─ allocation/ # 配貨相關
  │   ├─ auth/       # 權限管理
  │   ├─ branch/     # 營業所資料
  │   ├─ closing/    # 結帳相關
  │   ├─ delivery/   # 配送相關
  │   ├─ inventory/  # 庫存
  │   ├─ master/     # 主檔資料
  │   ├─ purchase/   # 訂貨相關
  │   ├─ receive/    # 收貨相關
  │   └─ sequence/   # 序號管理
  └─ dto/            # 資料傳輸物件
```

### 前端架構

```
src/
  ├─ main.js         # 應用程式進入點
  ├─ App.vue         # 根組件
  ├─ router/         # Vue Router 路由配置
  ├─ views/          # 頁面級組件
  │   ├─ IndexView.vue
  │   ├─ PurchaseBranchView.vue  # 訂貨門市頁面
  │   └─ PurchaseSalesView.vue   # 訂貨業務頁面
  └─ api/            # API 呼叫模組
      ├─ branch.js
      ├─ branchProductList.js
      └─ branchPurchase.js
```

### 關鍵技術整合

**JPA Auditing**: 所有 Entity 繼承 `AuditEntity`，自動記錄 `createdAt`、`updatedAt`、`createdBy`、`updatedBy`
- 配置於 `JpaAuditingConfig` + `AuditorAwareImpl`
- `createdBy`/`updatedBy` 從 Spring Security Context 獲取當前使用者

**MapStruct**: 編譯時期自動生成 Entity ↔ DTO 轉換程式碼
- 設定 `mapstruct.defaultComponentModel=spring` 自動註冊為 Spring Bean
- 需同時配置 `lombok-mapstruct-binding` 以支援 Lombok

**Spring Security + JWT**:
- JWT 密鑰與過期時間設定於 `application.properties`
- Controller 方法可透過 `@AuthenticationPrincipal UserDetails` 取得當前使用者

### 資料初始化

- `spring.jpa.hibernate.ddl-auto=create`: 每次啟動重建資料表
- `spring.sql.init.mode=always`: 每次啟動執行 `data.sql` 插入測試資料
- 測試資料包含味全產品、工廠、客戶等主檔資料

## API 設計模式

所有 API 端點以 `/api/` 為前綴，例如：
- `GET /api/purchase/branch/summary?branchCode=xxx&date=yyyy-MM-dd`
- `PUT /api/purchase/branch/summary`
- `POST /api/purchase/branch/freeze`

前端 API 模組使用原生 `fetch`，統一錯誤處理模式。
