import type { DashboardData, Caixa, ItemCaixa, Bem, Sku, EnderecoEstoque, User, Filial, PageResponse } from '../types'

// ── time helpers ───────────────────────────────────────────────────────────

function ago(h: number, m = 0): string {
  const d = new Date()
  d.setHours(d.getHours() - h, d.getMinutes() - m, 0, 0)
  return d.toISOString()
}

// ── SKUs reais da Kyly (roupa infantil) ────────────────────────────────────

const SKUS = [
  { ref: '1000079', cor: 'BRANCO', tam: 'RN',  desc: 'Body ML Bebê Branco RN',            end: 'A01.01.1A' },
  { ref: '1000079', cor: 'BRANCO', tam: 'P',   desc: 'Body ML Bebê Branco P',             end: 'A01.01.1A' },
  { ref: '1000079', cor: 'BRANCO', tam: 'M',   desc: 'Body ML Bebê Branco M',             end: 'A01.01.1B' },
  { ref: '1000079', cor: 'BRANCO', tam: 'G',   desc: 'Body ML Bebê Branco G',             end: 'A01.01.1B' },
  { ref: '1000080', cor: 'AZUL',   tam: 'P',   desc: 'Conjunto Moletom Bebê Azul P',      end: 'A01.01.2A' },
  { ref: '1000080', cor: 'AZUL',   tam: 'M',   desc: 'Conjunto Moletom Bebê Azul M',      end: 'A01.01.2A' },
  { ref: '1000080', cor: 'CINZA',  tam: 'M',   desc: 'Conjunto Moletom Bebê Cinza M',     end: 'A01.01.2B' },
  { ref: '1000081', cor: 'ROSA',   tam: 'P',   desc: 'Vestido Tricot Rosa P',             end: 'A02.03.3A' },
  { ref: '1000081', cor: 'ROSA',   tam: 'M',   desc: 'Vestido Tricot Rosa M',             end: 'A02.03.3A' },
  { ref: '1000081', cor: 'LILÁS',  tam: 'G',   desc: 'Vestido Tricot Lilás G',            end: 'A02.03.3B' },
  { ref: '1000082', cor: 'ESTAMPADO', tam: '1', desc: 'Pijama Longo Kyly Estampado T1',   end: 'A02.04.4A' },
  { ref: '1000082', cor: 'ESTAMPADO', tam: '2', desc: 'Pijama Longo Kyly Estampado T2',   end: 'A02.04.4A' },
  { ref: '1000082', cor: 'ESTAMPADO', tam: '4', desc: 'Pijama Longo Kyly Estampado T4',   end: 'A02.04.4B' },
  { ref: '1000083', cor: 'VERDE',  tam: 'P',   desc: 'Macacão Plush Verde P',             end: 'B07.05.2A' },
  { ref: '1000083', cor: 'VERDE',  tam: 'M',   desc: 'Macacão Plush Verde M',             end: 'B07.05.2A' },
  { ref: '1000083', cor: 'AMARELO', tam: 'G',  desc: 'Macacão Plush Amarelo G',           end: 'B07.05.2B' },
  { ref: '1000084', cor: 'MARINHO', tam: '2',  desc: 'Blusa de Frio c/ Capuz Marinho T2', end: 'B07.06.3A' },
  { ref: '1000084', cor: 'MARINHO', tam: '4',  desc: 'Blusa de Frio c/ Capuz Marinho T4', end: 'B07.06.3A' },
  { ref: '1000084', cor: 'MARINHO', tam: '6',  desc: 'Blusa de Frio c/ Capuz Marinho T6', end: 'B07.06.3B' },
  { ref: '1000085', cor: 'JEANS',  tam: '4',   desc: 'Calça Jogger Jeans T4',             end: 'C15.08.4A' },
  { ref: '1000085', cor: 'JEANS',  tam: '6',   desc: 'Calça Jogger Jeans T6',             end: 'C15.08.4A' },
  { ref: '1000085', cor: 'JEANS',  tam: '8',   desc: 'Calça Jogger Jeans T8',             end: 'C15.08.4B' },
]

// ── gera itens coerentes com o status da caixa ─────────────────────────────

let _itemIdSeq = 300

function makeItens(papeleta: string, status: Caixa['status']): ItemCaixa[] {
  // Escolhe 3-5 SKUs baseado no código da papeleta (determinístico)
  const seed = parseInt(papeleta.slice(-3), 10) % SKUS.length
  const count = 3 + (parseInt(papeleta.slice(-2), 10) % 3) // 3, 4 ou 5 itens
  const picks = Array.from({ length: count }, (_, i) => SKUS[(seed + i) % SKUS.length])

  return picks.map((sku, i) => {
    const solicitado = [4, 6, 8, 12, 3, 5, 10][( parseInt(papeleta, 10) + i) % 7]
    let coletado = 0
    let itemStatus: string = 'PENDENTE'

    if (status === 'FINALIZADA') {
      coletado = solicitado
      itemStatus = 'COMPLETO'
    } else if (status === 'EM_PICKING') {
      if (i === 0) { coletado = solicitado; itemStatus = 'COMPLETO' }
      else if (i === 1) { coletado = Math.floor(solicitado / 2); itemStatus = 'EM_COLETA' }
      else { coletado = 0; itemStatus = 'PENDENTE' }
    } else if (status === 'PARCIAL') {
      if (i === 0) { coletado = solicitado; itemStatus = 'COMPLETO' }
      else if (i === 1) { coletado = solicitado; itemStatus = 'COMPLETO' }
      else { coletado = 0; itemStatus = 'EM_FALTA' }
    } else {
      // AGUARDANDO / CANCELADA
      coletado = 0
      itemStatus = 'PENDENTE'
    }

    return {
      id: ++_itemIdSeq,
      skuReferencia: sku.ref,
      skuCor: sku.cor,
      skuTamanho: sku.tam,
      skuDescricao: sku.desc,
      enderecoCodigo: sku.end,
      qtdeSolicitada: solicitado,
      qtdeColetada: coletado,
      status: itemStatus,
      ordemPicking: i + 1,
    } satisfies ItemCaixa
  })
}

