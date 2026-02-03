<template>
  <q-page padding>
    <!-- 標題 -->
    <div class="text-h5 q-mb-md">營業所訂貨彙總</div>

    <!-- 搜尋區 -->
    <q-card class="q-mb-md">
      <q-card-section>
        <div class="row q-gutter-md items-end">
          <q-select
            v-model="searchForm.branchCode"
            :options="branchOptions"
            label="營業所"
            emit-value
            map-options
            dense
            outlined
            style="min-width: 200px"
          />
          <q-input
            v-model="searchForm.date"
            type="date"
            label="訂貨日期"
            dense
            outlined
            style="min-width: 180px"
          />
          <q-btn
            color="primary"
            label="查詢"
            icon="search"
            :loading="loading"
            @click="fetchSummary"
          />
        </div>
      </q-card-section>
    </q-card>

    <!-- 狀態與操作區 -->
    <q-card class="q-mb-md" v-if="summary">
      <q-card-section>
        <div class="row items-center q-gutter-md">
          <div>
            <q-chip
              :color="statusColor"
              text-color="white"
              :label="statusLabel"
              icon="info"
            />
          </div>
          <q-space />
          <q-btn
            v-if="!summary.frozenStatus"
            color="orange"
            label="凍結"
            icon="lock"
            :loading="actionLoading"
            @click="handleFreeze"
          />
          <q-btn
            v-if="summary.frozenStatus === 'FROZEN'"
            color="grey"
            label="解除凍結"
            icon="lock_open"
            :loading="actionLoading"
            @click="handleUnfreeze"
          />
          <q-btn
            v-if="summary.frozenStatus === 'FROZEN'"
            color="green"
            label="確認完成"
            icon="check"
            :loading="actionLoading"
            @click="handleConfirm"
          />
          <q-btn
            v-if="summary.frozenStatus === 'CONFIRMED'"
            color="purple"
            label="執行彙總"
            icon="merge_type"
            :loading="actionLoading"
            @click="handleAggregate"
          />
          <q-btn
            color="blue"
            label="查看 BPO"
            icon="list_alt"
            outline
            @click="showBpoDialog = true; fetchBpoList()"
          />
          <q-btn
            v-if="!summary.frozenStatus"
            color="teal"
            label="新增產品"
            icon="add"
            outline
            @click="openAddProductDialog"
          />
        </div>
      </q-card-section>
    </q-card>

    <!-- 彙總表格 -->
    <q-card v-if="summary">
      <q-card-section>
        <div class="text-subtitle1 q-mb-sm">彙總明細</div>
        <q-table
          :rows="summary.details"
          :columns="tableColumns"
          row-key="productCode"
          dense
          flat
          bordered
          :pagination="{ rowsPerPage: 50 }"
        >
          <!-- 確認數量欄位 (可編輯) -->
          <template v-slot:body-cell-confirmedQty="props">
            <q-td :props="props">
              <q-input
                v-if="summary.frozenStatus === 'FROZEN'"
                v-model.number="props.row.confirmedQty"
                type="number"
                dense
                outlined
                style="width: 80px"
                @change="markDirty(props.row)"
              />
              <span v-else>{{ props.row.confirmedQty }}</span>
            </q-td>
          </template>

          <!-- 增減數量欄位 -->
          <template v-slot:body-cell-diffQty="props">
            <q-td :props="props">
              <span :class="diffQtyClass(props.row)">
                {{ formatDiff(props.row.confirmedQty - props.row.totalQty) }}
              </span>
            </q-td>
          </template>

          <!-- 動態儲位欄位 -->
          <template v-for="loc in summary.locations" :key="loc.locationCode"
                    v-slot:[`body-cell-loc_${loc.locationCode}`]="props">
            <q-td :props="props">
              {{ props.row.locationQtyMap?.[loc.locationCode] || 0 }}
            </q-td>
          </template>

          <!-- 刪除按鈕 -->
          <template v-slot:body-cell-actions="props">
            <q-td :props="props">
              <q-btn
                v-if="!summary.frozenStatus"
                icon="delete"
                color="red"
                flat
                dense
                round
                @click="handleDeleteProduct(props.row)"
              />
            </q-td>
          </template>
        </q-table>

        <!-- 儲存按鈕 -->
        <div class="q-mt-md" v-if="summary.frozenStatus === 'FROZEN' && dirtyRows.size > 0">
          <q-btn
            color="primary"
            label="儲存變更"
            icon="save"
            :loading="saveLoading"
            @click="handleSave"
          />
          <span class="q-ml-md text-grey">{{ dirtyRows.size }} 筆資料已修改</span>
        </div>
      </q-card-section>
    </q-card>

    <!-- 無資料提示 -->
    <q-card v-if="summary && summary.details.length === 0">
      <q-card-section class="text-center text-grey">
        目前沒有訂貨資料
      </q-card-section>
    </q-card>

    <!-- BPO 清單對話框 -->
    <q-dialog v-model="showBpoDialog" maximized>
      <q-card>
        <q-card-section class="row items-center">
          <div class="text-h6">營業所訂貨單 (BPO) 清單</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section>
          <div v-if="bpoList.length === 0" class="text-center text-grey q-pa-lg">
            尚無 BPO 資料
          </div>

          <div v-for="bpo in bpoList" :key="bpo.bpoNo" class="q-mb-lg">
            <div class="text-subtitle1 q-mb-sm">
              <q-icon name="receipt" class="q-mr-sm" />
              {{ bpo.bpoNo }}
              <q-chip size="sm" color="blue" text-color="white" class="q-ml-sm">
                工廠: {{ bpo.factoryCode }}
              </q-chip>
              <q-chip size="sm" :color="bpo.status === 'PENDING' ? 'orange' : 'green'" text-color="white">
                {{ bpo.status === 'PENDING' ? '待收貨' : '已收貨' }}
              </q-chip>
            </div>
            <q-table
              :rows="bpo.details"
              :columns="bpoColumns"
              row-key="itemNo"
              dense
              flat
              bordered
              :pagination="{ rowsPerPage: 0 }"
              hide-bottom
            />
          </div>
        </q-card-section>
      </q-card>
    </q-dialog>

    <!-- 新增產品對話框 -->
    <q-dialog v-model="showAddProductDialog">
      <q-card style="min-width: 500px; max-width: 700px">
        <q-card-section class="row items-center">
          <div class="text-h6">新增產品</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section>
          <q-input
            v-model="productFilter"
            label="搜尋產品"
            dense
            outlined
            class="q-mb-md"
            clearable
          >
            <template v-slot:prepend>
              <q-icon name="search" />
            </template>
          </q-input>

          <q-table
            :rows="filteredBranchProducts"
            :columns="addProductColumns"
            row-key="productCode"
            dense
            flat
            bordered
            :pagination="{ rowsPerPage: 15 }"
            selection="multiple"
            v-model:selected="selectedProducts"
          />
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="取消" v-close-popup />
          <q-btn
            color="primary"
            label="加入選取的產品"
            icon="add"
            :disable="selectedProducts.length === 0"
            @click="handleAddProducts"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useNotify } from '../composables/useNotify'
