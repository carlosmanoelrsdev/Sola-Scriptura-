import { Loader2, LogIn } from 'lucide-react'
import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useMutation } from '@tanstack/react-query'

import { login } from '@/services/auth-service'
import { setAuthToken } from '@/services/auth-token'

export function LoginPage() {
  const navigate = useNavigate()

  const loginMutation = useMutation({
    mutationFn: login,
    onSuccess: (response) => {
      setAuthToken(response.token)
      navigate('/app/dashboard')
    },
  })

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const formData = new FormData(event.currentTarget)

    loginMutation.mutate({
      email: String(formData.get('email') ?? ''),
      password: String(formData.get('password') ?? ''),
    })
  }

  return (
    <section className="mx-auto max-w-md rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
      <p className="text-sm font-medium uppercase text-sky-700">Acesso</p>
      <h1 className="mt-2 font-serif text-3xl font-semibold text-slate-950">Login</h1>
      <p className="mt-2 text-sm leading-6 text-slate-700">
        Entre para registrar leituras, acompanhar progresso e gerenciar seus devocionais.
      </p>

      <form onSubmit={handleSubmit} className="mt-6 grid gap-4">
        <label className="grid gap-2 text-sm font-medium text-slate-800">
          Email
          <input
            name="email"
            type="email"
            autoComplete="email"
            className="h-11 rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-900 outline-none transition focus:border-sky-700 focus:ring-2 focus:ring-sky-100"
            required
          />
        </label>

        <label className="grid gap-2 text-sm font-medium text-slate-800">
          Senha
          <input
            name="password"
            type="password"
            autoComplete="current-password"
            className="h-11 rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-900 outline-none transition focus:border-sky-700 focus:ring-2 focus:ring-sky-100"
            required
          />
        </label>

        {loginMutation.isError ? (
          <p className="rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-900">
            Email ou senha invalidos.
          </p>
        ) : null}

        <button
          type="submit"
          className="inline-flex h-11 items-center justify-center gap-2 rounded-md bg-sky-900 px-4 text-sm font-medium text-white transition hover:bg-sky-800 disabled:cursor-not-allowed disabled:opacity-60"
          disabled={loginMutation.isPending}
        >
          {loginMutation.isPending ? <Loader2 className="h-4 w-4 animate-spin" /> : <LogIn className="h-4 w-4" />}
          Entrar
        </button>
      </form>

      <p className="mt-5 text-sm text-slate-600">
        Ainda nao tem conta?{' '}
        <Link to="/register" className="font-medium text-sky-800 hover:text-sky-700">
          Criar cadastro
        </Link>
      </p>
    </section>
  )
}
