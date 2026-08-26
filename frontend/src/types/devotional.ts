export type DevotionalReference = {
  id: string
  book: string
  chapter: number
  startVerse: number
  endVerse: number | null
  version: string
}

export type Devotional = {
  id: string
  title: string
  content: string
  createdAt: string
  updatedAt: string
  references: DevotionalReference[]
}

export type DevotionalReferenceInput = {
  book: string
  chapter: number
  startVerse: number
  endVerse: number | null
  version: string
}

export type SaveDevotionalInput = {
  title: string
  content: string
  references: DevotionalReferenceInput[]
}
