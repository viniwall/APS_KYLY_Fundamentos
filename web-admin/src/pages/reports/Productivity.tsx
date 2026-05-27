import React, { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { apiClient } from '../../api/client'
import { Spinner } from '../../components/ui/Spinner'
import { format } from 'date-fns'

interface Resumo {
  totalCaixasFinalizadas: number
  totalItensColetados: number
  mediaMinutosPorCaixa: number
  taxaDivergencia: string
}

interface OperadorRow {
  nome: string
  cracha: string
  caixas: number
  itens: number
  mediaMin: number
}

interface ProdutividadeData {
  periodo?: { de: string; ate: string }
  resumo?: Resumo
  porOperador?: OperadorRow[]
}

export default function Productivity() {
  const today = format(new Date(), 'yyyy-MM-dd')
  const [de, setDe] = useState(today)
  const [ate, setAte] = useState(today)

  const { data, isLoading } = useQuery<ProdutividadeData>({
    queryKey: ['relatorio-produtividade', de, ate],
    queryFn: () =>
      apiClient.get('/v1/admin/relatorios/produtividade', { params: { de, ate } }).then(r => r.data),
  })

  const resumo = data?.resumo
  const operadores = data?.porOperador ?? []

  return (
    <div className="flex flex-col gap-4">
      <div className="bg-white p-3 border border-divider flex gap-4 items-end flex-wrap">
        <div>
          <label className="block text-xs text-text-secondary mb-1">De</label>
          <input type="date" value={de} onChange={e => setDe(e.target.value)}
            className="h-9 px-3 border border-divider text-sm focus:outline-none" />
        </div>
        <div>
          <label className="block text-xs text-text-secondary mb-1">Até</label>
          <input type="date" value={ate} onChange={e => setAte(e.target.value)}
            className="h-9 px-3 border border-divider text-sm focus:outline-none" />
        </div>
      </div>

      {isLoading ? <Spinner /> : (
        <>
          {resumo && (
            <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
              {[
                { label: 'Caixas finalizadas', value: resumo.totalCaixasFinalizadas, color: '#2E7D32' },
                { label: 'Itens coletados',    value: resumo.totalItensColetados,    color: '#1976D2' },
                { label: 'Média por caixa',    value: `${resumo.mediaMinutosPorCaixa} min`, color: '#F9A825' },
                { label: 'Taxa de divergência',value: resumo.taxaDivergencia,        color: '#C62828' },
              ].map(({ label, value, color }) => (
                <div key={label} className="bg-white border border-divider p-4 flex flex-col gap-1">
                  <div className="text-xs text-text-secondary">{label}</div>
                  <div className="text-2xl font-bold" style={{ color }}>{value}</div>
                </div>
              ))}
            </div>
          )}

          {operadores.length > 0 && (
            <div className="bg-white border border-divider">
              <div className="px-4 py-3 border-b border-divider font-bold text-sm">
                Produtividade por Operador
              </div>
              <table>
                <thead>
                  <tr className="border-b border-divider bg-surface text-xs text-text-secondary text-left">
                    <th className="px-4 py-2 font-medium">Operador</th>
                    <th className="px-4 py-2 font-medium">Crachá</th>
                    <th className="px-4 py-2 font-medium text-center">Caixas</th>
                    <th className="px-4 py-2 font-medium text-center">Itens</th>
                    <th className="px-4 py-2 font-medium text-center">Média (min)</th>
                  </tr>
                </thead>
                <tbody>
                  {operadores.map(op => (
                    <tr key={op.cracha} className="border-b border-divider text-sm">
                      <td className="px-4 py-3 font-medium">{op.nome}</td>
                      <td className="px-4 py-3 font-mono text-text-secondary">{op.cracha}</td>
                      <td className="px-4 py-3 text-center">{op.caixas}</td>
                      <td className="px-4 py-3 text-center">{op.itens}</td>
                      <td className="px-4 py-3 text-center">{op.mediaMin}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {!resumo && operadores.length === 0 && (
            <div className="bg-white border border-divider p-6 text-center text-text-secondary text-sm">
              Relatório completo disponível na versão 1.1 (requer dados de sessão do coletor).
            </div>
          )}
        </>
      )}
    </div>
  )
}
