import React, { createContext, useContext, useState, useEffect, useCallback } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import { demoInterceptor, installDemoInterceptor } from './interceptor'

interface DemoCtx {
  isDemoMode: boolean
  toggleDemo: () => void
}

const Ctx = createContext<DemoCtx>({ isDemoMode: false, toggleDemo: () => {} })

export function DemoProvider({ children }: { children: React.ReactNode }) {
  const qc = useQueryClient()
  const [isDemoMode, setIsDemoMode] = useState<boolean>(() => {
    return localStorage.getItem('demoMode') === 'true'
  })

  useEffect(() => {
    installDemoInterceptor()
    // Sync interceptor flag on mount (handles page refresh with demo already on)
    demoInterceptor.active = isDemoMode
  }, []) // eslint-disable-line react-hooks/exhaustive-deps

  const toggleDemo = useCallback(() => {
    setIsDemoMode(prev => {
      const next = !prev
      demoInterceptor.active = next
      localStorage.setItem('demoMode', String(next))
      // Force all queries to re-fetch with the new mode
      qc.invalidateQueries()
      return next
    })
  }, [qc])

  return <Ctx.Provider value={{ isDemoMode, toggleDemo }}>{children}</Ctx.Provider>
}

export function useDemoMode() {
  return useContext(Ctx)
}
