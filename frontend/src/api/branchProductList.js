const BASE_URL = '/api/branch-product-list'

export async function getByBranchCode(branchCode) {
  const response = await fetch(`${BASE_URL}/${branchCode}`)
  if (!response.ok) {
    const error = await response.json()
    throw new Error(error.message || 'Failed to fetch branch product list')
  }
  return response.json()
}

export async function create(products) {
  const response = await fetch(BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(products)
  })
  if (!response.ok) {
    const error = await response.json()
    throw new Error(error.message || 'Failed to create products')
  }
  return response.json()
}

export async function update(products) {
  const response = await fetch(BASE_URL, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(products)
  })
  if (!response.ok) {
    const error = await response.json()
    throw new Error(error.message || 'Failed to update products')
  }
  return response.json()
}

export async function deleteProducts(products) {
  const response = await fetch(BASE_URL, {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(products)
  })
  if (!response.ok) {
    const error = await response.json()
    throw new Error(error.message || 'Failed to delete products')
  }
  return response.json()
}

export async function copy(fromBranchCode, toBranchCode) {
  const response = await fetch(`${BASE_URL}/copy?from=${encodeURIComponent(fromBranchCode)}&to=${encodeURIComponent(toBranchCode)}`, {
    method: 'POST'
  })
  if (!response.ok) {
    const error = await response.json()
    throw new Error(error.message || 'Failed to copy products')
  }
  return response.json()
}
