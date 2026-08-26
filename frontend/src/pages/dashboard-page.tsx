import { BookOpen, CalendarDays, FileText, Flame, Loader2, RefreshCw, TrendingUp } from 'lucide-react'
import { Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'

import { getDashboard } from '@/services/dashboard-service'

const dateFormatter = new Intl.DateTimeFormat('pt-BR', {
  day: '2-digit',
  month: 'short',
  year: 'numeric',
})

export function DashboardPage() {
  const dashboardQuery = useQuery({
    queryKey: ['dashboard'],
    queryFn: getDashboard,
  })

  const dashboard = dashboardQuery.data
  const progress = dashboard?.progress

  return (
    <section className="space-y-6">
      <div className="flex flex-col gap-4 border-b border-slate-200 pb-6 lg:flex-row lg:items-end lg:justify-between">
        <div>
          <p className="text-sm font-medium uppercase text-sky-700">Area pessoal</p>
          <h1 className="mt-2 font-serif text-3xl font-semibold text-slate-950 md:text-4xl">Dashboard</h1>
          <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-700">
            Acompanhe sua leitura, progresso e devocionais recentes em um so lugar.
          </p>
        </div>

        <button
          type="button"
          onClick={() => dashboardQuery.refetch()}
          className="inline-flex h-10 items-center justify-center gap-2 rounded-md border border-slate-300 bg-white px-3 text-sm font-medium text-slate-800 shadow-sm transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60"
          disabled={dashboardQuery.isFetching}
        >
          <RefreshCw className="h-4 w-4" />
          Atualizar
        </button>
      </div>

      {dashboardQuery.isLoading ? (
        <div className="flex min-h-[420px] items-center justify-center rounded-lg border border-slate-200 bg-white text-slate-600">
          <Loader2 className="mr-2 h-5 w-5 animate-spin" />
          Carregando dashboard
        </div>
      ) : dashboardQuery.isError ? (
        <div className="rounded-lg border border-amber-200 bg-amber-50 p-6">
          <h2 className="text-lg font-semibold text-amber-950">Nao foi possivel carregar o dashboard</h2>
          <p className="mt-2 max-w-2xl text-sm leading-6 text-amber-900">
            Verifique se voce esta autenticado e tente novamente. O endpoint do dashboard exige token Bearer.
          </p>
          <Link
            to="/login"
            className="mt-4 inline-flex h-10 items-center justify-center rounded-md bg-sky-900 px-4 text-sm font-medium text-white transition hover:bg-sky-800"
          >
            Ir para login
          </Link>
        </div>
      ) : progress ? (
        <>
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
            <MetricCard
              icon={<Flame className="h-5 w-5" />}
              label="Streak atual"
              value={`${progress.currentStreak} dias`}
              description={`Maior streak: ${progress.longestStreak} dias`}
            />
            <MetricCard
              icon={<BookOpen className="h-5 w-5" />}
              label="Capitulos lidos"
              value={String(progress.chaptersRead)}
              description={`${progress.totalChapters} capitulos no total`}
            />
            <MetricCard
              icon={<TrendingUp className="h-5 w-5" />}
              label="Progresso"
              value={`${progress.percent.toFixed(2)}%`}
              description={`${progress.booksCompleted} livros concluidos`}
            />
            <MetricCard
              icon={<CalendarDays className="h-5 w-5" />}
              label="Ultima leitura"
              value={progress.lastReading ? `${progress.lastReading.book} ${progress.lastReading.chapter}` : 'Nenhuma'}
              description={progress.lastReading ? formatDate(progress.lastReading.readAt) : 'Comece pela tela Biblia'}
            />
          </div>

          <div className="grid gap-6 xl:grid-cols-[1fr_360px]">
            <article className="rounded-lg border border-slate-200 bg-[#fffdf8] p-6 shadow-sm">
              <div className="flex items-start gap-3">
                <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-md bg-sky-100 text-sky-900">
                  <BookOpen className="h-5 w-5" />
                </div>
                <div>
                  <p className="text-sm font-medium uppercase text-sky-700">Versiculo do dia</p>
                  <h2 className="mt-1 font-serif text-2xl font-semibold text-slate-950">
                    {dashboard?.verseOfDay?.reference ?? 'Indisponivel no momento'}
                  </h2>
                </div>
              </div>

              {dashboard?.verseOfDay ? (
                <p className="mt-6 max-w-3xl font-serif text-2xl leading-10 text-slate-900">
                  {dashboard.verseOfDay.text}
                </p>
              ) : (
                <p className="mt-6 max-w-2xl text-sm leading-6 text-slate-600">
                  A BIBLIAAPI nao retornou um versiculo agora. O restante do dashboard continua disponivel.
                </p>
              )}
            </article>

            <aside className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
              <div className="flex items-center justify-between gap-3">
                <div>
                  <p className="text-sm font-medium uppercase text-sky-700">Devocionais</p>
                  <h2 className="mt-1 text-xl font-semibold text-slate-950">Recentes</h2>
                </div>
                <FileText className="h-5 w-5 text-slate-500" />
              </div>

              <div className="mt-5 space-y-3">
                {dashboard?.recentDevotionals.length ? (
                  dashboard.recentDevotionals.map((devotional) => (
                    <div key={devotional.id} className="rounded-md border border-slate-200 p-4">
                      <h3 className="line-clamp-1 font-medium text-slate-950">{devotional.title}</h3>
                      <p className="mt-2 line-clamp-2 text-sm leading-6 text-slate-600">{devotional.content}</p>
                      <p className="mt-3 text-xs font-medium text-slate-500">
                        Atualizado em {formatDate(devotional.updatedAt)}
                      </p>
                    </div>
                  ))
                ) : (
                  <p className="rounded-md border border-dashed border-slate-300 p-4 text-sm leading-6 text-slate-600">
                    Nenhum devocional criado ainda.
                  </p>
                )}
              </div>
            </aside>
          </div>
        </>
      ) : null}
    </section>
  )
}

type MetricCardProps = {
  icon: React.ReactNode
  label: string
  value: string
  description: string
}

function MetricCard({ icon, label, value, description }: MetricCardProps) {
  return (
    <article className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
      <div className="flex items-center gap-3 text-sky-900">
        <span className="flex h-10 w-10 items-center justify-center rounded-md bg-sky-100">{icon}</span>
        <span className="text-sm font-medium text-slate-600">{label}</span>
      </div>
      <p className="mt-4 text-3xl font-semibold text-slate-950">{value}</p>
      <p className="mt-2 text-sm text-slate-600">{description}</p>
    </article>
  )
}

function formatDate(value: string) {
  return dateFormatter.format(new Date(value))
}
