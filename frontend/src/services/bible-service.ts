import { apiGet } from '@/services/http'
import type { BibleBook, BibleChapter, BibleVersion } from '@/types/bible'

function normalizeVersionForApi(version: string) {
  const normalized = version?.trim()
  if (!normalized) return normalized

  return normalized.toLowerCase() === 'nvi' ? 'ara' : normalized
}

export function getBibleVersions() {
  return apiGet<BibleVersion[]>('/api/bible/versions')
}

export function getBibleBooks(version: string) {
  const apiVersion = normalizeVersionForApi(version)
  const query = apiVersion ? `?version=${encodeURIComponent(apiVersion)}` : ''
  return apiGet<BibleBook[]>(`/api/bible/books${query}`)
}

export function getBibleChapter(version: string, book: string, chapter: number) {
  const apiVersion = normalizeVersionForApi(version)
  return apiGet<BibleChapter>(
    `/api/bible/versions/${encodeURIComponent(apiVersion)}/books/${encodeURIComponent(book)}/chapters/${chapter}`,
  )
}
