# Mock ODS VUE

模擬 ODS 系統的 Vue 前端專案，搭配 Spring Boot 後端。

## 技術棧

**前端**
- Vue 3
- Vite
- Quasar 2
- Vue Router

**後端**
- Spring Boot
- Java

## 專案結構

```
Mock-ODS-VUE/
├── frontend/                      # 前端專案
│   ├── src/
│   │   ├── main.js               # 應用入口
│   │   ├── App.vue               # 根組件
│   │   ├── router/               # 路由配置
│   │   └── views/                # 頁面組件
│   ├── package.json
│   └── vite.config.js
├── src/main/java/                 # 後端 Java 程式碼
└── src/main/resources/static/     # 前端建置輸出
```

## 開發指南

### 前端開發

```bash
cd frontend
npm install     # 安裝依賴（首次）
npm run dev     # 啟動開發伺服器 http://localhost:5173
```

開發模式下修改程式碼會自動熱更新。

### 後端開發

使用 IDE（IntelliJ IDEA）直接啟動 Spring Boot Application。

### 建置部署

```bash
cd frontend
npm run build   # 輸出至 src/main/resources/static/
```

建置完成後啟動 Spring Boot，訪問 http://localhost:8080 即可。

## 頁面路由

| 路徑 | 頁面 |
|------|------|
| `/` | 首頁 |
| `/purchase-branch` | 訂貨門市 |
| `/purchase-sales` | 訂貨業務 |