// ── caixas (20 registros — mistura realista de status) ─────────────────────

interface CaixaDemo extends Caixa {
  _itens: ItemCaixa[]
}

function box(
  id: number,
  papeleta: string,
  op: string,
  cliente: string,
  status: Caixa['status'],
  corTarja: Caixa['corTarja'],
  seq: number,
  total: number,
  abertaH: number | null,
  finalizadaH: number | null,
): CaixaDemo {
  return {
    id,
    codigoPapeleta: papeleta,
    numeroOp: op,
    clienteNome: cliente,
    status,
    corTarja,
    sequencia: seq,
    totalCaixasPedido: total,
    abertaEm:     abertaH  !== null ? ago(abertaH)  : null,
    finalizadaEm: finalizadaH !== null ? ago(finalizadaH) : null,
    _itens: makeItens(papeleta, status),
  }
}

const CAIXAS_DEMO: CaixaDemo[] = [
  // Pedido 1 — Riachuelo (3 caixas)
  box(101,'06900101','OP-2025-0101','Riachuelo Infantil - RJ',   'FINALIZADA',   'PADRAO',          1,3,  7, 6),
  box(102,'06900102','OP-2025-0101','Riachuelo Infantil - RJ',   'FINALIZADA',   'PADRAO',          2,3,  6, 5),
  box(103,'06900103','OP-2025-0101','Riachuelo Infantil - RJ',   'EM_PICKING',   'PADRAO',          3,3,  1, null),

  // Pedido 2 — Lojas Renner (2 caixas)
  box(104,'06900210','OP-2025-0102','Lojas Renner - POA',        'FINALIZADA',   'PADRAO',          1,2,  4, 3),
  box(105,'06900211','OP-2025-0102','Lojas Renner - POA',        'PARCIAL',      'PADRAO',          2,2,  3, 2),

  // Pedido 3 — C&A Modas (4 caixas)
  box(106,'06900320','OP-2025-0103','C&A Modas - SP',            'FINALIZADA',   'PADRAO',          1,4,  5, 4),
  box(107,'06900321','OP-2025-0103','C&A Modas - SP',            'FINALIZADA',   'PADRAO',          2,4,  4, 3),
  box(108,'06900322','OP-2025-0103','C&A Modas - SP',            'EM_PICKING',   'PADRAO',          3,4,  1, null),
  box(109,'06900323','OP-2025-0103','C&A Modas - SP',            'AGUARDANDO',   'PADRAO',          4,4,  null, null),

  // Pedido 4 — Exportação (2 caixas — tarja azul)
  box(110,'06900430','OP-2025-0104','JC Penney Export - Miami',  'FINALIZADA',   'EXPORTACAO_AZUL', 1,2,  8, 7),
  box(111,'06900431','OP-2025-0104','JC Penney Export - Miami',  'FINALIZADA',   'EXPORTACAO_AZUL', 2,2,  7, 6),

  // Pedido 5 — Hering Kids (3 caixas)
  box(112,'06900540','OP-2025-0105','Hering Kids - Blumenau',    'EM_PICKING',   'TAG_VERDE',       1,3,  0, null),
  box(113,'06900541','OP-2025-0105','Hering Kids - Blumenau',    'AGUARDANDO',   'TAG_VERDE',       2,3,  null, null),
  box(114,'06900542','OP-2025-0105','Hering Kids - Blumenau',    'AGUARDANDO',   'TAG_VERDE',       3,3,  null, null),

  // Pedido 6 — Pernambucanas (3 caixas)
  box(115,'06900650','OP-2025-0106','Pernambucanas - SP',        'FINALIZADA',   'PADRAO',          1,3,  6, 5),
  box(116,'06900651','OP-2025-0106','Pernambucanas - SP',        'PARCIAL',      'PADRAO',          2,3,  2, 1),
  box(117,'06900652','OP-2025-0106','Pernambucanas - SP',        'AGUARDANDO',   'PADRAO',          3,3,  null, null),

  // Pedido 7 — Kyly e-Commerce (2 caixas — multi amarelo)
  box(118,'06900760','OP-2025-0107','Kyly e-Commerce',           'EM_PICKING',   'MULTI_AMARELO',   1,2,  0, null),
  box(119,'06900761','OP-2025-0107','Kyly e-Commerce',           'AGUARDANDO',   'MULTI_AMARELO',   2,2,  null, null),

  // Pedido 8 — C&A cancelado (1 caixa — vermelha)
  box(120,'06900870','OP-2025-0108','C&A Modas - BH',            'CANCELADA',    'VERMELHA',        1,1,  3, null),
]

// ── dashboard ──────────────────────────────────────────────────────────────