import * as branchApi from '../api/branch'
import * as branchPurchaseApi from '../api/branchPurchase'
import * as branchProductListApi from '../api/branchProductList'

const { notifyWarning, handleError, confirmAction } = useNotify()

// 搜尋表單
const searchForm = ref({
  branchCode: '',
  date: getDefaultDate()
})

// 資料狀態
const loading = ref(false)
const actionLoading = ref(false)
const saveLoading = ref(false)
const summary = ref(null)
const branchOptions = ref([])
const dirtyRows = ref(new Set())

// BPO 對話框
const showBpoDialog = ref(false)
const bpoList = ref([])

// 預設日期 (後天)
function getDefaultDate() {
  const date = new Date()
  date.setDate(date.getDate() + 2)
  return date.toISOString().split('T')[0]
}

// 狀態標籤
const statusLabel = computed(() => {
  if (!summary.value) return ''
  switch (summary.value.frozenStatus) {
    case 'FROZEN': return '已凍結'
    case 'CONFIRMED': return '已確認'
    default: return '開放中'
  }
})

const statusColor = computed(() => {
  if (!summary.value) return 'grey'
  switch (summary.value.frozenStatus) {
    case 'FROZEN': return 'orange'
    case 'CONFIRMED': return 'green'
    default: return 'blue'
  }
})

