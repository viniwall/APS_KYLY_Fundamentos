# KollectaOps — Plataforma de Operações de Chão

**APS A2 — Fundamentos de Sistemas de Informação — UNIFEBE**  
Equipe: Vinicius Imhof Waldrigues | 2026

---

## Visão Geral

O **KollectaOps** é uma plataforma de operações de chão de fábrica composta por três componentes integrados:

```
┌─────────────────────────┐        ┌──────────────────────────┐
│  App Android (Kotlin)   │        │   Painel Web (React/TS)  │
│  Datalogic Memor 11     │        │   Desktop-first SPA      │
│  Min SDK 24 / Target 34 │        │   Vercel (deploy)        │
└───────────┬─────────────┘        └──────────┬───────────────┘
            │ Wi-Fi depósito                   │ HTTPS
            │ JSON REST                        │ JSON REST
            ▼                                  ▼
┌─────────────────────────────────────────────────────────────┐
│           Backend REST — Java 17 + Spring Boot 3.2          │
│           VPS Hostinger / Railway                           │
│           /v1/** (JWT HS256, BCrypt, Flyway)                │
└─────────────────────────┬───────────────────────────────────┘
                          │ JDBC / HikariCP
                          ▼
              ┌────────────────────────┐
              │  MySQL 8 (Hostinger)   │
              │  Host: 45.132.157.7    │
              │  Database: picking     │
              └────────────────────────┘
```

**Módulos:**
- **PICKING** (~80%): Substitui o sistema de picking de expedição têxtil em coletores Windows CE. Fluxo 100% por bipagem de scanner, offline-first, feedback sonoro + luminoso.
- **INVENTÁRIO PATRIMONIAL** (~20%): Demonstra a extensibilidade da plataforma — mesmo APK, mesmo backend, mesmo design system.

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| JDK | 17 |
| Maven | 3.9+ |
| Node.js | 20 LTS |
| Android Studio | Hedgehog (2023.1.1) |
| Gradle | 8.2+ |
| MySQL | 8.0+ |
| Docker | 24+ (opcional) |

---

## Estrutura de Diretórios

```
kollectaops/
├── README.md
├── docs/
│   ├── dicionario_de_dados.md
│   ├── mer.md                 (Mermaid erDiagram)
│   ├── arquitetura.md         (Mermaid graph)
│   ├── fluxo_picking.md       (Mermaid flowchart)
│   ├── casos_de_uso.md
│   ├── diagrama_classes.md
│   ├── infraestrutura.md
│   ├── datawedge_setup.md
│   ├── manual_usuario_app.md
│   └── manual_usuario_web.md
├── backend/                   (Spring Boot 3.2 / Java 17)
│   ├── src/main/java/br/com/kollectaops/api/
│   ├── src/main/resources/db/migration/
│   ├── pom.xml
│   ├── Dockerfile
│   └── .env.example
├── app-android/               (Kotlin + XML Views)
│   ├── app/src/main/
│   ├── app/build.gradle.kts
│   └── settings.gradle.kts
└── web-admin/                 (React 18 + Vite + TypeScript)
    ├── src/
    ├── package.json
    └── vite.config.ts
```

---

## Como Buildar

### Backend

```bash
cd backend

# Copiar variáveis de ambiente
cp .env.example .env
# Preencher DB_HOST, DB_USERNAME, DB_PASSWORD, JWT_SECRET

# Build (pula testes para ambiente sem banco)
mvn clean package -DskipTests

# Rodar localmente (com banco configurado)
java -jar target/kollectaops-api-1.0.0.jar
```

A API ficará em `http://localhost:8080`. Swagger UI em `http://localhost:8080/swagger-ui.html`.

### App Android

```bash
cd app-android

# Build de debug (emulador / USB)
./gradlew assembleDebug

# APK gerado em:
# app/build/outputs/apk/debug/app-debug.apk

# Instalar via ADB
adb install app/build/outputs/apk/debug/app-debug.apk
```

Para build de release, configure o keystore em `app/build.gradle.kts` e:
```bash
./gradlew assembleRelease
```

### Painel Web

```bash
cd web-admin

npm install

# Desenvolvimento local
echo "VITE_API_URL=http://localhost:8080" > .env.local
npm run dev
# Acesse http://localhost:5173

# Build de produção
npm run build
# Pasta dist/ pronta para Vercel
```

---

## Variáveis de Ambiente (Backend)

Copie `.env.example` para `.env` e preencha:

