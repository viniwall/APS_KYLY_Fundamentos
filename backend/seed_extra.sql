-- KollectaOps – Dados complementares para demo completa
-- Execute após V2__seed_data.sql
-- Data de referência: 2026-05-26

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- FILIAIS
-- ============================================================
INSERT IGNORE INTO filial (codigo, nome, endereco, ativo) VALUES
('KYLY-BLU', 'Kyly Blumenau — CD',         'Av. Brasil, 1500 — Blumenau/SC — CEP 89010-001',            1),
('KYLY-SPO', 'Kyly São Paulo — Escritório', 'R. Augusta, 200 — Consolação — SP — CEP 01305-000',         0);

-- ============================================================
-- USUÁRIOS ADICIONAIS (senha padrão: Kolecta@2024)
-- ============================================================
INSERT IGNORE INTO usuario (codigo_cracha, nome, email, senha_hash, perfil, ativo) VALUES
('SUP002', 'Supervisora Maria Silva',  'supervisora.maria@kyly.com.br',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'SUPERVISOR', 1),
('OP003',  'Operador Carlos Souza',    'operador.carlos@kyly.com.br',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'OPERADOR', 1),
('OP004',  'Operadora Ana Lima',       'operador.ana@kyly.com.br',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'OPERADOR', 1),
('OP005',  'Operador José Pereira',    'operador.jose@kyly.com.br',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'OPERADOR', 0);

INSERT IGNORE INTO usuario_filial (usuario_id, filial_id)
SELECT u.id, 1 FROM usuario u WHERE u.codigo_cracha IN ('SUP002','OP003','OP004','OP005');

-- ============================================================
-- SKUs ADICIONAIS (Kyly — linha infantil real)
-- ============================================================
INSERT IGNORE INTO sku (referencia, cor, tamanho, descricao, codigo_ean, ativo) VALUES
('1000079', 'BRANCO', 'RN', 'Body ML Bebê Branco RN',            '7891234500100', 1),
('1000079', 'BRANCO', 'P',  'Body ML Bebê Branco P',             '7891234500101', 1),
('1000079', 'BRANCO', 'G',  'Body ML Bebê Branco G',             '7891234500102', 1),
('1000080', 'CINZA',  'P',  'Conjunto Moletom Bebê Cinza P',     '7891234500103', 1),
('1000080', 'CINZA',  'M',  'Conjunto Moletom Bebê Cinza M',     '7891234500104', 1),
('1000080', 'CINZA',  'G',  'Conjunto Moletom Bebê Cinza G',     '7891234500105', 1),
('1000081', 'ROSA',   'P',  'Vestido Tricot Rosa P',             '7891234500106', 1),
('1000081', 'LILÁS',  'G',  'Vestido Tricot Lilás G',            '7891234500107', 1),
('1000082', 'ESTAMPADO','1','Pijama Longo Kyly Estampado T1',    '7891234500108', 1),
('1000082', 'ESTAMPADO','2','Pijama Longo Kyly Estampado T2',    '7891234500109', 1),
('1000082', 'ESTAMPADO','4','Pijama Longo Kyly Estampado T4',    '7891234500110', 1),
('1000083', 'VERDE',  'P',  'Macacão Plush Verde P',             '7891234500111', 1),
('1000083', 'VERDE',  'M',  'Macacão Plush Verde M',             '7891234500112', 1),
('1000083', 'AMARELO','G',  'Macacão Plush Amarelo G',           '7891234500113', 1),
('1000084', 'MARINHO','2',  'Blusa de Frio c/ Capuz Marinho T2', '7891234500114', 1),
('1000084', 'MARINHO','4',  'Blusa de Frio c/ Capuz Marinho T4', '7891234500115', 1),
('1000084', 'MARINHO','6',  'Blusa de Frio c/ Capuz Marinho T6', '7891234500116', 1),
('1000085', 'JEANS',  '4',  'Calça Jogger Jeans T4',             '7891234500117', 1),
('1000085', 'JEANS',  '6',  'Calça Jogger Jeans T6',             '7891234500118', 1),
('1000085', 'JEANS',  '8',  'Calça Jogger Jeans T8',             '7891234500119', 1),
('1000086', 'BRANCO', '4',  'Camiseta Básica Branca T4',         '7891234500120', 0),
('1000086', 'BRANCO', '6',  'Camiseta Básica Branca T6',         '7891234500121', 0);

