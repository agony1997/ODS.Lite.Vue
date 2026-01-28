import { httpGet, httpPost, httpPut } from './httpClient'

const BASE_URL = '/api/purchase/sales'

export async function getDetails(branchCode, salesEmpNo, purchaseDate) {
  const params = new URLSearchParams({ branchCode, salesEmpNo, purchaseDate })
  return httpGet(`${BASE_URL}?${params}`)
}

export async function updateOrder(data) {
  return httpPut(BASE_URL, data)
}

export async function loadYesterday(data) {
  return httpPost(`${BASE_URL}/load/yesterday`, data)
}

export async function loadCustom(data) {
  return httpPost(`${BASE_URL}/load/custom`, data)
}

export async function loadBranch(data) {
  return httpPost(`${BASE_URL}/load/branch`, data)
}

export async function getCustomList(branchCode, salesEmpNo) {
  const params = new URLSearchParams({ branchCode, salesEmpNo })
  return httpGet(`${BASE_URL}/custom-list?${params}`)
}

export async function saveCustomList(data) {
  return httpPut(`${BASE_URL}/custom-list`, data)
}
