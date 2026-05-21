import React from 'react'
import { NavLink } from 'react-router-dom'

interface NavItem {
  label: string
  to: string
}

const navGroups: { label: string; items: NavItem[] }[] = [
  { label: '', items: [{ label: 'Dashboard', to: '/dashboard' }] },
  {
    label: 'Picking',
    items: [
      { label: 'Caixas', to: '/picking/caixas' },
    ],
  },
  {
    label: 'Inventário',
    items: [
      { label: 'Bens', to: '/inventario/bens' },
      { label: 'Inventários', to: '/inventario/inventarios' },
    ],
  },
  {
    label: 'Relatórios',
    items: [
      { label: 'Produtividade', to: '/relatorios/produtividade' },
      { label: 'Divergências', to: '/relatorios/divergencias' },
      { label: 'Caixas Parciais', to: '/relatorios/parciais' },
    ],
  },
  {
    label: 'Cadastros',
    items: [
      { label: 'SKUs', to: '/admin/skus' },
      { label: 'Endereços', to: '/admin/enderecos' },
      { label: 'Usuários', to: '/admin/usuarios' },
      { label: 'Filiais', to: '/admin/filiais' },
    ],
  },
  { label: '', items: [{ label: 'Integrações', to: '/integracoes' }] },
]

export function Sidebar() {
  return (
    <nav className="w-52 min-h-screen bg-primary flex flex-col shrink-0">
      <div className="px-4 py-4 border-b border-primary-dark">
        <div className="text-white font-bold text-lg">KollectaOps</div>
        <div className="text-white/60 text-xs mt-0.5">Operações de Chão</div>
      </div>

      <div className="flex-1 overflow-y-auto py-2">
        {navGroups.map((group, gi) => (
          <div key={gi}>
            {group.label && (
              <div className="px-4 py-2 text-white/40 text-xs font-medium uppercase tracking-wider">
                {group.label}
              </div>
            )}
            {group.items.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  `block px-4 py-2.5 text-sm transition-colors ${
                    isActive
                      ? 'bg-white/10 text-white font-medium'
                      : 'text-white/70 hover:bg-white/5 hover:text-white'
                  }`
                }
              >
                {item.label}
              </NavLink>
            ))}
          </div>
        ))}
      </div>

      <div className="px-4 py-3 border-t border-primary-dark">
        <div className="flex items-center gap-2">
          <div className="w-2 h-2 rounded-full bg-success"></div>
          <span className="text-white/60 text-xs">Conectado</span>
        </div>
      </div>
    </nav>
  )
}
