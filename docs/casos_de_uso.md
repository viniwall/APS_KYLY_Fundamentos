# Casos de Uso — KollectaOps

## Atores

| Ator | Descrição |
|---|---|
| **OPERADOR** | Executa o picking ou inventário no coletor Android |
| **SUPERVISOR** | Autoriza a abertura de sessão via bipagem de crachá |
| **GESTOR** | Monitora operações em tempo real no painel web |
| **ADMIN** | Gerencia cadastros, usuários e configurações da plataforma |

---

## Módulo Picking (App Android)

### UC01 — Logar no Coletor

**Ator principal:** Supervisor + Operador  
**Pré-condição:** Ambos os usuários têm crachás ativos no sistema; Wi-Fi disponível (para validação online) ou sessão cacheada.  
**Pós-condição:** SessaoColetor criada; JWT emitido; operador acessa a Home.

**Fluxo principal:**
1. Operador abre o app — tela de login exibe campo de scanner.
2. Supervisor bipa seu crachá → sistema valida perfil (≠ OPERADOR) e status ativo.
3. Campo do operador se torna visível.
4. Operador bipa seu crachá → sistema valida status ativo.
5. SessaoColetor é criada no backend; token JWT é emitido e armazenado localmente.
6. App exibe "Bem-vindo, [Nome]" por 1 segundo e navega para a Home.

**Fluxos alternativos:**
- 2a. Crachá do supervisor inativo → mensagem de erro; laser reativado.
- 2b. Supervisor com perfil OPERADOR → "Crachá não tem perfil de supervisor"; laser reativado.
- 4a. Crachá do operador não encontrado → mensagem de erro; reinicia passo 3.

---

### UC02 — Abrir Caixa por Papeleta

**Ator principal:** Operador  
**Pré-condição:** Sessão válida; módulo Picking selecionado.  
**Pós-condição:** Caixa com status EM_PICKING; tela de coleta exibida no primeiro item.

**Fluxo principal:**
1. Operador bipa a papeleta da caixa.
2. Sistema localiza a caixa pelo código de barras da papeleta.
3. Se status = AGUARDANDO: muda para EM_PICKING, registra EVENTO ABRIR_CAIXA, abre na tela de coleta no primeiro item por ordemPicking.

**Fluxos alternativos:**
- 3a. Status = PARCIAL → modal "Caixa parcial — continuar coleta?". Ao confirmar, abre no próximo item pendente.
- 3b. Status = FINALIZADA → modal "Já finalizada em [data]"; operador confirma e retorna ao menu.
- 3c. Papeleta não encontrada → bipe de erro, mensagem, laser reativado.

---

### UC03 — Coletar Peça (Bipar Código)

**Ator principal:** Operador  
**Pré-condição:** Caixa aberta (EM_PICKING); item ativo na tela de coleta.  
**Pós-condição:** Peça registrada como EM_CAIXA; EventoPicking criado localmente.

**Fluxo principal:**
1. Operador bipa o código de barras da peça.
2. Sistema verifica se a peça pertence ao SKU do item atual (referência + cor + tamanho).
3. Sistema verifica se a peça já não foi bipada anteriormente.
4. Peça é aceita: status → EM_CAIXA; qtdeColetada++; histórico de leituras atualizado.
5. Flash verde (200ms) + 1 bipe + vibração curta.
6. Se qtdeColetada = qtdeSolicitada → SKU completa (ver UC03a).

**Fluxo UC03a — SKU Completa:**
1. Flash verde (400ms) + 2 bipes.
2. Exibe "SKU COMPLETA →".
3. Sistema avança para o próximo ItemCaixa pendente.
4. Se não há próximo: verifica finalização (ver UC07).

**Fluxos alternativos:**
- 2a. Peça não pertence ao SKU → flash vermelho, bipe contínuo 2s, modal "SKU não pertence à caixa".
- 3a. Peça já bipada → flash vermelho, bipe contínuo 2s, modal "Peça já bipada".

---

### UC04 — Pular Item em Falta

**Ator principal:** Operador  
**Pré-condição:** Item com status PENDENTE ou EM_COLETA na caixa ativa.  
**Pós-condição:** ItemCaixa.status = EM_FALTA; próximo item carregado.

**Fluxo principal:**
1. Operador toca "PULAR SKU".
2. Modal: "Retirar item em falta? [SKU]". Operador confirma.
3. Item marcado como EM_FALTA; EventoPicking PULAR_ITEM registrado.
4. Sistema sugere "Ver outras posições com esta SKU?" (Sim → UC05 / Não → continua).
5. Avança para próximo item pendente ou finaliza (UC07).

---

### UC05 — Consultar Outras Posições da SKU

**Ator principal:** Operador  
**Pré-condição:** Item ativo na coleta; tabela ESTOQUE com registros para o SKU.  
**Pós-condição:** Operador visualiza até 5 endereços alternativos com estoque disponível.

**Fluxo principal:**
1. Operador toca "VER POSIÇÕES".
2. Sistema busca até 5 EnderecoEstoque com quantidade > 0 para o SKU, ordenados por quantidade decrescente.
3. Tela exibe lista: endereço (bold), quantidade, indicador de proximidade.
4. Operador toca "Voltar à coleta".

---

### UC06 — Salvar Coleta Parcial

**Ator principal:** Operador  
**Pré-condição:** Caixa com pelo menos uma peça coletada ou iniciada.  
**Pós-condição:** Caixa com status PARCIAL; operador retorna à Home; dados preservados.

