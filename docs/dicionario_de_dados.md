# Dicionário de Dados — KollectaOps

Banco: MySQL 8 | Charset: utf8mb4_unicode_ci | Schema: `picking`

---

## Tabela: `usuario`

| Campo | Tipo SQL | Nulo | Default | PK/FK | Descrição |
|---|---|---|---|---|---|
| id | BIGINT AUTO_INCREMENT | NÃO | — | PK | Identificador interno |
| codigo_cracha | VARCHAR(50) | NÃO | — | UK | Código impresso no crachá físico do usuário; usado para login por bipagem |
| nome | VARCHAR(150) | NÃO | — | — | Nome completo do usuário |
| email | VARCHAR(200) | SIM | NULL | UK | E-mail para acesso ao painel web (opcional para operadores) |
| senha_hash | VARCHAR(255) | NÃO | — | — | Hash BCrypt (cost 10) da senha; nunca armazenar em texto plano |
| perfil | ENUM | NÃO | OPERADOR | — | Nível de acesso: ADMIN, GESTOR, SUPERVISOR, OPERADOR |
| ativo | TINYINT(1) | NÃO | 1 | — | 1 = ativo, 0 = desativado (soft delete) |
| criado_em | DATETIME | NÃO | CURRENT_TIMESTAMP | — | Data e hora de criação do registro |

---

## Tabela: `filial`

| Campo | Tipo SQL | Nulo | Default | PK/FK | Descrição |
|---|---|---|---|---|---|
| id | BIGINT AUTO_INCREMENT | NÃO | — | PK | Identificador interno |
| codigo | VARCHAR(20) | NÃO | — | UK | Código curto da filial (ex: KYLY-POM) |
| nome | VARCHAR(150) | NÃO | — | — | Nome completo da filial |
| endereco | TEXT | SIM | NULL | — | Endereço físico completo |
| ativo | TINYINT(1) | NÃO | 1 | — | 1 = ativa, 0 = desativada |

---

## Tabela: `usuario_filial`

| Campo | Tipo SQL | Nulo | Default | PK/FK | Descrição |
|---|---|---|---|---|---|
| usuario_id | BIGINT | NÃO | — | PK, FK → usuario.id | Referência ao usuário |
| filial_id | BIGINT | NÃO | — | PK, FK → filial.id | Referência à filial |

*Chave primária composta (usuario_id, filial_id). Permite que um usuário pertença a múltiplas filiais.*

---

## Tabela: `turno`

| Campo | Tipo SQL | Nulo | Default | PK/FK | Descrição |
|---|---|---|---|---|---|
| id | BIGINT AUTO_INCREMENT | NÃO | — | PK | Identificador interno |
| codigo | VARCHAR(20) | NÃO | — | UK | Código do turno (ex: PICK-T1) |
| nome | VARCHAR(100) | NÃO | — | — | Nome descritivo (ex: PICKING 1º TURNO) |
| hora_inicio | TIME | NÃO | — | — | Horário de início do turno |
| hora_fim | TIME | NÃO | — | — | Horário de término do turno |

---

## Tabela: `sessao_coletor`

| Campo | Tipo SQL | Nulo | Default | PK/FK | Descrição |
|---|---|---|---|---|---|
| id | BIGINT AUTO_INCREMENT | NÃO | — | PK | Identificador interno |
| supervisor_id | BIGINT | NÃO | — | FK → usuario.id | Supervisor que autorizou o acesso via bipagem de crachá |
| operador_id | BIGINT | NÃO | — | FK → usuario.id | Operador que está usando o coletor |
| turno_id | BIGINT | SIM | NULL | FK → turno.id | Turno em que a sessão ocorre |
| coletor_serial | VARCHAR(100) | SIM | NULL | — | Número de série do coletor físico (para auditoria) |
| aberta_em | DATETIME | NÃO | CURRENT_TIMESTAMP | — | Momento de criação da sessão |
| encerrada_em | DATETIME | SIM | NULL | — | Momento de encerramento; NULL se ainda ativa |
| motivo_encerramento | VARCHAR(255) | SIM | NULL | — | Motivo do encerramento (ex: timeout, logout, troca de turno) |

---

## Tabela: `pedido`

| Campo | Tipo SQL | Nulo | Default | PK/FK | Descrição |
|---|---|---|---|---|---|
| id | BIGINT AUTO_INCREMENT | NÃO | — | PK | Identificador interno |
| numero_pedido | VARCHAR(50) | NÃO | — | UK | Número do pedido no ERP (ex: PED-2024-0001) |
| cliente_nome | VARCHAR(200) | SIM | NULL | — | Nome do cliente destinatário |
| cliente_doc | VARCHAR(30) | SIM | NULL | — | CNPJ ou CPF do cliente |
| criado_em | DATETIME | NÃO | CURRENT_TIMESTAMP | — | Data de criação do pedido |
| status | ENUM | NÃO | ABERTO | — | Status: ABERTO, EM_PICKING, FINALIZADO, CANCELADO |