// 表格欄位 (動態產生)
const tableColumns = computed(() => {
  const cols = [
    { name: 'productCode', label: '產品代碼', field: 'productCode', align: 'left', sortable: true },
    { name: 'productName', label: '產品名稱', field: 'productName', align: 'left' },
    { name: 'unit', label: '單位', field: 'unit', align: 'center' },
    { name: 'confirmedQty', label: '確認數量', field: 'confirmedQty', align: 'right' },
    { name: 'totalQty', label: '原始數量', field: 'totalQty', align: 'right' },
    { name: 'diffQty', label: '增減', field: 'diffQty', align: 'right' },
    { name: 'actions', label: '操作', align: 'center' }
  ]

  // 動態加入儲位欄位
  if (summary.value?.locations) {
    for (const loc of summary.value.locations) {
      cols.push({
        name: `loc_${loc.locationCode}`,
        label: loc.locationName || loc.locationCode,
        field: row => row.locationQtyMap?.[loc.locationCode] || 0,
        align: 'right'
      })
    }
  }

  return cols
})

// BPO 表格欄位
const bpoColumns = [
  { name: 'itemNo', label: '項次', field: 'itemNo', align: 'center' },
  { name: 'productCode', label: '產品代碼', field: 'productCode', align: 'left' },
  { name: 'productName', label: '產品名稱', field: 'productName', align: 'left' },
  { name: 'unit', label: '單位', field: 'unit', align: 'center' },
  { name: 'qty', label: '數量', field: 'qty', align: 'right' }
]

// 增減數量樣式
function diffQtyClass(row) {
  const diff = row.confirmedQty - row.totalQty
  if (diff > 0) return 'text-green'
  if (diff < 0) return 'text-red'
  return ''
}

function formatDiff(diff) {
  if (diff > 0) return `+${diff}`
  return diff.toString()
}

// 標記為已修改
function markDirty(row) {
  dirtyRows.value.add(`${row.productCode}-${row.unit}`)
}

// 載入營業所選項
async function loadBranches() {
  try {
    const data = await branchApi.getAllEnabled()
    branchOptions.value = data.map(b => ({
      label: `${b.branchCode} - ${b.branchName}`,
      value: b.branchCode
    }))
    if (branchOptions.value.length > 0 && !searchForm.value.branchCode) {
      searchForm.value.branchCode = branchOptions.value[0].value
    }
  } catch (e) {
    handleError(e)
  }
}

// 查詢彙總資料
async function fetchSummary() {
  if (!searchForm.value.branchCode || !searchForm.value.date) return

  loading.value = true
  dirtyRows.value.clear()
  try {
    summary.value = await branchPurchaseApi.getSummary(
      searchForm.value.branchCode,
      searchForm.value.date
    )
  } catch (e) {
    handleError(e)
  } finally {
    loading.value = false
  }
}

// 凍結
async function handleFreeze() {
  const ok = await confirmAction('確定要凍結此營業所的訂單嗎？凍結後業務員將無法修改。')
  if (!ok) return

  actionLoading.value = true
  try {
    summary.value = await branchPurchaseApi.freeze(
      searchForm.value.branchCode,
      searchForm.value.date
    )
  } catch (e) {
    handleError(e)
  } finally {
    actionLoading.value = false
  }
}

// 解除凍結
async function handleUnfreeze() {
  const ok = await confirmAction('確定要解除凍結嗎？業務員將可以再次修改訂單。')
  if (!ok) return

  actionLoading.value = true
  try {
    summary.value = await branchPurchaseApi.unfreeze(
      searchForm.value.branchCode,
      searchForm.value.date
    )
    dirtyRows.value.clear()
  } catch (e) {
    handleError(e)
  } finally {
    actionLoading.value = false
  }
}