**Fluxo principal:**
1. Operador toca "SALVAR".
2. Modal: "Salvar e continuar depois? Sim/Não".
3. Confirmado: Caixa.status = PARCIAL; EventoPicking SALVAR_PARCIAL registrado.
4. Navega para Home. Dados de bipagem permanecem no coletor e serão sincronizados.

---

### UC07 — Finalizar Caixa

**Ator principal:** Sistema (automático) / Operador  
**Pré-condição:** Todos os itens da caixa estão COMPLETO ou EM_FALTA.  
**Pós-condição:** Caixa FINALIZADA (ou PARCIAL se houver EM_FALTA); operador na Home.

**Fluxo principal (automático):**
1. Após o último item ser completado, sistema detecta ausência de pendentes.
2. Modal "CAIXA FINALIZADA" + 3 bipes + LED azul/branco.
3. Operador confirma → Caixa.status = FINALIZADA; EventoPicking FINALIZAR_CAIXA registrado.
4. Navega para Home.

**Fluxo alternativo (com itens em falta):**
- Sistema detecta que todos os itens são COMPLETO ou EM_FALTA.
- Modal "CAIXA COM PICKING PARCIAL" + alerta sonoro.
- Caixa.status = PARCIAL.

---

### UC08 — Reabrir Caixa Parcial

**Ator principal:** Operador  
**Pré-condição:** Caixa com status PARCIAL.  
**Pós-condição:** Caixa em EM_PICKING; operador prossegue coleta a partir do próximo item pendente.

**Fluxo principal:**
1. Operador bipa a papeleta de uma caixa PARCIAL (UC02).
2. Modal "Caixa parcial — continuar coleta?". Operador confirma.
3. Sistema exibe a tela de coleta no primeiro item com status PENDENTE ou EM_COLETA.

---

### UC09 — Sincronizar com Servidor

**Ator principal:** Sistema (WorkManager) / Operador  
**Pré-condição:** Há EventoPicking com sincronizado = false no banco local.  
**Pós-condição:** Eventos enviados ao backend; marcados como sincronizados.

**Fluxo principal (automático):**
1. WorkManager executa a cada 5 minutos (com Wi-Fi e bateria > 20%).
2. Busca todos os EventoPicking locais com sincronizado = false.
3. Envia lote via POST /v1/sync/picking-events.
4. Backend processa e retorna {processados, erros}.
5. App marca eventos enviados com sincronizado = true.

**Fluxo alternativo (manual):**
- Operador acessa "SINCRO" na Home e toca "Sincronizar tudo".

---

## Módulo Inventário Patrimonial (App Android)

### UC10 — Iniciar Inventário

**Ator principal:** Gestor (via painel web) / Admin  
**Pré-condição:** Filial cadastrada e localização ativas.  
**Pós-condição:** Inventario com status ABERTO disponível para coleta no coletor.

### UC11 — Coletar Bem (Bipar Código de Patrimônio)

**Ator principal:** Operador  
**Pré-condição:** Inventário ABERTO disponível; operador na tela de coleta de inventário.  
**Pós-condição:** LeituraInventario registrada; divergência identificada se houver.

**Fluxo principal:**
1. Operador seleciona inventário na lista.
2. Bipa o código de patrimônio do bem.
3. Sistema localiza o Bem pelo código.
4. Compara localizacao_atual do Bem com a localização atual do operador.
5. Se coincide: LeituraInventario com divergencia = NENHUMA; linha verde "BEM OK".

**Fluxos alternativos:**
- 4a. Localização diverge → linha amarela "LOCALIZAÇÃO DIVERGENTE". Modal: "Atualizar para [local]? Sim/Não".
- 3a. Bem não cadastrado → linha vermelha "BEM NÃO CADASTRADO". Botão "Cadastrar agora" → UC13.

---

### UC12 — Registrar Divergência de Localização

**Ator principal:** Operador  
**Pré-condição:** Bem bipado com localização diferente do cadastro.  
**Pós-condição:** LeituraInventario com divergencia = LOCALIZACAO; gestor notificado no painel.

### UC13 — Cadastrar Novo Bem

**Ator principal:** Operador / Admin  
**Pré-condição:** Bem não encontrado no sistema.  
**Pós-condição:** Bem cadastrado; LeituraInventario com divergencia = NAO_CADASTRADO.

### UC14 — Consultar Bem

**Ator principal:** Operador / Gestor  
**Fluxo:** Busca por código ou descrição → exibe detalhe completo → opção de editar.

---

## Módulo Admin (Painel Web)

### UC15 — Visualizar Dashboard

**Ator principal:** Gestor / Admin  
**Pré-condição:** Usuário autenticado no painel web.  
**Fluxo:** Acessa /dashboard → 4 StatCards atualizam a cada 30s; tabela de caixas finalizadas hoje.

### UC16 — Monitorar Caixas em Picking

**Ator principal:** Gestor  
**Fluxo:** Acessa /picking/caixas → aplica filtros → lista paginada → clica na linha → detalhe.

### UC17 — Ver Timeline de Eventos da Caixa

**Ator principal:** Gestor  
**Fluxo:** Na BoxDetail, visualiza lista cronológica de EventoPicking (tipo, peça, hora).

### UC18 — Gerenciar Cadastros

**Ator principal:** Admin  
**Fluxo:** CRUD completo de Usuários, Filiais, SKUs, Endereços via tabelas paginadas.

### UC19 — Emitir Relatório de Produtividade

**Ator principal:** Gestor / Admin  
**Fluxo:** Filtros de período e operador → tabela de peças/hora e caixas/turno → exportar CSV.

### UC20 — Emitir Relatório de Divergências

**Ator principal:** Gestor / Admin  
**Fluxo:** Lista leituras de inventário com divergência ≠ NENHUMA → exportar CSV.
