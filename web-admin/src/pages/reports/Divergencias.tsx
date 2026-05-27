import React, { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { apiClient } from '../../api/client'
import { Spinner } from '../../components/ui/Spinner'
import { Badge } from '../../components/ui/Badge'
import { Pagination } from '../../components/ui/Pagination'
import { format } from 'date-fns'
import type { DivergenciaRow } from '../../demo/demoData'

interface DivergenciasData {
  periodo: { de: string; ate: string }
  resumo: {
    totalDivergencias: number
    totalItensEmFalta: number
    totalDivergentes: number
    caixasAfetadas: number
  }
  content: DivergenciaRow[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export default function Divergencias() {
  const today = format(new Date(), 'yyyy-MM-dd')
  const [de, setDe] = useState(today)
  const [ate, setAte] = useState(today)
  const [page, setPage] = useState(0)
  const [q, setQ] = useState('')

  const { data, isLoading } = useQuery<DivergenciasData>({
    queryKey: ['relatorio-divergencias', de, ate, page, q],
    queryFn: () =>
      apiClient.get('/v1/admin/relatorios/divergencias', {
        params: { de, ate, page, size: 20, q: q || undefined },
      }).then(r => r.data),
  })

  const resumo = data?.resumo
  const fmtDate = (d: string) => { try { return format(new Date(d), 'dd/MM HH:mm') } catch { return d } }

  return (
    <div className="flex flex-col gap-4">
      <div className="bg-white p-3 border border-divider flex gap-4 items-end flex-wrap">
        <div>
          <label className="block text-xs text-text-secondary mb-1">De</label>
          <input type="date" value={de} onChange={e => { setDe(e.target.value); setPage(0) }}
            className="h-9 px-3 border border-divider text-sm focus:outline-none" />
        </div>
        <div>
          <label className="block text-xs text-text-secondary mb-1">Até</label>
          <input type="date" value={ate} onChange={e => { setAte(e.target.value); setPage(0) }}
            className="h-9 px-3 border border-divider text-sm focus:outline-none" />
        </div>
        <input
          type="text"
          placeholder="Papeleta, cliente ou referência..."
          value={q}
          onChange={e => { setQ(e.target.value); setPage(0) }}
          className="h-9 px-3 border border-divider text-sm focus:outline-none w-64"
        />
      </div>

      {isLoading ? <Spinner /> : (
        <>
          {resumo && (
            <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
              {[
                { label: 'Total divergências',  value: resumo.totalDivergencias, color: '#C62828' },
                { label: 'Itens em falta',       value: resumo.totalItensEmFalta, color: '#E65100' },
                { label: 'Itens divergentes',    value: resumo.totalDivergentes,  color: '#F9A825' },
                { label: 'Caixas afetadas',      value: resumo.caixasAfetadas,    color: '#1B3A57' },
              ].map(({ label, value, color }) => (
                <div key={label} className="bg-white border border-divider p-4 flex flex-col gap-1">
                  <div className="text-xs text-text-secondary">{label}</div>
                  <div className="text-2xl font-bold" style={{ color }}>{value}</div>
                </div>
              ))}
            </div>
          )}

          <div className="bg-white border border-divider">
            <div className="px-4 py-3 border-b border-divider font-bold text-sm">Itens com Divergência</div>
            <div className="overflow-x-auto">
              <table>
                <thead>
                  <tr className="border-b border-divider bg-surface text-xs text-text-secondary text-left">
                    <th className="px-4 py-2 font-medium">Papeleta</th>
                    <th className="px-4 py-2 font-medium">Cliente</th>
                    <th className="px-4 py-2 font-medium">Referência</th>
                    <th className="px-4 py-2 font-medium">Descrição</th>
                    <th className="px-4 py-2 font-medium">Cor / Tam</th>
                    <th className="px-4 py-2 font-medium">Endereço</th>
                    <th className="px-4 py-2 font-medium text-center">Solicitado</th>
                    <th className="px-4 py-2 font-medium text-center">Coletado</th>
                    <th className="px-4 py-2 font-medium">Tipo</th>
                    <th className="px-4 py-2 font-medium">Registrado</th>
                  </tr>
                </thead>
                <tbody>
                  {(data?.content ?? []).map(d => (
                    <tr key={d.id} className="border-b border-divider hover:bg-surface text-sm">
                      <td className="px-4 py-3 font-mono font-bold text-primary">{d.caixaPapeleta}</td>
                      <td className="px-4 py-3 text-text-secondary">{d.clienteNome}</td>
                      <td className="px-4 py-3 font-mono font-bold">{d.skuReferencia}</td>
                      <td className="px-4 py-3">{d.skuDescricao}</td>
                      <td className="px-4 py-3 text-text-secondary">{d.skuCor} / {d.skuTamanho}</td>
                      <td className="px-4 py-3 font-mono text-xs text-primary">{d.enderecoCodigo}</td>
                      <td className="px-4 py-3 text-center">{d.qtdeSolicitada}</td>
                      <td className="px-4 py-3 text-center font-bold text-error">{d.qtdeColetada}</td>
                      <td className="px-4 py-3">
                        <Badge label={d.status} variant={d.status === 'EM_FALTA' ? 'warning' : 'error'} />
                      </td>
                      <td className="px-4 py-3 text-text-secondary text-xs">{fmtDate(d.registradoEm)}</td>
                    </tr>
                  ))}
                  {(data?.content ?? []).length === 0 && (
                    <tr><td colSpan={10} className="px-4 py-8 text-center text-text-secondary text-sm">Nenhuma divergência encontrada.</td></tr>
                  )}
                </tbody>
              </table>
            </div>
            <div className="px-4">
              <Pagination page={page} totalPages={data?.totalPages ?? 0} onPageChange={setPage} />
            </div>
          </div>
        </>
      )}
    </div>
  )
}