---

## Tabela: `caixa`

| Campo | Tipo SQL | Nulo | Default | PK/FK | Descrição |
|---|---|---|---|---|---|
| id | BIGINT AUTO_INCREMENT | NÃO | — | PK | Identificador interno |
| codigo_papeleta | VARCHAR(50) | NÃO | — | UK | Código de barras da papeleta colada na caixa (lido pelo scanner) |
| numero_op | VARCHAR(50) | SIM | NULL | — | Número da Ordem de Produção / serviço |
| pedido_id | BIGINT | SIM | NULL | FK → pedido.id | Pedido ao qual esta caixa pertence |
| tipo_caixa | VARCHAR(50) | SIM | NULL | — | Tipo físico da caixa (ex: POLY, KIT) |
| peso_bruto | DECIMAL(8,3) | SIM | NULL | — | Peso bruto em kg com 3 casas decimais |
| cor_tarja | ENUM | NÃO | PADRAO | — | Cor da tarja identificadora: PADRAO, EXPORTACAO_AZUL, TAG_VERDE, TAG_ROSA, MULTI_AMARELO, VERMELHA |
| sequencia | INT | SIM | NULL | — | Número sequencial da caixa dentro do pedido |
| total_caixas_pedido | INT | SIM | NULL | — | Total de caixas do pedido (denominador do progresso) |
| status | ENUM | NÃO | AGUARDANDO | — | Status: AGUARDANDO, EM_PICKING, PARCIAL, FINALIZADA, CANCELADA |
| aberta_em | DATETIME | SIM | NULL | — | Momento em que o operador bipou a papeleta pela primeira vez |
| finalizada_em | DATETIME | SIM | NULL | — | Momento em que todos os itens foram coletados ou em falta |

---

## Tabela: `sku`

| Campo | Tipo SQL | Nulo | Default | PK/FK | Descrição |
|---|---|---|---|---|---|
| id | BIGINT AUTO_INCREMENT | NÃO | — | PK | Identificador interno |
| referencia | VARCHAR(50) | NÃO | — | UK (com cor, tamanho) | Referência do produto (ex: 1000079) |
| cor | VARCHAR(50) | NÃO | — | UK (com referencia, tamanho) | Cor do produto (ex: ÚNICO, AZUL, ROSA) |
| tamanho | VARCHAR(20) | NÃO | — | UK (com referencia, cor) | Tamanho do produto (ex: 18, PP, M) |
| descricao | VARCHAR(200) | SIM | NULL | — | Descrição textual do produto |
| codigo_ean | VARCHAR(30) | SIM | NULL | — | Código EAN-13/14 do produto |
| ativo | TINYINT(1) | NÃO | 1 | — | 1 = SKU ativo no catálogo |

---

## Tabela: `endereco_estoque`

| Campo | Tipo SQL | Nulo | Default | PK/FK | Descrição |
|---|---|---|---|---|---|
| id | BIGINT AUTO_INCREMENT | NÃO | — | PK | Identificador interno |
| codigo | VARCHAR(30) | NÃO | — | UK | Código completo do endereço (ex: C37.09.6B) |
| andar_rua | VARCHAR(10) | SIM | NULL | — | Andar ou rua do depósito (ex: C37) |
| secao | VARCHAR(10) | SIM | NULL | — | Seção dentro do andar (ex: 09) |
| posicao_nivel | VARCHAR(10) | SIM | NULL | — | Posição e nível na seção (ex: 6B) |
| ativo | TINYINT(1) | NÃO | 1 | — | 1 = endereço ativo e utilizável |

---

## Tabela: `estoque`

| Campo | Tipo SQL | Nulo | Default | PK/FK | Descrição |
|---|---|---|---|---|---|
| sku_id | BIGINT | NÃO | — | PK, FK → sku.id | SKU armazenada neste endereço |
| endereco_id | BIGINT | NÃO | — | PK, FK → endereco_estoque.id | Endereço de armazenamento |
| quantidade | INT | NÃO | 0 | — | Quantidade atual em estoque |
| atualizado_em | DATETIME | NÃO | CURRENT_TIMESTAMP ON UPDATE | — | Última atualização da quantidade |

*Chave primária composta (sku_id, endereco_id). Representa a posição física de cada SKU.*

