import { apiGet } from '@/services/http'
import type { BibleBook, BibleChapter, BibleVersion } from '@/types/bible'

export function getBibleVersions() {
  return apiGet<BibleVersion[]>('/api/bible/versions')
}

export function getBibleBooks(version: string) {
  const query = version ? `?version=${encodeURIComponent(version)}` : ''
  return apiGet<BibleBook[]>(`/api/bible/books${query}`)
}

export function getBibleChapter(version: string, book: string, chapter: number) {
  return apiGet<BibleChapter>(
    `/api/bible/versions/${encodeURIComponent(version)}/books/${encodeURIComponent(book)}/chapters/${chapter}`,
  )
}
