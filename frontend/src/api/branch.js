const BASE_URL = '/api/branches'

export async function getAllEnabled() {
  const response = await fetch(BASE_URL)
  if (!response.ok) {
    const error = await response.json()
    throw new Error(error.message || 'Failed to fetch branches')
  }
  return response.json()
}
