import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login(email, senha)
      navigate('/dashboard')
    } catch {
      setError('Credenciais inválidas. Verifique e tente novamente.')
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
            <label className="block text-sm text-text-secondary mb-1">Código / E-mail</label>
            <input
              type="text"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full h-12 px-3 border border-divider text-sm focus:outline-none focus:border-primary"
              placeholder="admin@kollectaops.com.br"
              autoFocus
            />
          </div>

          <div>
            <label className="block text-sm text-text-secondary mb-1">Senha</label>
            <input
              type="password"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              className="w-full h-12 px-3 border border-divider text-sm focus:outline-none focus:border-primary"
              placeholder="••••••••"
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

        <p className="text-xs text-text-secondary mt-6 text-center">
          Homologação: ADMIN01 / Kolecta@2024
        </p>
      </div>
    </div>
  )
}
