#!/bin/bash
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$PROJECT_DIR/backend"
WEBADMIN_DIR="$PROJECT_DIR/web-admin"
LOGS_DIR="$PROJECT_DIR/logs"

mkdir -p "$LOGS_DIR"

# ── Backend ────────────────────────────────────────────────────────────────
if pgrep -f "kollectaops-api" > /dev/null; then
    echo "[backend] já está rodando (PID $(pgrep -f kollectaops-api))"
else
    if [ ! -f "$BACKEND_DIR/.env" ]; then
        echo "[backend] ERRO: arquivo .env não encontrado em $BACKEND_DIR"
        exit 1
    fi
    if [ ! -f "$BACKEND_DIR/target/kollectaops-api-1.0.0.jar" ]; then
        echo "[backend] ERRO: JAR não encontrado. Compile primeiro:"
        echo "  cd backend && JAVA_HOME=/tmp/jdk-21.0.5+11 /tmp/apache-maven-3.9.9/bin/mvn package -DskipTests"
        exit 1
    fi

    export $(grep -v '^#' "$BACKEND_DIR/.env" | xargs)
    nohup /tmp/jdk-21.0.5+11/bin/java -jar "$BACKEND_DIR/target/kollectaops-api-1.0.0.jar" \
        > "$LOGS_DIR/backend.log" 2>&1 &
    echo "[backend] iniciado (PID $!) — logs: logs/backend.log"
fi

# ── Web Admin ──────────────────────────────────────────────────────────────
if pgrep -f "vite --host" > /dev/null; then
    echo "[web-admin] já está rodando (PID $(pgrep -f 'vite --host'))"
else
    cd "$WEBADMIN_DIR"
    nohup npm run dev -- --host > "$LOGS_DIR/webadmin.log" 2>&1 &
    echo "[web-admin] iniciado (PID $!) — logs: logs/webadmin.log"
fi

echo ""
echo "Aguarde ~15 segundos e acesse:"
echo "  Web Admin → http://192.168.6.19:5173"
echo "  API       → http://192.168.6.19:8080"
