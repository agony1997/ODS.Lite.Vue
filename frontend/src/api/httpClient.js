import router from '@/router'

const TOKEN_KEY = 'mock_ods_token'

function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

function clearAuth() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem('mock_ods_user')
}

async function request(url, options = {}) {
  const token = getToken()
  const headers = { ...options.headers }

  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  if (options.body && !headers['Content-Type']) {
    headers['Content-Type'] = 'application/json'
  }

  const response = await fetch(url, { ...options, headers })

  if (response.status === 401) {
    clearAuth()
    router.push('/login')
    throw new Error('未授權，請重新登入')
  }

  if (!response.ok) {
    const error = await response.json().catch(() => ({}))
    throw new Error(error.message || `請求失敗 (${response.status})`)
  }

  if (response.status === 204) {
    return null
  }

  return response.json()
}

export function httpGet(url) {
  return request(url)
}

export function httpPost(url, data) {
  return request(url, {
    method: 'POST',
    body: data != null ? JSON.stringify(data) : undefined
  })
}

export function httpPut(url, data) {
  return request(url, {
    method: 'PUT',
    body: JSON.stringify(data)
  })
}

export function httpDelete(url, data) {
  return request(url, {
    method: 'DELETE',
    body: data != null ? JSON.stringify(data) : undefined
  })
}
