import React from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './hooks/useAuth'
import { Layout } from './components/layout/Layout'
import { DemoProvider } from './demo/DemoContext'
import { DemoToggle } from './components/ui/DemoToggle'

import Login from './pages/Login'
import Dashboard from './pages/Dashboard'

import BoxList from './pages/picking/BoxList'
import BoxDetail from './pages/picking/BoxDetail'

import AssetList from './pages/inventory/AssetList'
import InventarioList from './pages/inventory/InventarioList'

import Productivity from './pages/reports/Productivity'
import Divergencias from './pages/reports/Divergencias'
import Parciais from './pages/reports/Parciais'

import SkuList from './pages/admin/SkuList'
import EnderecoList from './pages/admin/EnderecoList'
import UsuarioList from './pages/admin/UsuarioList'
import FilialList from './pages/admin/FilialList'

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuth()
  if (!isAuthenticated) return <Navigate to="/login" replace />
  return <>{children}</>
}

export default function App() {
  return (
    <DemoProvider>
      <DemoToggle />
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <Layout />
              </ProtectedRoute>
            }
          >
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<Dashboard />} />

            <Route path="picking/caixas" element={<BoxList />} />
            <Route path="picking/caixas/:papeleta" element={<BoxDetail />} />

            <Route path="inventario/bens" element={<AssetList />} />
            <Route path="inventario/inventarios" element={<InventarioList />} />

            <Route path="relatorios/produtividade" element={<Productivity />} />
            <Route path="relatorios/divergencias" element={<Divergencias />} />
            <Route path="relatorios/parciais" element={<Parciais />} />

            <Route path="admin/skus" element={<SkuList />} />
            <Route path="admin/enderecos" element={<EnderecoList />} />
            <Route path="admin/usuarios" element={<UsuarioList />} />
            <Route path="admin/filiais" element={<FilialList />} />

            <Route path="integracoes" element={<Dashboard />} />
          </Route>
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </DemoProvider>
  )
}
