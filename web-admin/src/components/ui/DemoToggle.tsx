import React from 'react'
import { useDemoMode } from '../../demo/DemoContext'

export function DemoToggle() {
  const { isDemoMode, toggleDemo } = useDemoMode()

  return (
    <button
      onClick={toggleDemo}
      title={isDemoMode ? 'Desativar modo demo' : 'Ativar modo demo'}
      className={[
        'fixed bottom-6 right-6 z-50',
        'flex items-center gap-2 px-4 py-2.5',
        'rounded-full shadow-lg border text-sm font-semibold',
        'transition-all duration-200 select-none',
        isDemoMode
          ? 'bg-amber-400 border-amber-500 text-amber-900 hover:bg-amber-300'
          : 'bg-white border-gray-300 text-gray-600 hover:bg-gray-50',
      ].join(' ')}
    >
      <span className="text-base">{isDemoMode ? '🔬' : '🔬'}</span>
      {isDemoMode ? 'Demo ON' : 'Demo OFF'}
      <span
        className={[
          'w-2.5 h-2.5 rounded-full',
          isDemoMode ? 'bg-amber-700 animate-pulse' : 'bg-gray-400',
        ].join(' ')}
      />
    </button>
  )
}
