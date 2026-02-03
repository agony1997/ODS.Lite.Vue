<template>
  <q-page padding>
    <div class="text-h5 q-mb-md">業務員訂貨</div>

    <!-- 日期選擇與載入按鈕 -->
    <q-card class="q-mb-md">
      <q-card-section>
        <div class="row q-gutter-md items-end">
          <q-select
            v-model="selectedDate"
            :options="dateOptions"
            label="訂貨日期"
            emit-value
            map-options
            dense
            outlined
            style="min-width: 200px"
          />
          <q-btn
            color="secondary"
            label="載入昨日"
            icon="history"
            :loading="loadLoading"
            :disable="isFrozen"
            @click="handleLoadYesterday"
          />
          <q-btn
            color="secondary"
            label="載入自訂清單"
            icon="list"
            :loading="loadLoading"
            :disable="isFrozen"
            @click="handleLoadCustom"
          />
          <q-btn
            color="secondary"
            label="載入營業所清單"
            icon="store"
            :loading="loadLoading"
            :disable="isFrozen"
            @click="handleLoadBranch"
          />
        </div>
      </q-card-section>
    </q-card>

    <!-- 凍結提示 -->
    <q-banner v-if="isFrozen" class="bg-orange text-white q-mb-md" rounded>
      <template v-slot:avatar>
        <q-icon name="warning" />
      </template>
      組長已凍結，無法編輯
    </q-banner>

    <!-- 訂貨表格 -->
    <q-card v-if="details.length > 0">
      <q-card-section>
        <div class="text-subtitle1 q-mb-sm">訂貨明細</div>
        <q-table
          :rows="details"
          :columns="tableColumns"
          row-key="productCode"
          dense
          flat
          bordered
          :pagination="{ rowsPerPage: 50 }"
        >
          <template v-slot:body-cell-qty="props">
            <q-td :props="props">
              <q-input
                v-if="!isFrozen"
                v-model.number="props.row.qty"
                type="number"
                dense
                outlined
                :min="0"
                style="width: 90px"
                @update:model-value="markDirty(props.row)"
              />
              <span v-else>{{ props.row.qty }}</span>
            </q-td>
          </template>

        </q-table>

        <div class="q-mt-md row q-gutter-md">
          <q-btn
            color="primary"
            label="儲存訂貨單"
            icon="save"
            :loading="saveLoading"
            :disable="isFrozen || dirtyRows.size === 0"
            @click="handleSave"
          />
          <q-space />
          <q-btn
            color="teal"
            label="管理自訂清單"
            icon="edit_note"
            outline
            @click="openCustomListDialog"
          />
        </div>
      </q-card-section>
    </q-card>

    <!-- 無資料時仍顯示管理自訂清單按鈕 -->
    <q-card v-if="details.length === 0 && !loading">
      <q-card-section class="text-center text-grey">
        <div class="q-mb-md">目前沒有訂貨資料，請載入清單開始訂貨</div>
        <q-btn
          color="teal"
          label="管理自訂清單"
          icon="edit_note"
          outline
          @click="openCustomListDialog"
        />
      </q-card-section>
    </q-card>

    <!-- 自訂清單管理對話框 -->
    <q-dialog v-model="showCustomDialog">
      <q-card style="min-width: 500px; max-width: 700px">
        <q-card-section class="row items-center">
          <div class="text-h6">管理自訂清單</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section>
          <div class="row q-gutter-sm items-end q-mb-md">
            <q-input
              v-model="newProductCode"
              label="產品代碼"
              dense
              outlined
              style="min-width: 150px"
            />
            <q-input
              v-model="newProductName"
              label="產品名稱"
              dense
              outlined
              style="min-width: 200px"
            />
            <q-btn
              color="primary"
              icon="add"
              label="新增"
              dense
              :disable="!newProductCode"
              @click="addCustomItem"
            />
          </div>

          <q-table
            :rows="customList"
            :columns="customListColumns"
            row-key="productCode"
            dense
            flat
            bordered
            :pagination="{ rowsPerPage: 20 }"
          >
            <template v-slot:body-cell-actions="props">
              <q-td :props="props">
                <q-btn
                  icon="delete"
                  color="red"
                  flat
                  dense
                  round
                  @click="removeCustomItem(props.rowIndex)"
                />
              </q-td>
            </template>
          </q-table>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="取消" v-close-popup />
          <q-btn
            color="primary"
            label="儲存清單"
            icon="save"
            :loading="customSaveLoading"
            @click="handleSaveCustomList"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useNotify } from '../composables/useNotify'
import * as salesPurchaseApi from '../api/salesPurchase'

const { notifySuccess, notifyWarning, handleError } = useNotify()

