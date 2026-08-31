import { LogOut } from 'lucide-react'
import { Link, Navigate, Outlet, useNavigate } from 'react-router-dom'

import { getAuthToken } from '@/services/auth-token'
import { logout } from '@/services/user-service'

export function PrivateLayout() {
  const navigate = useNavigate()
  const token = getAuthToken()

  if (!token) {
    return <Navigate to="/login" replace />
  }

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <div className="grid min-h-screen grid-cols-1 md:grid-cols-[240px_1fr]">
      <aside className="border-b border-slate-800 bg-slate-900 px-4 py-4 text-slate-100 md:border-b-0 md:border-r md:border-slate-200 md:p-6">
        <div className="flex items-center justify-between gap-3 md:block">
          <h1 className="text-lg font-semibold md:mb-6">Sola Scriptura</h1>
          <button
            type="button"
            onClick={handleLogout}
            className="inline-flex h-9 items-center justify-center rounded-md px-3 text-slate-200 transition hover:bg-slate-800 hover:text-white md:hidden"
            aria-label="Sair"
          >
            <LogOut className="h-4 w-4" />
          </button>
        </div>
        <div className="mt-4 flex items-center gap-2 overflow-x-auto pb-1 text-sm md:mt-0 md:flex-col md:items-stretch md:gap-3 md:overflow-visible md:pb-0">
          <nav className="flex flex-1 gap-2 md:flex-col md:gap-3">
            <Link className="shrink-0 rounded-md px-3 py-2 text-slate-200 transition hover:bg-slate-800 hover:text-white md:px-0 md:py-0 md:hover:bg-transparent" to="/app/dashboard">
              Dashboard
            </Link>
            <Link className="shrink-0 rounded-md px-3 py-2 text-slate-200 transition hover:bg-slate-800 hover:text-white md:px-0 md:py-0 md:hover:bg-transparent" to="/bible">
              Biblia
            </Link>
            <Link className="shrink-0 rounded-md px-3 py-2 text-slate-200 transition hover:bg-slate-800 hover:text-white md:px-0 md:py-0 md:hover:bg-transparent" to="/app/devotionals">
              Devocionais
            </Link>
            <Link className="shrink-0 rounded-md px-3 py-2 text-slate-200 transition hover:bg-slate-800 hover:text-white md:px-0 md:py-0 md:hover:bg-transparent" to="/app/profile">
              Perfil
            </Link>
            <Link className="shrink-0 rounded-md px-3 py-2 text-slate-200 transition hover:bg-slate-800 hover:text-white md:px-0 md:py-0 md:hover:bg-transparent" to="/app/biblia-teste-login">
              Biblia-teste-login
            </Link>
          </nav>
          <button
            type="button"
            onClick={handleLogout}
            className="hidden h-10 items-center justify-center gap-2 rounded-md border border-slate-700 px-3 text-sm text-slate-200 transition hover:bg-slate-800 hover:text-white md:mt-6 md:inline-flex"
          >
            <LogOut className="h-4 w-4" />
            Sair
          </button>
        </div>
      </aside>
      <main className="bg-[#f8f5ef] p-4 md:p-8">
        <Outlet />
      </main>
    </div>
  )
}
