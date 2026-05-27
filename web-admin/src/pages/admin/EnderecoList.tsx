import React, { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { apiClient } from '../../api/client'
import { Spinner } from '../../components/ui/Spinner'
import { Pagination } from '../../components/ui/Pagination'
import { Badge } from '../../components/ui/Badge'
import type { EnderecoEstoque, PageResponse } from '../../types'

export default function EnderecoList() {
  const [page, setPage] = useState(0)
  const [q, setQ] = useState('')
  const [ativo, setAtivo] = useState('')

  const { data, isLoading } = useQuery<PageResponse<EnderecoEstoque>>({
    queryKey: ['enderecos', page, q, ativo],
    queryFn: () =>
      apiClient.get('/v1/admin/enderecos', {
        params: { page, size: 20, q: q || undefined, ativo: ativo || undefined },
      }).then(r => r.data),
  })

  return (
    <div className="flex flex-col gap-4">
      <div className="flex gap-3 flex-wrap items-center bg-white p-3 border border-divider">
        <input
          type="text"
          placeholder="Buscar código ou rua..."
          value={q}
          onChange={e => { setQ(e.target.value); setPage(0) }}
          className="h-9 px-3 border border-divider text-sm focus:outline-none focus:border-primary w-56"
        />
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
                  <th className="px-4 py-2 font-medium">Código</th>
                  <th className="px-4 py-2 font-medium">Andar / Rua</th>
                  <th className="px-4 py-2 font-medium">Seção</th>
                  <th className="px-4 py-2 font-medium">Posição / Nível</th>
                  <th className="px-4 py-2 font-medium">Status</th>
                </tr>
              </thead>
              <tbody>
                {(data?.content ?? []).map(end => (
                  <tr key={end.id} className="border-b border-divider hover:bg-surface text-sm">
                    <td className="px-4 py-3 font-mono font-bold text-primary">{end.codigo}</td>
                    <td className="px-4 py-3 text-text-secondary">{end.andarRua}</td>
                    <td className="px-4 py-3 text-text-secondary">{end.secao}</td>
                    <td className="px-4 py-3 font-medium">{end.posicaoNivel}</td>
                    <td className="px-4 py-3">
                      <Badge label={end.ativo ? 'ATIVO' : 'INATIVO'} variant={end.ativo ? 'success' : 'error'} />
                    </td>
                  </tr>
                ))}
                {(data?.content ?? []).length === 0 && (
                  <tr><td colSpan={5} className="px-4 py-8 text-center text-text-secondary text-sm">Nenhum endereço encontrado.</td></tr>
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
