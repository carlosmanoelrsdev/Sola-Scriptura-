export type BibleVersion = {
  code: string
  copyright: string
  permissions: string
  language: string
}

export type BibleBook = {
  id: number
  name: string
  abbrev: string
  testament: string
}

export type BibleChapterInfo = {
  number: number
  verses: number
}

export type BibleVerse = {
  reference: string
  version: string
  book: BibleBook
  chapter: number
  verse: number
  text: string
}

export type BibleChapter = {
  reference: string
  version: string
  book: BibleBook
  chapter: BibleChapterInfo
  verses: BibleVerse[]
}