export const DEMO_DASHBOARD: DashboardData = {
  caixasFinalizadasHoje: CAIXAS_DEMO.filter(c => c.status === 'FINALIZADA').length,
  emPickingAgora:        CAIXAS_DEMO.filter(c => c.status === 'EM_PICKING').length,
  caixasParciais:        CAIXAS_DEMO.filter(c => c.status === 'PARCIAL').length,
  operadoresAtivos:      4,
}

// ── bens patrimoniais (contexto de fábrica têxtil) ─────────────────────────

const BENS: Bem[] = [
  { id:  1, codigoPatrimonio: 'PAT-00001', descricao: 'Coletor Datalogic Memor 11', marca: 'Datalogic', modelo: 'Memor 11',      serial: 'DL-MEM-001', situacao: 'ATIVO',         localizacaoAtual: { id:2, codigo:'DEP-01', nome:'Depósito — Área de Picking' }, atualizadoEm: ago(1) },
  { id:  2, codigoPatrimonio: 'PAT-00002', descricao: 'Coletor Datalogic Memor 11', marca: 'Datalogic', modelo: 'Memor 11',      serial: 'DL-MEM-002', situacao: 'ATIVO',         localizacaoAtual: { id:2, codigo:'DEP-01', nome:'Depósito — Área de Picking' }, atualizadoEm: ago(1) },
  { id:  3, codigoPatrimonio: 'PAT-00003', descricao: 'Coletor Datalogic Memor 11', marca: 'Datalogic', modelo: 'Memor 11',      serial: 'DL-MEM-003', situacao: 'EM_MANUTENCAO', localizacaoAtual: { id:4, codigo:'TI-01',  nome:'TI — Sala de Suporte' },           atualizadoEm: ago(24) },
  { id:  4, codigoPatrimonio: 'PAT-00004', descricao: 'Coletor Datalogic Memor 11', marca: 'Datalogic', modelo: 'Memor 11',      serial: 'DL-MEM-004', situacao: 'ATIVO',         localizacaoAtual: { id:2, codigo:'DEP-01', nome:'Depósito — Área de Picking' }, atualizadoEm: ago(2) },
  { id:  5, codigoPatrimonio: 'PAT-00005', descricao: 'Impressora Térmica de Etiqueta', marca: 'Zebra', modelo: 'ZD421',        serial: 'ZB-ZD421-01', situacao: 'ATIVO',        localizacaoAtual: { id:2, codigo:'DEP-01', nome:'Depósito — Área de Picking' }, atualizadoEm: ago(48) },
  { id:  6, codigoPatrimonio: 'PAT-00006', descricao: 'Impressora Térmica de Etiqueta', marca: 'Zebra', modelo: 'ZD421',        serial: 'ZB-ZD421-02', situacao: 'ATIVO',        localizacaoAtual: { id:3, codigo:'EXP-01', nome:'Expedição' },                    atualizadoEm: ago(48) },
  { id:  7, codigoPatrimonio: 'PAT-00007', descricao: 'Switch PoE 24 Portas',       marca: 'Cisco',     modelo: 'SG350-28P',    serial: 'CS-SG350-01', situacao: 'ATIVO',        localizacaoAtual: { id:4, codigo:'TI-01',  nome:'TI — Sala de Suporte' },           atualizadoEm: ago(72) },
  { id:  8, codigoPatrimonio: 'PAT-00008', descricao: 'Nobreak 1500VA',             marca: 'APC',       modelo: 'SMT1500I',     serial: 'APC-001',    situacao: 'ATIVO',         localizacaoAtual: { id:4, codigo:'TI-01',  nome:'TI — Sala de Suporte' },           atualizadoEm: ago(96) },
  { id:  9, codigoPatrimonio: 'PAT-00009', descricao: 'Notebook Dell Latitude',     marca: 'Dell',      modelo: 'Latitude 5540',serial: 'DL-LAT-001', situacao: 'ATIVO',         localizacaoAtual: { id:1, codigo:'ADM-01', nome:'Administração' },                  atualizadoEm: ago(12) },
  { id: 10, codigoPatrimonio: 'PAT-00010', descricao: 'Notebook Dell Latitude',     marca: 'Dell',      modelo: 'Latitude 5540',serial: 'DL-LAT-002', situacao: 'ATIVO',         localizacaoAtual: { id:1, codigo:'ADM-01', nome:'Administração' },                  atualizadoEm: ago(12) },
  { id: 11, codigoPatrimonio: 'PAT-00011', descricao: 'Monitor LG 24" Full HD',     marca: 'LG',        modelo: '24MK430H',     serial: 'LG-24-001',  situacao: 'ATIVO',         localizacaoAtual: { id:1, codigo:'ADM-01', nome:'Administração' },                  atualizadoEm: ago(72) },
  { id: 12, codigoPatrimonio: 'PAT-00012', descricao: 'Monitor LG 24" Full HD',     marca: 'LG',        modelo: '24MK430H',     serial: 'LG-24-002',  situacao: 'ATIVO',         localizacaoAtual: { id:1, codigo:'ADM-01', nome:'Administração' },                  atualizadoEm: ago(72) },
  { id: 13, codigoPatrimonio: 'PAT-00013', descricao: 'Empilhadeira Elétrica 1,5t', marca: 'Still',     modelo: 'EXV14',        serial: 'ST-EXV-001', situacao: 'ATIVO',         localizacaoAtual: { id:2, codigo:'DEP-01', nome:'Depósito — Área de Picking' }, atualizadoEm: ago(4) },
  { id: 14, codigoPatrimonio: 'PAT-00014', descricao: 'Empilhadeira Elétrica 1,5t', marca: 'Still',     modelo: 'EXV14',        serial: 'ST-EXV-002', situacao: 'EM_MANUTENCAO', localizacaoAtual: { id:5, codigo:'MAN-01', nome:'Manutenção' },                    atualizadoEm: ago(48) },
  { id: 15, codigoPatrimonio: 'PAT-00015', descricao: 'Balança de Bancada 30kg',    marca: 'Toledo',    modelo: 'Prix 3 Plus',  serial: 'TOL-001',    situacao: 'ATIVO',         localizacaoAtual: { id:3, codigo:'EXP-01', nome:'Expedição' },                    atualizadoEm: ago(20) },
  { id: 16, codigoPatrimonio: 'PAT-00016', descricao: 'Leitor de Código de Barras', marca: 'Honeywell', modelo: 'Xenon 1900',   serial: 'HW-XN-001',  situacao: 'BAIXADO',       atualizadoEm: ago(200) },
  { id: 17, codigoPatrimonio: 'PAT-00017', descricao: 'Roteador Wi-Fi Industrial',  marca: 'Cisco',     modelo: 'C9115AXI',     serial: 'CS-C9115-01', situacao: 'ATIVO',        localizacaoAtual: { id:2, codigo:'DEP-01', nome:'Depósito — Área de Picking' }, atualizadoEm: ago(168) },
  { id: 18, codigoPatrimonio: 'PAT-00018', descricao: 'Roteador Wi-Fi Industrial',  marca: 'Cisco',     modelo: 'C9115AXI',     serial: 'CS-C9115-02', situacao: 'ATIVO',        localizacaoAtual: { id:3, codigo:'EXP-01', nome:'Expedição' },                    atualizadoEm: ago(168) },
  { id: 19, codigoPatrimonio: 'PAT-00019', descricao: 'Coletor Datalogic Memor 11', marca: 'Datalogic', modelo: 'Memor 11',      serial: 'DL-MEM-005', situacao: 'NAO_LOCALIZADO',atualizadoEm: ago(500) },
  { id: 20, codigoPatrimonio: 'PAT-00020', descricao: 'Servidor Dell PowerEdge',    marca: 'Dell',      modelo: 'PowerEdge R350',serial: 'DL-PE-001',  situacao: 'ATIVO',         localizacaoAtual: { id:4, codigo:'TI-01', nome:'TI — Sala de Suporte' },           atualizadoEm: ago(720) },
]

