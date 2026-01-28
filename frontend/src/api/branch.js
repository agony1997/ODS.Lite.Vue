import { httpGet } from './httpClient'

const BASE_URL = '/api/branches'

export async function getAllEnabled() {
  return httpGet(BASE_URL)
}
