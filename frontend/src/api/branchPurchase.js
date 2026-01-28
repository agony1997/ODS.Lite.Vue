import { httpGet, httpPost, httpPut } from './httpClient'

const BASE_URL = '/api/purchase/branch'

export async function getSummary(branchCode, date) {
  const params = new URLSearchParams({ branchCode, date })
  return httpGet(`${BASE_URL}/summary?${params}`)
}

export async function updateConfirmedQty(data) {
  return httpPut(`${BASE_URL}/summary`, data)
}

export async function freeze(branchCode, purchaseDate) {
  return httpPost(`${BASE_URL}/freeze`, { branchCode, purchaseDate })
}

export async function unfreeze(branchCode, purchaseDate) {
  return httpPost(`${BASE_URL}/unfreeze`, { branchCode, purchaseDate })
}

export async function confirm(branchCode, purchaseDate) {
  return httpPost(`${BASE_URL}/confirm`, { branchCode, purchaseDate })
}

export async function aggregate(branchCode, purchaseDate) {
  return httpPost(`${BASE_URL}/aggregate`, { branchCode, purchaseDate })
}

export async function getBpoList(branchCode, date) {
  const params = new URLSearchParams({ branchCode, date })
  return httpGet(`${BASE_URL}/bpo?${params}`)
}
