# Infraestrutura — KollectaOps

## Requisitos Mínimos de Hardware

| Componente | Especificação Mínima | Recomendado |
|---|---|---|
| **Coletor industrial** | Datalogic Memor 11, Android 11, 3GB RAM, scanner SE4750, Wi-Fi 802.11ac | Memor 11 com SD card 32GB |
| **Smartphone (fallback)** | Android 8+, 2GB RAM, câmera traseira autofoco, Wi-Fi | Android 11+, câmera 12MP |
| **Estação admin (web)** | Chromium 120+ ou Firefox 120+, 1366×768 | 1920×1080, Chrome 122+ |
| **Servidor backend** | 2 vCPU, 2GB RAM, 20GB SSD, Ubuntu 22.04 LTS, Java 17 | 4 vCPU, 4GB RAM, SSD NVMe |
| **Banco de dados** | MySQL 8, 2GB RAM dedicado, 10GB storage | MySQL 8.0.32+, 4GB RAM, backups diários |
| **Rede do depósito** | Wi-Fi 802.11ac, cobertura ≥ -70dBm nos corredores | Wi-Fi 6 (802.11ax), SSID dedicado |

---

## Diagrama de Rede do Depósito

```
┌─────────────────────────────────────────────────────────────┐
│                    Depósito Kyly — Pomerode/SC              │
│                                                             │
│   [Coletor 1]  [Coletor 2]  [Coletor 3]  (Datalogic)       │
│        │            │            │                          │
│        └────────────┴────────────┘                          │
│                     │ Wi-Fi 5GHz (SSID: KOLLECTA-PROD)      │
│              ┌──────▼──────┐                                │
│              │  Wi-Fi AP 1  │  Ubiquiti UniFi / similar     │
│              │  (Área A)   │                                │
│              └──────┬──────┘                                │
│                     │ Cabeamento Cat6                       │
│              ┌──────▼──────┐                                │
│              │   Switch    │  Cisco SG350 ou similar        │
│              │  24 portas  │                                │
│              └──────┬──────┘                                │
│                     │                                       │
│              ┌──────▼──────┐                                │
│              │   Firewall  │  pfSense / MikroTik            │
│              └──────┬──────┘                                │
└─────────────────────┼───────────────────────────────────────┘
                      │ Internet Fibra ≥ 100Mbps
                      │
              ┌───────▼─────────────────────────┐
              │   VPS Hostinger (Ubuntu 22.04)   │
              │   IP: 45.132.157.7               │
              │   Spring Boot :8080              │
              │   Nginx (reverse proxy :443)     │
              │   Let's Encrypt SSL              │
              └───────────────┬─────────────────┘
                              │ TCP 3306 (privado)
                      ┌───────▼───────┐
                      │  MySQL 8      │
                      │  picking DB   │
                      └───────────────┘
```

---

## Configuração de Rede Recomendada

| Parâmetro | Valor recomendado |
|---|---|
| SSID dedicado | `KOLLECTA-PROD` (isolado de rede de escritório) |
| Banda | 5 GHz preferida (menos interferência no depósito) |
| Cobertura mínima | -70 dBm em todos os corredores |
| DHCP lease time | 8 horas (duração de um turno) |
| QoS | Priorizar tráfego da faixa IP dos coletores |
| Segurança | WPA3-Personal ou WPA2-Enterprise |

---

## Configuração do Servidor (Ubuntu 22.04)

### Java 17

```bash
sudo apt update && sudo apt install -y openjdk-17-jre-headless
java -version  # openjdk 17.x.x
```

### Nginx (Reverse Proxy)

```nginx
# /etc/nginx/sites-available/kollectaops
server {
    listen 80;
    server_name api.kollectaops.com.br;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.kollectaops.com.br;

    ssl_certificate /etc/letsencrypt/live/api.kollectaops.com.br/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.kollectaops.com.br/privkey.pem;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_read_timeout 30s;
        proxy_connect_timeout 10s;
    }
}
```

```bash
sudo certbot --nginx -d api.kollectaops.com.br
sudo systemctl reload nginx
```

---

## Docker Compose (Desenvolvimento Local)

```yaml
# docker-compose.yml
version: '3.9'

services:
  mysql:
    image: mysql:8.0
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: root_local
      MYSQL_DATABASE: picking
      MYSQL_USER: kollecta
      MYSQL_PASSWORD: kollecta_local
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

  backend:
    build: ./backend
    restart: unless-stopped
    ports:
      - "8080:8080"
    environment:
      DB_HOST: mysql
      DB_PORT: 3306
      DB_NAME: picking
      DB_USERNAME: kollecta
      DB_PASSWORD: kollecta_local
      JWT_SECRET: dev_secret_aqui_min_32_caracteres_ok
      CORS_ORIGINS: http://localhost:5173
    depends_on:
      - mysql
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 5s
      retries: 3

volumes:
  mysql_data:
```

---

## Backup do Banco de Dados

### Script de backup diário

```bash
#!/bin/bash
# /opt/kollectaops/backup.sh

BACKUP_DIR="/opt/kollectaops/backups"
DATE=$(date +%Y%m%d_%H%M%S)
FILE="$BACKUP_DIR/picking_$DATE.sql.gz"

mkdir -p $BACKUP_DIR

mysqldump \
  --host="${DB_HOST}" \
  --user="${DB_USERNAME}" \
  --password="${DB_PASSWORD}" \
  --single-transaction \
  --routines \
  --triggers \
  picking | gzip > "$FILE"

# Manter apenas os 7 backups mais recentes
ls -t $BACKUP_DIR/*.sql.gz | tail -n +8 | xargs -r rm

echo "Backup concluído: $FILE"
```

```bash
# Crontab: executar às 2h da manhã todo dia
0 2 * * * /opt/kollectaops/backup.sh >> /var/log/kollectaops-backup.log 2>&1
```

---

## Monitoramento

| Endpoint | Descrição |
|---|---|
| `GET /actuator/health` | Status de saúde da aplicação e banco |
| `GET /actuator/metrics` | Métricas JVM e HTTP |
| `GET /actuator/info` | Versão e informações do build |

Acesso ao actuator restrito a IP do servidor de monitoramento via Nginx.
