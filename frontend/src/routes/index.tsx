import { Navigate, createBrowserRouter } from 'react-router-dom'

import { PrivateLayout } from '@/layouts/private-layout'
import { PublicLayout } from '@/layouts/public-layout'
import { BiblePage } from '@/pages/bible-page'
import { DashboardPage } from '@/pages/dashboard-page'
import { DevotionalsPage } from '@/pages/devotionals-page'
import { HomePage } from '@/pages/home-page'
import { LoginPage } from '@/pages/login-page'
import { ProfilePage } from '@/pages/profile-page'
import { RegisterPage } from '@/pages/register-page'
import { BibleLoginPage } from '@/pages/bible-login-page.tsx'

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
    children: [
      { index: true, element: <Navigate to="/app/dashboard" replace /> },
      { path: 'dashboard', element: <DashboardPage /> },
      { path: 'devotionals', element: <DevotionalsPage /> },
      { path: 'profile', element: <ProfilePage /> },
      { path: 'Biblia', element: <BibleLoginPage /> },
    ],
  },
])
