export type Reading = {
  id: string
  version: string
  book: string
  chapter: number
  readAt: string
}

export type ReadingProgress = {
  chaptersRead: number
  totalChapters: number
  percent: number
  booksCompleted: number
  lastReading: Reading | null
  currentStreak: number
  longestStreak: number
}
