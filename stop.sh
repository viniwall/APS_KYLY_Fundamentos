#!/bin/bash
echo "Parando backend..."
pkill -f "kollectaops-api" && echo "  parado" || echo "  não estava rodando"

echo "Parando web-admin..."
pkill -f "vite --host" && echo "  parado" || echo "  não estava rodando"
