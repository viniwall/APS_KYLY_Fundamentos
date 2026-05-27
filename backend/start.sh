#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

if [ ! -f .env ]; then
  echo "Arquivo .env não encontrado. Crie-o com base em .env.example"
  exit 1
fi

export $(grep -v '^#' .env | xargs)

JAVA_BIN="${JAVA_HOME:-/tmp/jdk-21.0.5+11}/bin/java"
JAR="target/kollectaops-api-1.0.0.jar"

if [ ! -f "$JAR" ]; then
  echo "JAR não encontrado. Compile com: JAVA_HOME=/tmp/jdk-21.0.5+11 /tmp/apache-maven-3.9.9/bin/mvn package -DskipTests"
  exit 1
fi

echo "Iniciando KollectaOps API na porta ${PORT:-8080}..."
exec "$JAVA_BIN" -jar "$JAR"
