import type { BibleVerse } from '@/types/bible'
import type { Devotional } from '@/types/devotional'
import type { ReadingProgress } from '@/types/reading'

export type Dashboard = {
  progress: ReadingProgress
  verseOfDay: BibleVerse | null
  recentDevotionals: Devotional[]
}
