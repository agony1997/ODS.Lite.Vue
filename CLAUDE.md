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

### 後端分層

```
src/main/java/com/example/mockodsvue/
├── controller/       # REST API（/api/ 前綴）
├── service/          # 業務邏輯
├── repository/       # Spring Data JPA
├── mapper/           # MapStruct DTO 轉換
├── model/
│   ├── entity/       # JPA 實體（繼承 BaseEntity，含 Audit 欄位）
│   │   ├── allocation/  # 配貨
│   │   ├── auth/        # 權限
│   │   ├── branch/      # 營業所
│   │   ├── closing/     # 結帳
│   │   ├── delivery/    # 配送
│   │   ├── inventory/   # 庫存
│   │   ├── master/      # 主檔
│   │   ├── purchase/    # 訂貨
│   │   ├── receive/     # 收貨
│   │   └── sequence/    # 序號
│   └── dto/          # 資料傳輸物件
└── config/           # 設定（Security, JPA Auditing 等）
```

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

- **JPA Auditing**: BaseEntity 自動記錄 createdAt/updatedAt/createdBy/updatedBy
- **MapStruct**: 編譯時生成 Entity ↔ DTO 轉換，需搭配 lombok-mapstruct-binding
- **Spring Security + JWT**: 密鑰設定於 application.properties
- **資料初始化**: ddl-auto=create + data.sql 每次啟動重建

## 慣例

- 所有 API 端點以 `/api/` 為前綴
- 前端 API 模組使用原生 `fetch`
- Entity 必須繼承 BaseEntity
- DTO 與 Entity 透過 MapStruct Mapper 轉換
- 回應語言使用繁體中文
