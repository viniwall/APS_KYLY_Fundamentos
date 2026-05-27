import type { InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { apiClient } from '../api/client'
import {
  getDemoDashboard, getDemoCaixas, getDemoCaixasRecentes, getDemoBoxDetail,
  getDemoBens, getDemoInventarios, getDemoProdutividade, getDemoDivergencias, getDemoParciais,
  getDemoSkus, getDemoEnderecos, getDemoUsuarios, getDemoFiliais,
} from './demoData'

let _active = false
export const demoInterceptor = {
  get active() { return _active },
  set active(v: boolean) { _active = v },
}

function mergeParams(config: InternalAxiosRequestConfig): Record<string, string> {
  const result: Record<string, string> = {}
  const qs = (config.url ?? '').split('?')[1]
  if (qs) new URLSearchParams(qs).forEach((v, k) => { result[k] = v })
  if (config.params && typeof config.params === 'object') {
    for (const [k, v] of Object.entries(config.params)) {
      if (v !== undefined && v !== null) result[k] = String(v)
    }
  }
  return result
}

function basePath(config: InternalAxiosRequestConfig): string {
  return (config.url ?? '').split('?')[0]
}

function fake(config: InternalAxiosRequestConfig, data: unknown): AxiosResponse {
  return { data, status: 200, statusText: 'OK', headers: {}, config } as AxiosResponse
}

function resolve(config: InternalAxiosRequestConfig): AxiosResponse {
  const path = basePath(config)
  const params = mergeParams(config)

  // Dashboard
  if (path === '/v1/admin/dashboard')
    return fake(config, getDemoDashboard())

  // Picking — caixas
  if (path === '/v1/picking/caixas') {
    if (params.status === 'FINALIZADA' && !params.page)
      return fake(config, getDemoCaixasRecentes())
    return fake(config, getDemoCaixas(params))
  }
  const boxMatch = path.match(/^\/v1\/picking\/caixas\/(.+)$/)
  if (boxMatch)
    return fake(config, getDemoBoxDetail(boxMatch[1]))

  // Inventário
  if (path.startsWith('/v1/inventario/inventarios'))
    return fake(config, getDemoInventarios(params))
  if (path.startsWith('/v1/inventario/bens'))
    return fake(config, getDemoBens(params))

  // Relatórios
  if (path === '/v1/admin/relatorios/produtividade')
    return fake(config, getDemoProdutividade(params.de ?? '', params.ate ?? ''))
  if (path === '/v1/admin/relatorios/divergencias')
    return fake(config, getDemoDivergencias(params))
  if (path === '/v1/admin/relatorios/parciais')
    return fake(config, getDemoParciais(params))

  // Cadastros
  if (path === '/v1/admin/skus')
    return fake(config, getDemoSkus(params))
  if (path === '/v1/admin/enderecos')
    return fake(config, getDemoEnderecos(params))
  if (path === '/v1/admin/usuarios')
    return fake(config, getDemoUsuarios(params))
  if (path === '/v1/admin/filiais')
    return fake(config, getDemoFiliais(params))

  return fake(config, { content: [], totalElements: 0, totalPages: 1, number: 0, size: 20 })
}

let _interceptorId: number | null = null

export function installDemoInterceptor() {
  if (_interceptorId !== null) return
  _interceptorId = apiClient.interceptors.request.use((config) => {
    if (!_active) return config
    config.adapter = (cfg: InternalAxiosRequestConfig) => Promise.resolve(resolve(cfg))
    return config
  })
}