// ── relatório de produtividade ─────────────────────────────────────────────

export const DEMO_PRODUTIVIDADE = {
  periodo: { de: null, ate: null },
  resumo: {
    totalCaixasFinalizadas: 47,
    totalItensColetados: 312,
    mediaMinutosPorCaixa: 18.4,
    taxaDivergencia: '4.2%',
  },
  porOperador: [
    { nome: 'Operador Alpha', cracha: 'OP001', caixas: 14, itens: 92, mediaMin: 16.2 },
    { nome: 'Operador Beta',  cracha: 'OP002', caixas: 11, itens: 78, mediaMin: 19.1 },
    { nome: 'Operador Gama',  cracha: 'OP003', caixas: 12, itens: 81, mediaMin: 17.8 },
    { nome: 'Operador Delta', cracha: 'OP004', caixas: 10, itens: 61, mediaMin: 21.3 },
  ],
}

// ── funções de consulta (com filtros reais) ────────────────────────────────

export function getDemoDashboard(): DashboardData {
  return DEMO_DASHBOARD
}

export function getDemoCaixas(params: Record<string, string>): PageResponse<Caixa> {
  const statusFilter = params.status || ''
  const page = Number(params.page ?? 0)
  const size = Number(params.size ?? 20)

  let list: Caixa[] = CAIXAS_DEMO.map(({ _itens: _, ...c }) => c)
  if (statusFilter) list = list.filter(c => c.status === statusFilter)

  const start = page * size
  return {
    content: list.slice(start, start + size),
    totalElements: list.length,
    totalPages: Math.max(1, Math.ceil(list.length / size)),
    number: page,
    size,
  }
}

export function getDemoCaixasRecentes(): { content: Caixa[] } {
  return {
    content: CAIXAS_DEMO
      .filter(c => c.status === 'FINALIZADA')
      .map(({ _itens: _, ...c }) => c),
  }
}

export function getDemoBoxDetail(papeleta: string): Caixa {
  const found = CAIXAS_DEMO.find(c => c.codigoPapeleta === papeleta)
  const base = found ?? CAIXAS_DEMO[0]
  const { _itens, ...rest } = base
  return { ...rest, itens: _itens }
}

export function getDemoBens(params: Record<string, string>): PageResponse<Bem> {
  const situacaoFilter = params.situacao || ''
  const q = (params.q || '').toLowerCase().trim()
  const page = Number(params.page ?? 0)
  const size = Number(params.size ?? 20)

  let list = BENS
  if (situacaoFilter) list = list.filter(b => b.situacao === situacaoFilter)
  if (q) list = list.filter(b =>
    b.codigoPatrimonio.toLowerCase().includes(q) ||
    b.descricao.toLowerCase().includes(q) ||
    b.marca.toLowerCase().includes(q) ||
    b.modelo.toLowerCase().includes(q)
  )

  const start = page * size
  return {
    content: list.slice(start, start + size),
    totalElements: list.length,
    totalPages: Math.max(1, Math.ceil(list.length / size)),
    number: page,
    size,
  }
}

export function getDemoProdutividade(de: string, ate: string) {
  return { ...DEMO_PRODUTIVIDADE, periodo: { de, ate } }
}

