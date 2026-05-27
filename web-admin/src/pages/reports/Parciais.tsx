import React, { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { apiClient } from '../../api/client'
import { Spinner } from '../../components/ui/Spinner'
import { Pagination } from '../../components/ui/Pagination'
import { format } from 'date-fns'
import type { ParcialRow } from '../../demo/demoData'

interface ParciaisData {
  periodo: { de: string; ate: string }
  resumo: {
    totalCaixasParciais: number
    totalItensEmFalta: number
  }
  content: ParcialRow[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export default function Parciais() {
  const today = format(new Date(), 'yyyy-MM-dd')
  const [de, setDe] = useState(today)
  const [ate, setAte] = useState(today)
  const [page, setPage] = useState(0)

  const { data, isLoading } = useQuery<ParciaisData>({
    queryKey: ['relatorio-parciais', de, ate, page],
    queryFn: () =>
      apiClient.get('/v1/admin/relatorios/parciais', {
        params: { de, ate, page, size: 20 },
      }).then(r => r.data),
  })

  const resumo = data?.resumo
  const fmtDate = (d: string) => { try { return format(new Date(d), 'dd/MM HH:mm') } catch { return '—' } }

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
      </div>

      {isLoading ? <Spinner /> : (
        <>
          {resumo && (
            <div className="grid grid-cols-2 gap-4">
              <div className="bg-white border border-divider p-4 flex flex-col gap-1">
                <div className="text-xs text-text-secondary">Caixas parciais no período</div>
                <div className="text-2xl font-bold" style={{ color: '#F9A825' }}>{resumo.totalCaixasParciais}</div>
              </div>
              <div className="bg-white border border-divider p-4 flex flex-col gap-1">
                <div className="text-xs text-text-secondary">Total de itens em falta</div>
                <div className="text-2xl font-bold" style={{ color: '#C62828' }}>{resumo.totalItensEmFalta}</div>
              </div>
            </div>
          )}

          <div className="bg-white border border-divider">
            <div className="px-4 py-3 border-b border-divider font-bold text-sm">Caixas Finalizadas Parcialmente</div>
            <div className="overflow-x-auto">
              <table>
                <thead>
                  <tr className="border-b border-divider bg-surface text-xs text-text-secondary text-left">
                    <th className="px-4 py-2 font-medium">Papeleta</th>
                    <th className="px-4 py-2 font-medium">OP</th>
                    <th className="px-4 py-2 font-medium">Cliente</th>
                    <th className="px-4 py-2 font-medium">Abertura</th>
                    <th className="px-4 py-2 font-medium">Fechamento</th>
                    <th className="px-4 py-2 font-medium text-center">Itens</th>
                    <th className="px-4 py-2 font-medium text-center">Completos</th>
                    <th className="px-4 py-2 font-medium text-center">Em falta</th>
                  </tr>
                </thead>
                <tbody>
                  {(data?.content ?? []).map(p => (
                    <tr key={p.id} className="border-b border-divider hover:bg-surface text-sm">
                      <td className="px-4 py-3 font-mono font-bold text-primary">{p.codigoPapeleta}</td>
                      <td className="px-4 py-3 text-text-secondary">{p.numeroOp}</td>
                      <td className="px-4 py-3">{p.clienteNome}</td>
                      <td className="px-4 py-3 text-text-secondary">{fmtDate(p.abertaEm)}</td>
                      <td className="px-4 py-3 text-text-secondary">{fmtDate(p.finalizadaEm)}</td>
                      <td className="px-4 py-3 text-center">{p.totalItens}</td>
                      <td className="px-4 py-3 text-center font-bold text-success">{p.itensCompletos}</td>
                      <td className="px-4 py-3 text-center font-bold text-warning">{p.itensEmFalta}</td>
                    </tr>
                  ))}
                  {(data?.content ?? []).length === 0 && (
                    <tr><td colSpan={8} className="px-4 py-8 text-center text-text-secondary text-sm">Nenhuma caixa parcial encontrada.</td></tr>
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
