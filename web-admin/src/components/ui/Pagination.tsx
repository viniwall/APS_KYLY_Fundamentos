import React from 'react'

interface PaginationProps {
  page: number
  totalPages: number
  onPageChange: (page: number) => void
}

export function Pagination({ page, totalPages, onPageChange }: PaginationProps) {
  if (totalPages <= 1) return null

  return (
    <div className="flex items-center gap-2 justify-end pt-4">
      <button
        disabled={page === 0}
        onClick={() => onPageChange(page - 1)}
        className="px-3 py-1.5 text-sm border border-divider disabled:opacity-40 hover:bg-surface"
      >
        Anterior
      </button>
      <span className="text-sm text-text-secondary">
        {page + 1} / {totalPages}
      </span>
      <button
        disabled={page >= totalPages - 1}
        onClick={() => onPageChange(page + 1)}
        className="px-3 py-1.5 text-sm border border-divider disabled:opacity-40 hover:bg-surface"
      >
        Próxima
      </button>
    </div>
  )
}
