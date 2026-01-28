import { httpGet, httpPost, httpPut, httpDelete } from './httpClient'

const BASE_URL = '/api/branch-product-list'

export async function getByBranchCode(branchCode) {
  return httpGet(`${BASE_URL}/${branchCode}`)
}

export async function create(products) {
  return httpPost(BASE_URL, products)
}

export async function update(products) {
  return httpPut(BASE_URL, products)
}

export async function deleteProducts(products) {
  return httpDelete(BASE_URL, products)
}

export async function copy(fromBranchCode, toBranchCode) {
  return httpPost(`${BASE_URL}/copy?from=${encodeURIComponent(fromBranchCode)}&to=${encodeURIComponent(toBranchCode)}`)
}
