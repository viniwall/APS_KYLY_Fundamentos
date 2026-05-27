import React, { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { apiClient } from '../../api/client'
import { Spinner } from '../../components/ui/Spinner'
import { Badge } from '../../components/ui/Badge'
import { format } from 'date-fns'
import { ptBR } from 'date-fns/locale'

interface Filial {
  id: number
  codigo: string
  nome: string
}

interface Inventario {
  id: number
  filial: Filial
  descricao: string
  status: 'ABERTO' | 'EM_ANDAMENTO' | 'FECHADO'
  iniciadoEm: string
  finalizadoEm: string | null
  criadoPor?: { nome: string }
}

const statusVariant = (s: string) => {
  if (s === 'FECHADO')      return 'success'
  if (s === 'EM_ANDAMENTO') return 'warning'
  return 'info'
}

const statusLabel = (s: string) => {
  if (s === 'FECHADO')      return 'FECHADO'
  if (s === 'EM_ANDAMENTO') return 'EM ANDAMENTO'
  return 'ABERTO'
}

function fmtDate(d: string | null) {
  if (!d) return '—'
  try { return format(new Date(d), 'dd/MM/yyyy HH:mm', { locale: ptBR }) } catch { return d }
}

export default function InventarioList() {
  const [statusFilter, setStatusFilter] = useState('')

  const { data, isLoading } = useQuery<Inventario[]>({
    queryKey: ['inventarios', statusFilter],
    queryFn: () =>
      apiClient.get('/v1/inventario/inventarios', {
        params: { status: statusFilter || undefined },
      }).then(r => r.data),
  })

  const list = data ?? []
  const filtered = statusFilter ? list.filter(i => i.status === statusFilter) : list

  return (
    <div className="flex flex-col gap-4">
      <div className="flex gap-3 items-center bg-white p-3 border border-divider flex-wrap">
        <select
          value={statusFilter}
          onChange={e => setStatusFilter(e.target.value)}
          className="h-9 px-3 border border-divider text-sm focus:outline-none bg-white"
        >
          <option value="">Todos os status</option>
          <option value="ABERTO">Aberto</option>
          <option value="EM_ANDAMENTO">Em andamento</option>
          <option value="FECHADO">Fechado</option>
        </select>
      </div>

      <div className="bg-white border border-divider">
        {isLoading ? <Spinner /> : (
          <div className="overflow-x-auto">
            <table>
              <thead>
                <tr className="border-b border-divider bg-surface text-xs text-text-secondary text-left">
                  <th className="px-4 py-2 font-medium">#</th>
                  <th className="px-4 py-2 font-medium">Descrição</th>
                  <th className="px-4 py-2 font-medium">Filial</th>
                  <th className="px-4 py-2 font-medium">Status</th>
                  <th className="px-4 py-2 font-medium">Iniciado em</th>
                  <th className="px-4 py-2 font-medium">Finalizado em</th>
                  <th className="px-4 py-2 font-medium">Criado por</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map(inv => (
                  <tr key={inv.id} className="border-b border-divider hover:bg-surface text-sm">
                    <td className="px-4 py-3 font-mono text-text-secondary text-xs">{inv.id}</td>
                    <td className="px-4 py-3 font-medium">{inv.descricao}</td>
                    <td className="px-4 py-3 text-text-secondary text-xs">
                      <span className="font-mono font-bold text-primary">{inv.filial?.codigo}</span>
                      {' — '}
                      {inv.filial?.nome}
                    </td>
                    <td className="px-4 py-3">
                      <Badge label={statusLabel(inv.status)} variant={statusVariant(inv.status) as any} />
                    </td>
                    <td className="px-4 py-3 text-text-secondary text-xs">{fmtDate(inv.iniciadoEm)}</td>
                    <td className="px-4 py-3 text-text-secondary text-xs">{fmtDate(inv.finalizadoEm)}</td>
                    <td className="px-4 py-3 text-text-secondary text-xs">{inv.criadoPor?.nome ?? '—'}</td>
                  </tr>
                ))}
                {filtered.length === 0 && (
                  <tr>
                    <td colSpan={7} className="px-4 py-8 text-center text-text-secondary text-sm">
                      Nenhum inventário encontrado.
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
