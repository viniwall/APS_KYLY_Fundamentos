import React from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'

interface HeaderProps {
  title: string
}

export function Header({ title }: HeaderProps) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <header className="h-14 bg-white border-b border-divider flex items-center justify-between px-6 shrink-0">
      <h1 className="text-lg font-bold text-text-primary">{title}</h1>
      <div className="flex items-center gap-4">
        {user && (
          <span className="text-sm text-text-secondary">{user.nome}</span>
        )}
        <button
          onClick={handleLogout}
          className="text-sm text-primary hover:underline"
        >
          Sair
        </button>
      </div>
    </header>
  )
}
