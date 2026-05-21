import React from 'react'
import { Outlet, useLocation } from 'react-router-dom'
import { Sidebar } from './Sidebar'
import { Header } from './Header'

const PAGE_TITLES: Record<string, string> = {
  '/dashboard': 'Dashboard',
  '/picking/caixas': 'Caixas',
  '/inventario/bens': 'Bens Patrimoniais',
  '/inventario/inventarios': 'Inventários',
  '/relatorios/produtividade': 'Produtividade',
  '/relatorios/divergencias': 'Divergências',
  '/relatorios/parciais': 'Caixas Parciais',
  '/admin/skus': 'SKUs',
  '/admin/enderecos': 'Endereços',
  '/admin/usuarios': 'Usuários',
  '/admin/filiais': 'Filiais',
  '/integracoes': 'Integrações',
}

export function Layout() {
  const location = useLocation()
  const title = PAGE_TITLES[location.pathname] ?? 'KollectaOps'

  return (
    <div className="flex h-screen overflow-hidden">
      <Sidebar />
      <div className="flex flex-col flex-1 overflow-hidden">
        <Header title={title} />
        <main className="flex-1 overflow-y-auto bg-surface p-6">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
