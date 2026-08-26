import { AlertTriangle, Loader2, RefreshCw, Save, Trash2, UserRound } from 'lucide-react'
import { useState } from 'react'
import type { FormEvent } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'

import { deleteCurrentUser, getCurrentUser, logout, updateCurrentUser } from '@/services/user-service'

export function ProfilePage() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [deleteConfirmation, setDeleteConfirmation] = useState('')

  const userQuery = useQuery({
    queryKey: ['current-user'],
    queryFn: getCurrentUser,
  })

  const updateMutation = useMutation({
    mutationFn: updateCurrentUser,
    onSuccess: (user) => {
      queryClient.setQueryData(['current-user'], user)
    },
  })

  const deleteMutation = useMutation({
    mutationFn: deleteCurrentUser,
    onSuccess: () => {
      logout()
      queryClient.clear()
      navigate('/')
    },
  })

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const formData = new FormData(event.currentTarget)
    const name = String(formData.get('name') ?? '')
    const email = String(formData.get('email') ?? '')

    updateMutation.mutate({ name, email })
  }

  const canDelete = deleteConfirmation === userQuery.data?.email

  return (
    <section className="space-y-6">
      <div className="flex flex-col gap-4 border-b border-slate-200 pb-6 lg:flex-row lg:items-end lg:justify-between">
        <div>
          <p className="text-sm font-medium uppercase text-sky-700">Conta</p>
          <h1 className="mt-2 font-serif text-3xl font-semibold text-slate-950 md:text-4xl">Perfil</h1>
          <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-700">
            Gerencie seus dados pessoais e as configuracoes da sua conta.
          </p>
        </div>

        <button
          type="button"
          onClick={() => userQuery.refetch()}
          className="inline-flex h-10 items-center justify-center gap-2 rounded-md border border-slate-300 bg-white px-3 text-sm font-medium text-slate-800 shadow-sm transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60"
          disabled={userQuery.isFetching}
        >
          <RefreshCw className="h-4 w-4" />
          Atualizar
        </button>
      </div>

      {userQuery.isLoading ? (
        <div className="flex min-h-[360px] items-center justify-center rounded-lg border border-slate-200 bg-white text-slate-600">
          <Loader2 className="mr-2 h-5 w-5 animate-spin" />
          Carregando perfil
        </div>
      ) : userQuery.isError ? (
        <div className="rounded-lg border border-amber-200 bg-amber-50 p-6">
          <h2 className="text-lg font-semibold text-amber-950">Nao foi possivel carregar seu perfil</h2>
          <p className="mt-2 max-w-2xl text-sm leading-6 text-amber-900">
            Verifique se voce esta autenticado e tente novamente.
          </p>
        </div>
      ) : userQuery.data ? (
        <div className="grid gap-6 xl:grid-cols-[1fr_360px]">
          <form
            key={`${userQuery.data.id}-${userQuery.data.updatedAt}`}
            onSubmit={handleSubmit}
            className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm"
          >
            <div className="flex items-start gap-3">
              <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-md bg-sky-100 text-sky-900">
                <UserRound className="h-5 w-5" />
              </div>
              <div>
                <p className="text-sm font-medium uppercase text-sky-700">Dados pessoais</p>
                <h2 className="mt-1 text-xl font-semibold text-slate-950">Informacoes da conta</h2>
              </div>
            </div>

            <div className="mt-6 grid gap-4">
              <label className="grid gap-2 text-sm font-medium text-slate-800">
                Nome
                <input
                  name="name"
                  defaultValue={userQuery.data.name}
                  className="h-11 rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-900 outline-none transition focus:border-sky-700 focus:ring-2 focus:ring-sky-100"
                  minLength={2}
                  maxLength={120}
                  required
                />
              </label>

              <label className="grid gap-2 text-sm font-medium text-slate-800">
                Email
                <input
                  name="email"
                  type="email"
                  defaultValue={userQuery.data.email}
                  className="h-11 rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-900 outline-none transition focus:border-sky-700 focus:ring-2 focus:ring-sky-100"
                  maxLength={180}
                  required
                />
              </label>
            </div>

            <div className="mt-6 grid gap-3 border-t border-slate-200 pt-5 text-sm text-slate-600 sm:grid-cols-2">
              <p>
                Perfil criado em <strong className="font-medium text-slate-900">{formatDate(userQuery.data.createdAt)}</strong>
              </p>
              <p>
                Role <strong className="font-medium text-slate-900">{userQuery.data.role}</strong>
              </p>
            </div>

            {updateMutation.isError ? (
              <p className="mt-4 rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-900">
                Nao foi possivel atualizar o perfil. Verifique os dados informados.
              </p>
            ) : null}

            {updateMutation.isSuccess ? (
              <p className="mt-4 rounded-md border border-emerald-200 bg-emerald-50 p-3 text-sm text-emerald-900">
                Perfil atualizado com sucesso.
              </p>
            ) : null}

            <button
              type="submit"
              className="mt-6 inline-flex h-10 items-center justify-center gap-2 rounded-md bg-sky-900 px-4 text-sm font-medium text-white transition hover:bg-sky-800 disabled:cursor-not-allowed disabled:opacity-60"
              disabled={updateMutation.isPending}
            >
              {updateMutation.isPending ? <Loader2 className="h-4 w-4 animate-spin" /> : <Save className="h-4 w-4" />}
              Salvar alteracoes
            </button>
          </form>

          <aside className="rounded-lg border border-red-200 bg-white p-5 shadow-sm">
            <div className="flex items-start gap-3">
              <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-md bg-red-100 text-red-900">
                <AlertTriangle className="h-5 w-5" />
              </div>
              <div>
                <p className="text-sm font-medium uppercase text-red-700">Zona de risco</p>
                <h2 className="mt-1 text-xl font-semibold text-slate-950">Excluir conta</h2>
              </div>
            </div>

            <p className="mt-4 text-sm leading-6 text-slate-600">
              Esta acao remove sua conta, leituras e devocionais. Para confirmar, digite seu email.
            </p>

            <label className="mt-5 grid gap-2 text-sm font-medium text-slate-800">
              Confirmacao
              <input
                value={deleteConfirmation}
                onChange={(event) => setDeleteConfirmation(event.target.value)}
                placeholder={userQuery.data.email}
                className="h-11 rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-900 outline-none transition focus:border-red-700 focus:ring-2 focus:ring-red-100"
              />
            </label>

            {deleteMutation.isError ? (
              <p className="mt-4 rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-900">
                Nao foi possivel excluir a conta. Tente novamente.
              </p>
            ) : null}

            <button
              type="button"
              onClick={() => deleteMutation.mutate()}
              className="mt-5 inline-flex h-10 w-full items-center justify-center gap-2 rounded-md bg-red-700 px-4 text-sm font-medium text-white transition hover:bg-red-800 disabled:cursor-not-allowed disabled:opacity-60"
              disabled={!canDelete || deleteMutation.isPending}
            >
              {deleteMutation.isPending ? <Loader2 className="h-4 w-4 animate-spin" /> : <Trash2 className="h-4 w-4" />}
              Excluir minha conta
            </button>
          </aside>
        </div>
      ) : null}
    </section>
  )
}

function formatDate(value: string) {
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  }).format(new Date(value))
}
