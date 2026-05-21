# Manual do Gestor — Painel Web KollectaOps

**Versão 1.0 | Acesso via Navegador (Chrome/Firefox)**

---

## Seção 1 — Acesso e Login

### 1.1 Acessando o Painel

1. Abra o Chrome ou Firefox.
2. Acesse: `https://painel.kollectaops.com.br` (ou o endereço configurado pela equipe).
3. A tela de login é exibida automaticamente.

### 1.2 Fazendo Login

1. Insira seu **código de usuário** (ex: GEST01) ou **e-mail** no campo superior.
2. Insira sua **senha**.
3. Clique em **ENTRAR**.

> Credenciais de homologação: código `ADMIN01`, senha `Kolecta@2024`

### 1.3 Navegação

O menu lateral esquerdo (barra azul-petróleo) organiza as seções:
- **Dashboard** — Visão geral em tempo real
- **Picking** → Caixas
- **Inventário** → Bens, Inventários
- **Relatórios** → Produtividade, Divergências, Caixas Parciais
- **Cadastros** → SKUs, Endereços, Usuários, Filiais
- **Integrações**

---

## Seção 2 — Dashboard

### 2.1 Interpretando os Indicadores

Os 4 cards no topo atualizam automaticamente a cada 30 segundos:

| Card | O que significa |
|---|---|
| **Caixas finalizadas hoje** | Total de caixas com status FINALIZADA desde 00h do dia atual |
| **Em picking agora** | Caixas com status EM_PICKING neste momento |
| **Caixas parciais** | Caixas com status PARCIAL (iniciadas mas não finalizadas) |
| **Operadores ativos** | Coletores com sessão aberta (não encerrada) |

### 2.2 Tabela de Últimas Caixas

A tabela abaixo dos cards exibe as 20 últimas caixas finalizadas com colunas:
- **Papeleta**: código da caixa
- **Cliente**: destinatário do pedido
- **Status**: badge colorido (verde = finalizada, amarelo = parcial, etc.)
- **Abertura / Finalização**: horário de início e conclusão

A tabela atualiza a cada 30 segundos. Clique em qualquer linha para ver o detalhe.

---

## Seção 3 — Monitorando o Picking

### 3.1 Lista de Caixas

Acesse **Picking → Caixas**.

**Filtros disponíveis (em linha):**
- **Busca**: pesquisa por papeleta ou nome do cliente
- **Status**: filtra por AGUARDANDO, EM_PICKING, PARCIAL, FINALIZADA, CANCELADA

A lista pagina 20 registros por vez. Use os botões **Anterior / Próxima** para navegar.

**Cores dos status:**
| Badge | Cor | Significado |
|---|---|---|
| AGUARDANDO | Cinza | Caixa ainda não iniciada |
| EM_PICKING | Azul | Operador coletando agora |
| PARCIAL | Amarelo | Coleta incompleta (turno anterior) |
| FINALIZADA | Verde | Todos os itens coletados |
| CANCELADA | Vermelho | Caixa cancelada |

### 3.2 Detalhe de Caixa

Clique em qualquer linha da lista para abrir o detalhe completo:

**Header:** papeleta, status badge, cliente, OP
**Grid de informações:** operador, turno, data de abertura e finalização
**Tabela de itens:** SKU (referência/cor/tamanho), endereço sugerido, qtde solicitada, qtde coletada, status por item
**Timeline de eventos:** lista cronológica mostrando cada ação do operador (bipagem, pular, salvar, etc.)

---

## Seção 4 — Módulo Inventário

### 4.1 Lista de Bens

Acesse **Inventário → Bens**.

**Filtros:** busca por código ou descrição, situação (ATIVO, EM_MANUTENCAO, BAIXADO, NAO_LOCALIZADO).

**Exportar CSV:** clique no botão "Exportar CSV" no topo direito. O arquivo é compatível com Excel (separador ponto-e-vírgula, BOM UTF-8).

### 4.2 Inventários

Acesse **Inventário → Inventários** para ver todos os inventários criados, com status e data de início.

---

## Seção 5 — Relatórios

### 5.1 Produtividade

Acesse **Relatórios → Produtividade**.

**Filtros:** período (data início → data fim).

O relatório exibirá métricas de produção por operador: total de peças bipadas, caixas finalizadas, itens em falta.

### 5.2 Divergências de Inventário

Acesse **Relatórios → Divergências**.

Lista todos os bens encontrados em localização diferente do cadastrado, com data da leitura e operador responsável.

### 5.3 Caixas Parciais

Acesse **Relatórios → Caixas Parciais**.

Exibe caixas com picking não concluído, os itens em falta e a data da última atividade.

---

## Seção 6 — Cadastros

Cada cadastro segue o mesmo padrão:
1. Tabela com busca e paginação.
2. Botão **+ Novo** para cadastrar.
3. Ícone de lápis na linha para editar.
4. Ícone de toggle para ativar/desativar (soft delete — não apaga definitivamente).

### 6.1 SKUs

Campos: Referência, Cor, Tamanho, Descrição, Código EAN, Ativo.

> A combinação Referência + Cor + Tamanho deve ser única.

### 6.2 Endereços de Estoque

Campos: Código (ex: C37.09.6B), Andar/Rua, Seção, Posição/Nível, Ativo.

### 6.3 Usuários

Campos: Código Crachá, Nome, E-mail, Perfil (ADMIN/GESTOR/SUPERVISOR/OPERADOR), Filiais (seleção múltipla), Ativo.

> A senha inicial deve ser comunicada ao usuário separadamente. Ela pode ser redefinida pelo administrador.

### 6.4 Filiais

Campos: Código, Nome, Endereço, Ativo.

---

## Seção 7 — Exportações

O painel suporta exportação de tabelas no formato **CSV** (compatível com Excel brasileiro):

1. Nas telas de Bens e Relatórios, localize o botão **"Exportar CSV"** no topo direito.
2. Clique — o arquivo é baixado imediatamente.
3. Abra no Excel: os separadores são ponto-e-vírgula (`;`), compatível com configuração regional brasileira.

> Para abrir corretamente no Excel: **Dados → De Texto/CSV → Selecionar o arquivo → Delimitador: Ponto e vírgula → OK**.
