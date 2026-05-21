-- KollectaOps – Schema Inicial
-- V1__create_schema.sql
-- Banco: MySQL 8  |  Charset: utf8mb4

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------------------------------------------
-- SHARED
-- ----------------------------------------------------------------

CREATE TABLE IF NOT EXISTS usuario (
    id             BIGINT         NOT NULL AUTO_INCREMENT,
    codigo_cracha  VARCHAR(50)    NOT NULL,
    nome           VARCHAR(150)   NOT NULL,
    email          VARCHAR(200)   NULL,
    senha_hash     VARCHAR(255)   NOT NULL,
    perfil         ENUM('ADMIN','GESTOR','SUPERVISOR','OPERADOR') NOT NULL DEFAULT 'OPERADOR',
    ativo          TINYINT(1)     NOT NULL DEFAULT 1,
    criado_em      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario_cracha (codigo_cracha),
    UNIQUE KEY uk_usuario_email  (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS filial (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    codigo   VARCHAR(20)  NOT NULL,
    nome     VARCHAR(150) NOT NULL,
    endereco TEXT         NULL,
    ativo    TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uk_filial_codigo (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS usuario_filial (
    usuario_id BIGINT NOT NULL,
    filial_id  BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, filial_id),
    CONSTRAINT fk_uf_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_uf_filial  FOREIGN KEY (filial_id)  REFERENCES filial(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS turno (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    codigo      VARCHAR(20) NOT NULL,
    nome        VARCHAR(100) NOT NULL,
    hora_inicio TIME        NOT NULL,
    hora_fim    TIME        NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_turno_codigo (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sessao_coletor (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    supervisor_id       BIGINT       NOT NULL,
    operador_id         BIGINT       NOT NULL,
    turno_id            BIGINT       NULL,
    coletor_serial      VARCHAR(100) NULL,
    aberta_em           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    encerrada_em        DATETIME     NULL,
    motivo_encerramento VARCHAR(255) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_sc_supervisor FOREIGN KEY (supervisor_id) REFERENCES usuario(id),
    CONSTRAINT fk_sc_operador   FOREIGN KEY (operador_id)   REFERENCES usuario(id),
    CONSTRAINT fk_sc_turno      FOREIGN KEY (turno_id)      REFERENCES turno(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------
-- PICKING
-- ----------------------------------------------------------------

CREATE TABLE IF NOT EXISTS pedido (
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    numero_pedido VARCHAR(50) NOT NULL,
    cliente_nome  VARCHAR(200) NULL,
    cliente_doc   VARCHAR(30)  NULL,
    criado_em     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status        ENUM('ABERTO','EM_PICKING','FINALIZADO','CANCELADO') NOT NULL DEFAULT 'ABERTO',
    PRIMARY KEY (id),
    UNIQUE KEY uk_pedido_numero (numero_pedido)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS caixa (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    codigo_papeleta     VARCHAR(50)     NOT NULL,
    numero_op           VARCHAR(50)     NULL,
    pedido_id           BIGINT          NULL,
    tipo_caixa          VARCHAR(50)     NULL,
    peso_bruto          DECIMAL(8,3)    NULL,
    cor_tarja           ENUM('PADRAO','EXPORTACAO_AZUL','TAG_VERDE','TAG_ROSA','MULTI_AMARELO','VERMELHA') NOT NULL DEFAULT 'PADRAO',
    sequencia           INT             NULL,
    total_caixas_pedido INT             NULL,
    status              ENUM('AGUARDANDO','EM_PICKING','PARCIAL','FINALIZADA','CANCELADA') NOT NULL DEFAULT 'AGUARDANDO',
    aberta_em           DATETIME        NULL,
    finalizada_em       DATETIME        NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_caixa_papeleta (codigo_papeleta),
    CONSTRAINT fk_caixa_pedido FOREIGN KEY (pedido_id) REFERENCES pedido(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sku (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    referencia VARCHAR(50)  NOT NULL,
    cor        VARCHAR(50)  NOT NULL,
    tamanho    VARCHAR(20)  NOT NULL,
    descricao  VARCHAR(200) NULL,
    codigo_ean VARCHAR(30)  NULL,
    ativo      TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uk_sku_ref_cor_tam (referencia, cor, tamanho),
    KEY idx_sku_ean (codigo_ean)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS endereco_estoque (
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    codigo        VARCHAR(30) NOT NULL,
    andar_rua     VARCHAR(10) NULL,
    secao         VARCHAR(10) NULL,
    posicao_nivel VARCHAR(10) NULL,
    ativo         TINYINT(1)  NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uk_endereco_codigo (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS estoque (
    sku_id       BIGINT   NOT NULL,
    endereco_id  BIGINT   NOT NULL,
    quantidade   INT      NOT NULL DEFAULT 0,
    atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (sku_id, endereco_id),
    CONSTRAINT fk_est_sku      FOREIGN KEY (sku_id)      REFERENCES sku(id),
    CONSTRAINT fk_est_endereco FOREIGN KEY (endereco_id) REFERENCES endereco_estoque(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS item_caixa (
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    caixa_id            BIGINT NOT NULL,
    sku_id              BIGINT NOT NULL,
    endereco_sugerido_id BIGINT NULL,
    qtde_solicitada     INT    NOT NULL DEFAULT 0,
    qtde_coletada       INT    NOT NULL DEFAULT 0,
    status              ENUM('PENDENTE','EM_COLETA','COMPLETO','EM_FALTA','DIVERGENTE') NOT NULL DEFAULT 'PENDENTE',
    ordem_picking       INT    NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_ic_caixa (caixa_id),
    KEY idx_ic_sku   (sku_id),
    CONSTRAINT fk_ic_caixa    FOREIGN KEY (caixa_id)             REFERENCES caixa(id),
    CONSTRAINT fk_ic_sku      FOREIGN KEY (sku_id)               REFERENCES sku(id),
    CONSTRAINT fk_ic_endereco FOREIGN KEY (endereco_sugerido_id) REFERENCES endereco_estoque(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS peca (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    codigo_unico VARCHAR(100) NOT NULL,
    sku_id       BIGINT       NOT NULL,
    status       ENUM('DISPONIVEL','BIPADA','EM_CAIXA','DIVERGENTE') NOT NULL DEFAULT 'DISPONIVEL',
    bipada_em    DATETIME     NULL,
    bipada_por_id BIGINT      NULL,
    item_caixa_id BIGINT      NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_peca_codigo (codigo_unico),
    KEY idx_peca_sku       (sku_id),
    KEY idx_peca_item      (item_caixa_id),
    CONSTRAINT fk_peca_sku        FOREIGN KEY (sku_id)        REFERENCES sku(id),
    CONSTRAINT fk_peca_operador   FOREIGN KEY (bipada_por_id) REFERENCES usuario(id),
    CONSTRAINT fk_peca_item_caixa FOREIGN KEY (item_caixa_id) REFERENCES item_caixa(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS evento_picking (
    id           BIGINT   NOT NULL AUTO_INCREMENT,
    sessao_id    BIGINT   NULL,
    caixa_id     BIGINT   NOT NULL,
    item_caixa_id BIGINT  NULL,
    peca_id      BIGINT   NULL,
    tipo         ENUM('ABRIR_CAIXA','BIPAR_OK','BIPAR_OK_SKU_COMPLETA',
                      'BIPAR_ERRO_NAO_PERTENCE','BIPAR_ERRO_SEM_SALDO',
                      'PULAR_ITEM','CONSULTAR_OUTRAS_POSICOES',
                      'SALVAR_PARCIAL','FINALIZAR_CAIXA','REABRIR_CAIXA') NOT NULL,
    mensagem     VARCHAR(500) NULL,
    ocorrido_em  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sincronizado TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_ep_caixa   (caixa_id),
    KEY idx_ep_sessao  (sessao_id),
    KEY idx_ep_sync    (sincronizado),
    CONSTRAINT fk_ep_sessao    FOREIGN KEY (sessao_id)     REFERENCES sessao_coletor(id),
    CONSTRAINT fk_ep_caixa     FOREIGN KEY (caixa_id)      REFERENCES caixa(id),
    CONSTRAINT fk_ep_item      FOREIGN KEY (item_caixa_id) REFERENCES item_caixa(id),
    CONSTRAINT fk_ep_peca      FOREIGN KEY (peca_id)       REFERENCES peca(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------
-- INVENTÁRIO PATRIMONIAL
-- ----------------------------------------------------------------

CREATE TABLE IF NOT EXISTS localizacao_patrimonial (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    codigo    VARCHAR(30)  NOT NULL,
    nome      VARCHAR(150) NOT NULL,
    filial_id BIGINT       NULL,
    ativo     TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uk_loc_codigo (codigo),
    CONSTRAINT fk_loc_filial FOREIGN KEY (filial_id) REFERENCES filial(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS bem (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    codigo_patrimonio     VARCHAR(100) NOT NULL,
    descricao            VARCHAR(300) NULL,
    marca                VARCHAR(100) NULL,
    modelo               VARCHAR(100) NULL,
    serial               VARCHAR(100) NULL,
    situacao             ENUM('ATIVO','EM_MANUTENCAO','BAIXADO','NAO_LOCALIZADO') NOT NULL DEFAULT 'ATIVO',
    localizacao_atual_id BIGINT       NULL,
    foto_url             VARCHAR(500) NULL,
    observacao           TEXT         NULL,
    atualizado_em        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    atualizado_por_id    BIGINT       NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_bem_patrimonio (codigo_patrimonio),
    KEY idx_bem_localizacao (localizacao_atual_id),
    CONSTRAINT fk_bem_localizacao FOREIGN KEY (localizacao_atual_id) REFERENCES localizacao_patrimonial(id),
    CONSTRAINT fk_bem_operador    FOREIGN KEY (atualizado_por_id)    REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS inventario (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    filial_id     BIGINT       NOT NULL,
    descricao     VARCHAR(200) NULL,
    iniciado_em   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finalizado_em DATETIME     NULL,
    status        ENUM('ABERTO','EM_ANDAMENTO','FECHADO') NOT NULL DEFAULT 'ABERTO',
    criado_por_id BIGINT       NULL,
    PRIMARY KEY (id),
    KEY idx_inv_filial (filial_id),
    CONSTRAINT fk_inv_filial    FOREIGN KEY (filial_id)     REFERENCES filial(id),
    CONSTRAINT fk_inv_criado_por FOREIGN KEY (criado_por_id) REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS leitura_inventario (
    id                 BIGINT    NOT NULL AUTO_INCREMENT,
    inventario_id      BIGINT    NOT NULL,
    bem_id             BIGINT    NULL,
    localizacao_lida_id BIGINT   NULL,
    operador_id        BIGINT    NULL,
    lida_em            DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    divergencia        ENUM('NENHUMA','LOCALIZACAO','SITUACAO','NAO_CADASTRADO') NOT NULL DEFAULT 'NENHUMA',
    PRIMARY KEY (id),
    KEY idx_li_inventario (inventario_id),
    KEY idx_li_bem        (bem_id),
    CONSTRAINT fk_li_inventario  FOREIGN KEY (inventario_id)       REFERENCES inventario(id),
    CONSTRAINT fk_li_bem         FOREIGN KEY (bem_id)              REFERENCES bem(id),
    CONSTRAINT fk_li_localizacao FOREIGN KEY (localizacao_lida_id) REFERENCES localizacao_patrimonial(id),
    CONSTRAINT fk_li_operador    FOREIGN KEY (operador_id)         REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------
-- SYNC
-- ----------------------------------------------------------------

CREATE TABLE IF NOT EXISTS sync_envio (
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    dispositivo_id VARCHAR(100) NOT NULL,
    tabela        VARCHAR(50) NOT NULL,
    payload_json  MEDIUMTEXT  NOT NULL,
    criado_em     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    enviado_em    DATETIME    NULL,
    status        ENUM('PENDENTE','ENVIADO','FALHA') NOT NULL DEFAULT 'PENDENTE',
    tentativas    INT         NOT NULL DEFAULT 0,
    erro_msg      TEXT        NULL,
    PRIMARY KEY (id),
    KEY idx_se_status    (status),
    KEY idx_se_dispositivo (dispositivo_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
