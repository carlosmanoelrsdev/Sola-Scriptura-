import {
  BookMarked,
  Edit3,
  FileText,
  Loader2,
  Plus,
  RefreshCw,
  Save,
  Trash2,
  X,
} from 'lucide-react'
import type { FormEvent } from 'react'
import { useMemo, useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'

import {
  createDevotional,
  deleteDevotional,
  listDevotionals,
  updateDevotional,
} from '@/services/devotional-service'
import { getBibleBooks, getBibleChapter, getBibleVersions } from '@/services/bible-service'
import type { Devotional, DevotionalReferenceInput } from '@/types/devotional'

const EMPTY_REFERENCE: DevotionalReferenceInput = {
  book: '',
  chapter: 1,
  startVerse: 1,
  endVerse: null,
  version: 'ACF',
}

export function DevotionalsPage() {
  const queryClient = useQueryClient()
  const [editingDevotional, setEditingDevotional] = useState<Devotional | null>(null)
  const [references, setReferences] = useState<DevotionalReferenceInput[]>([])
  const [saveError, setSaveError] = useState<string | null>(null)

  // Bible helpers for reference selection/validation
  const [selectedRefVersion, setSelectedRefVersion] = useState<string>('ACF')
  const versionsQuery = useQuery({ queryKey: ['bible', 'versions'], queryFn: getBibleVersions })
  const booksQuery = useQuery({
    queryKey: ['bible', 'books', selectedRefVersion],
    queryFn: () => getBibleBooks(selectedRefVersion),
    enabled: Boolean(selectedRefVersion),
  })

  const devotionalsQuery = useQuery({
    queryKey: ['devotionals'],
    queryFn: listDevotionals,
  })

  const saveMutation = useMutation({
    mutationFn: (input: { devotionalId?: string; title: string; content: string; references: DevotionalReferenceInput[] }) =>
      input.devotionalId
        ? updateDevotional(input.devotionalId, input)
        : createDevotional(input),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['devotionals'] })
      clearForm()
    },
  })

  const deleteMutation = useMutation({
    mutationFn: deleteDevotional,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['devotionals'] })
      clearForm()
    },
  })

  const sortedDevotionals = useMemo(
    () => devotionalsQuery.data ?? [],
    [devotionalsQuery.data],
  )

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setSaveError(null)
    const formData = new FormData(event.currentTarget)

    const payloadReferences = references
      .filter((reference) => reference.book.trim())
      .map((reference) => ({
        ...reference,
        book: reference.book.trim(),
        version: reference.version.trim(),
        endVerse: reference.endVerse || null,
      }))

    // Validate that referenced chapters exist in the Bible provider
    try {
      await Promise.all(
        payloadReferences.map((ref) => getBibleChapter(ref.version || 'ACF', ref.book, ref.chapter)),
      )
    } catch (err) {
      setSaveError('Uma ou mais referencias nao existem na Biblia selecionada. Verifique livro e capitulo.')
      return
    }

    saveMutation.mutate({
      devotionalId: editingDevotional?.id,
      title: String(formData.get('title') ?? ''),
      content: String(formData.get('content') ?? ''),
      references: payloadReferences,
    })
  }

  function startEditing(devotional: Devotional) {
    setEditingDevotional(devotional)
    setReferences(
      devotional.references.map((reference) => ({
        book: reference.book,
        chapter: reference.chapter,
        startVerse: reference.startVerse,
        endVerse: reference.endVerse,
        version: reference.version,
      })),
    )
  }

  function clearForm() {
    setEditingDevotional(null)
    setReferences([])
  }

  function addReference() {
    const defaultBook = booksQuery.data?.[0]?.abbrev ?? ''
    setReferences((current) => [...current, { ...EMPTY_REFERENCE, version: selectedRefVersion, book: defaultBook }])
  }

  function updateReference(index: number, value: Partial<DevotionalReferenceInput>) {
    setReferences((current) =>
      current.map((reference, currentIndex) =>
        currentIndex === index ? { ...reference, ...value } : reference,
      ),
    )
  }

  function removeReference(index: number) {
    setReferences((current) => current.filter((_, currentIndex) => currentIndex !== index))
  }

  return (
    <section className="space-y-6">
      <div className="flex flex-col gap-4 border-b border-slate-200 pb-6 lg:flex-row lg:items-end lg:justify-between">
        <div>
          <p className="text-sm font-medium uppercase text-sky-700">Estudo pessoal</p>
          <h1 className="mt-2 font-serif text-3xl font-semibold text-slate-950 md:text-4xl">Devocionais</h1>
          <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-700">
            Escreva reflexoes pessoais e associe referencias biblicas ao seu estudo.
          </p>
        </div>

        <button
          type="button"
          onClick={() => devotionalsQuery.refetch()}
          className="inline-flex h-10 items-center justify-center gap-2 rounded-md border border-slate-300 bg-white px-3 text-sm font-medium text-slate-800 shadow-sm transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60"
          disabled={devotionalsQuery.isFetching}
        >
          <RefreshCw className="h-4 w-4" />
          Atualizar
        </button>
      </div>

      <div className="grid gap-6 xl:grid-cols-[1fr_420px]">
        <form
          key={editingDevotional?.id ?? 'new'}
          onSubmit={handleSubmit}
          className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm"
        >
          <div className="flex items-start justify-between gap-4">
            <div className="flex items-start gap-3">
              <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-md bg-sky-100 text-sky-900">
                {editingDevotional ? <Edit3 className="h-5 w-5" /> : <FileText className="h-5 w-5" />}
              </div>
              <div>
                <p className="text-sm font-medium uppercase text-sky-700">
                  {editingDevotional ? 'Editar devocional' : 'Novo devocional'}
                </p>
                <h2 className="mt-1 text-xl font-semibold text-slate-950">
                  {editingDevotional ? editingDevotional.title : 'Registro de reflexao'}
                </h2>
              </div>
            </div>

            {editingDevotional ? (
              <button
                type="button"
                onClick={clearForm}
                className="inline-flex h-9 w-9 items-center justify-center rounded-md border border-slate-300 text-slate-700 transition hover:bg-slate-50"
                aria-label="Cancelar edicao"
              >
                <X className="h-4 w-4" />
              </button>
            ) : null}
          </div>

          <div className="mt-6 grid gap-4">
            <label className="grid gap-2 text-sm font-medium text-slate-800">
              Titulo
              <input
                name="title"
                defaultValue={editingDevotional?.title}
                minLength={2}
                maxLength={160}
                className="h-11 rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-900 outline-none transition focus:border-sky-700 focus:ring-2 focus:ring-sky-100"
                required
              />
            </label>

            <label className="grid gap-2 text-sm font-medium text-slate-800">
              Conteudo
              <textarea
                name="content"
                defaultValue={editingDevotional?.content}
                minLength={10}
                maxLength={10000}
                rows={10}
                className="resize-y rounded-md border border-slate-300 bg-white px-3 py-3 text-sm leading-6 text-slate-900 outline-none transition focus:border-sky-700 focus:ring-2 focus:ring-sky-100"
                required
              />
            </label>
          </div>

          <div className="mt-6 border-t border-slate-200 pt-5">
            <div className="flex items-center justify-between gap-3">
              <div>
                <p className="text-sm font-medium text-slate-950">Referencias biblicas</p>
                <p className="mt-1 text-xs text-slate-500">Ate 20 referencias por devocional.</p>
              </div>
              <button
                type="button"
                onClick={addReference}
                className="inline-flex h-9 items-center justify-center gap-2 rounded-md border border-slate-300 px-3 text-sm font-medium text-slate-800 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60"
                disabled={references.length >= 20}
              >
                <Plus className="h-4 w-4" />
                Adicionar
              </button>
            </div>

            <div className="mt-4 space-y-3">
              <div className="flex items-center gap-3">
                <label className="text-sm font-medium">Versao (para referencias)</label>
                <select
                  value={selectedRefVersion}
                  onChange={(e) => setSelectedRefVersion(e.target.value)}
                  className="h-9 rounded-md border border-slate-300 px-3 text-sm"
                >
                  {versionsQuery.data?.map((v) => (
                    <option key={v.code} value={v.code}>
                      {v.code}
                    </option>
                  ))}
                </select>
              </div>

              {references.map((reference, index) => (
                <div key={index} className="grid gap-3 rounded-md border border-slate-200 p-3 md:grid-cols-[1fr_90px_90px_90px_90px_40px]">
                  <select
                    aria-label="Livro"
                    value={reference.book}
                    onChange={(event) => updateReference(index, { book: event.target.value })}
                    className="h-10 rounded-md border border-slate-300 px-3 text-sm outline-none focus:border-sky-700 focus:ring-2 focus:ring-sky-100"
                  >
                    <option value="">Escolher livro</option>
                    {booksQuery.data?.map((b) => (
                      <option key={b.id} value={b.abbrev}>
                        {b.name}
                      </option>
                    ))}
                  </select>

                  <NumberInput
                    label="Capitulo"
                    value={reference.chapter}
                    max={150}
                    onChange={(value) => updateReference(index, { chapter: value })}
                  />
                  <NumberInput
                    label="Inicio"
                    value={reference.startVerse}
                    max={200}
                    onChange={(value) => updateReference(index, { startVerse: value })}
                  />
                  <OptionalNumberInput
                    label="Fim"
                    value={reference.endVerse ?? ''}
                    max={200}
                    onChange={(value) => updateReference(index, { endVerse: value || null })}
                  />
                  <input
                    aria-label="Traducao"
                    value={reference.version}
                    onChange={(event) => updateReference(index, { version: event.target.value })}
                    placeholder="ACF"
                    maxLength={20}
                    className="h-10 rounded-md border border-slate-300 px-3 text-sm uppercase outline-none focus:border-sky-700 focus:ring-2 focus:ring-sky-100"
                  />
                  <button
                    type="button"
                    onClick={() => removeReference(index)}
                    className="inline-flex h-10 w-10 items-center justify-center rounded-md border border-slate-300 text-slate-700 transition hover:bg-slate-50"
                    aria-label="Remover referencia"
                  >
                    <X className="h-4 w-4" />
                  </button>
                </div>
              ))}

              {!references.length ? (
                <p className="rounded-md border border-dashed border-slate-300 p-4 text-sm text-slate-600">
                  Nenhuma referencia adicionada.
                </p>
              ) : null}

              {saveError ? (
                <p className="mt-2 rounded-md border border-amber-200 bg-amber-50 p-3 text-sm text-amber-900">{saveError}</p>
              ) : null}
            </div>
          </div>

          {saveMutation.isError ? (
            <p className="mt-4 rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-900">
              Nao foi possivel salvar o devocional. Verifique titulo, conteudo e referencias.
            </p>
          ) : null}

          <button
            type="submit"
            className="mt-6 inline-flex h-10 items-center justify-center gap-2 rounded-md bg-sky-900 px-4 text-sm font-medium text-white transition hover:bg-sky-800 disabled:cursor-not-allowed disabled:opacity-60"
            disabled={saveMutation.isPending}
          >
            {saveMutation.isPending ? <Loader2 className="h-4 w-4 animate-spin" /> : <Save className="h-4 w-4" />}
            {editingDevotional ? 'Salvar alteracoes' : 'Criar devocional'}
          </button>
        </form>

        <aside className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
          <div className="flex items-start gap-3">
            <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-md bg-sky-100 text-sky-900">
              <BookMarked className="h-5 w-5" />
            </div>
            <div>
              <p className="text-sm font-medium uppercase text-sky-700">Biblioteca</p>
              <h2 className="mt-1 text-xl font-semibold text-slate-950">Seus devocionais</h2>
            </div>
          </div>

          <div className="mt-5 space-y-3">
            {devotionalsQuery.isLoading ? (
              <div className="flex min-h-[180px] items-center justify-center text-slate-600">
                <Loader2 className="mr-2 h-5 w-5 animate-spin" />
                Carregando
              </div>
            ) : devotionalsQuery.isError ? (
              <p className="rounded-md border border-red-200 bg-red-50 p-4 text-sm text-red-900">
                Nao foi possivel carregar seus devocionais.
              </p>
            ) : sortedDevotionals.length ? (
              sortedDevotionals.map((devotional) => (
                <article key={devotional.id} className="rounded-md border border-slate-200 p-4">
                  <div className="flex items-start justify-between gap-3">
                    <div>
                      <h3 className="font-medium text-slate-950">{devotional.title}</h3>
                      <p className="mt-1 text-xs text-slate-500">Atualizado em {formatDate(devotional.updatedAt)}</p>
                    </div>
                    <div className="flex shrink-0 gap-1">
                      <button
                        type="button"
                        onClick={() => startEditing(devotional)}
                        className="inline-flex h-8 w-8 items-center justify-center rounded-md text-slate-700 transition hover:bg-slate-100"
                        aria-label="Editar devocional"
                      >
                        <Edit3 className="h-4 w-4" />
                      </button>
                      <button
                        type="button"
                        onClick={() => deleteMutation.mutate(devotional.id)}
                        className="inline-flex h-8 w-8 items-center justify-center rounded-md text-red-700 transition hover:bg-red-50 disabled:cursor-not-allowed disabled:opacity-60"
                        disabled={deleteMutation.isPending}
                        aria-label="Excluir devocional"
                      >
                        <Trash2 className="h-4 w-4" />
                      </button>
                    </div>
                  </div>
                  <p className="mt-3 line-clamp-3 text-sm leading-6 text-slate-600">{devotional.content}</p>
                  {devotional.references.length ? (
                    <div className="mt-3 flex flex-wrap gap-2">
                      {devotional.references.map((reference) => (
                        <span key={reference.id} className="rounded-md bg-slate-100 px-2 py-1 text-xs text-slate-700">
                          {formatReference(reference)}
                        </span>
                      ))}
                    </div>
                  ) : null}
                </article>
              ))
            ) : (
              <p className="rounded-md border border-dashed border-slate-300 p-4 text-sm leading-6 text-slate-600">
                Voce ainda nao criou devocionais.
              </p>
            )}
          </div>
        </aside>
      </div>
    </section>
  )
}