```env
DB_HOST=45.132.157.7
DB_PORT=3306
DB_NAME=picking
DB_USERNAME=<usuario_mysql>
DB_PASSWORD=<senha_mysql>
JWT_SECRET=<string_aleatoria_min_32_chars>
JWT_EXPIRATION_MINUTES=480
CORS_ORIGINS=http://localhost:5173,https://painel.kollectaops.com.br
PORT=8080
```

**NUNCA commitar o arquivo `.env` real. O `.gitignore` já o exclui.**

---

## Docker Compose (desenvolvimento local)

```yaml
# docker-compose.yml (criar na raiz)
version: '3.9'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: picking
      MYSQL_ROOT_PASSWORD: root_local
      MYSQL_USER: kollecta
      MYSQL_PASSWORD: kollecta_local
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      DB_HOST: mysql
      DB_PORT: 3306
      DB_NAME: picking
      DB_USERNAME: kollecta
      DB_PASSWORD: kollecta_local
      JWT_SECRET: dev_secret_min_32_characters_here
      CORS_ORIGINS: http://localhost:5173
    depends_on:
      - mysql

volumes:
  mysql_data:
```

```bash
docker-compose up -d
```

---

## Configurar o Datalogic Memor 11

O coletor deve estar configurado para enviar leituras via **intent broadcast**:

1. Abra o app **Scan2Deploy** ou **Datalogic Settings**.
2. Vá em **Scanner Settings → Intent Output**.
3. Configure:
   - **Action**: `com.datalogic.decode.action.DECODE_ACTION`
   - **Extra data**: `com.datalogic.decode.intentwedge.barcode_string`
   - **Category**: `android.intent.category.DEFAULT`
4. O app `br.com.kollectaops.collector` captura esse intent automaticamente via `ScannerReceiver`.

Detalhes completos em [`docs/datawedge_setup.md`](docs/datawedge_setup.md).

---

## Credenciais de Homologação

Apenas credenciais de **teste** (nunca use em produção sem alterar senhas):

| Campo | Valor |
|---|---|
| Crachá Supervisor | `SUP001` |
| Crachá Operador | `OP001` |
| Papeleta teste | `06772401` |
| Login web admin | `ADMIN01` |
| Senha padrão | `Kolecta@2024` |
| URL Swagger | `http://localhost:8080/swagger-ui.html` |

---

## Subir em Produção (VPS Hostinger)

```bash
# No servidor Ubuntu 22.04
sudo apt install openjdk-17-jre-headless -y

# Upload do JAR
scp backend/target/kollectaops-api-1.0.0.jar user@servidor:/opt/kollectaops/

# Criar systemd service
sudo nano /etc/systemd/system/kollectaops.service
```

```ini
[Unit]
Description=KollectaOps API
After=network.target

[Service]
User=kollecta
WorkingDirectory=/opt/kollectaops
EnvironmentFile=/opt/kollectaops/.env
ExecStart=/usr/bin/java -jar /opt/kollectaops/kollectaops-api-1.0.0.jar
Restart=always

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl enable kollectaops
sudo systemctl start kollectaops
```

---

## Painel Web no Vercel

1. Fazer push do repositório para GitHub.
2. No Vercel: Import → selecionar `web-admin/` como root.
3. Adicionar variável de ambiente: `VITE_API_URL=https://api.kollectaops.com.br`.
4. Deploy automático a cada push na branch `main`.

---

## Documentação

| Documento | Descrição |
|---|---|
| [`docs/dicionario_de_dados.md`](docs/dicionario_de_dados.md) | Todos os campos de todas as tabelas |
| [`docs/mer.md`](docs/mer.md) | Diagrama Entidade-Relacionamento (Mermaid) |
| [`docs/arquitetura.md`](docs/arquitetura.md) | Diagrama de componentes (Mermaid) |
| [`docs/fluxo_picking.md`](docs/fluxo_picking.md) | Fluxo BPMN do picking (Mermaid) |
| [`docs/casos_de_uso.md`](docs/casos_de_uso.md) | UML de casos de uso |
| [`docs/diagrama_classes.md`](docs/diagrama_classes.md) | Classes do domínio (Mermaid) |
| [`docs/infraestrutura.md`](docs/infraestrutura.md) | Hardware, rede, servidores |
| [`docs/datawedge_setup.md`](docs/datawedge_setup.md) | Config de scanner por fabricante |
| [`docs/manual_usuario_app.md`](docs/manual_usuario_app.md) | Manual do operador |
| [`docs/manual_usuario_web.md`](docs/manual_usuario_web.md) | Manual do gestor |

---

*KollectaOps — UNIFEBE — Fundamentos de Sistemas de Informação — APS A2 — 2026*
