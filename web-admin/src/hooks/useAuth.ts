import { useState } from 'react'
import { apiClient } from '../api/client'

interface AuthUser {
  id: number
  nome: string
  codigoCracha: string
  perfil: string
}

export function useAuth() {
  const [user, setUser] = useState<AuthUser | null>(() => {
    const stored = localStorage.getItem('user')
    return stored ? JSON.parse(stored) : null
  })

  const token = localStorage.getItem('token')
  const isAuthenticated = !!token && !!user

  const login = async (email: string, senha: string) => {
    const response = await apiClient.post('/v1/auth/login', {
      codigoCrachaSupervisor: email,
      codigoCrachaOperador: email,
      coletorSerial: 'WEB-ADMIN',
    })
    const { token: newToken, usuario } = response.data
    localStorage.setItem('token', newToken)
    localStorage.setItem('user', JSON.stringify(usuario))
    setUser(usuario)
    return usuario
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser(null)
  }

  return { user, token, isAuthenticated, login, logout }
}