-- ============================================================
-- ENDEREÇOS DE ESTOQUE ADICIONAIS
-- ============================================================
INSERT IGNORE INTO endereco_estoque (codigo, andar_rua, secao, posicao_nivel, ativo) VALUES
('A01.01.1A', 'A01', '01', '1A', 1),
('A01.01.1B', 'A01', '01', '1B', 1),
('A01.01.2A', 'A01', '01', '2A', 1),
('A01.01.2B', 'A01', '01', '2B', 1),
('A02.03.3A', 'A02', '03', '3A', 1),
('A02.03.3B', 'A02', '03', '3B', 1),
('A02.04.4A', 'A02', '04', '4A', 1),
('A02.04.4B', 'A02', '04', '4B', 1),
('B07.05.2A', 'B07', '05', '2A', 1),
('B07.05.2B', 'B07', '05', '2B', 1),
('B07.06.3A', 'B07', '06', '3A', 1),
('B07.06.3B', 'B07', '06', '3B', 1),
('C15.08.4A', 'C15', '08', '4A', 1),
('C15.08.4B', 'C15', '08', '4B', 1),
('C15.09.5A', 'C15', '09', '5A', 1),
('C15.09.5B', 'C15', '09', '5B', 1),
('D22.11.6A', 'D22', '11', '6A', 0),
('D22.11.6B', 'D22', '11', '6B', 0);

-- ============================================================
-- PEDIDOS
-- ============================================================
INSERT IGNORE INTO pedido (numero_pedido, cliente_nome, cliente_doc, status) VALUES
('PED-2026-0003', 'Lojas Renner S.A.',         '92.754.738/0001-62', 'FINALIZADO'),
('PED-2026-0004', 'Riachuelo S.A.',             '33.200.056/0001-06', 'FINALIZADO'),
('PED-2026-0005', 'Marisa Lojas S.A.',          '61.189.288/0001-89', 'FINALIZADO'),
('PED-2026-0006', 'C&A Modas S.A.',             '45.242.914/0001-05', 'EM_PICKING'),
('PED-2026-0007', 'Pernambucanas S.A.',         '61.055.530/0001-48', 'EM_PICKING'),
('PED-2026-0008', 'JC Penney Export - Miami',   '00.000.001/0001-00', 'ABERTO');

-- ============================================================
-- CAIXAS FINALIZADAS HOJE (2026-05-26) → alimentam dashboard
-- ============================================================
INSERT IGNORE INTO caixa (codigo_papeleta, numero_op, pedido_id, cor_tarja, sequencia, total_caixas_pedido, status, aberta_em, finalizada_em) VALUES
('06772410', '2026-0003-001', (SELECT id FROM pedido WHERE numero_pedido='PED-2026-0003'), 'PADRAO',          1, 3, 'FINALIZADA', '2026-05-26 06:15:00', '2026-05-26 06:42:00'),
('06772411', '2026-0003-002', (SELECT id FROM pedido WHERE numero_pedido='PED-2026-0003'), 'PADRAO',          2, 3, 'FINALIZADA', '2026-05-26 06:50:00', '2026-05-26 07:18:00'),
('06772412', '2026-0003-003', (SELECT id FROM pedido WHERE numero_pedido='PED-2026-0003'), 'TAG_VERDE',       3, 3, 'FINALIZADA', '2026-05-26 07:25:00', '2026-05-26 07:55:00'),
('06772413', '2026-0004-001', (SELECT id FROM pedido WHERE numero_pedido='PED-2026-0004'), 'EXPORTACAO_AZUL', 1, 2, 'FINALIZADA', '2026-05-26 08:00:00', '2026-05-26 08:38:00'),
('06772414', '2026-0004-002', (SELECT id FROM pedido WHERE numero_pedido='PED-2026-0004'), 'EXPORTACAO_AZUL', 2, 2, 'FINALIZADA', '2026-05-26 08:45:00', '2026-05-26 09:20:00'),
('06772415', '2026-0005-001', (SELECT id FROM pedido WHERE numero_pedido='PED-2026-0005'), 'PADRAO',          1, 3, 'FINALIZADA', '2026-05-26 09:30:00', '2026-05-26 10:05:00'),
('06772416', '2026-0005-002', (SELECT id FROM pedido WHERE numero_pedido='PED-2026-0005'), 'PADRAO',          2, 3, 'FINALIZADA', '2026-05-26 10:10:00', '2026-05-26 10:45:00'),
('06772417', '2026-0005-003', (SELECT id FROM pedido WHERE numero_pedido='PED-2026-0005'), 'TAG_ROSA',        3, 3, 'FINALIZADA', '2026-05-26 11:00:00', '2026-05-26 11:35:00');

