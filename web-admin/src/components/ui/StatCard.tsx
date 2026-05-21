import React from 'react'

interface StatCardProps {
  label: string
  value: number | string
  color?: string
}

export function StatCard({ label, value, color = '#1B3A57' }: StatCardProps) {
  return (
    <div
      className="bg-white border-l-4 p-4 flex flex-col gap-1 min-w-0"
      style={{ borderLeftColor: color }}
    >
      <div className="text-4xl font-bold" style={{ color }}>
        {value}
      </div>
      <div className="text-sm text-text-secondary">{label}</div>
    </div>
  )
}