---

## Tabela: `item_caixa`

| Campo | Tipo SQL | Nulo | Default | PK/FK | Descrição |
|---|---|---|---|---|---|
| id | BIGINT AUTO_INCREMENT | NÃO | — | PK | Identificador interno |
| caixa_id | BIGINT | NÃO | — | FK → caixa.id | Caixa à qual este item pertence |
| sku_id | BIGINT | NÃO | — | FK → sku.id | SKU que deve ser coletada |
| endereco_sugerido_id | BIGINT | SIM | NULL | FK → endereco_estoque.id | Endereço de estoque sugerido pelo sistema (otimização de rota) |
| qtde_solicitada | INT | NÃO | 0 | — | Quantidade de peças a coletar para este SKU |
| qtde_coletada | INT | NÃO | 0 | — | Quantidade efetivamente bipada até o momento |
| status | ENUM | NÃO | PENDENTE | — | Status: PENDENTE, EM_COLETA, COMPLETO, EM_FALTA, DIVERGENTE |
| ordem_picking | INT | NÃO | 0 | — | Ordem de apresentação ao operador (otimização de rota) |

---

## Tabela: `peca`

| Campo | Tipo SQL | Nulo | Default | PK/FK | Descrição |
|---|---|---|---|---|---|
| id | BIGINT AUTO_INCREMENT | NÃO | — | PK | Identificador interno |
| codigo_unico | VARCHAR(100) | NÃO | — | UK | Código de barras único impresso na etiqueta de cada peça individual |
| sku_id | BIGINT | NÃO | — | FK → sku.id | SKU a qual esta peça pertence |
| status | ENUM | NÃO | DISPONIVEL | — | Status: DISPONIVEL, BIPADA, EM_CAIXA, DIVERGENTE |
| bipada_em | DATETIME | SIM | NULL | — | Momento em que a peça foi bipada pelo operador |
| bipada_por_id | BIGINT | SIM | NULL | FK → usuario.id | Operador que realizou a bipagem |
| item_caixa_id | BIGINT | SIM | NULL | FK → item_caixa.id | Item de caixa ao qual a peça foi associada após bipagem |

---

## Tabela: `evento_picking`

| Campo | Tipo SQL | Nulo | Default | PK/FK | Descrição |
|---|---|---|---|---|---|
| id | BIGINT AUTO_INCREMENT | NÃO | — | PK | Identificador interno |
| sessao_id | BIGINT | SIM | NULL | FK → sessao_coletor.id | Sessão do coletor em que o evento ocorreu |
| caixa_id | BIGINT | NÃO | — | FK → caixa.id | Caixa relacionada ao evento |
| item_caixa_id | BIGINT | SIM | NULL | FK → item_caixa.id | Item específico relacionado ao evento |
| peca_id | BIGINT | SIM | NULL | FK → peca.id | Peça específica (para eventos de bipagem) |
| tipo | ENUM | NÃO | — | — | Tipo do evento: ABRIR_CAIXA, BIPAR_OK, BIPAR_OK_SKU_COMPLETA, BIPAR_ERRO_NAO_PERTENCE, BIPAR_ERRO_SEM_SALDO, PULAR_ITEM, CONSULTAR_OUTRAS_POSICOES, SALVAR_PARCIAL, FINALIZAR_CAIXA, REABRIR_CAIXA |
| mensagem | VARCHAR(500) | SIM | NULL | — | Mensagem adicional (código de barras, motivo, etc.) |
| ocorrido_em | DATETIME | NÃO | CURRENT_TIMESTAMP | — | Timestamp do evento no dispositivo |
| sincronizado | TINYINT(1) | NÃO | 0 | — | 0 = pendente de envio ao servidor; 1 = já sincronizado |

---

## Tabela: `localizacao_patrimonial`

| Campo | Tipo SQL | Nulo | Default | PK/FK | Descrição |
|---|---|---|---|---|---|
| id | BIGINT AUTO_INCREMENT | NÃO | — | PK | Identificador interno |
| codigo | VARCHAR(30) | NÃO | — | UK | Código único da localização (ex: ADM-01, DEP-01) |
| nome | VARCHAR(150) | NÃO | — | — | Nome descritivo da localização (ex: Administração - Sala 1) |
| filial_id | BIGINT | SIM | NULL | FK → filial.id | Filial à qual pertence esta localização |
| ativo | TINYINT(1) | NÃO | 1 | — | 1 = localização ativa |

---

## Tabela: `bem`

