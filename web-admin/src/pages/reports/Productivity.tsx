import React, { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { apiClient } from '../../api/client'
import { Spinner } from '../../components/ui/Spinner'
import { format } from 'date-fns'

export default function Productivity() {
  const today = format(new Date(), 'yyyy-MM-dd')
  const [de, setDe] = useState(today)
  const [ate, setAte] = useState(today)

  const { data, isLoading } = useQuery({
    queryKey: ['relatorio-produtividade', de, ate],
    queryFn: () =>
      apiClient.get('/v1/admin/relatorios/produtividade', { params: { de, ate } }).then(r => r.data),
  })

  return (
    <div className="flex flex-col gap-4">
      <div className="bg-white p-3 border border-divider flex gap-4 items-center flex-wrap">
        <div>
          <label className="block text-xs text-text-secondary mb-1">De</label>
          <input
            type="date"
            value={de}
            onChange={e => setDe(e.target.value)}
            className="h-9 px-3 border border-divider text-sm focus:outline-none"
          />
        </div>
        <div>
          <label className="block text-xs text-text-secondary mb-1">Até</label>
          <input
            type="date"
            value={ate}
            onChange={e => setAte(e.target.value)}
            className="h-9 px-3 border border-divider text-sm focus:outline-none"
          />
        </div>
      </div>

      <div className="bg-white border border-divider p-6">
        {isLoading ? (
          <Spinner />
        ) : (
          <div className="text-text-secondary text-sm">
            <pre className="text-xs">{JSON.stringify(data, null, 2)}</pre>
            <p className="mt-4 text-xs text-text-secondary italic">
              Relatório completo disponível na versão 1.1 (requer dados de sessão do coletor).
            </p>
          </div>
        )}
      </div>
    </div>
  )
}