-- ============================================================
-- CAIXAS EM PICKING (4) → alimentam dashboard emPickingAgora
-- ============================================================
INSERT IGNORE INTO caixa (codigo_papeleta, numero_op, pedido_id, cor_tarja, sequencia, total_caixas_pedido, status, aberta_em) VALUES
('06772420', '2026-0006-001', (SELECT id FROM pedido WHERE numero_pedido='PED-2026-0006'), 'PADRAO',       1, 3, 'EM_PICKING', '2026-05-26 11:45:00'),
('06772421', '2026-0006-002', (SELECT id FROM pedido WHERE numero_pedido='PED-2026-0006'), 'PADRAO',       2, 3, 'EM_PICKING', '2026-05-26 11:45:00'),
('06772422', '2026-0007-001', (SELECT id FROM pedido WHERE numero_pedido='PED-2026-0007'), 'MULTI_AMARELO',1, 2, 'EM_PICKING', '2026-05-26 12:00:00'),
('06772423', '2026-0007-002', (SELECT id FROM pedido WHERE numero_pedido='PED-2026-0007'), 'MULTI_AMARELO',2, 2, 'EM_PICKING', '2026-05-26 12:00:00');

-- ============================================================
-- CAIXAS PARCIAIS (2) → alimentam dashboard + relatório parciais
-- ============================================================
INSERT IGNORE INTO caixa (codigo_papeleta, numero_op, pedido_id, cor_tarja, sequencia, total_caixas_pedido, status, aberta_em, finalizada_em) VALUES
('06772430', '2026-0003-004', (SELECT id FROM pedido WHERE numero_pedido='PED-2026-0003'), 'PADRAO', 4, 4, 'PARCIAL', '2026-05-26 10:55:00', '2026-05-26 11:30:00'),
('06772431', '2026-0004-003', (SELECT id FROM pedido WHERE numero_pedido='PED-2026-0004'), 'PADRAO', 3, 3, 'PARCIAL', '2026-05-26 09:25:00', '2026-05-26 09:58:00');

-- ============================================================
-- ITENS DAS CAIXAS FINALIZADAS (status COMPLETO)
-- ============================================================
-- Caixa 06772410
INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 6, 6, 'COMPLETO', 1
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772410' AND s.referencia='1000079' AND s.cor='ÚNICO' AND s.tamanho='18' AND e.codigo='A02.01.4A';

INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 4, 4, 'COMPLETO', 2
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772410' AND s.referencia='1000079' AND s.cor='ÚNICO' AND s.tamanho='24' AND e.codigo='A02.01.4B';

INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 8, 8, 'COMPLETO', 3
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772410' AND s.referencia='1000080' AND s.cor='AZUL' AND s.tamanho='PP' AND e.codigo='C37.09.6B';

-- Caixa 06772411
INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 4, 4, 'COMPLETO', 1
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772411' AND s.referencia='1000080' AND s.cor='AZUL' AND s.tamanho='P' AND e.codigo='C37.09.6C';

INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 6, 6, 'COMPLETO', 2
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772411' AND s.referencia='1000081' AND s.cor='ROSA' AND s.tamanho='M' AND e.codigo='B15.03.2A';

-- Caixa 06772412
INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 12, 12, 'COMPLETO', 1
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772412' AND s.referencia='1000079' AND s.cor='ÚNICO' AND s.tamanho='18' AND e.codigo='A02.01.4A';

INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 6, 6, 'COMPLETO', 2
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772412' AND s.referencia='1000081' AND s.cor='ROSA' AND s.tamanho='M' AND e.codigo='B15.03.2A';

-- Caixa 06772413
INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 6, 6, 'COMPLETO', 1
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772413' AND s.referencia='1000080' AND s.cor='AZUL' AND s.tamanho='PP' AND e.codigo='C37.09.6B';

INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 4, 4, 'COMPLETO', 2
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772413' AND s.referencia='1000080' AND s.cor='AZUL' AND s.tamanho='P' AND e.codigo='C37.09.6C';

-- Caixa 06772414
INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 8, 8, 'COMPLETO', 1
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772414' AND s.referencia='1000079' AND s.cor='ÚNICO' AND s.tamanho='24' AND e.codigo='A02.01.4B';

-- Caixa 06772415
INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 6, 6, 'COMPLETO', 1
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772415' AND s.referencia='1000079' AND s.cor='ÚNICO' AND s.tamanho='18' AND e.codigo='A02.01.4A';

INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 4, 4, 'COMPLETO', 2
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772415' AND s.referencia='1000081' AND s.cor='ROSA' AND s.tamanho='M' AND e.codigo='B15.03.2A';

-- Caixa 06772416
INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 6, 6, 'COMPLETO', 1
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772416' AND s.referencia='1000080' AND s.cor='AZUL' AND s.tamanho='PP' AND e.codigo='C37.09.6B';

-- Caixa 06772417
INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 3, 3, 'COMPLETO', 1
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772417' AND s.referencia='1000081' AND s.cor='ROSA' AND s.tamanho='M' AND e.codigo='B15.03.2A';

INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 6, 6, 'COMPLETO', 2
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772417' AND s.referencia='1000079' AND s.cor='ÚNICO' AND s.tamanho='18' AND e.codigo='A02.01.4A';

-- ============================================================
-- ITENS DAS CAIXAS EM PICKING (mix PENDENTE / EM_COLETA / COMPLETO)
-- ============================================================
-- Caixa 06772420
INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 6, 6, 'COMPLETO', 1
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772420' AND s.referencia='1000079' AND s.cor='ÚNICO' AND s.tamanho='18' AND e.codigo='A02.01.4A';

INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 4, 2, 'EM_COLETA', 2
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772420' AND s.referencia='1000080' AND s.cor='AZUL' AND s.tamanho='P' AND e.codigo='C37.09.6C';

INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 8, 0, 'PENDENTE', 3
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772420' AND s.referencia='1000081' AND s.cor='ROSA' AND s.tamanho='M' AND e.codigo='B15.03.2A';

-- Caixa 06772421
INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 4, 4, 'COMPLETO', 1
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772421' AND s.referencia='1000079' AND s.cor='ÚNICO' AND s.tamanho='24' AND e.codigo='A02.01.4B';

INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 6, 0, 'PENDENTE', 2
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772421' AND s.referencia='1000080' AND s.cor='AZUL' AND s.tamanho='PP' AND e.codigo='C37.09.6B';

-- Caixa 06772422
INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 8, 8, 'COMPLETO', 1
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772422' AND s.referencia='1000079' AND s.cor='ÚNICO' AND s.tamanho='18' AND e.codigo='A02.01.4A';

INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 6, 3, 'EM_COLETA', 2
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772422' AND s.referencia='1000081' AND s.cor='ROSA' AND s.tamanho='M' AND e.codigo='B15.03.2A';

INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 4, 0, 'PENDENTE', 3
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772422' AND s.referencia='1000080' AND s.cor='AZUL' AND s.tamanho='PP' AND e.codigo='C37.09.6B';

-- Caixa 06772423
INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 6, 6, 'COMPLETO', 1
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772423' AND s.referencia='1000079' AND s.cor='ÚNICO' AND s.tamanho='24' AND e.codigo='A02.01.4B';

INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 4, 0, 'PENDENTE', 2
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772423' AND s.referencia='1000080' AND s.cor='AZUL' AND s.tamanho='P' AND e.codigo='C37.09.6C';

