import React, { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { apiClient } from '../../api/client'
import { Badge, statusToBadge } from '../../components/ui/Badge'
import { Spinner } from '../../components/ui/Spinner'
import { Pagination } from '../../components/ui/Pagination'
import type { Caixa, PageResponse } from '../../types'
import { format } from 'date-fns'

const STATUSES = ['', 'AGUARDANDO', 'EM_PICKING', 'PARCIAL', 'FINALIZADA', 'CANCELADA']

const TARJA_CONFIG: Record<string, { label: string; color: string; bg: string }> = {
  PADRAO:          { label: 'Padrão',     color: '#555555', bg: '#E0E0E0' },
  EXPORTACAO_AZUL: { label: 'Exportação', color: '#FFFFFF', bg: '#1565C0' },
  TAG_VERDE:       { label: 'Tag Verde',  color: '#FFFFFF', bg: '#2E7D32' },
  TAG_ROSA:        { label: 'Tag Rosa',   color: '#FFFFFF', bg: '#C2185B' },
  MULTI_AMARELO:   { label: 'Multi',      color: '#111111', bg: '#F9A825' },
  VERMELHA:        { label: 'Vermelha',   color: '#FFFFFF', bg: '#C62828' },
}

function TarjaBadge({ corTarja }: { corTarja: string }) {
  const cfg = TARJA_CONFIG[corTarja] ?? { label: corTarja, color: '#333', bg: '#DDD' }
  return (
    <span
      style={{ backgroundColor: cfg.bg, color: cfg.color }}
      className="inline-block px-2 py-0.5 text-xs font-bold rounded-sm"
    >
      {cfg.label}
    </span>
  )
}

export default function BoxList() {
  const navigate = useNavigate()
  const [page, setPage] = useState(0)
  const [status, setStatus] = useState('')
  const [search, setSearch] = useState('')

  const { data, isLoading } = useQuery<PageResponse<Caixa>>({
    queryKey: ['caixas', page, status, search],
    queryFn: () =>
      apiClient
        .get(`/v1/picking/caixas`, { params: { page, size: 20, status: status || undefined } })
        .then(r => r.data),
    refetchInterval: 15_000,
  })

  const fmtDate = (d: string | null) => {
    if (!d) return '—'
    try { return format(new Date(d), 'dd/MM HH:mm') } catch { return d }
  }

  const filtered = (data?.content ?? []).filter(c =>
    !search || c.codigoPapeleta.includes(search) || (c.clienteNome || '').toLowerCase().includes(search.toLowerCase())
  )

  return (
    <div className="flex flex-col gap-4">
      {/* Filters */}
      <div className="flex gap-3 flex-wrap items-center bg-white p-3 border border-divider">
        <input
          type="text"
          placeholder="Buscar papeleta ou cliente..."
          value={search}
          onChange={e => setSearch(e.target.value)}
          className="h-9 px-3 border border-divider text-sm focus:outline-none focus:border-primary w-56"
        />
        <select
          value={status}
          onChange={e => { setStatus(e.target.value); setPage(0) }}
          className="h-9 px-3 border border-divider text-sm focus:outline-none bg-white"
        >
          {STATUSES.map(s => (
            <option key={s} value={s}>{s || 'Todos os status'}</option>
          ))}
        </select>
      </div>

      {/* Table */}
      <div className="bg-white">
        {isLoading ? (
          <Spinner />
        ) : (
          <div className="overflow-x-auto">
            <table>
              <thead>
                <tr className="border-b border-divider bg-surface text-left text-xs text-text-secondary">
                  <th className="px-4 py-2 font-medium">Papeleta</th>
                  <th className="px-4 py-2 font-medium">OP</th>
                  <th className="px-4 py-2 font-medium">Cliente</th>
                  <th className="px-4 py-2 font-medium">Tarja</th>
                  <th className="px-4 py-2 font-medium">Status</th>
                  <th className="px-4 py-2 font-medium">Abertura</th>
                  <th className="px-4 py-2 font-medium">Finalização</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((caixa) => (
                  <tr
                    key={caixa.id}
                    className="border-b border-divider hover:bg-surface text-sm cursor-pointer"
                    onClick={() => navigate(`/picking/caixas/${caixa.codigoPapeleta}`)}
                  >
                    <td className="px-4 py-3 font-mono font-bold text-primary">{caixa.codigoPapeleta}</td>
                    <td className="px-4 py-3 text-text-secondary">{caixa.numeroOp || '—'}</td>
                    <td className="px-4 py-3">{caixa.clienteNome || '—'}</td>
                    <td className="px-4 py-3">
                      <TarjaBadge corTarja={caixa.corTarja} />
                    </td>
                    <td className="px-4 py-3">
                      <Badge label={caixa.status} variant={statusToBadge(caixa.status)} />
                    </td>
                    <td className="px-4 py-3 text-text-secondary">{fmtDate(caixa.abertaEm)}</td>
                    <td className="px-4 py-3 text-text-secondary">{fmtDate(caixa.finalizadaEm)}</td>
                  </tr>
                ))}
                {filtered.length === 0 && (
                  <tr>
                    <td colSpan={7} className="px-4 py-8 text-center text-text-secondary text-sm">
                      Nenhuma caixa encontrada.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
        <div className="px-4">
          <Pagination
            page={page}
            totalPages={data?.totalPages ?? 0}
            onPageChange={setPage}
          />
        </div>
      </div>
    </div>
  )
}
