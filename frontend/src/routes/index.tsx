import { createBrowserRouter } from 'react-router-dom'

import { PrivateLayout } from '@/layouts/private-layout'
import { PublicLayout } from '@/layouts/public-layout'
import { BiblePage } from '@/pages/bible-page'
import { DashboardPage } from '@/pages/dashboard-page'
import { HomePage } from '@/pages/home-page'
import { LoginPage } from '@/pages/login-page'
import { RegisterPage } from '@/pages/register-page'

export const appRouter = createBrowserRouter([
  {
    path: '/',
    element: <PublicLayout />,
    children: [
      { index: true, element: <HomePage /> },
      { path: 'bible', element: <BiblePage /> },
      { path: 'login', element: <LoginPage /> },
      { path: 'register', element: <RegisterPage /> },
    ],
  },
  {
    path: '/app',
    element: <PrivateLayout />,
    children: [{ path: 'dashboard', element: <DashboardPage /> }],
  },
])
