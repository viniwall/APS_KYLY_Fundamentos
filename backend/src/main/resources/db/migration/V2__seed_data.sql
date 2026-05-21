-- KollectaOps – Dados de Homologação
-- V2__seed_data.sql
-- ATENÇÃO: apenas dados de teste. NÃO usar em produção sem redefinir senhas.

SET NAMES utf8mb4;

-- Filial
INSERT INTO filial (codigo, nome, endereco, ativo) VALUES
('KYLY-POM', 'Kyly Pomerode', 'Rua Exp. Antonio Hansen, 380 - Pomerode/SC - CEP 89107-000', 1);

-- Turnos
INSERT INTO turno (codigo, nome, hora_inicio, hora_fim) VALUES
('PICK-T1', 'PICKING 1º TURNO', '06:00:00', '14:00:00'),
('PICK-T2', 'PICKING 2º TURNO', '14:00:00', '22:00:00'),
('PICK-T3', 'PICKING 3º TURNO', '22:00:00', '06:00:00');

-- Usuários de homologação
-- Senha padrão: Kolecta@2024  (BCrypt hash gerado com cost 10)
INSERT INTO usuario (codigo_cracha, nome, email, senha_hash, perfil, ativo) VALUES
('ADMIN01', 'Administrador KollectaOps', 'admin@kollectaops.com.br',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', 1),
('GEST01', 'Gestor Kyly', 'gestor@kyly.com.br',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'GESTOR', 1),
('SUP001', 'Supervisor Teste', 'supervisor@kyly.com.br',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'SUPERVISOR', 1),
('OP001', 'Operador Alpha', 'operador.alpha@kyly.com.br',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'OPERADOR', 1),
('OP002', 'Operador Beta', 'operador.beta@kyly.com.br',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'OPERADOR', 1);

-- Vincular usuários à filial
INSERT INTO usuario_filial (usuario_id, filial_id) VALUES
(1, 1), (2, 1), (3, 1), (4, 1), (5, 1);

-- SKUs
INSERT INTO sku (referencia, cor, tamanho, descricao, codigo_ean, ativo) VALUES
('1000079', 'ÚNICO', '18', 'Body ML Bebê Branco T18', '7891234500001', 1),
('1000079', 'ÚNICO', '24', 'Body ML Bebê Branco T24', '7891234500002', 1),
('1000080', 'AZUL',  'PP', 'Conjunto Moletom Azul PP',  '7891234500003', 1),
('1000080', 'AZUL',  'P',  'Conjunto Moletom Azul P',   '7891234500004', 1),
('1000081', 'ROSA',  'M',  'Vestido Tricot Rosa M',     '7891234500005', 1);

-- Endereços de estoque
INSERT INTO endereco_estoque (codigo, andar_rua, secao, posicao_nivel, ativo) VALUES
('A02.01.4A', 'A02', '01', '4A', 1),
('A02.01.4B', 'A02', '01', '4B', 1),
('C37.09.6B', 'C37', '09', '6B', 1),
('C37.09.6C', 'C37', '09', '6C', 1),
('B15.03.2A', 'B15', '03', '2A', 1);

-- Estoque
INSERT INTO estoque (sku_id, endereco_id, quantidade) VALUES
(1, 1, 24),
(1, 3, 12),
(2, 1, 18),
(3, 2, 36),
(4, 4, 24),
(5, 5, 16);

-- Pedido de homologação
INSERT INTO pedido (numero_pedido, cliente_nome, cliente_doc, status) VALUES
('PED-2024-0001', 'Loja Kyly Online', '12.345.678/0001-99', 'ABERTO'),
('PED-2024-0002', 'Multimarcas Centro', '98.765.432/0001-11', 'ABERTO');

-- Caixas
INSERT INTO caixa (codigo_papeleta, numero_op, pedido_id, cor_tarja, sequencia, total_caixas_pedido, status) VALUES
('06772401', '2024-0001-001', 1, 'PADRAO',        1, 2, 'AGUARDANDO'),
('06772402', '2024-0001-002', 1, 'PADRAO',        2, 2, 'AGUARDANDO'),
('06772403', '2024-0002-001', 2, 'EXPORTACAO_AZUL', 1, 1, 'AGUARDANDO');

-- Itens das caixas
INSERT INTO item_caixa (caixa_id, sku_id, endereco_sugerido_id, qtde_solicitada, qtde_coletada, status, ordem_picking) VALUES
-- Caixa 06772401: 2 SKUs
(1, 1, 1, 4, 0, 'PENDENTE', 1),
(1, 2, 1, 2, 0, 'PENDENTE', 2),
-- Caixa 06772402: 1 SKU
(2, 3, 2, 6, 0, 'PENDENTE', 1),
-- Caixa 06772403: 3 SKUs
(3, 4, 4, 3, 0, 'PENDENTE', 1),
(3, 5, 5, 2, 0, 'PENDENTE', 2);

-- Peças disponíveis para teste (códigos de barras simulados)
INSERT INTO peca (codigo_unico, sku_id, status) VALUES
('0424451405703940001001', 1, 'DISPONIVEL'),
('0424451405703940001002', 1, 'DISPONIVEL'),
('0424451405703940001003', 1, 'DISPONIVEL'),
('0424451405703940001004', 1, 'DISPONIVEL'),
('0424451405703940002001', 2, 'DISPONIVEL'),
('0424451405703940002002', 2, 'DISPONIVEL'),
('0424451405703940003001', 3, 'DISPONIVEL'),
('0424451405703940003002', 3, 'DISPONIVEL'),
('0424451405703940003003', 3, 'DISPONIVEL'),
('0424451405703940003004', 3, 'DISPONIVEL'),
('0424451405703940003005', 3, 'DISPONIVEL'),
('0424451405703940003006', 3, 'DISPONIVEL');

-- Localização patrimonial
INSERT INTO localizacao_patrimonial (codigo, nome, filial_id, ativo) VALUES
('ADM-01', 'Administração - Sala 1', 1, 1),
('DEP-01', 'Depósito - Área Principal', 1, 1),
('TI-01',  'TI - Sala de Servidores', 1, 1),
('RH-01',  'Recursos Humanos', 1, 1);

-- Bens patrimoniais para demo
INSERT INTO bem (codigo_patrimonio, descricao, marca, modelo, serial, situacao, localizacao_atual_id) VALUES
('PAT-00001', 'Notebook Dell Latitude', 'Dell', 'Latitude 5520', 'DL5520XYZ', 'ATIVO', 1),
('PAT-00002', 'Monitor LG 24"', 'LG', '24MK430H', 'LG24MK001', 'ATIVO', 1),
('PAT-00003', 'Impressora Térmica Zebra', 'Zebra', 'ZD220', 'ZBR001', 'ATIVO', 2),
('PAT-00004', 'Coletor Datalogic Memor 11', 'Datalogic', 'Memor 11', 'DL-MEM11-001', 'ATIVO', 2),
('PAT-00005', 'Switch Cisco 24P', 'Cisco', 'SG350-28', 'CSC001', 'ATIVO', 3);

-- Inventário aberto para demo
INSERT INTO inventario (filial_id, descricao, status, criado_por_id) VALUES
(1, 'Inventário Patrimonial Anual 2024', 'ABERTO', 1);