// ── SKUs ───────────────────────────────────────────────────────────────────

const DEMO_SKUS: Sku[] = [
  { id: 1,  referencia: '1000079', cor: 'BRANCO',    tamanho: 'RN', descricao: 'Body ML Bebê Branco RN',             codigoEan: '7891234500001', ativo: true  },
  { id: 2,  referencia: '1000079', cor: 'BRANCO',    tamanho: 'P',  descricao: 'Body ML Bebê Branco P',              codigoEan: '7891234500002', ativo: true  },
  { id: 3,  referencia: '1000079', cor: 'BRANCO',    tamanho: 'M',  descricao: 'Body ML Bebê Branco M',              codigoEan: '7891234500003', ativo: true  },
  { id: 4,  referencia: '1000079', cor: 'BRANCO',    tamanho: 'G',  descricao: 'Body ML Bebê Branco G',              codigoEan: '7891234500004', ativo: true  },
  { id: 5,  referencia: '1000080', cor: 'AZUL',      tamanho: 'P',  descricao: 'Conjunto Moletom Bebê Azul P',       codigoEan: '7891234500005', ativo: true  },
  { id: 6,  referencia: '1000080', cor: 'AZUL',      tamanho: 'M',  descricao: 'Conjunto Moletom Bebê Azul M',       codigoEan: '7891234500006', ativo: true  },
  { id: 7,  referencia: '1000080', cor: 'CINZA',     tamanho: 'M',  descricao: 'Conjunto Moletom Bebê Cinza M',      codigoEan: '7891234500007', ativo: true  },
  { id: 8,  referencia: '1000081', cor: 'ROSA',      tamanho: 'P',  descricao: 'Vestido Tricot Rosa P',              codigoEan: '7891234500008', ativo: true  },
  { id: 9,  referencia: '1000081', cor: 'ROSA',      tamanho: 'M',  descricao: 'Vestido Tricot Rosa M',              codigoEan: '7891234500009', ativo: true  },
  { id: 10, referencia: '1000081', cor: 'LILÁS',     tamanho: 'G',  descricao: 'Vestido Tricot Lilás G',             codigoEan: '7891234500010', ativo: true  },
  { id: 11, referencia: '1000082', cor: 'ESTAMPADO', tamanho: '1',  descricao: 'Pijama Longo Kyly Estampado T1',     codigoEan: '7891234500011', ativo: true  },
  { id: 12, referencia: '1000082', cor: 'ESTAMPADO', tamanho: '2',  descricao: 'Pijama Longo Kyly Estampado T2',     codigoEan: '7891234500012', ativo: true  },
  { id: 13, referencia: '1000082', cor: 'ESTAMPADO', tamanho: '4',  descricao: 'Pijama Longo Kyly Estampado T4',     codigoEan: '7891234500013', ativo: true  },
  { id: 14, referencia: '1000083', cor: 'VERDE',     tamanho: 'P',  descricao: 'Macacão Plush Verde P',              codigoEan: '7891234500014', ativo: true  },
  { id: 15, referencia: '1000083', cor: 'VERDE',     tamanho: 'M',  descricao: 'Macacão Plush Verde M',              codigoEan: '7891234500015', ativo: true  },
  { id: 16, referencia: '1000083', cor: 'AMARELO',   tamanho: 'G',  descricao: 'Macacão Plush Amarelo G',            codigoEan: '7891234500016', ativo: true  },
  { id: 17, referencia: '1000084', cor: 'MARINHO',   tamanho: '2',  descricao: 'Blusa de Frio c/ Capuz Marinho T2',  codigoEan: '7891234500017', ativo: true  },
  { id: 18, referencia: '1000084', cor: 'MARINHO',   tamanho: '4',  descricao: 'Blusa de Frio c/ Capuz Marinho T4',  codigoEan: '7891234500018', ativo: true  },
  { id: 19, referencia: '1000084', cor: 'MARINHO',   tamanho: '6',  descricao: 'Blusa de Frio c/ Capuz Marinho T6',  codigoEan: '7891234500019', ativo: true  },
  { id: 20, referencia: '1000085', cor: 'JEANS',     tamanho: '4',  descricao: 'Calça Jogger Jeans T4',              codigoEan: '7891234500020', ativo: true  },
  { id: 21, referencia: '1000085', cor: 'JEANS',     tamanho: '6',  descricao: 'Calça Jogger Jeans T6',              codigoEan: '7891234500021', ativo: true  },
  { id: 22, referencia: '1000085', cor: 'JEANS',     tamanho: '8',  descricao: 'Calça Jogger Jeans T8',              codigoEan: '7891234500022', ativo: true  },
  { id: 23, referencia: '1000086', cor: 'BRANCO',    tamanho: '4',  descricao: 'Camiseta Básica Branca T4',          codigoEan: '7891234500023', ativo: false },
  { id: 24, referencia: '1000086', cor: 'BRANCO',    tamanho: '6',  descricao: 'Camiseta Básica Branca T6',          codigoEan: '7891234500024', ativo: false },
]

export function getDemoSkus(params: Record<string, string>): PageResponse<Sku> {
  const q = (params.q || '').toLowerCase().trim()
  const ativoFilter = params.ativo
  const page = Number(params.page ?? 0)
  const size = Number(params.size ?? 20)

  let list = DEMO_SKUS
  if (ativoFilter === 'true')  list = list.filter(s => s.ativo)
  if (ativoFilter === 'false') list = list.filter(s => !s.ativo)
  if (q) list = list.filter(s =>
    s.referencia.includes(q) ||
    s.descricao.toLowerCase().includes(q) ||
    s.cor.toLowerCase().includes(q) ||
    s.codigoEan.includes(q)
  )
  const start = page * size
  return {
    content: list.slice(start, start + size),
    totalElements: list.length,
    totalPages: Math.max(1, Math.ceil(list.length / size)),
    number: page, size,
  }
}