-- ============================================================
-- ITENS DAS CAIXAS PARCIAIS (mix COMPLETO / EM_FALTA)
-- ============================================================
-- Caixa 06772430 (PARCIAL — Lojas Renner)
INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 6, 6, 'COMPLETO', 1
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772430' AND s.referencia='1000079' AND s.cor='ÚNICO' AND s.tamanho='18' AND e.codigo='A02.01.4A';

INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 4, 4, 'COMPLETO', 2
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772430' AND s.referencia='1000080' AND s.cor='AZUL' AND s.tamanho='P' AND e.codigo='C37.09.6C';

INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 8, 0, 'EM_FALTA', 3
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772430' AND s.referencia='1000081' AND s.cor='ROSA' AND s.tamanho='M' AND e.codigo='B15.03.2A';

-- Caixa 06772431 (PARCIAL — Riachuelo)
INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 4, 4, 'COMPLETO', 1
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772431' AND s.referencia='1000079' AND s.cor='ÚNICO' AND s.tamanho='24' AND e.codigo='A02.01.4B';

INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 6, 0, 'EM_FALTA', 2
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772431' AND s.referencia='1000080' AND s.cor='AZUL' AND s.tamanho='PP' AND e.codigo='C37.09.6B';

INSERT IGNORE INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking)
SELECT c.id, s.id, e.id, 3, 1, 'DIVERGENTE', 3
FROM caixa c, sku s, endereco_estoque e
WHERE c.codigo_papeleta='06772431' AND s.referencia='1000081' AND s.cor='ROSA' AND s.tamanho='M' AND e.codigo='B15.03.2A';

-- ============================================================
-- SESSÕES DO COLETOR ABERTAS → alimentam operadoresAtivos
-- ============================================================
INSERT INTO sessao_coletor (supervisor_id, operador_id, turno_id, coletor_serial, aberta_em)
SELECT
    (SELECT id FROM usuario WHERE codigo_cracha = 'SUP001'),
    (SELECT id FROM usuario WHERE codigo_cracha = 'OP001'),
    1, 'COL-DL-001', '2026-05-26 06:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM sessao_coletor WHERE coletor_serial = 'COL-DL-001' AND encerrada_em IS NULL
);

INSERT INTO sessao_coletor (supervisor_id, operador_id, turno_id, coletor_serial, aberta_em)
SELECT
    (SELECT id FROM usuario WHERE codigo_cracha = 'SUP001'),
    (SELECT id FROM usuario WHERE codigo_cracha = 'OP002'),
    1, 'COL-DL-002', '2026-05-26 06:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM sessao_coletor WHERE coletor_serial = 'COL-DL-002' AND encerrada_em IS NULL
);

INSERT INTO sessao_coletor (supervisor_id, operador_id, turno_id, coletor_serial, aberta_em)
SELECT
    (SELECT id FROM usuario WHERE codigo_cracha = 'SUP002'),
    (SELECT id FROM usuario WHERE codigo_cracha = 'OP003'),
    1, 'COL-DL-003', '2026-05-26 06:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM sessao_coletor WHERE coletor_serial = 'COL-DL-003' AND encerrada_em IS NULL
);

INSERT INTO sessao_coletor (supervisor_id, operador_id, turno_id, coletor_serial, aberta_em)
SELECT
    (SELECT id FROM usuario WHERE codigo_cracha = 'SUP002'),
    (SELECT id FROM usuario WHERE codigo_cracha = 'OP004'),
    1, 'COL-DL-004', '2026-05-26 06:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM sessao_coletor WHERE coletor_serial = 'COL-DL-004' AND encerrada_em IS NULL
);

-- ============================================================
-- LOCALIZAÇÕES PATRIMONIAIS ADICIONAIS
-- ============================================================
INSERT IGNORE INTO localizacao_patrimonial (codigo, nome, filial_id, ativo) VALUES
('EXP-01', 'Expedição',           1, 1),
('MAN-01', 'Manutenção',          1, 1),
('REC-01', 'Recebimento',         1, 1);

-- ============================================================
-- BENS PATRIMONIAIS ADICIONAIS
-- ============================================================
INSERT IGNORE INTO bem (codigo_patrimonio, descricao, marca, modelo, serial, situacao, localizacao_atual_id) VALUES
('PAT-00006', 'Coletor Datalogic Memor 11', 'Datalogic', 'Memor 11',     'DL-MEM-002', 'ATIVO',
    (SELECT id FROM localizacao_patrimonial WHERE codigo='DEP-01')),
