import { Loader2, UserPlus } from 'lucide-react'
import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useMutation } from '@tanstack/react-query'

import { register } from '@/services/auth-service'

export function RegisterPage() {
  const navigate = useNavigate()

  const registerMutation = useMutation({
    mutationFn: register,
    onSuccess: () => {
      navigate('/login')
    },
  })

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const formData = new FormData(event.currentTarget)

    registerMutation.mutate({
      name: String(formData.get('name') ?? ''),
      email: String(formData.get('email') ?? ''),
      password: String(formData.get('password') ?? ''),
    })
  }

  return (
    <section className="mx-auto max-w-md rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
      <p className="text-sm font-medium uppercase text-sky-700">Conta</p>
      <h1 className="mt-2 font-serif text-3xl font-semibold text-slate-950">Cadastro</h1>
      <p className="mt-2 text-sm leading-6 text-slate-700">
        Crie uma conta para acompanhar sua leitura e manter seus devocionais pessoais.
      </p>

      <form onSubmit={handleSubmit} className="mt-6 grid gap-4">
        <label className="grid gap-2 text-sm font-medium text-slate-800">
          Nome
          <input
            name="name"
            autoComplete="name"
            minLength={2}
            maxLength={120}
            className="h-11 rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-900 outline-none transition focus:border-sky-700 focus:ring-2 focus:ring-sky-100"
            required
          />
        </label>

        <label className="grid gap-2 text-sm font-medium text-slate-800">
          Email
          <input
            name="email"
            type="email"
            autoComplete="email"
            maxLength={180}
            className="h-11 rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-900 outline-none transition focus:border-sky-700 focus:ring-2 focus:ring-sky-100"
            required
          />
        </label>

        <label className="grid gap-2 text-sm font-medium text-slate-800">
          Senha
          <input
            name="password"
            type="password"
            autoComplete="new-password"
            minLength={8}
            maxLength={72}
            className="h-11 rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-900 outline-none transition focus:border-sky-700 focus:ring-2 focus:ring-sky-100"
            required
          />
        </label>

        {registerMutation.isError ? (
          <p className="rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-900">
            Nao foi possivel criar a conta. Verifique os dados ou use outro email.
          </p>
        ) : null}

        <button
          type="submit"
          className="inline-flex h-11 items-center justify-center gap-2 rounded-md bg-sky-900 px-4 text-sm font-medium text-white transition hover:bg-sky-800 disabled:cursor-not-allowed disabled:opacity-60"
          disabled={registerMutation.isPending}
        >
          {registerMutation.isPending ? <Loader2 className="h-4 w-4 animate-spin" /> : <UserPlus className="h-4 w-4" />}
          Criar conta
        </button>
      </form>

      <p className="mt-5 text-sm text-slate-600">
        Ja tem conta?{' '}
        <Link to="/login" className="font-medium text-sky-800 hover:text-sky-700">
          Entrar
        </Link>
      </p>
    </section>
  )
}
