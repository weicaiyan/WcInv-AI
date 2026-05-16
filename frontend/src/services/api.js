const TOKEN_KEY = 'wcinv_token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
}

async function request(path, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {})
  }

  const token = getToken()
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  const response = await fetch(path, {
    ...options,
    headers
  })

  let body = null
  try {
    body = await response.json()
  } catch (error) {
    body = null
  }

  if (!response.ok || body?.error) {
    const message = body?.error?.message || `请求失败（${response.status}）`
    const err = new Error(message)
    err.status = response.status
    err.body = body
    throw err
  }

  return body?.data
}

export async function login(username, password) {
  const data = await request('/api/v1/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password })
  })
  setToken(data.token)
  return data
}

export function fetchTemperatures() {
  return request('/api/v1/indices/temperatures')
}

export function fetchMarketEntry() {
  return request('/api/v1/market/entry')
}

export function fetchCheapPortfolio() {
  return request('/api/v1/cheap-portfolio')
}

export function fetchBogle() {
  return request('/api/v1/bogle')
}
