import React, { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { apiClient } from '../../api/client'
import { Spinner } from '../../components/ui/Spinner'
import { Pagination } from '../../components/ui/Pagination'
import { Badge } from '../../components/ui/Badge'
import type { Sku, PageResponse } from '../../types'

export default function SkuList() {
  const [page, setPage] = useState(0)
  const [q, setQ] = useState('')
  const [ativo, setAtivo] = useState('')

  const { data, isLoading } = useQuery<PageResponse<Sku>>({
    queryKey: ['skus', page, q, ativo],
    queryFn: () =>
      apiClient.get('/v1/admin/skus', {
        params: { page, size: 20, q: q || undefined, ativo: ativo || undefined },
      }).then(r => r.data),
  })

  return (
    <div className="flex flex-col gap-4">
      <div className="flex gap-3 flex-wrap items-center bg-white p-3 border border-divider">
        <input
          type="text"
          placeholder="Referência, descrição ou EAN..."
          value={q}
          onChange={e => { setQ(e.target.value); setPage(0) }}
          className="h-9 px-3 border border-divider text-sm focus:outline-none focus:border-primary w-64"
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
                  <th className="px-4 py-2 font-medium">Referência</th>
                  <th className="px-4 py-2 font-medium">Descrição</th>
                  <th className="px-4 py-2 font-medium">Cor</th>
                  <th className="px-4 py-2 font-medium">Tamanho</th>
                  <th className="px-4 py-2 font-medium">EAN</th>
                  <th className="px-4 py-2 font-medium">Status</th>
                </tr>
              </thead>
              <tbody>
                {(data?.content ?? []).map(sku => (
                  <tr key={sku.id} className="border-b border-divider hover:bg-surface text-sm">
                    <td className="px-4 py-3 font-mono font-bold text-primary">{sku.referencia}</td>
                    <td className="px-4 py-3">{sku.descricao}</td>
                    <td className="px-4 py-3 text-text-secondary">{sku.cor}</td>
                    <td className="px-4 py-3 text-center font-medium">{sku.tamanho}</td>
                    <td className="px-4 py-3 font-mono text-text-secondary text-xs">{sku.codigoEan}</td>
                    <td className="px-4 py-3">
                      <Badge label={sku.ativo ? 'ATIVO' : 'INATIVO'} variant={sku.ativo ? 'success' : 'error'} />
                    </td>
                  </tr>
                ))}
                {(data?.content ?? []).length === 0 && (
                  <tr><td colSpan={6} className="px-4 py-8 text-center text-text-secondary text-sm">Nenhum SKU encontrado.</td></tr>
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
