# Configuração de Scanner por Fabricante — KollectaOps

## 1. Datalogic Memor 11 (principal)

O KollectaOps foi desenvolvido primariamente para o Datalogic Memor 11 com scanner SE4750.

### 1.1 Configuração via Scan2Deploy

1. No Memor 11, abra **Settings → Datalogic Settings → Scanner Settings**.
2. Ative **Intent Wedge**.
3. Configure:
   - **Intent Action**: `com.datalogic.decode.action.DECODE_ACTION`
   - **Intent Category**: `android.intent.category.DEFAULT`
   - **Delivery Mode**: Broadcast
   - **Extra barcode data**: `com.datalogic.decode.intentwedge.barcode_string`

4. Salve e reinicie o serviço de scanner.

### 1.2 Perfil de Ativação Automática

Para que o scanner ative automaticamente ao abrir o app:

1. Em **Scan2Deploy** → **App Association** → adicionar `br.com.kollectaops.collector`.
2. O KollectaOps gerencia o laser programaticamente via `ScannerService` — ativar somente nas telas autorizadas.

### 1.3 Intents Enviados

O KollectaOps registra um `BroadcastReceiver` para:

```xml
<receiver android:name=".domain.service.ScannerReceiver">
    <intent-filter>
        <action android:name="com.datalogic.decode.action.DECODE_ACTION"/>
    </intent-filter>
</receiver>
```

**Dados do intent:**

| Extra | Tipo | Conteúdo |
|---|---|---|
| `com.datalogic.decode.intentwedge.barcode_string` | String | Código de barras lido |
| `com.datalogic.decode.intentwedge.barcode_type` | String | Tipo (CODE_128, QR_CODE, etc.) |

### 1.4 Controle do Laser

O app habilita/desabilita o laser via:
- **Habilitar**: `Intent("com.datalogic.decode.action.ENABLE_SCAN")`
- **Desabilitar**: `Intent("com.datalogic.decode.action.DISABLE_SCAN")`

**Regra Kyly — laser APENAS habilitado em:**
1. LoginActivity (bipar crachá)
2. OpenBoxActivity (bipar papeleta)
3. CollectActivity (bipar peças)

---

## 2. Zebra TC2x / TC5x / TC7x (DataWedge)

### 2.1 Criar Perfil KollectaOps

1. Abra o app **DataWedge** no dispositivo.
2. Toque em **Profiles → New Profile** → nome: `KollectaOps`.
3. Em **Applications**:
   - Activity: `br.com.kollectaops.collector.*`
4. Em **Barcode Input**:
   - Habilite o plugin de scanner.
5. Em **Intent Output**:
   - **Enabled**: ✅
   - **Intent action**: `com.symbol.datawedge.api.RESULT_ACTION`
   - **Intent category**: `android.intent.category.DEFAULT`
   - **Intent delivery**: Broadcast Intent
   - **String data intent key**: `com.symbol.datawedge.data_string`

### 2.2 Dados do Intent

```java
// No ScannerService do KollectaOps
case "com.symbol.datawedge.api.RESULT_ACTION":
    return intent.getStringExtra("com.symbol.datawedge.data_string");
```

---

## 3. Honeywell (ScanPal / CT47)

### 3.1 Configuração do Scanner Service

1. Abra **Settings → Honeywell Settings → Scanning → Internal Scanner**.
2. Em **Profile settings**, crie o perfil `KollectaOps`.
3. Configure **Data Intent**:
   - **Intent Action**: `com.honeywell.intent.action.SCANNER_RESULT`
   - **Intent Category**: `android.intent.category.DEFAULT`
   - **Extra**: `data` (String)

### 3.2 Dados do Intent

```java
case "com.honeywell.intent.action.SCANNER_RESULT":
    return intent.getStringExtra("data");
```

---

## 4. Genérico / Smartphone (câmera)

Para testes em smartphones sem scanner físico, o KollectaOps aceita digitação manual no campo de bipagem — basta digitar o código e pressionar Enter.

Para leitura via câmera (modo desenvolvimento), é possível integrar o **ML Kit Barcode Scanning** no `build.gradle.kts`:

```kotlin
implementation("com.google.mlkit:barcode-scanning:17.2.0")
```

> **Nota:** A leitura por câmera é apenas para desenvolvimento e testes. Em produção no Memor 11, use exclusivamente o scanner laser físico.

---

## 5. Verificação Rápida

Para confirmar que o scanner está configurado corretamente:

1. Abra o KollectaOps → tela de **Diagnóstico** (toque longo no título da Home).
2. Bipe qualquer código de barras.
3. O código deve aparecer no log de diagnóstico em menos de 500ms.

Se não aparecer:
- Verifique se o perfil do scanner está associado ao pacote `br.com.kollectaops.collector`.
- Reinicie o serviço de scanner do dispositivo.
- Consulte os logs via `adb logcat -s KollectaOps:V`.
