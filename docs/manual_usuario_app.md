# Manual do Operador — App KollectaOps

**Versão 1.0 | Datalogic Memor 11 | Android 11**

---

## Seção 1 — Primeiros Passos

### 1.1 Ligando o Coletor

1. Pressione o botão de energia (lateral direita) por 3 segundos.
2. Aguarde a inicialização do Android (10–20 segundos).
3. Desbloqueie a tela deslizando para cima (sem senha — operação industrial).

### 1.2 Conectando ao Wi-Fi do Depósito

1. Vá em **Configurações → Wi-Fi**.
2. Selecione a rede `KOLLECTA-PROD`.
3. Confirme a senha se solicitado (contate o supervisor).
4. O ícone de Wi-Fi deve aparecer na barra de status (sinal cheio).

### 1.3 Abrindo o App

- O ícone **KollectaOps** está na tela inicial do coletor.
- O app abre automaticamente se o dispositivo foi configurado como quiosque.
- O app verifica a sessão: se ainda válida, vai direto para o menu principal.

---

## Seção 2 — Login por Bipagem

### 2.1 Passo a Passo

**Tela azul com o texto "ACESSAR COLETOR"**

**Passo 1 — Supervisor:**
1. O campo superior está ativo (borda brilhando).
2. O **supervisor** aponta o scanner para o código de barras do seu crachá.
3. Pressione o gatilho lateral ou traseiro do coletor.
4. O campo exibe o código lido — aguarde a validação (menos de 1 segundo).

**Passo 2 — Operador:**
1. Após o supervisor ser validado, o campo do operador aparece automaticamente.
2. O **operador** bipa seu próprio crachá.
3. O sistema exibe "Bem-vindo, [Nome]" e entra no menu principal.

### 2.2 Possíveis Mensagens de Erro

| Mensagem | Causa | Solução |
|---|---|---|
| "Usuário inativo" | Crachá desativado no sistema | Contate o supervisor ou administrador |
| "Crachá não tem perfil de supervisor" | Operador tentou bipar no campo do supervisor | Usar o crachá correto |
| "Não encontrado" | Crachá não cadastrado | Cadastrar no painel web |
| "Sem conexão com o servidor" | Wi-Fi desconectado | Verificar conexão Wi-Fi |

---

## Seção 3 — Realizando o Picking

### 3.1 Abrindo uma Caixa

1. Na Home, toque em **PICKING**.
2. A câmera de scan ativa automaticamente.
3. Bipe o código de barras da **papeleta** colada na caixa.
4. O sistema mostra o primeiro item para coletar.

> Se a papeleta indicar uma caixa já finalizada, um aviso será exibido.  
> Se for uma caixa parcial (picking incompleto de um turno anterior), o sistema pergunta se deseja continuar.

### 3.2 A Tela de Coleta

A tela de coleta mostra as informações essenciais:

```
CAIXA 06772401          PROGRESSO 2/8

REF: 1000079   COR: ÚNICO   TAM: 18

        ┌─────────────────────┐
        │      A02.01.4A      │   ← Vá até este endereço
        └─────────────────────┘

            QUANTIDADE: 3           ← Pegue 3 peças

Últimas leituras:
✓ 042445140570394...  09:14
✓ 042445140570394...  09:13
```

**Como proceder:**
1. Vá até o endereço exibido em grande (**A02.01.4A**).
2. Procure as peças com a etiqueta correspondente à referência + cor + tamanho.
3. Bipe cada peça individualmente.

### 3.3 Interpretando os Feedbacks

| Feedback Visual | Som | Vibração | Significado |
|---|---|---|---|
| Tela flash VERDE (rápido) | 1 bipe | Curta | Peça aceita, ainda faltam mais |
| Tela flash VERDE (longo) + "SKU COMPLETA →" | 2 bipes | Curta | SKU completou, próximo endereço |
| Tela flash VERMELHO | Bipe contínuo | Longa | Erro — leia a mensagem de erro |
| Modal "CAIXA FINALIZADA" + LED | 3 tons | — | Caixa concluída |

### 3.4 Pular um Item em Falta

Se uma peça não está no endereço indicado:

1. Toque em **PULAR SKU**.
2. O sistema pergunta "Retirar item em falta?". Toque **Sim**.
3. O item é marcado como "em falta" e o sistema avança para o próximo.
4. O sistema pode sugerir outros endereços com o mesmo SKU — toque **Ver posições** se quiser verificar.

### 3.5 Salvar e Continuar Depois

Se precisar pausar o picking:

1. Toque em **SALVAR**.
2. Confirme: "Salvar e continuar depois?".
3. A caixa fica como "parcial" e você volta ao menu.
4. Na próxima vez que bipar a mesma papeleta, o sistema continua de onde parou.

### 3.6 Finalização da Caixa

Quando todos os itens forem coletados (ou marcados em falta), o sistema:
- Exibe o modal **"CAIXA FINALIZADA"** em tela cheia.
- Emite 3 tons sonoros.
- Aguarda sua confirmação com **OK**.

---

## Seção 4 — Módulo Inventário

### 4.1 Abrindo um Inventário

1. Na Home, toque em **INVENTÁRIO**.
2. A lista exibe os inventários abertos para sua filial.
3. Toque no inventário desejado.

### 4.2 Bipando Bens

1. A tela exibe: Contador "Lidos X / Esperados Y" + campo de bipagem + histórico de leituras.
2. Aponte o scanner para o código de patrimônio do bem (etiqueta geralmente na parte traseira ou inferior).
3. Bipe o código.

**Resultado da bipagem:**
| Cor da linha | Significado |
|---|---|
| Verde — "BEM OK" | Bem encontrado, localização confere |
| Amarelo — "LOCALIZAÇÃO DIVERGENTE" | Bem encontrado, mas está em lugar diferente do cadastro. Confirmar atualização? |
| Vermelho — "BEM NÃO CADASTRADO" | Código não existe no sistema. Toque "Cadastrar agora" |

### 4.3 Cadastrando um Novo Bem

Se o bem não estiver cadastrado:
1. Toque em **Cadastrar agora**.
2. Preencha: Código (já preenchido do scan), Descrição, Localização, Situação.
3. Opcionalmente: foto (toque "Tirar foto").
4. Toque **SALVAR**.

---

## Seção 5 — Sincronização

### 5.1 Quando Sincronizar

O coletor sincroniza automaticamente a cada 5 minutos quando conectado ao Wi-Fi. Você não precisa fazer nada.

Para sincronizar manualmente:
1. Na Home, toque em **SINCRO**.
2. Toque **Sincronizar tudo**.
3. Aguarde a barra de progresso completar.

### 5.2 Verificando Pendências

O ícone **SINCRO** na Home exibe um contador se houver bipagens pendentes (ex: "12"). Isso é normal durante a coleta — os dados serão enviados ao servidor na próxima sincronização.

### 5.3 Trabalho Offline

Se o Wi-Fi cair, o coletor continua funcionando normalmente. Todas as bipagens são salvas localmente. Ao reconectar, os dados são enviados automaticamente.

O indicador **OFFLINE** (em vermelho) aparece no cabeçalho quando sem rede.

---

## Seção 6 — Solução de Problemas

| Problema | Solução |
|---|---|
| Scanner não bipa (LED não acende) | Verifique se o app está na tela de bipagem correta. Fora das telas autorizadas, o laser fica desativado. |
| App exibe "OFFLINE" em vermelho | Verifique o Wi-Fi nas configurações do Android. Reconecte à rede KOLLECTA-PROD. |
| Bipagem não é reconhecida | Certifique-se de que a etiqueta não está amassada ou coberta. Bipe de distância 10–20 cm. |
| Tela congela | Pressione o botão home do Android e abra o app novamente. Os dados estão salvos localmente. |
| Peça aceita mas não deveria | Informe ao supervisor — pode ser divergência de cadastro. Use a tela de Diagnóstico para ver os logs. |
| Bateria baixa | O WorkManager pausa a sync automática com < 20% de bateria. Conecte o carregador ou troque o coletor. |
| "Sessão expirada" ao voltar | Normal após 60 minutos de inatividade. Refaça o login por bipagem. |
