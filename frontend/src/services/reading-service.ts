import { apiPost } from '@/services/http'
import type { Reading } from '@/types/reading'

export type RegisterReadingInput = {
  version: string
  book: string
  chapter: number
}

export function registerReading(input: RegisterReadingInput) {
  return apiPost<Reading>('/api/readings', input, { auth: true })
}