type NumberInputProps = {
  label: string
  value: number
  max: number
  onChange: (value: number) => void
}

function NumberInput({ label, value, max, onChange }: NumberInputProps) {
  return (
    <input
      aria-label={label}
      type="number"
      min={1}
      max={max}
      value={value}
      onChange={(event) => onChange(Math.max(1, Number(event.target.value)))}
      placeholder={label}
      className="h-10 rounded-md border border-slate-300 px-3 text-sm outline-none focus:border-sky-700 focus:ring-2 focus:ring-sky-100"
    />
  )
}

type OptionalNumberInputProps = {
  label: string
  value: number | ''
  max: number
  onChange: (value: number | '') => void
}

function OptionalNumberInput({ label, value, max, onChange }: OptionalNumberInputProps) {
  return (
    <input
      aria-label={label}
      type="number"
      min={1}
      max={max}
      value={value}
      onChange={(event) => {
        if (event.target.value === '') {
          onChange('')
          return
        }

        onChange(Math.max(1, Number(event.target.value)))
      }}
      placeholder={label}
      className="h-10 rounded-md border border-slate-300 px-3 text-sm outline-none focus:border-sky-700 focus:ring-2 focus:ring-sky-100"
    />
  )
}

function formatReference(reference: DevotionalReferenceInput) {
  const verses = reference.endVerse
    ? `${reference.startVerse}-${reference.endVerse}`
    : String(reference.startVerse)

  return `${reference.book} ${reference.chapter}:${verses} ${reference.version}`
}

function formatDate(value: string) {
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  }).format(new Date(value))
}
