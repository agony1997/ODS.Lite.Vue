import router from '@/router'

const TOKEN_KEY = 'mock_ods_token'
const REQUEST_TIMEOUT = 30000

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

  const controller = new AbortController()
  const timeoutId = setTimeout(() => controller.abort(), REQUEST_TIMEOUT)

  let response
  try {
    if (import.meta.env.DEV) {
      console.log(`[HTTP] ${options.method || 'GET'} ${url}`)
    }

    response = await fetch(url, { ...options, headers, signal: controller.signal })
  } catch (err) {
    clearTimeout(timeoutId)
    if (err.name === 'AbortError') {
      throw new Error('請求逾時，請稍後再試')
    }
    throw new Error('網路連線失敗，請檢查網路狀態')
  } finally {
    clearTimeout(timeoutId)
  }

  if (import.meta.env.DEV) {
    console.log(`[HTTP] ${response.status} ${url}`)
  }

  if (response.status === 401) {
    clearAuth()
    router.push('/login')
    throw new Error('未授權，請重新登入')
  }

  if (response.status === 403) {
    throw new Error('權限不足，無法執行此操作')
  }

  if (!response.ok) {
    let message = `請求失敗 (${response.status})`
    try {
      const error = await response.json()
      if (error.message) {
        message = error.message
      }
    } catch {
      // 非 JSON 回應，使用預設訊息
    }
    throw new Error(message)
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
