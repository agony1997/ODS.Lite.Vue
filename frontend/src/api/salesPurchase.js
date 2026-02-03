import { httpGet, httpPost, httpPut } from './httpClient'

const BASE_URL = '/api/purchase/sales'

export async function getDetails(purchaseDate) {
  const params = new URLSearchParams({ date: purchaseDate })
  return httpGet(`${BASE_URL}?${params}`)
}

export async function updateOrder(data) {
  return httpPut(BASE_URL, data)
}

export async function loadYesterday(purchaseDate) {
  const params = new URLSearchParams({ date: purchaseDate })
  return httpPost(`${BASE_URL}/load/yesterday?${params}`)
}

export async function loadCustom(purchaseDate) {
  const params = new URLSearchParams({ date: purchaseDate })
  return httpPost(`${BASE_URL}/load/custom?${params}`)
}

export async function loadBranch(purchaseDate) {
  const params = new URLSearchParams({ date: purchaseDate })
  return httpPost(`${BASE_URL}/load/branch?${params}`)
}

export async function getCustomList() {
  return httpGet(`${BASE_URL}/custom-list`)
}

export async function saveCustomList(items) {
  return httpPut(`${BASE_URL}/custom-list`, items)
}
