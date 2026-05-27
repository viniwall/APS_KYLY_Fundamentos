import React, { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { apiClient } from '../../api/client'
import { Spinner } from '../../components/ui/Spinner'
import { Pagination } from '../../components/ui/Pagination'
import { Badge } from '../../components/ui/Badge'
import type { User, PageResponse } from '../../types'

const PERFIS = ['', 'ADMIN', 'GESTOR', 'SUPERVISOR', 'OPERADOR']

const perfilVariant = (p: string) => {
  if (p === 'ADMIN')      return 'error'
  if (p === 'GESTOR')     return 'warning'
  if (p === 'SUPERVISOR') return 'info'
  return 'default'
}

export default function UsuarioList() {
  const [page, setPage] = useState(0)
  const [q, setQ] = useState('')
  const [perfil, setPerfil] = useState('')
  const [ativo, setAtivo] = useState('')

  const { data, isLoading } = useQuery<PageResponse<User>>({
    queryKey: ['usuarios', page, q, perfil, ativo],
    queryFn: () =>
      apiClient.get('/v1/admin/usuarios', {
        params: { page, size: 20, q: q || undefined, perfil: perfil || undefined, ativo: ativo || undefined },
      }).then(r => r.data),
  })

  return (
    <div className="flex flex-col gap-4">
      <div className="flex gap-3 flex-wrap items-center bg-white p-3 border border-divider">
        <input
          type="text"
          placeholder="Nome, crachá ou e-mail..."
          value={q}
          onChange={e => { setQ(e.target.value); setPage(0) }}
          className="h-9 px-3 border border-divider text-sm focus:outline-none focus:border-primary w-64"
        />
        <select
          value={perfil}
          onChange={e => { setPerfil(e.target.value); setPage(0) }}
          className="h-9 px-3 border border-divider text-sm focus:outline-none bg-white"
        >
          {PERFIS.map(p => (
            <option key={p} value={p}>{p || 'Todos os perfis'}</option>
          ))}
        </select>
        <select
          value={ativo}
          onChange={e => { setAtivo(e.target.value); setPage(0) }}
          className="h-9 px-3 border border-divider text-sm focus:outline-none bg-white"
        >
          <option value="">Todos</option>
          <option value="true">Ativos</option>
          <option value="false">Inativos</option>
        </select>
      </div>

      <div className="bg-white border border-divider">
        {isLoading ? <Spinner /> : (
          <div className="overflow-x-auto">
            <table>
              <thead>
                <tr className="border-b border-divider bg-surface text-xs text-text-secondary text-left">
                  <th className="px-4 py-2 font-medium">Crachá</th>
                  <th className="px-4 py-2 font-medium">Nome</th>
                  <th className="px-4 py-2 font-medium">E-mail</th>
                  <th className="px-4 py-2 font-medium">Perfil</th>
                  <th className="px-4 py-2 font-medium">Status</th>
                </tr>
              </thead>
              <tbody>
                {(data?.content ?? []).map(u => (
                  <tr key={u.id} className="border-b border-divider hover:bg-surface text-sm">
                    <td className="px-4 py-3 font-mono font-bold text-primary">{u.codigoCracha}</td>
                    <td className="px-4 py-3 font-medium">{u.nome}</td>
                    <td className="px-4 py-3 text-text-secondary text-xs">{u.email}</td>
                    <td className="px-4 py-3">
                      <Badge label={u.perfil} variant={perfilVariant(u.perfil) as any} />
                    </td>
                    <td className="px-4 py-3">
                      <Badge label={u.ativo ? 'ATIVO' : 'INATIVO'} variant={u.ativo ? 'success' : 'error'} />
                    </td>
                  </tr>
                ))}
                {(data?.content ?? []).length === 0 && (
                  <tr><td colSpan={5} className="px-4 py-8 text-center text-text-secondary text-sm">Nenhum usuário encontrado.</td></tr>
                )}
              </tbody>
            </table>
          </div>
        )}
        <div className="px-4">
          <Pagination page={page} totalPages={data?.totalPages ?? 0} onPageChange={setPage} />
        </div>
      </div>
    </div>
  )
}