// ── Endereços de Estoque ───────────────────────────────────────────────────

const DEMO_ENDERECOS: EnderecoEstoque[] = [
  { id:  1, codigo: 'A01.01.1A', andarRua: 'A01', secao: '01', posicaoNivel: '1A', ativo: true  },
  { id:  2, codigo: 'A01.01.1B', andarRua: 'A01', secao: '01', posicaoNivel: '1B', ativo: true  },
  { id:  3, codigo: 'A01.01.2A', andarRua: 'A01', secao: '01', posicaoNivel: '2A', ativo: true  },
  { id:  4, codigo: 'A01.01.2B', andarRua: 'A01', secao: '01', posicaoNivel: '2B', ativo: true  },
  { id:  5, codigo: 'A02.03.3A', andarRua: 'A02', secao: '03', posicaoNivel: '3A', ativo: true  },
  { id:  6, codigo: 'A02.03.3B', andarRua: 'A02', secao: '03', posicaoNivel: '3B', ativo: true  },
  { id:  7, codigo: 'A02.04.4A', andarRua: 'A02', secao: '04', posicaoNivel: '4A', ativo: true  },
  { id:  8, codigo: 'A02.04.4B', andarRua: 'A02', secao: '04', posicaoNivel: '4B', ativo: true  },
  { id:  9, codigo: 'B07.05.2A', andarRua: 'B07', secao: '05', posicaoNivel: '2A', ativo: true  },
  { id: 10, codigo: 'B07.05.2B', andarRua: 'B07', secao: '05', posicaoNivel: '2B', ativo: true  },
  { id: 11, codigo: 'B07.06.3A', andarRua: 'B07', secao: '06', posicaoNivel: '3A', ativo: true  },
  { id: 12, codigo: 'B07.06.3B', andarRua: 'B07', secao: '06', posicaoNivel: '3B', ativo: true  },
  { id: 13, codigo: 'C15.08.4A', andarRua: 'C15', secao: '08', posicaoNivel: '4A', ativo: true  },
  { id: 14, codigo: 'C15.08.4B', andarRua: 'C15', secao: '08', posicaoNivel: '4B', ativo: true  },
  { id: 15, codigo: 'C15.09.5A', andarRua: 'C15', secao: '09', posicaoNivel: '5A', ativo: true  },
  { id: 16, codigo: 'C15.09.5B', andarRua: 'C15', secao: '09', posicaoNivel: '5B', ativo: true  },
  { id: 17, codigo: 'D22.11.6A', andarRua: 'D22', secao: '11', posicaoNivel: '6A', ativo: false },
  { id: 18, codigo: 'D22.11.6B', andarRua: 'D22', secao: '11', posicaoNivel: '6B', ativo: false },
]

export function getDemoEnderecos(params: Record<string, string>): PageResponse<EnderecoEstoque> {
  const q = (params.q || '').toLowerCase().trim()
  const ativoFilter = params.ativo
  const page = Number(params.page ?? 0)
  const size = Number(params.size ?? 20)

  let list = DEMO_ENDERECOS
  if (ativoFilter === 'true')  list = list.filter(e => e.ativo)
  if (ativoFilter === 'false') list = list.filter(e => !e.ativo)
  if (q) list = list.filter(e =>
    e.codigo.toLowerCase().includes(q) ||
    e.andarRua.toLowerCase().includes(q) ||
    e.secao.includes(q)
  )
  const start = page * size
  return {
    content: list.slice(start, start + size),
    totalElements: list.length,
    totalPages: Math.max(1, Math.ceil(list.length / size)),
    number: page, size,
  }
}

// ── Usuários ───────────────────────────────────────────────────────────────

const DEMO_USUARIOS: User[] = [
  { id: 1, codigoCracha: 'ADMIN01', nome: 'Administrador KollectaOps', email: 'admin@kollectaops.com.br',        perfil: 'ADMIN',      ativo: true  },
  { id: 2, codigoCracha: 'GEST01',  nome: 'Gestor Kyly',               email: 'gestor@kyly.com.br',              perfil: 'GESTOR',     ativo: true  },
  { id: 3, codigoCracha: 'SUP001',  nome: 'Supervisor Turno A',        email: 'sup.turno.a@kyly.com.br',         perfil: 'SUPERVISOR', ativo: true  },
  { id: 4, codigoCracha: 'SUP002',  nome: 'Supervisor Turno B',        email: 'sup.turno.b@kyly.com.br',         perfil: 'SUPERVISOR', ativo: true  },
  { id: 5, codigoCracha: 'OP001',   nome: 'Operador Alpha',            email: 'operador.alpha@kyly.com.br',      perfil: 'OPERADOR',   ativo: true  },
  { id: 6, codigoCracha: 'OP002',   nome: 'Operador Beta',             email: 'operador.beta@kyly.com.br',       perfil: 'OPERADOR',   ativo: true  },
  { id: 7, codigoCracha: 'OP003',   nome: 'Operador Gama',             email: 'operador.gama@kyly.com.br',       perfil: 'OPERADOR',   ativo: true  },
  { id: 8, codigoCracha: 'OP004',   nome: 'Operador Delta',            email: 'operador.delta@kyly.com.br',      perfil: 'OPERADOR',   ativo: true  },
  { id: 9, codigoCracha: 'OP005',   nome: 'Operador Épsilon',          email: 'operador.epsilon@kyly.com.br',    perfil: 'OPERADOR',   ativo: false },
]