// 確認完成
async function handleConfirm() {
  if (dirtyRows.value.size > 0) {
    notifyWarning('請先儲存變更後再確認')
    return
  }
  const ok = await confirmAction('確定要確認完成嗎？確認後將無法再修改確認數量。')
  if (!ok) return

  actionLoading.value = true
  try {
    summary.value = await branchPurchaseApi.confirm(
      searchForm.value.branchCode,
      searchForm.value.date
    )
  } catch (e) {
    handleError(e)
  } finally {
    actionLoading.value = false
  }
}

// 執行彙總
async function handleAggregate() {
  const ok = await confirmAction('確定要執行彙總嗎？將會建立 BPO 訂貨單。')
  if (!ok) return

  actionLoading.value = true
  try {
    bpoList.value = await branchPurchaseApi.aggregate(
      searchForm.value.branchCode,
      searchForm.value.date
    )
    showBpoDialog.value = true
    await fetchSummary()
  } catch (e) {
    handleError(e)
  } finally {
    actionLoading.value = false
  }
}

// 儲存變更
async function handleSave() {
  saveLoading.value = true
  try {
    const details = summary.value.details.map(d => ({
      productCode: d.productCode,
      unit: d.unit,
      confirmedQty: d.confirmedQty
    }))

    summary.value = await branchPurchaseApi.updateConfirmedQty({
      branchCode: searchForm.value.branchCode,
      purchaseDate: searchForm.value.date,
      details
    })
    dirtyRows.value.clear()
  } catch (e) {
    handleError(e)
  } finally {
    saveLoading.value = false
  }
}

// 查詢 BPO 清單
async function fetchBpoList() {
  try {
    bpoList.value = await branchPurchaseApi.getBpoList(
      searchForm.value.branchCode,
      searchForm.value.date
    )
  } catch (e) {
    handleError(e)
  }
}

// 新增產品對話框
const showAddProductDialog = ref(false)
const branchProducts = ref([])
const selectedProducts = ref([])
const productFilter = ref('')

const addProductColumns = [
  { name: 'productCode', label: '產品代碼', field: 'productCode', align: 'left', sortable: true },
  { name: 'productName', label: '產品名稱', field: 'productName', align: 'left' },
  { name: 'unit', label: '單位', field: 'unit', align: 'center' }
]

const filteredBranchProducts = computed(() => {
  const existingCodes = new Set(summary.value?.details?.map(d => d.productCode) || [])
  let list = branchProducts.value.filter(p => !existingCodes.has(p.productCode))
  if (productFilter.value) {
    const kw = productFilter.value.toLowerCase()
    list = list.filter(p =>
      p.productCode.toLowerCase().includes(kw) ||
      (p.productName && p.productName.toLowerCase().includes(kw))
    )
  }
  return list
})

async function openAddProductDialog() {
  showAddProductDialog.value = true
  selectedProducts.value = []
  productFilter.value = ''
  try {
    branchProducts.value = await branchProductListApi.getByBranchCode(searchForm.value.branchCode)
  } catch (e) {
    handleError(e)
  }
}

function handleAddProducts() {
  if (!summary.value || selectedProducts.value.length === 0) return
  for (const p of selectedProducts.value) {
    summary.value.details.push({
      productCode: p.productCode,
      productName: p.productName,
      unit: p.unit,
      confirmedQty: 0,
      totalQty: 0,
      locationQtyMap: {}
    })
  }
  showAddProductDialog.value = false
}

async function handleDeleteProduct(row) {
  const ok = await confirmAction(`確定要移除產品 ${row.productCode} - ${row.productName}？`)
  if (!ok) return
  const idx = summary.value.details.findIndex(d => d.productCode === row.productCode)
  if (idx >= 0) {
    summary.value.details.splice(idx, 1)
  }
}

// 初始化
onMounted(async () => {
  await loadBranches()
  if (searchForm.value.branchCode) {
    await fetchSummary()
  }
})
</script>

<style scoped>
.text-green {
  color: #21ba45;
  font-weight: bold;
}

.text-red {
  color: #c10015;
  font-weight: bold;
}
</style>
