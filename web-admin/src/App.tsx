import React from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './hooks/useAuth'
import { Layout } from './components/layout/Layout'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import BoxList from './pages/picking/BoxList'
import BoxDetail from './pages/picking/BoxDetail'
import AssetList from './pages/inventory/AssetList'
import Productivity from './pages/reports/Productivity'

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuth()
  if (!isAuthenticated) return <Navigate to="/login" replace />
  return <>{children}</>
}

export default function App() {
  return (
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
          <Route path="inventario/inventarios" element={<AssetList />} />
          <Route path="relatorios/produtividade" element={<Productivity />} />
          <Route path="relatorios/divergencias" element={<Productivity />} />
          <Route path="relatorios/parciais" element={<Productivity />} />
          <Route path="admin/skus" element={<AssetList />} />
          <Route path="admin/enderecos" element={<AssetList />} />
          <Route path="admin/usuarios" element={<AssetList />} />
          <Route path="admin/filiais" element={<AssetList />} />
          <Route path="integracoes" element={<Dashboard />} />
        </Route>
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