('PAT-00007', 'Coletor Datalogic Memor 11', 'Datalogic', 'Memor 11',     'DL-MEM-003', 'EM_MANUTENCAO',
    (SELECT id FROM localizacao_patrimonial WHERE codigo='TI-01')),
('PAT-00008', 'Coletor Datalogic Memor 11', 'Datalogic', 'Memor 11',     'DL-MEM-004', 'ATIVO',
    (SELECT id FROM localizacao_patrimonial WHERE codigo='DEP-01')),
('PAT-00009', 'Impressora Térmica Zebra ZD421', 'Zebra', 'ZD421',        'ZB-ZD421-01','ATIVO',
    (SELECT id FROM localizacao_patrimonial WHERE codigo='DEP-01')),
('PAT-00010', 'Impressora Térmica Zebra ZD421', 'Zebra', 'ZD421',        'ZB-ZD421-02','ATIVO',
    (SELECT id FROM localizacao_patrimonial WHERE codigo='EXP-01')),
('PAT-00011', 'Switch PoE 24 Portas',      'Cisco',    'SG350-28P',      'CS-SG350-01','ATIVO',
    (SELECT id FROM localizacao_patrimonial WHERE codigo='TI-01')),
('PAT-00012', 'Nobreak 1500VA',            'APC',      'SMT1500I',       'APC-001',    'ATIVO',
    (SELECT id FROM localizacao_patrimonial WHERE codigo='TI-01')),
('PAT-00013', 'Notebook Dell Latitude',    'Dell',     'Latitude 5540',  'DL-LAT-001', 'ATIVO',
    (SELECT id FROM localizacao_patrimonial WHERE codigo='ADM-01')),
('PAT-00014', 'Notebook Dell Latitude',    'Dell',     'Latitude 5540',  'DL-LAT-002', 'ATIVO',
    (SELECT id FROM localizacao_patrimonial WHERE codigo='ADM-01')),
('PAT-00015', 'Monitor LG 24" Full HD',   'LG',       '24MK430H',       'LG-24-001',  'ATIVO',
    (SELECT id FROM localizacao_patrimonial WHERE codigo='ADM-01')),
('PAT-00016', 'Monitor LG 24" Full HD',   'LG',       '24MK430H',       'LG-24-002',  'ATIVO',
    (SELECT id FROM localizacao_patrimonial WHERE codigo='ADM-01')),
('PAT-00017', 'Empilhadeira Elétrica 1,5t','Still',   'EXV14',          'ST-EXV-001', 'ATIVO',
    (SELECT id FROM localizacao_patrimonial WHERE codigo='DEP-01')),
('PAT-00018', 'Empilhadeira Elétrica 1,5t','Still',   'EXV14',          'ST-EXV-002', 'EM_MANUTENCAO',
    (SELECT id FROM localizacao_patrimonial WHERE codigo='MAN-01')),
('PAT-00019', 'Balança de Bancada 30kg',  'Toledo',   'Prix 3 Plus',    'TOL-001',    'ATIVO',
    (SELECT id FROM localizacao_patrimonial WHERE codigo='EXP-01')),
('PAT-00020', 'Leitor Honeywell Xenon',   'Honeywell','Xenon 1900',      'HW-XN-001',  'BAIXADO', NULL),
('PAT-00021', 'Roteador Wi-Fi Cisco',     'Cisco',    'C9115AXI',       'CS-C9115-01','ATIVO',
    (SELECT id FROM localizacao_patrimonial WHERE codigo='DEP-01')),
('PAT-00022', 'Roteador Wi-Fi Cisco',     'Cisco',    'C9115AXI',       'CS-C9115-02','ATIVO',
    (SELECT id FROM localizacao_patrimonial WHERE codigo='EXP-01')),
('PAT-00023', 'Coletor Datalogic Memor 11','Datalogic','Memor 11',       'DL-MEM-005', 'NAO_LOCALIZADO', NULL),
('PAT-00024', 'Servidor Dell PowerEdge',  'Dell',     'PowerEdge R350', 'DL-PE-001',  'ATIVO',
    (SELECT id FROM localizacao_patrimonial WHERE codigo='TI-01'));

