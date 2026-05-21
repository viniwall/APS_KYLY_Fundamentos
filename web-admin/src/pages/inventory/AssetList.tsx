import React, { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { apiClient } from '../../api/client'
import { Badge, statusToBadge } from '../../components/ui/Badge'
import { Spinner } from '../../components/ui/Spinner'
import { Pagination } from '../../components/ui/Pagination'
import type { Bem, PageResponse } from '../../types'

const SITUACOES = ['', 'ATIVO', 'EM_MANUTENCAO', 'BAIXADO', 'NAO_LOCALIZADO']

export default function AssetList() {
  const [page, setPage] = useState(0)
  const [situacao, setSituacao] = useState('')
  const [q, setQ] = useState('')

  const { data, isLoading } = useQuery<PageResponse<Bem>>({
    queryKey: ['bens', page, situacao, q],
    queryFn: () =>
      apiClient
        .get('/v1/inventario/bens', { params: { page, size: 20, situacao: situacao || undefined, q: q || undefined } })
        .then(r => r.data),
  })

  const exportCSV = () => {
    const rows = [
      ['Código', 'Descrição', 'Marca', 'Modelo', 'Situação', 'Localização'],
      ...(data?.content ?? []).map(b => [
        b.codigoPatrimonio, b.descricao, b.marca, b.modelo,
        b.situacao, b.localizacaoAtual?.nome ?? '',
      ]),
    ]
    const csv = rows.map(r => r.map(c => `"${c ?? ''}"`).join(';')).join('\n')
    const blob = new Blob(['﻿' + csv], { type: 'text/csv;charset=utf-8;' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'bens.csv'
    a.click()
    URL.revokeObjectURL(url)
  }

  return (
    <div className="flex flex-col gap-4">
      <div className="flex gap-3 flex-wrap items-center justify-between bg-white p-3 border border-divider">
        <div className="flex gap-3 flex-wrap">
          <input
            type="text"
            placeholder="Buscar código ou descrição..."
            value={q}
            onChange={e => { setQ(e.target.value); setPage(0) }}
            className="h-9 px-3 border border-divider text-sm focus:outline-none focus:border-primary w-60"
          />
          <select
            value={situacao}
            onChange={e => { setSituacao(e.target.value); setPage(0) }}
            className="h-9 px-3 border border-divider text-sm focus:outline-none bg-white"
          >
            {SITUACOES.map(s => (
              <option key={s} value={s}>{s || 'Todas as situações'}</option>
            ))}
          </select>
        </div>
        <button
          onClick={exportCSV}
          className="h-9 px-4 text-sm border border-primary text-primary hover:bg-primary hover:text-white transition-colors"
        >
          Exportar CSV
        </button>
      </div>

      <div className="bg-white border border-divider">
        {isLoading ? (
          <Spinner />
        ) : (
          <div className="overflow-x-auto">
            <table>
              <thead>
                <tr className="border-b border-divider bg-surface text-xs text-text-secondary text-left">
                  <th className="px-4 py-2 font-medium">Código</th>
                  <th className="px-4 py-2 font-medium">Descrição</th>
                  <th className="px-4 py-2 font-medium">Marca / Modelo</th>
                  <th className="px-4 py-2 font-medium">Localização</th>
                  <th className="px-4 py-2 font-medium">Situação</th>
                </tr>
              </thead>
              <tbody>
                {(data?.content ?? []).map((bem) => (
                  <tr key={bem.id} className="border-b border-divider hover:bg-surface text-sm">
                    <td className="px-4 py-3 font-mono font-bold text-primary">{bem.codigoPatrimonio}</td>
                    <td className="px-4 py-3">{bem.descricao || '—'}</td>
                    <td className="px-4 py-3 text-text-secondary">{[bem.marca, bem.modelo].filter(Boolean).join(' / ') || '—'}</td>
                    <td className="px-4 py-3 text-text-secondary">{bem.localizacaoAtual?.nome || '—'}</td>
                    <td className="px-4 py-3">
                      <Badge label={bem.situacao} variant={statusToBadge(bem.situacao)} />
                    </td>
                  </tr>
                ))}
                {(data?.content ?? []).length === 0 && (
                  <tr>
                    <td colSpan={5} className="px-4 py-8 text-center text-text-secondary text-sm">
                      Nenhum bem encontrado.
                    </td>
                  </tr>
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
