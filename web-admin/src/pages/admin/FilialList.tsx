import React, { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { apiClient } from '../../api/client'
import { Spinner } from '../../components/ui/Spinner'
import { Pagination } from '../../components/ui/Pagination'
import { Badge } from '../../components/ui/Badge'
import type { Filial, PageResponse } from '../../types'

export default function FilialList() {
  const [page, setPage] = useState(0)
  const [q, setQ] = useState('')

  const { data, isLoading } = useQuery<PageResponse<Filial>>({
    queryKey: ['filiais', page, q],
    queryFn: () =>
      apiClient.get('/v1/admin/filiais', {
        params: { page, size: 20, q: q || undefined },
      }).then(r => r.data),
  })

  return (
    <div className="flex flex-col gap-4">
      <div className="flex gap-3 items-center bg-white p-3 border border-divider">
        <input
          type="text"
          placeholder="Buscar código ou nome..."
          value={q}
          onChange={e => { setQ(e.target.value); setPage(0) }}
          className="h-9 px-3 border border-divider text-sm focus:outline-none focus:border-primary w-64"
        />
      </div>

      <div className="bg-white border border-divider">
        {isLoading ? <Spinner /> : (
          <div className="overflow-x-auto">
            <table>
              <thead>
                <tr className="border-b border-divider bg-surface text-xs text-text-secondary text-left">
                  <th className="px-4 py-2 font-medium">Código</th>
                  <th className="px-4 py-2 font-medium">Nome</th>
                  <th className="px-4 py-2 font-medium">Endereço</th>
                  <th className="px-4 py-2 font-medium">Status</th>
                </tr>
              </thead>
              <tbody>
                {(data?.content ?? []).map(f => (
                  <tr key={f.id} className="border-b border-divider hover:bg-surface text-sm">
                    <td className="px-4 py-3 font-mono font-bold text-primary">{f.codigo}</td>
                    <td className="px-4 py-3 font-medium">{f.nome}</td>
                    <td className="px-4 py-3 text-text-secondary text-xs">{f.endereco}</td>
                    <td className="px-4 py-3">
                      <Badge label={f.ativo ? 'ATIVA' : 'INATIVA'} variant={f.ativo ? 'success' : 'error'} />
                    </td>
                  </tr>
                ))}
                {(data?.content ?? []).length === 0 && (
                  <tr><td colSpan={4} className="px-4 py-8 text-center text-text-secondary text-sm">Nenhuma filial encontrada.</td></tr>
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
