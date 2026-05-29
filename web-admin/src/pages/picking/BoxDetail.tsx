import React from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { apiClient } from '../../api/client'
import { Badge, statusToBadge } from '../../components/ui/Badge'
import { Spinner } from '../../components/ui/Spinner'
import type { Caixa } from '../../types'
import { format } from 'date-fns'

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

// Formata endereço "C37.09.6B" em partes coloridas
function EnderecoFormatado({ codigo }: { codigo: string }) {
  const parts = codigo.split('.')
  if (parts.length !== 3) return <span className="font-mono text-primary">{codigo}</span>
  return (
    <span className="font-mono font-bold text-base tracking-wide">
      <span style={{ color: '#1B3A57' }}>{parts[0]}</span>
      <span className="text-text-secondary">.</span>
      <span style={{ color: '#E0A800' }}>{parts[1]}</span>
      <span className="text-text-secondary">.</span>
      <span style={{ color: '#2E7D32' }}>{parts[2]}</span>
    </span>
  )
}

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
        <div className="flex items-center gap-3 mb-3 flex-wrap">
          <h2 className="text-xl font-bold font-mono">{caixa.codigoPapeleta}</h2>
          <Badge label={caixa.status} variant={statusToBadge(caixa.status)} />
          <TarjaBadge corTarja={caixa.corTarja} />
          {caixa.sequencia > 0 && caixa.totalCaixasPedido > 0 && (
            <span className="text-xs text-text-secondary font-medium">
              Caixa {caixa.sequencia} de {caixa.totalCaixasPedido}
            </span>
          )}
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
                  <td className="px-4 py-2.5">
                    {item.enderecoCodigo
                      ? <EnderecoFormatado codigo={item.enderecoCodigo} />
                      : <span className="text-text-secondary">—</span>}
                  </td>
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
