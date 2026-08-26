import { Link, Outlet } from 'react-router-dom'

export function PrivateLayout() {
  return (
    <div className="grid min-h-screen grid-cols-1 md:grid-cols-[240px_1fr]">
      <aside className="border-r border-slate-200 bg-slate-900 p-6 text-slate-100">
        <h1 className="mb-6 text-lg font-semibold">Sola Scriptura</h1>
        <nav className="flex flex-col gap-3 text-sm">
          <Link to="/app/dashboard">Dashboard</Link>
          <Link to="/bible">Biblia</Link>
        </nav>
      </aside>
      <main className="bg-[#f8f5ef] p-6 md:p-8">
        <Outlet />
      </main>
    </div>
  )
}