-- ============================================================
-- INVENTÁRIO ADICIONAL + LEITURAS COM DIVERGÊNCIA
-- ============================================================
INSERT IGNORE INTO inventario (filial_id, descricao, status, criado_por_id, iniciado_em, finalizado_em) VALUES
(1, 'Inventário Patrimonial Anual 2025', 'FECHADO',
    (SELECT id FROM usuario WHERE codigo_cracha = 'GEST01'),
    '2025-12-10 08:00:00', '2025-12-10 17:30:00'),
(1, 'Inventário Parcial — Área de Picking', 'EM_ANDAMENTO',
    (SELECT id FROM usuario WHERE codigo_cracha = 'GEST01'),
    '2026-05-20 08:00:00', NULL);

-- Leituras de inventário com divergências (para relatório)
INSERT INTO leitura_inventario (inventario_id, bem_id, localizacao_lida_id, operador_id, lida_em, divergencia)
SELECT
    (SELECT id FROM inventario WHERE descricao='Inventário Patrimonial Anual 2024' LIMIT 1),
    (SELECT id FROM bem WHERE codigo_patrimonio='PAT-00023'),
    (SELECT id FROM localizacao_patrimonial WHERE codigo='DEP-01'),
    (SELECT id FROM usuario WHERE codigo_cracha='OP001'),
    '2025-01-15 10:30:00', 'NAO_LOCALIZADO'
WHERE NOT EXISTS (
    SELECT 1 FROM leitura_inventario li
    JOIN bem b ON b.id = li.bem_id
    WHERE b.codigo_patrimonio = 'PAT-00023'
);

INSERT INTO leitura_inventario (inventario_id, bem_id, localizacao_lida_id, operador_id, lida_em, divergencia)
SELECT
    (SELECT id FROM inventario WHERE descricao='Inventário Patrimonial Anual 2024' LIMIT 1),
    (SELECT id FROM bem WHERE codigo_patrimonio='PAT-00007'),
    (SELECT id FROM localizacao_patrimonial WHERE codigo='DEP-01'),
    (SELECT id FROM usuario WHERE codigo_cracha='OP002'),
    '2025-01-15 11:00:00', 'LOCALIZACAO'
WHERE NOT EXISTS (
    SELECT 1 FROM leitura_inventario li
    JOIN bem b ON b.id = li.bem_id
    WHERE b.codigo_patrimonio = 'PAT-00007'
);

-- Leituras normais (sem divergência) para o inventário anual 2024
INSERT INTO leitura_inventario (inventario_id, bem_id, localizacao_lida_id, operador_id, lida_em, divergencia)
SELECT inv.id, b.id,
    (SELECT id FROM localizacao_patrimonial WHERE codigo='DEP-01'),
    (SELECT id FROM usuario WHERE codigo_cracha='OP001'),
    '2025-01-15 09:00:00', 'NENHUMA'
FROM inventario inv, bem b
WHERE inv.descricao = 'Inventário Patrimonial Anual 2024'
  AND b.codigo_patrimonio = 'PAT-00001'
  AND NOT EXISTS (
      SELECT 1 FROM leitura_inventario li WHERE li.inventario_id = inv.id AND li.bem_id = b.id
  );

INSERT INTO leitura_inventario (inventario_id, bem_id, localizacao_lida_id, operador_id, lida_em, divergencia)
SELECT inv.id, b.id,
    (SELECT id FROM localizacao_patrimonial WHERE codigo='DEP-01'),
    (SELECT id FROM usuario WHERE codigo_cracha='OP002'),
    '2025-01-15 09:15:00', 'NENHUMA'
FROM inventario inv, bem b
WHERE inv.descricao = 'Inventário Patrimonial Anual 2024'
  AND b.codigo_patrimonio = 'PAT-00004'
  AND NOT EXISTS (
      SELECT 1 FROM leitura_inventario li WHERE li.inventario_id = inv.id AND li.bem_id = b.id
  );

SET FOREIGN_KEY_CHECKS = 1;