| Campo | Tipo SQL | Nulo | Default | PK/FK | Descrição |
|---|---|---|---|---|---|
| id | BIGINT AUTO_INCREMENT | NÃO | — | PK | Identificador interno |
| codigo_patrimonio | VARCHAR(100) | NÃO | — | UK | Código de patrimônio impresso na etiqueta do bem (ex: PAT-00001) |
| descricao | VARCHAR(300) | SIM | NULL | — | Descrição do bem patrimonial |
| marca | VARCHAR(100) | SIM | NULL | — | Fabricante / marca do bem |
| modelo | VARCHAR(100) | SIM | NULL | — | Modelo específico |
| serial | VARCHAR(100) | SIM | NULL | — | Número de série do fabricante |
| situacao | ENUM | NÃO | ATIVO | — | Situação: ATIVO, EM_MANUTENCAO, BAIXADO, NAO_LOCALIZADO |
| localizacao_atual_id | BIGINT | SIM | NULL | FK → localizacao_patrimonial.id | Localização física atual do bem |
| foto_url | VARCHAR(500) | SIM | NULL | — | URL da foto do bem (armazenada externamente) |
| observacao | TEXT | SIM | NULL | — | Observações livres sobre o bem |
| atualizado_em | DATETIME | NÃO | CURRENT_TIMESTAMP ON UPDATE | — | Última atualização do registro |
| atualizado_por_id | BIGINT | SIM | NULL | FK → usuario.id | Usuário que realizou a última atualização |

---

## Tabela: `inventario`

| Campo | Tipo SQL | Nulo | Default | PK/FK | Descrição |
|---|---|---|---|---|---|
| id | BIGINT AUTO_INCREMENT | NÃO | — | PK | Identificador interno |
| filial_id | BIGINT | NÃO | — | FK → filial.id | Filial em que o inventário está sendo realizado |
| descricao | VARCHAR(200) | SIM | NULL | — | Descrição do inventário (ex: Inventário Patrimonial Anual 2024) |
| iniciado_em | DATETIME | NÃO | CURRENT_TIMESTAMP | — | Data/hora de criação e início do inventário |
| finalizado_em | DATETIME | SIM | NULL | — | Data/hora de encerramento do inventário |
| status | ENUM | NÃO | ABERTO | — | Status: ABERTO, EM_ANDAMENTO, FECHADO |
| criado_por_id | BIGINT | SIM | NULL | FK → usuario.id | Usuário que criou o inventário |

---

## Tabela: `leitura_inventario`

| Campo | Tipo SQL | Nulo | Default | PK/FK | Descrição |
|---|---|---|---|---|---|
| id | BIGINT AUTO_INCREMENT | NÃO | — | PK | Identificador interno |
| inventario_id | BIGINT | NÃO | — | FK → inventario.id | Inventário ao qual pertence esta leitura |
| bem_id | BIGINT | SIM | NULL | FK → bem.id | Bem lido (NULL se não cadastrado) |
| localizacao_lida_id | BIGINT | SIM | NULL | FK → localizacao_patrimonial.id | Localização onde o bem foi fisicamente encontrado |
| operador_id | BIGINT | SIM | NULL | FK → usuario.id | Operador que realizou a leitura |
| lida_em | DATETIME | NÃO | CURRENT_TIMESTAMP | — | Data e hora da leitura pelo operador |
| divergencia | ENUM | NÃO | NENHUMA | — | Tipo de divergência: NENHUMA, LOCALIZACAO, SITUACAO, NAO_CADASTRADO |

---

## Tabela: `sync_envio`

| Campo | Tipo SQL | Nulo | Default | PK/FK | Descrição |
|---|---|---|---|---|---|
| id | BIGINT AUTO_INCREMENT | NÃO | — | PK | Identificador interno |
| dispositivo_id | VARCHAR(100) | NÃO | — | — | Identificador do coletor que originou o dado (serial ou UUID) |
| tabela | VARCHAR(50) | NÃO | — | — | Nome da tabela/entidade sendo sincronizada (ex: evento_picking) |
| payload_json | MEDIUMTEXT | NÃO | — | — | Conteúdo JSON do dado a ser sincronizado |
| criado_em | DATETIME | NÃO | CURRENT_TIMESTAMP | — | Momento em que o dado foi gerado no dispositivo |
| enviado_em | DATETIME | SIM | NULL | — | Momento em que foi enviado com sucesso ao servidor |
| status | ENUM | NÃO | PENDENTE | — | Status: PENDENTE, ENVIADO, FALHA |
| tentativas | INT | NÃO | 0 | — | Número de tentativas de envio já realizadas |
| erro_msg | TEXT | SIM | NULL | — | Mensagem de erro da última tentativa (para diagnóstico) |
