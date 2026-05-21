package br.com.kollectaops.collector.domain.service

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerencia a integração com o scanner laser.
 *
 * Regra Kyly: laser só habilitado em telas de login, abertura de caixa e coleta de peças.
 * Após cada leitura bem-sucedida, deve chamar disableScanner().
 */
@Singleton
class ScannerService @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        // Datalogic
        const val DATALOGIC_ACTION_DECODE = "com.datalogic.decode.action.DECODE_ACTION"
        const val DATALOGIC_EXTRA_DATA = "com.datalogic.decode.intentwedge.barcode_string"
        const val DATALOGIC_ENABLE_ACTION = "com.datalogic.decode.action.ENABLE_SCAN"
        const val DATALOGIC_DISABLE_ACTION = "com.datalogic.decode.action.DISABLE_SCAN"

        // Zebra DataWedge
        const val ZEBRA_ACTION = "com.symbol.datawedge.api.RESULT_ACTION"
        const val ZEBRA_EXTRA_DATA = "com.symbol.datawedge.data_string"

        // Honeywell
        const val HONEYWELL_ACTION = "com.honeywell.intent.action.SCANNER_RESULT"
        const val HONEYWELL_EXTRA_DATA = "data"
    }

    private val _scanResult = MutableLiveData<String>()
    val scanResult: LiveData<String> = _scanResult

    fun enableScanner() {
        try {
            val intent = Intent(DATALOGIC_ENABLE_ACTION)
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            // Fallback: scanner will respond to hardware trigger naturally
        }
    }

    fun disableScanner() {
        try {
            val intent = Intent(DATALOGIC_DISABLE_ACTION)
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            // No-op on non-Datalogic hardware
        }
    }

    fun processScanIntent(intent: Intent): String? {
        return when (intent.action) {
            DATALOGIC_ACTION_DECODE -> intent.getStringExtra(DATALOGIC_EXTRA_DATA)
            ZEBRA_ACTION -> intent.getStringExtra(ZEBRA_EXTRA_DATA)
            HONEYWELL_ACTION -> intent.getStringExtra(HONEYWELL_EXTRA_DATA)
            else -> null
        }
    }

    fun notifyResult(barcode: String) {
        _scanResult.postValue(barcode)
    }
}
