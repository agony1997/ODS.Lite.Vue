const BASE_URL = '/api/purchase/branch'

/**
 * 查詢營業所彙總資料
 */
export async function getSummary(branchCode, date) {
  const params = new URLSearchParams({ branchCode, date })
  const response = await fetch(`${BASE_URL}/summary?${params}`)
  if (!response.ok) {
    const error = await response.json()
    throw new Error(error.message || '查詢彙總資料失敗')
  }
  return response.json()
}

/**
 * 更新確認數量
 */
export async function updateConfirmedQty(data) {
  const response = await fetch(`${BASE_URL}/summary`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  })
  if (!response.ok) {
    const error = await response.json()
    throw new Error(error.message || '更新確認數量失敗')
  }
  return response.json()
}

/**
 * 凍結營業所
 */
export async function freeze(branchCode, purchaseDate) {
  const response = await fetch(`${BASE_URL}/freeze`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ branchCode, purchaseDate })
  })
  if (!response.ok) {
    const error = await response.json()
    throw new Error(error.message || '凍結失敗')
  }
  return response.json()
}

/**
 * 解除凍結
 */
export async function unfreeze(branchCode, purchaseDate) {
  const response = await fetch(`${BASE_URL}/unfreeze`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ branchCode, purchaseDate })
  })
  if (!response.ok) {
    const error = await response.json()
    throw new Error(error.message || '解除凍結失敗')
  }
  return response.json()
}

/**
 * 確認完成
 */
export async function confirm(branchCode, purchaseDate) {
  const response = await fetch(`${BASE_URL}/confirm`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ branchCode, purchaseDate })
  })
  if (!response.ok) {
    const error = await response.json()
    throw new Error(error.message || '確認失敗')
  }
  return response.json()
}

/**
 * 執行彙總建立 BPO
 */
export async function aggregate(branchCode, purchaseDate) {
  const response = await fetch(`${BASE_URL}/aggregate`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ branchCode, purchaseDate })
  })
  if (!response.ok) {
    const error = await response.json()
    throw new Error(error.message || '彙總失敗')
  }
  return response.json()
}

/**
 * 查詢 BPO 清單
 */
export async function getBpoList(branchCode, date) {
  const params = new URLSearchParams({ branchCode, date })
  const response = await fetch(`${BASE_URL}/bpo?${params}`)
  if (!response.ok) {
    const error = await response.json()
    throw new Error(error.message || '查詢 BPO 清單失敗')
  }
  return response.json()
}
