import React from 'react'
import { useQuery } from '@tanstack/react-query'
import { apiClient } from '../api/client'
import { StatCard } from '../components/ui/StatCard'
import { Badge, statusToBadge } from '../components/ui/Badge'
import { Spinner } from '../components/ui/Spinner'
import type { DashboardData, Caixa } from '../types'
import { format } from 'date-fns'
import { ptBR } from 'date-fns/locale'

export default function Dashboard() {
  const { data: dashboard, isLoading: loadingDash } = useQuery<DashboardData>({
    queryKey: ['dashboard'],
    queryFn: () => apiClient.get('/v1/admin/dashboard').then(r => r.data),
    refetchInterval: 30_000,
  })

  const { data: caixasPage, isLoading: loadingCaixas } = useQuery<{ content: Caixa[] }>({
    queryKey: ['caixas-recentes'],
    queryFn: () => apiClient.get('/v1/picking/caixas?status=FINALIZADA&size=20').then(r => r.data),
    refetchInterval: 30_000,
  })

  const fmtDate = (d: string | null) => {
    if (!d) return '—'
    try {
      return format(new Date(d), 'dd/MM HH:mm', { locale: ptBR })
    } catch { return d }
  }

  return (
    <div className="flex flex-col gap-6">
      <h2 className="text-xl font-bold text-text-primary">Visão Geral</h2>

      {loadingDash ? (
        <Spinner />
      ) : (
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
          <StatCard
            label="Caixas finalizadas hoje"
            value={dashboard?.caixasFinalizadasHoje ?? 0}
            color="#2E7D32"
          />
          <StatCard
            label="Em picking agora"
            value={dashboard?.emPickingAgora ?? 0}
            color="#1976D2"
          />
          <StatCard
            label="Caixas parciais"
            value={dashboard?.caixasParciais ?? 0}
            color="#F9A825"
          />
          <StatCard
            label="Operadores ativos"
            value={dashboard?.operadoresAtivos ?? 0}
            color="#1B3A57"
          />
        </div>
      )}

      <div className="bg-white">
        <div className="px-4 py-3 border-b border-divider">
          <h3 className="font-bold text-text-primary">Últimas 20 caixas finalizadas</h3>
        </div>
        {loadingCaixas ? (
          <Spinner />
        ) : (
          <div className="overflow-x-auto">
            <table>
              <thead>
                <tr className="border-b border-divider bg-surface text-left text-xs text-text-secondary">
                  <th className="px-4 py-2 font-medium">Papeleta</th>
                  <th className="px-4 py-2 font-medium">Cliente</th>
                  <th className="px-4 py-2 font-medium">Status</th>
                  <th className="px-4 py-2 font-medium">Abertura</th>
                  <th className="px-4 py-2 font-medium">Finalização</th>
                </tr>
              </thead>
              <tbody>
                {(caixasPage?.content ?? []).map((caixa) => (
                  <tr key={caixa.id} className="border-b border-divider hover:bg-surface text-sm">
                    <td className="px-4 py-2.5 font-mono font-bold">{caixa.codigoPapeleta}</td>
                    <td className="px-4 py-2.5 text-text-secondary">{caixa.clienteNome || '—'}</td>
                    <td className="px-4 py-2.5">
                      <Badge label={caixa.status} variant={statusToBadge(caixa.status)} />
                    </td>
                    <td className="px-4 py-2.5 text-text-secondary">{fmtDate(caixa.abertaEm)}</td>
                    <td className="px-4 py-2.5 text-text-secondary">{fmtDate(caixa.finalizadaEm)}</td>
                  </tr>
                ))}
                {(caixasPage?.content ?? []).length === 0 && (
                  <tr>
                    <td colSpan={5} className="px-4 py-6 text-center text-text-secondary text-sm">
                      Nenhuma caixa finalizada ainda hoje.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}
