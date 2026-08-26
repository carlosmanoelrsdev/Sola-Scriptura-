import { getAuthToken } from '@/services/auth-token'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

type ApiGetOptions = {
  auth?: boolean
}

type ApiRequestOptions = {
  auth?: boolean
  body?: unknown
}

function buildHeaders(options: ApiRequestOptions) {
  const headers = new Headers()

  if (options.auth) {
    const token = getAuthToken()

    if (token) {
      headers.set('Authorization', `Bearer ${token}`)
    }
  }

  if (options.body !== undefined) {
    headers.set('Content-Type', 'application/json')
  }

  return headers
}

async function parseResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`)
  }

  if (response.status === 204) {
    return undefined as T
  }

  return response.json() as Promise<T>
}

export async function apiGet<T>(path: string, options: ApiGetOptions = {}): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: buildHeaders(options),
  })

  return parseResponse<T>(response)
}

export async function apiPost<T>(path: string, body: unknown, options: ApiGetOptions = {}): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: 'POST',
    headers: buildHeaders({ ...options, body }),
    body: JSON.stringify(body),
  })

  return parseResponse<T>(response)
}

export async function apiPut<T>(path: string, body: unknown, options: ApiGetOptions = {}): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: 'PUT',
    headers: buildHeaders({ ...options, body }),
    body: JSON.stringify(body),
  })

  return parseResponse<T>(response)
}

export async function apiDelete(path: string, options: ApiGetOptions = {}): Promise<void> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: 'DELETE',
    headers: buildHeaders(options),
  })

  return parseResponse<void>(response)
}
