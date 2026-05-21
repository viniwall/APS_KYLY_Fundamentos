import React from 'react'

type Variant = 'success' | 'error' | 'warning' | 'info' | 'default'

const variants: Record<Variant, string> = {
  success: 'bg-green-100 text-success',
  error: 'bg-red-100 text-error',
  warning: 'bg-yellow-100 text-warning',
  info: 'bg-blue-100 text-info',
  default: 'bg-gray-100 text-text-secondary',
}

export function statusToBadge(status: string): Variant {
  switch (status) {
    case 'FINALIZADA': case 'ATIVO': return 'success'
    case 'CANCELADA': case 'BAIXADO': case 'NAO_LOCALIZADO': return 'error'
    case 'PARCIAL': case 'EM_FALTA': case 'EM_MANUTENCAO': return 'warning'
    case 'EM_PICKING': case 'EM_ANDAMENTO': return 'info'
    default: return 'default'
  }
}

interface BadgeProps {
  label: string
  variant?: Variant
}

export function Badge({ label, variant = 'default' }: BadgeProps) {
  return (
    <span className={`inline-block px-2 py-0.5 text-xs font-medium rounded ${variants[variant]}`}>
      {label}
    </span>
  )
}
