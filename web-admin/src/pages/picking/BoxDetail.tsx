import React from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { apiClient } from '../../api/client'
import { Badge, statusToBadge } from '../../components/ui/Badge'
import { Spinner } from '../../components/ui/Spinner'
import type { Caixa } from '../../types'
import { format } from 'date-fns'

export default function BoxDetail() {
  const { papeleta } = useParams<{ papeleta: string }>()
  const navigate = useNavigate()

  const { data: caixa, isLoading } = useQuery<Caixa>({
    queryKey: ['caixa', papeleta],
    queryFn: () => apiClient.get(`/v1/picking/caixas/${papeleta}`).then(r => r.data),
  })

  const fmtDate = (d: string | null) => {
    if (!d) return '—'
    try { return format(new Date(d), 'dd/MM/yyyy HH:mm') } catch { return d }
  }

  if (isLoading) return <Spinner />
  if (!caixa) return <p className="text-text-secondary">Caixa não encontrada.</p>

  return (
    <div className="flex flex-col gap-4">
      <button onClick={() => navigate(-1)} className="text-sm text-primary hover:underline w-fit">
        ← Voltar
      </button>

      <div className="bg-white p-4 border border-divider">
        <div className="flex items-center gap-3 mb-3">
          <h2 className="text-xl font-bold">{caixa.codigoPapeleta}</h2>
          <Badge label={caixa.status} variant={statusToBadge(caixa.status)} />
        </div>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
          <div>
            <div className="text-text-secondary text-xs">OP</div>
            <div className="font-medium">{caixa.numeroOp || '—'}</div>
          </div>
          <div>
            <div className="text-text-secondary text-xs">Cliente</div>
            <div className="font-medium">{caixa.clienteNome || '—'}</div>
          </div>
          <div>
            <div className="text-text-secondary text-xs">Abertura</div>
            <div className="font-medium">{fmtDate(caixa.abertaEm)}</div>
          </div>
          <div>
            <div className="text-text-secondary text-xs">Finalização</div>
            <div className="font-medium">{fmtDate(caixa.finalizadaEm)}</div>
          </div>
        </div>
      </div>

      {caixa.itens && (
        <div className="bg-white border border-divider">
          <div className="px-4 py-3 border-b border-divider font-bold text-sm">
            Itens da Caixa
          </div>
          <table>
            <thead>
              <tr className="border-b border-divider bg-surface text-xs text-text-secondary text-left">
                <th className="px-4 py-2 font-medium">Ordem</th>
                <th className="px-4 py-2 font-medium">Referência</th>
                <th className="px-4 py-2 font-medium">Cor</th>
                <th className="px-4 py-2 font-medium">Tam</th>
                <th className="px-4 py-2 font-medium">Endereço</th>
                <th className="px-4 py-2 font-medium">Solicitado</th>
                <th className="px-4 py-2 font-medium">Coletado</th>
                <th className="px-4 py-2 font-medium">Status</th>
              </tr>
            </thead>
            <tbody>
              {caixa.itens.map(item => (
                <tr key={item.id} className="border-b border-divider text-sm">
                  <td className="px-4 py-2.5 text-text-secondary">{item.ordemPicking}</td>
                  <td className="px-4 py-2.5 font-mono font-bold">{item.skuReferencia}</td>
                  <td className="px-4 py-2.5">{item.skuCor}</td>
                  <td className="px-4 py-2.5">{item.skuTamanho}</td>
                  <td className="px-4 py-2.5 font-mono text-primary">{item.enderecoCodigo || '—'}</td>
                  <td className="px-4 py-2.5 text-center">{item.qtdeSolicitada}</td>
                  <td className="px-4 py-2.5 text-center font-bold">{item.qtdeColetada}</td>
                  <td className="px-4 py-2.5">
                    <Badge label={item.status} variant={statusToBadge(item.status)} />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