export function getDemoUsuarios(params: Record<string, string>): PageResponse<User> {
  const q = (params.q || '').toLowerCase().trim()
  const perfilFilter = params.perfil || ''
  const ativoFilter = params.ativo
  const page = Number(params.page ?? 0)
  const size = Number(params.size ?? 20)

  let list = DEMO_USUARIOS
  if (perfilFilter) list = list.filter(u => u.perfil === perfilFilter)
  if (ativoFilter === 'true')  list = list.filter(u => u.ativo)
  if (ativoFilter === 'false') list = list.filter(u => !u.ativo)
  if (q) list = list.filter(u =>
    u.nome.toLowerCase().includes(q) ||
    u.codigoCracha.toLowerCase().includes(q) ||
    u.email.toLowerCase().includes(q)
  )
  const start = page * size
  return {
    content: list.slice(start, start + size),
    totalElements: list.length,
    totalPages: Math.max(1, Math.ceil(list.length / size)),
    number: page, size,
  }
}

// ── Filiais ────────────────────────────────────────────────────────────────

const DEMO_FILIAIS: Filial[] = [
  { id: 1, codigo: 'KYLY-POM', nome: 'Kyly Pomerode — Matriz',    endereco: 'R. Exp. Antonio Hansen, 380 — Pomerode/SC — CEP 89107-000', ativo: true  },
  { id: 2, codigo: 'KYLY-BLU', nome: 'Kyly Blumenau — CD',        endereco: 'Av. Brasil, 1500 — Blumenau/SC — CEP 89010-001',            ativo: true  },
  { id: 3, codigo: 'KYLY-SPO', nome: 'Kyly São Paulo — Escritório',endereco: 'R. Augusta, 200 — Consolação — SP — CEP 01305-000',         ativo: false },
]

export function getDemoFiliais(params: Record<string, string>): PageResponse<Filial> {
  const q = (params.q || '').toLowerCase().trim()
  const page = Number(params.page ?? 0)
  const size = Number(params.size ?? 20)

  let list: Filial[] = DEMO_FILIAIS
  if (q) list = list.filter(f =>
    f.codigo.toLowerCase().includes(q) ||
    f.nome.toLowerCase().includes(q)
  )
  const start = page * size
  return {
    content: list.slice(start, start + size),
    totalElements: list.length,
    totalPages: Math.max(1, Math.ceil(list.length / size)),
    number: page, size,
  }
}

// ── Relatório de Divergências ──────────────────────────────────────────────

export interface DivergenciaRow {
  id: number
  caixaPapeleta: string
  clienteNome: string
  skuReferencia: string
  skuDescricao: string
  skuCor: string
  skuTamanho: string
  qtdeSolicitada: number
  qtdeColetada: number
  status: 'EM_FALTA' | 'DIVERGENTE'
  enderecoCodigo: string
  registradoEm: string
}

const DEMO_DIVERGENCIAS: DivergenciaRow[] = [
  { id:1,  caixaPapeleta:'06900211', clienteNome:'Lojas Renner - POA',       skuReferencia:'1000082', skuDescricao:'Pijama Longo Estampado T4',    skuCor:'ESTAMPADO', skuTamanho:'4', qtdeSolicitada:6,  qtdeColetada:0, status:'EM_FALTA',   enderecoCodigo:'A02.04.4B', registradoEm: ago(2) },
  { id:2,  caixaPapeleta:'06900651', clienteNome:'Pernambucanas - SP',       skuReferencia:'1000083', skuDescricao:'Macacão Plush Amarelo G',       skuCor:'AMARELO',   skuTamanho:'G', qtdeSolicitada:4,  qtdeColetada:0, status:'EM_FALTA',   enderecoCodigo:'B07.05.2B', registradoEm: ago(1) },
  { id:3,  caixaPapeleta:'06900651', clienteNome:'Pernambucanas - SP',       skuReferencia:'1000084', skuDescricao:'Blusa de Frio c/ Capuz T6',     skuCor:'MARINHO',   skuTamanho:'6', qtdeSolicitada:8,  qtdeColetada:3, status:'DIVERGENTE',  enderecoCodigo:'B07.06.3B', registradoEm: ago(1) },
  { id:4,  caixaPapeleta:'06900103', clienteNome:'Riachuelo Infantil - RJ',  skuReferencia:'1000081', skuDescricao:'Vestido Tricot Lilás G',         skuCor:'LILÁS',     skuTamanho:'G', qtdeSolicitada:3,  qtdeColetada:0, status:'EM_FALTA',   enderecoCodigo:'A02.03.3B', registradoEm: ago(0) },
  { id:5,  caixaPapeleta:'06900760', clienteNome:'Kyly e-Commerce',          skuReferencia:'1000085', skuDescricao:'Calça Jogger Jeans T8',          skuCor:'JEANS',     skuTamanho:'8', qtdeSolicitada:10, qtdeColetada:4, status:'DIVERGENTE',  enderecoCodigo:'C15.08.4B', registradoEm: ago(0) },
]

