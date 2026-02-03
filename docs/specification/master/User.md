使用者規格書
===
---

## 概述

系統使用者，包含業務員、組長、庫務、系統管理員等角色。

---

## 資料結構

### AuthUser（使用者）

| 欄位 | 型別 | 說明 |
|------|------|------|
| userCode | String | 員工編號 (PK) |
| userName | String | 姓名 |
| password | String | 密碼（加密） |
| branchCode | String | 主要所屬營業所 |
| email | String | 電子郵件 |
| phone | String | 電話 |
| status | String | 狀態 (ACTIVE / INACTIVE) |

### AuthRole（角色）

| 欄位 | 型別 | 說明 |
|------|------|------|
| roleCode | String | 角色代碼 (PK) |
| roleName | String | 角色名稱 |
| description | String | 說明 |

### AuthUserBranchRole（使用者營業所角色）

使用者在不同營業所可有不同角色：

| 欄位 | 型別 | 說明 |
|------|------|------|
| id | Long | 主鍵 |
| userCode | String | 員工編號 |
| branchCode | String | 營業所代碼 |
| roleCode | String | 角色代碼 |

唯一鍵：(userCode, branchCode, roleCode)

---

## 角色定義

| 角色代碼 | 名稱 | 權限說明 |
|----------|------|----------|
| SALES | 業務員 | 訂貨、領貨、送貨、寄庫、退庫 |
| LEADER | 組長 | 凍結、調整確認數量、業務員管理 |
| WAREHOUSE | 庫務 | 收貨、配貨、彙整銷退 |
| ADMIN | 系統管理員 | 全部權限 |

---

## 關聯關係

```
AuthUser (1) ─────< AuthUserBranchRole (N) >───── AuthRole (1)
                           │
                           └───── Branch (1)

AuthUser (1) ─────< Location (N)   // 一個業務員可有多儲位
```

### 範例：多營業所角色

| userCode | branchCode | roleCode | 說明 |
|--------|------------|----------|------|
| U001 | 1000 | LEADER | U001 在營業所 1000 是組長 |
| U001 | 1000 | SALES | U001 在營業所 1000 也是業務員 |
| U001 | 2000 | SALES | U001 在營業所 2000 是業務員 |
| U002 | 1000 | WAREHOUSE | U002 在營業所 1000 是庫務 |
| U002 | 2000 | WAREHOUSE | U002 在營業所 2000 也是庫務 |

### 業務員與儲位

一個業務員可以負責多個儲位（可能在不同營業所）：

```
User U001
├── 營業所 1000
│   ├── 角色: LEADER + SALES
│   └── 儲位: S001
│
└── 營業所 2000
    ├── 角色: SALES
    └── 儲位: S003
```

---

## 權限矩陣

| 功能 | SALES | LEADER | WAREHOUSE | ADMIN |
|------|-------|--------|-----------|-------|
| 建立 SPO | ✅ | ✅ | - | ✅ |
| 凍結 BPF | - | ✅ | - | ✅ |
| 調整 confirmedQty | - | ✅ | - | ✅ |
| 收貨 FDO | - | - | ✅ | ✅ |
| 配貨 AO | - | - | ✅ | ✅ |
| 領貨 SRO | ✅ | - | - | ✅ |
| 送貨 SDO | ✅ | - | - | ✅ |
| 寄庫 SKR | ✅ | - | - | ✅ |
| 退庫 SRR | ✅ | - | - | ✅ |
| 彙整 BRO | - | - | ✅ | ✅ |

---

## API 設計

| 方法 | 端點 | 說明 |
|------|------|------|
| GET | /api/user | 查詢使用者清單 |
| GET | /api/user/{userCode} | 查詢單一使用者 |
| GET | /api/user/branch/{branchCode} | 查詢營業所下的使用者 |
| GET | /api/role | 查詢角色清單 |
| GET | /api/user/{userCode}/roles | 查詢使用者的角色 |

---

## 相關規格書

- [營業所主檔規格書](./Branch.md)
- [儲位主檔規格書](./Location.md)

---
