import { useMemo, useState } from 'react'
import { CheckCircle2, ChevronLeft, ChevronRight, Loader2, RefreshCw } from 'lucide-react'
import { Link } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'

import { getBibleBooks, getBibleChapter, getBibleVersions } from '@/services/bible-service'
import { getAuthToken } from '@/services/auth-token'
import { registerReading } from '@/services/reading-service'

const DEFAULT_VERSION = 'ACF'
const DEFAULT_BOOK = 'jo'
const DEFAULT_CHAPTER = 3

export function BiblePage() {
  const queryClient = useQueryClient()
  const [selectedVersion, setSelectedVersion] = useState(DEFAULT_VERSION)
  const [selectedBook, setSelectedBook] = useState(DEFAULT_BOOK)
  const [selectedChapter, setSelectedChapter] = useState(DEFAULT_CHAPTER)
  const token = getAuthToken()

  const versionsQuery = useQuery({
    queryKey: ['bible', 'versions'],
    queryFn: getBibleVersions,
  })

  const booksQuery = useQuery({
    queryKey: ['bible', 'books', selectedVersion],
    queryFn: () => getBibleBooks(selectedVersion),
    enabled: Boolean(selectedVersion),
  })

  const effectiveBook = useMemo(() => {
    if (!booksQuery.data?.length) {
      return selectedBook
    }

    return booksQuery.data.some((book) => book.abbrev === selectedBook)
      ? selectedBook
      : booksQuery.data[0].abbrev
  }, [booksQuery.data, selectedBook])

  const selectedBookData = useMemo(
    () => booksQuery.data?.find((book) => book.abbrev === effectiveBook),
    [booksQuery.data, effectiveBook],
  )

  const chapterQuery = useQuery({
    queryKey: ['bible', 'chapter', selectedVersion, effectiveBook, selectedChapter],
    queryFn: () => getBibleChapter(selectedVersion, effectiveBook, selectedChapter),
    enabled: Boolean(selectedVersion && effectiveBook && selectedChapter > 0),
  })

  const isLoading = versionsQuery.isLoading || booksQuery.isLoading || chapterQuery.isLoading

  const registerReadingMutation = useMutation({
    mutationFn: registerReading,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['dashboard'] })
    },
  })

  function goToPreviousChapter() {
    setSelectedChapter((current) => Math.max(1, current - 1))
  }

  function goToNextChapter() {
    setSelectedChapter((current) => current + 1)
  }

  function markChapterAsRead() {
    registerReadingMutation.mutate({
      version: selectedVersion,
      book: effectiveBook,
      chapter: selectedChapter,
    })
  }

  return (
    <section className="space-y-6">
      <div className="flex flex-col gap-4 border-b border-slate-200 pb-6 lg:flex-row lg:items-end lg:justify-between">
        <div>
          <p className="text-sm font-medium uppercase text-sky-700">Leitura publica</p>
          <h1 className="mt-2 font-serif text-3xl font-semibold text-slate-950 md:text-4xl">Biblia</h1>
          <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-700">
            Escolha a traducao, o livro e o capitulo para seguir a leitura.
          </p>
        </div>

        <button
          type="button"
          onClick={() => chapterQuery.refetch()}
          className="inline-flex h-10 items-center justify-center gap-2 rounded-md border border-slate-300 bg-white px-3 text-sm font-medium text-slate-800 shadow-sm transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60"
          disabled={chapterQuery.isFetching}
        >
          <RefreshCw className="h-4 w-4" />
          Atualizar
        </button>
      </div>

      <div className="grid gap-4 rounded-lg border border-slate-200 bg-white p-4 shadow-sm md:grid-cols-[1fr_1fr_140px]">
        <label className="grid gap-2 text-sm font-medium text-slate-800">
          Traducao
          <select
            value={selectedVersion}
            onChange={(event) => {
              setSelectedVersion(event.target.value)
              setSelectedChapter(1)
            }}
            className="h-11 rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-900 outline-none transition focus:border-sky-700 focus:ring-2 focus:ring-sky-100"
          >
            {versionsQuery.data?.map((version) => (
              <option key={version.code} value={version.code}>
                {version.code} - {version.language}
              </option>
            ))}
          </select>
        </label>

        <label className="grid gap-2 text-sm font-medium text-slate-800">
          Livro
          <select
            value={effectiveBook}
            onChange={(event) => {
              setSelectedBook(event.target.value)
              setSelectedChapter(1)
            }}
            className="h-11 rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-900 outline-none transition focus:border-sky-700 focus:ring-2 focus:ring-sky-100"
          >
            {booksQuery.data?.map((book) => (
              <option key={book.id} value={book.abbrev}>
                {book.name}
              </option>
            ))}
          </select>
        </label>

        <label className="grid gap-2 text-sm font-medium text-slate-800">
          Capitulo
          <input
            type="number"
            min={1}
            value={selectedChapter}
            onChange={(event) => setSelectedChapter(Math.max(1, Number(event.target.value)))}
            className="h-11 rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-900 outline-none transition focus:border-sky-700 focus:ring-2 focus:ring-sky-100"
          />
        </label>
      </div>

      <div className="grid gap-6 lg:grid-cols-[220px_1fr]">
        <aside className="hidden rounded-lg border border-slate-200 bg-white p-3 shadow-sm lg:block">
          <div className="mb-3 px-2 text-xs font-semibold uppercase text-slate-500">
            Livros
          </div>
          <div className="max-h-[680px] space-y-1 overflow-y-auto pr-1">
            {booksQuery.data?.map((book) => (
              <button
                key={book.id}
                type="button"
                onClick={() => {
                  setSelectedBook(book.abbrev)
                  setSelectedChapter(1)
                }}
                className={`flex h-9 w-full items-center justify-between rounded-md px-2 text-left text-sm transition ${
                  book.abbrev === effectiveBook
                    ? 'bg-sky-900 text-white'
                    : 'text-slate-700 hover:bg-slate-100'
                }`}
              >
                <span className="truncate">{book.name}</span>
                <span className="ml-2 text-xs opacity-75">{book.testament}</span>
              </button>
            ))}
          </div>
        </aside>

        <article className="min-h-[520px] rounded-lg border border-slate-200 bg-[#fffdf8] shadow-sm">
          <div className="flex flex-col gap-4 border-b border-slate-200 px-5 py-4 md:flex-row md:items-center md:justify-between">
            <div>
              <p className="text-sm text-slate-500">{selectedVersion.toUpperCase()}</p>
              <h2 className="font-serif text-2xl font-semibold text-slate-950">
                {chapterQuery.data?.reference ?? `${selectedBookData?.name ?? 'Livro'} ${selectedChapter}`}
              </h2>
            </div>

            <div className="flex items-center gap-2">
              {token ? (
                <button
                  type="button"
                  onClick={markChapterAsRead}
                  disabled={registerReadingMutation.isPending || chapterQuery.isError || isLoading}
                  className="inline-flex h-10 items-center justify-center gap-2 rounded-md bg-sky-900 px-3 text-sm font-medium text-white transition hover:bg-sky-800 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  {registerReadingMutation.isPending ? (
                    <Loader2 className="h-4 w-4 animate-spin" />
                  ) : (
                    <CheckCircle2 className="h-4 w-4" />
                  )}
                  Marcar como lido
                </button>
              ) : (
                <Link
                  to="/login"
                  className="inline-flex h-10 items-center justify-center rounded-md border border-slate-300 bg-white px-3 text-sm font-medium text-slate-800 transition hover:bg-slate-50"
                >
                  Entrar para registrar
                </Link>
              )}
              <button
                type="button"
                onClick={goToPreviousChapter}
                disabled={selectedChapter <= 1}
                className="inline-flex h-10 w-10 items-center justify-center rounded-md border border-slate-300 bg-white text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
                aria-label="Capitulo anterior"
              >
                <ChevronLeft className="h-5 w-5" />
              </button>
              <button
                type="button"
                onClick={goToNextChapter}
                className="inline-flex h-10 w-10 items-center justify-center rounded-md border border-slate-300 bg-white text-slate-700 transition hover:bg-slate-50"
                aria-label="Proximo capitulo"
              >
                <ChevronRight className="h-5 w-5" />
              </button>
            </div>
          </div>

          {isLoading ? (
            <div className="flex min-h-[420px] items-center justify-center text-slate-600">
              <Loader2 className="mr-2 h-5 w-5 animate-spin" />
              Carregando leitura
            </div>
          ) : chapterQuery.isError ? (
            <div className="mx-auto flex min-h-[420px] max-w-md flex-col items-center justify-center px-6 text-center">
              <h3 className="text-lg font-semibold text-slate-950">Capitulo nao encontrado</h3>
              <p className="mt-2 text-sm leading-6 text-slate-600">
                Verifique o livro, a traducao ou o numero do capitulo e tente novamente.
              </p>
            </div>
          ) : (
            <div className="px-5 py-6 md:px-8 md:py-8">
              {registerReadingMutation.isSuccess ? (
                <p className="mx-auto mb-6 max-w-3xl rounded-md border border-emerald-200 bg-emerald-50 p-3 text-sm text-emerald-900">
                  Capitulo marcado como lido.
                </p>
              ) : null}

              {registerReadingMutation.isError ? (
                <p className="mx-auto mb-6 max-w-3xl rounded-md border border-amber-200 bg-amber-50 p-3 text-sm text-amber-900">
                  Este capitulo ja pode estar marcado como lido ou a sessao expirou.
                </p>
              ) : null}

              <div className="mx-auto max-w-3xl space-y-5">
                {chapterQuery.data?.verses.map((verse) => (
                  <p key={verse.verse} className="font-serif text-xl leading-9 text-slate-900">
                    <sup className="mr-2 font-sans text-sm font-semibold text-sky-800">{verse.verse}</sup>
                    {verse.text}
                  </p>
                ))}
              </div>
            </div>
          )}
        </article>
      </div>
    </section>
  )
}