// 日期選項 D+2 ~ D+9
function buildDateOptions() {
  const options = []
  for (let i = 2; i <= 9; i++) {
    const d = new Date()
    d.setDate(d.getDate() + i)
    const value = d.toISOString().split('T')[0]
    const weekday = ['日', '一', '二', '三', '四', '五', '六'][d.getDay()]
    const label = `${d.getMonth() + 1}/${d.getDate()} (${weekday})`
    options.push({ label, value })
  }
  return options
}

const dateOptions = buildDateOptions()
const selectedDate = ref(dateOptions[0].value)

// 資料狀態
const orderData = ref(null)
const details = computed(() => orderData.value?.details || [])
const loading = ref(false)
const loadLoading = ref(false)
const saveLoading = ref(false)
const dirtyRows = ref(new Set())

// 凍結判斷（由後端 frozenStatus 決定）
const isFrozen = computed(() => !!orderData.value?.frozenStatus)

// 表格欄位（對應 SalesPurchaseDetailDTO）
const tableColumns = [
  { name: 'productCode', label: '產品代碼', field: 'productCode', align: 'left', sortable: true },
  { name: 'productName', label: '產品名稱', field: 'productName', align: 'left' },
  { name: 'unit', label: '單位', field: 'unit', align: 'center' },
  { name: 'qty', label: '訂貨數量', field: 'qty', align: 'right' },
  { name: 'confirmedQty', label: '確認數量', field: 'confirmedQty', align: 'right' },
  { name: 'lastQty', label: '前日訂購量', field: 'lastQty', align: 'right' }
]

// 自訂清單對話框
const showCustomDialog = ref(false)
const customList = ref([])
const customSaveLoading = ref(false)
const newProductCode = ref('')
const newProductName = ref('')

const customListColumns = [
  { name: 'productCode', label: '產品代碼', field: 'productCode', align: 'left' },
  { name: 'productName', label: '產品名稱', field: 'productName', align: 'left' },
  { name: 'actions', label: '操作', align: 'center' }
]

function markDirty(row) {
  dirtyRows.value.add(row.productCode)
}

// 查詢訂貨明細
async function fetchDetails() {
  loading.value = true
  dirtyRows.value.clear()
  try {
    orderData.value = await salesPurchaseApi.getDetails(selectedDate.value)
  } catch (e) {
    handleError(e)
  } finally {
    loading.value = false
  }
}

async function handleLoadYesterday() {
  loadLoading.value = true
  try {
    await salesPurchaseApi.loadYesterday(selectedDate.value)
    await fetchDetails()
  } catch (e) {
    handleError(e)
  } finally {
    loadLoading.value = false
  }
}

async function handleLoadCustom() {
  loadLoading.value = true
  try {
    await salesPurchaseApi.loadCustom(selectedDate.value)
    await fetchDetails()
  } catch (e) {
    handleError(e)
  } finally {
    loadLoading.value = false
  }
}

async function handleLoadBranch() {
  loadLoading.value = true
  try {
    await salesPurchaseApi.loadBranch(selectedDate.value)
    await fetchDetails()
  } catch (e) {
    handleError(e)
  } finally {
    loadLoading.value = false
  }
}

async function handleSave() {
  saveLoading.value = true
  try {
    await salesPurchaseApi.updateOrder({
      purchaseNo: orderData.value.purchaseNo,
      details: details.value.map(d => ({
        purchaseNo: orderData.value.purchaseNo,
        itemNo: d.itemNo,
        productCode: d.productCode,
        unit: d.unit,
        qty: d.qty,
        confirmedQty: d.confirmedQty
      }))
    })
    dirtyRows.value.clear()
    notifySuccess('儲存成功')
  } catch (e) {
    handleError(e)
  } finally {
    saveLoading.value = false
  }
}

// 自訂清單管理
async function openCustomListDialog() {
  showCustomDialog.value = true
  try {
    customList.value = await salesPurchaseApi.getCustomList()
  } catch (e) {
    handleError(e)
  }
}

function addCustomItem() {
  if (!newProductCode.value) return
  if (customList.value.some(i => i.productCode === newProductCode.value)) {
    notifyWarning('此產品已在清單中')
    return
  }
  customList.value.push({
    productCode: newProductCode.value,
    productName: newProductName.value || ''
  })
  newProductCode.value = ''
  newProductName.value = ''
}

function removeCustomItem(index) {
  customList.value.splice(index, 1)
}

async function handleSaveCustomList() {
  customSaveLoading.value = true
  try {
    await salesPurchaseApi.saveCustomList(customList.value)
    notifySuccess('自訂清單已儲存')
    showCustomDialog.value = false
  } catch (e) {
    handleError(e)
  } finally {
    customSaveLoading.value = false
  }
}

watch(selectedDate, () => {
  fetchDetails()
})

onMounted(() => {
  fetchDetails()
})
</script>

<style scoped>
</style>
