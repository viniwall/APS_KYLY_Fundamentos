export interface User {
  id: number
  nome: string
  codigoCracha: string
  email: string
  perfil: 'ADMIN' | 'GESTOR' | 'SUPERVISOR' | 'OPERADOR'
  ativo: boolean
}

export interface Filial {
  id: number
  codigo: string
  nome: string
  endereco: string
  ativo: boolean
}

export interface Caixa {
  id: number
  codigoPapeleta: string
  numeroOp: string
  clienteNome: string
  status: 'AGUARDANDO' | 'EM_PICKING' | 'PARCIAL' | 'FINALIZADA' | 'CANCELADA'
  corTarja: string
  sequencia: number
  totalCaixasPedido: number
  abertaEm: string | null
  finalizadaEm: string | null
  itens?: ItemCaixa[]
}

export interface ItemCaixa {
  id: number
  skuReferencia: string
  skuCor: string
  skuTamanho: string
  skuDescricao: string
  enderecoCodigo: string
  qtdeSolicitada: number
  qtdeColetada: number
  status: string
  ordemPicking: number
}

export interface EventoPicking {
  id: number
  tipo: string
  mensagem: string
  ocorridoEm: string
}

export interface Sku {
  id: number
  referencia: string
  cor: string
  tamanho: string
  descricao: string
  codigoEan: string
  ativo: boolean
}

export interface Bem {
  id: number
  codigoPatrimonio: string
  descricao: string
  marca: string
  modelo: string
  serial: string
  situacao: 'ATIVO' | 'EM_MANUTENCAO' | 'BAIXADO' | 'NAO_LOCALIZADO'
  localizacaoAtual?: { id: number; codigo: string; nome: string }
  fotoUrl?: string
  observacao?: string
  atualizadoEm: string
}

export interface Inventario {
  id: number
  descricao: string
  status: 'ABERTO' | 'EM_ANDAMENTO' | 'FECHADO'
  iniciadoEm: string
  finalizadoEm?: string
  filial: Filial
}

export interface DashboardData {
  caixasFinalizadasHoje: number
  emPickingAgora: number
  caixasParciais: number
  operadoresAtivos: number
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export interface LoginWebRequest {
  email: string
  senha: string
}

export interface LoginWebResponse {
  token: string
  expiresIn: number
  usuario: User
}
