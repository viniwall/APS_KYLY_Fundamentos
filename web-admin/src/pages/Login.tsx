import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [supervisor, setSupervisor] = useState('')
  const [operador, setOperador] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login(supervisor, operador)
      navigate('/dashboard')
    } catch {
      setError('Credenciais inválidas. Verifique os códigos e tente novamente.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-primary flex items-center justify-center">
      <div className="bg-white p-8 w-full max-w-sm">
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-primary">KollectaOps</h1>
          <p className="text-text-secondary text-sm mt-1">Painel Administrativo</p>
        </div>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <div>
            <label className="block text-sm text-text-secondary mb-1">Código do Crachá — Supervisor</label>
            <input
              type="text"
              value={supervisor}
              onChange={(e) => setSupervisor(e.target.value)}
              className="w-full h-12 px-3 border border-divider text-sm focus:outline-none focus:border-primary"
              placeholder="Ex: ADMIN01"
              autoFocus
            />
          </div>

          <div>
            <label className="block text-sm text-text-secondary mb-1">Código do Crachá — Operador</label>
            <input
              type="text"
              value={operador}
              onChange={(e) => setOperador(e.target.value)}
              className="w-full h-12 px-3 border border-divider text-sm focus:outline-none focus:border-primary"
              placeholder="Ex: OP001"
            />
          </div>

          {error && (
            <p className="text-sm text-error">{error}</p>
          )}

          <button
            type="submit"
            disabled={loading}
            className="h-12 bg-primary text-white font-bold text-sm disabled:opacity-60 hover:bg-primary-dark transition-colors"
          >
            {loading ? 'Entrando...' : 'ENTRAR'}
          </button>
        </form>
      </div>
    </div>
  )
}
