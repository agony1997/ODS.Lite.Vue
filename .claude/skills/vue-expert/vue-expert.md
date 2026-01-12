---
name: vue-expert
description: 精通 Vue 3 (Composition API)、TypeScript 與 Vite 工具鏈。當處理 .vue, .ts, 或前端架構設計時觸發。
allowed-tools: ["Bash", "Read", "Write"]
---

# Vue.js 前端專家技能

## 核心技術棧
- **Framework**: Vue 3 (Script Setup)
- **State Management**: Pinia
- **Styling**: Tailwind CSS
- **API Client**: Axios (封裝為統一的 request utility)

## 開發準則 (Code Excellence)
1. **元件化原則**：遵循 Atomic Design 或單一職責原則，單個 `.vue` 檔案建議不超過 300 行。
2. **型別安全**：嚴格使用 TypeScript 定義 Prop 與 Emit，禁止使用 `any`。
3. **異步處理**：所有的 API 調用必須包含 Loading 狀態處理與全域 Error Catch 邏輯。
4. **效能優化**：
    - 合理使用 `v-if` 與 `v-show`。
    - 列表渲染必須提供唯一且穩定的 `:key`。
    - 評估使用 `defineAsyncComponent` 進行路由懶加載。

## 任務流程
- 當後端 API 變動時，優先更新 `@/types` 下的介面定義。
- 確保元件具備良好的「反應式 (Reactivity)」效能，避免不必要的 `watch`。