export function getDemoDivergencias(params: Record<string, string>) {
  const page = Number(params.page ?? 0)
  const size = Number(params.size ?? 20)
  const q = (params.q || '').toLowerCase()

  let list = DEMO_DIVERGENCIAS
  if (q) list = list.filter(d =>
    d.caixaPapeleta.includes(q) ||
    d.clienteNome.toLowerCase().includes(q) ||
    d.skuReferencia.includes(q)
  )

  return {
    periodo: { de: params.de ?? '', ate: params.ate ?? '' },
    resumo: {
      totalDivergencias: DEMO_DIVERGENCIAS.length,
      totalItensEmFalta: DEMO_DIVERGENCIAS.filter(d => d.status === 'EM_FALTA').length,
      totalDivergentes: DEMO_DIVERGENCIAS.filter(d => d.status === 'DIVERGENTE').length,
      caixasAfetadas: new Set(DEMO_DIVERGENCIAS.map(d => d.caixaPapeleta)).size,
    },
    content: list.slice(page * size, page * size + size),
    totalElements: list.length,
    totalPages: Math.max(1, Math.ceil(list.length / size)),
    number: page,
    size,
  }
}

// ── Relatório de Caixas Parciais ───────────────────────────────────────────

export interface ParcialRow {
  id: number
  codigoPapeleta: string
  numeroOp: string
  clienteNome: string
  abertaEm: string
  finalizadaEm: string
  totalItens: number
  itensCompletos: number
  itensEmFalta: number
}

const DEMO_PARCIAIS: ParcialRow[] = CAIXAS_DEMO
  .filter(c => c.status === 'PARCIAL')
  .map((c, i) => ({
    id: c.id,
    codigoPapeleta: c.codigoPapeleta,
    numeroOp: c.numeroOp,
    clienteNome: c.clienteNome,
    abertaEm: c.abertaEm ?? '',
    finalizadaEm: c.finalizadaEm ?? ago(1),
    totalItens: c._itens.length,
    itensCompletos: c._itens.filter(it => it.status === 'COMPLETO').length,
    itensEmFalta: c._itens.filter(it => it.status === 'EM_FALTA').length,
  }))

// ── Inventários Patrimoniais ───────────────────────────────────────────────

interface InventarioDemo {
  id: number
  filial: { id: number; codigo: string; nome: string }
  descricao: string
  status: 'ABERTO' | 'EM_ANDAMENTO' | 'FECHADO'
  iniciadoEm: string
  finalizadoEm: string | null
  criadoPor: { nome: string }
}

const DEMO_INVENTARIOS: InventarioDemo[] = [
  {
    id: 1,
    filial: { id: 1, codigo: 'KYLY-POM', nome: 'Kyly Pomerode — Matriz' },
    descricao: 'Inventário Patrimonial Anual 2024',
    status: 'FECHADO',
    iniciadoEm: '2025-01-15T08:00:00',
    finalizadoEm: '2025-01-15T17:30:00',
    criadoPor: { nome: 'Gestor Kyly' },
  },
  {
    id: 2,
    filial: { id: 1, codigo: 'KYLY-POM', nome: 'Kyly Pomerode — Matriz' },
    descricao: 'Inventário Patrimonial Anual 2025',
    status: 'FECHADO',
    iniciadoEm: '2025-12-10T08:00:00',
    finalizadoEm: '2025-12-10T17:30:00',
    criadoPor: { nome: 'Gestor Kyly' },
  },
  {
    id: 3,
    filial: { id: 1, codigo: 'KYLY-POM', nome: 'Kyly Pomerode — Matriz' },
    descricao: 'Inventário Parcial — Área de Picking',
    status: 'EM_ANDAMENTO',
    iniciadoEm: '2026-05-20T08:00:00',
    finalizadoEm: null,
    criadoPor: { nome: 'Gestor Kyly' },
  },
  {
    id: 4,
    filial: { id: 2, codigo: 'KYLY-BLU', nome: 'Kyly Blumenau — CD' },
    descricao: 'Inventário Patrimonial Anual 2025 — Blumenau',
    status: 'FECHADO',
    iniciadoEm: '2025-11-05T08:00:00',
    finalizadoEm: '2025-11-06T16:00:00',
    criadoPor: { nome: 'Gestor Kyly' },
  },
  {
    id: 5,
    filial: { id: 1, codigo: 'KYLY-POM', nome: 'Kyly Pomerode — Matriz' },
    descricao: 'Inventário Aberto — Q2 2026',
    status: 'ABERTO',
    iniciadoEm: '2026-05-26T07:00:00',
    finalizadoEm: null,
    criadoPor: { nome: 'Gestor Kyly' },
  },
]

export function getDemoInventarios(params: Record<string, string>): InventarioDemo[] {
  const statusFilter = params.status || ''
  if (statusFilter) return DEMO_INVENTARIOS.filter(i => i.status === statusFilter)
  return DEMO_INVENTARIOS
}

export function getDemoParciais(params: Record<string, string>) {
  const page = Number(params.page ?? 0)
  const size = Number(params.size ?? 20)

  return {
    periodo: { de: params.de ?? '', ate: params.ate ?? '' },
    resumo: {
      totalCaixasParciais: DEMO_PARCIAIS.length,
      totalItensEmFalta: DEMO_PARCIAIS.reduce((acc, p) => acc + p.itensEmFalta, 0),
    },
    content: DEMO_PARCIAIS.slice(page * size, page * size + size),
    totalElements: DEMO_PARCIAIS.length,
    totalPages: Math.max(1, Math.ceil(DEMO_PARCIAIS.length / size)),
    number: page,
    size,
  }
}
