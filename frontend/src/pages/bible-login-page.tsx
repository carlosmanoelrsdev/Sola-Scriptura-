import { useMemo, useState } from 'react'
import { BookOpen, CheckCircle2, ChevronLeft, ChevronRight, Loader2, RefreshCw } from 'lucide-react'
import { Link } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'

import { getBibleBooks, getBibleChapter, getBibleVersions } from '@/services/bible-service'
import { getAuthToken } from '@/services/auth-token'
import { registerReading } from '@/services/reading-service'
import { getDashboard } from '@/services/dashboard-service'

const DEFAULT_VERSION = 'ARA'
const DEFAULT_BOOK = 'gn'
const DEFAULT_CHAPTER = 1

function resolveBookAbbrev(candidate: string, books: Array<{ abbrev: string; name?: string }> = []) {
    if (!candidate) return candidate

    const lowerCandidate = candidate.toLowerCase()
    const exactMatch = books.find((book) => book.abbrev.toLowerCase() === lowerCandidate)
    if (exactMatch) return exactMatch.abbrev

    if (lowerCandidate === 'jó') return 'jó'

    if (!books.length) return candidate
    return books[0].abbrev
}

export function BibleLoginPage() {
    const queryClient = useQueryClient()
    const [selectedVersion, setSelectedVersion] = useState(DEFAULT_VERSION)
    const [selectedBook, setSelectedBook] = useState(DEFAULT_BOOK)
    const [selectedChapter, setSelectedChapter] = useState(DEFAULT_CHAPTER)
    const [isBookDropdownOpen, setIsBookDropdownOpen] = useState(false)

    // Estado local para controlar instantaneamente os capítulos marcados como lidos nesta sessão
    const [readChapters, setReadChapters] = useState<Record<string, boolean>>({})
    const [selectedVerses, setSelectedVerses] = useState<Set<number>>(new Set())

    const token = getAuthToken()

    const dashboardQuery = useQuery({
        queryKey: ['dashboard'],
        queryFn: getDashboard,
        enabled: Boolean(token),
    })

    const versionsQuery = useQuery({
        queryKey: ['bible', 'versions'],
        queryFn: getBibleVersions,
    })

    const booksQuery = useQuery({
        queryKey: ['bible', 'books', selectedVersion],
        queryFn: () => getBibleBooks(selectedVersion),
        enabled: Boolean(selectedVersion),
    })

    const effectiveBook = useMemo(
        () => resolveBookAbbrev(selectedBook, booksQuery.data ?? []),
        [booksQuery.data, selectedBook],
    )

    const selectedBookData = useMemo(
        () =>
            booksQuery.data?.find((book) => book.abbrev === effectiveBook) ??
            (effectiveBook.toLowerCase() === 'jó'
                ? { id: 18, name: 'Jó', abbrev: 'jó', testament: 'Antigo Testamento' }
                : undefined),
        [booksQuery.data, effectiveBook],
    )

    const chapterQuery = useQuery({
        queryKey: ['bible', 'chapter', selectedVersion, effectiveBook, selectedChapter],
        queryFn: () => getBibleChapter(selectedVersion, effectiveBook, selectedChapter),
        enabled: Boolean(selectedVersion && effectiveBook && selectedChapter > 0),
    })

    // Chave única para identificar o capítulo atual (Ex: "ACF-jo-3")
    const currentChapterKey = `${selectedVersion}-${effectiveBook}-${selectedChapter}`.toUpperCase()

    // Verifica se já foi lido pelo dashboard (caso venha alguma lista) ou pelo estado local instantâneo
    const isAlreadyRead = useMemo(() => {
        // 1. Checa no estado local imediato
        if (readChapters[currentChapterKey]) return true

        // 2. Tenta checar se veio algo dentro do dashboard (caso sua API retorne histórico)
        const data = dashboardQuery.data as any
        if (!data) return false

        const readingsList = data.recentReadings || data.readings || data.history || data.readChapters || []
        if (!Array.isArray(readingsList) || !readingsList.length) return false

        return readingsList.some(
            (reading: any) =>
                (reading.version?.toUpperCase() === selectedVersion.toUpperCase() &&
                    reading.book?.toLowerCase() === effectiveBook.toLowerCase() &&
                    reading.chapter === selectedChapter) ||
                reading === currentChapterKey
        )
    }, [dashboardQuery.data, selectedVersion, effectiveBook, selectedChapter, readChapters, currentChapterKey])

    const isLoading = versionsQuery.isLoading || booksQuery.isLoading || chapterQuery.isLoading

    const registerReadingMutation = useMutation({
        mutationFn: registerReading,
        onSuccess: () => {
            // Marca imediatamente no estado local para feedback visual instantâneo
            setReadChapters((prev) => ({ ...prev, [currentChapterKey]: true }))
            setSelectedVerses(new Set())
            // Atualiza o dashboard em segundo plano
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
                    <p className="text-sm font-medium uppercase text-sky-700">Leitura bíblica</p>
                    <h1 className="mt-2 font-serif text-3xl font-semibold text-slate-950 md:text-4xl">Bíblia</h1>
                    <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-700">
                        Acompanhe as Escrituras, selecione traduções e registre suas leituras diárias.
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

            <div className="grid gap-6 xl:grid-cols-[320px_1fr]">
                <aside className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
                    <div className="flex items-start gap-3">
                        <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-md bg-sky-100 text-sky-900">
                            <BookOpen className="h-5 w-5" />
                        </div>
                        <div>
                            <p className="text-sm font-medium uppercase text-sky-700">Navegação</p>
                            <h2 className="mt-1 text-xl font-semibold text-slate-950">Livros da Bíblia</h2>
                        </div>
                    </div>

                    <div className="mt-4 grid gap-4">
                        <label className="grid gap-2 text-sm font-medium text-slate-800">
                            Tradução
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
                            Capítulo
                            <input
                                type="number"
                                min={1}
                                    max={150}
                                    value={selectedChapter}
                                    onChange={(event) => setSelectedChapter(Math.max(1, Number(event.target.value)))}
                                    className="h-11 rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-900 outline-none transition focus:border-sky-700 focus:ring-2 focus:ring-sky-100"
                                />
                            </label>
                    </div>

                    <div className="mt-6 border-t border-slate-200 pt-5">
                        <p className="mb-3 text-xs font-semibold uppercase text-slate-500">Livro</p>

                        <div className="relative">
                            <button
                                type="button"
                                onClick={() => setIsBookDropdownOpen((current) => !current)}
                                className="flex h-11 w-full items-center justify-between rounded-md border border-slate-300 bg-white px-3 text-left text-sm text-slate-900 shadow-sm transition hover:bg-slate-50"
                            >
                                <span className="truncate">{selectedBookData?.name ?? 'Selecionar livro'}</span>
                                <span className="ml-2 text-xs text-slate-500">{isBookDropdownOpen ? '▲' : '▼'}</span>
                            </button>

                            {isBookDropdownOpen ? (
                                <div className="absolute z-10 mt-2 w-full rounded-md border border-slate-200 bg-white shadow-lg">
                                    <div className="max-h-[360px] overflow-y-auto p-1">
                                        {booksQuery.data?.map((book) => (
                                            <button
                                                key={book.id}
                                                type="button"
                                                onClick={() => {
                                                    setSelectedBook(book.abbrev)
                                                    setSelectedChapter(1)
                                                    setIsBookDropdownOpen(false)
                                                }}
                                                className={`flex w-full items-center justify-between rounded-md px-3 py-2 text-left text-sm transition ${
                                                    book.abbrev === effectiveBook
                                                        ? 'bg-sky-900 text-white font-medium'
                                                        : 'text-slate-700 hover:bg-slate-100'
                                                }`}
                                            >
                                                <span className="truncate">{book.name}</span>
                                                <span className="ml-2 text-xs opacity-75">{book.testament}</span>
                                            </button>
                                        ))}
                                    </div>
                                </div>
                            ) : null}
                        </div>
                    </div>
                </aside>

                <article className="rounded-lg border border-slate-200 bg-[#fffdf8] shadow-sm">
                    <div className="flex flex-col gap-4 border-b border-slate-200 px-6 py-5 md:flex-row md:items-center md:justify-between">
                        <div>
                            <p className="text-xs font-semibold uppercase tracking-wider text-sky-700">{selectedVersion.toUpperCase()}</p>
                            <h2 className="mt-1 font-serif text-2xl font-semibold text-slate-950">
                                {chapterQuery.data?.reference ?? `${selectedBookData?.name ?? 'Livro'} ${selectedChapter}`}
                            </h2>
                        </div>

                        <div className="flex flex-wrap items-center gap-2">
                            {token ? (
                                <button
                                    type="button"
                                    onClick={markChapterAsRead}
                                    disabled={registerReadingMutation.isPending || chapterQuery.isError || isLoading || isAlreadyRead}
                                    className={`inline-flex h-10 items-center justify-center gap-2 rounded-md px-3 text-sm font-medium transition ${
                                        isAlreadyRead
                                            ? 'bg-emerald-600 text-white cursor-default shadow-sm'
                                            : 'bg-sky-900 text-white hover:bg-sky-800 disabled:cursor-not-allowed disabled:opacity-60'
                                    }`}
                                >
                                    {registerReadingMutation.isPending ? (
                                        <Loader2 className="h-4 w-4 animate-spin" />
                                    ) : (
                                        <CheckCircle2 className="h-4 w-4" />
                                    )}
                                    {isAlreadyRead ? 'Capítulo Lido' : 'Marcar como lido'}
                                </button>
                            ) : (
                                <Link
                                    to="/login"
                                    className="inline-flex h-10 items-center justify-center rounded-md border border-slate-300 bg-white px-3 text-sm font-medium text-slate-800 transition hover:bg-slate-50"
                                >
                                    Entrar para registrar
                                </Link>
                            )}

                            <div className="flex items-center gap-1 border-l border-slate-300 pl-2">
                                <button
                                    type="button"
                                    onClick={goToPreviousChapter}
                                    disabled={selectedChapter <= 1}
                                    className="inline-flex h-10 w-10 items-center justify-center rounded-md border border-slate-300 bg-white text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
                                    aria-label="Capítulo anterior"
                                >
                                    <ChevronLeft className="h-5 w-5" />
                                </button>
                                <button
                                    type="button"
                                    onClick={goToNextChapter}
                                    className="inline-flex h-10 w-10 items-center justify-center rounded-md border border-slate-300 bg-white text-slate-700 transition hover:bg-slate-50"
                                    aria-label="Próximo capítulo"
                                >
                                    <ChevronRight className="h-5 w-5" />
                                </button>
                            </div>
                        </div>
                    </div>

                    {isLoading ? (
                        <div className="flex min-h-[480px] items-center justify-center text-slate-600">
                            <Loader2 className="mr-2 h-5 w-5 animate-spin" />
                            Carregando leitura...
                        </div>
                    ) : chapterQuery.isError ? (
                        <div className="mx-auto flex min-h-[480px] max-w-md flex-col items-center justify-center px-6 text-center">
                            <h3 className="text-lg font-semibold text-slate-950">Capítulo não encontrado</h3>
                            <p className="mt-2 text-sm leading-6 text-slate-600">
                                Verifique o livro, a tradução ou o número do capítulo selecionado e tente novamente.
                            </p>
                        </div>
                    ) : (
                        <div className="px-6 py-8 md:px-12 md:py-10">
                            {registerReadingMutation.isSuccess && isAlreadyRead ? (
                                <p className="mx-auto mb-6 max-w-3xl rounded-md border border-emerald-200 bg-emerald-50 p-3 text-sm text-emerald-900">
                                    Capítulo marcado como lido com sucesso!
                                </p>
                            ) : null}

                            {registerReadingMutation.isError ? (
                                <p className="mx-auto mb-6 max-w-3xl rounded-md border border-amber-200 bg-amber-50 p-3 text-sm text-amber-900">
                                    Não foi possível registrar a leitura. Tente novamente ou verifique sua sessão.
                                </p>
                            ) : null}

                            <div className="mx-auto max-w-3xl space-y-5">
                                {chapterQuery.data?.verses.map((verse) => (
                                    <p
                                        key={verse.verse}
                                        onClick={() => {
                                            setSelectedVerses((prev) => {
                                                const next = new Set(prev)
                                                if (next.has(verse.verse)) next.delete(verse.verse)
                                                else next.add(verse.verse)
                                                return next
                                            })
                                        }}
                                        className={`font-serif text-xl leading-9 ${selectedVerses.has(verse.verse) ? 'bg-sky-100 rounded-md p-2 cursor-pointer' : 'text-slate-900 cursor-pointer'}`}>
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