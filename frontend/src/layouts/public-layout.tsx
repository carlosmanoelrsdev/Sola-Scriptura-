import { Link, Outlet } from 'react-router-dom'

export function PublicLayout() {
  return (
    <div className="min-h-screen">
      <header className="border-b border-slate-200 bg-white/80 backdrop-blur">
        <div className="mx-auto flex h-16 w-full max-w-6xl items-center justify-between px-4">
          <Link to="/" className="font-semibold text-slate-900">
            Sola Scriptura
          </Link>
          <nav className="flex items-center gap-4 text-sm text-slate-700">
            <Link to="/bible">Biblia</Link>
            <Link to="/login">Login</Link>
            <Link to="/register">Cadastro</Link>
          </nav>
        </div>
      </header>
      <main className="mx-auto w-full max-w-6xl px-4 py-8">
        <Outlet />
      </main>
    </div>
  )
}
