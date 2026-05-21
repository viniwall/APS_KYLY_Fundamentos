package br.com.kollectaops.collector.domain.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScannerReceiver : BroadcastReceiver() {

    @Inject
    lateinit var scannerService: ScannerService

    override fun onReceive(context: Context, intent: Intent) {
        val barcode = scannerService.processScanIntent(intent)
        if (!barcode.isNullOrBlank()) {
            scannerService.notifyResult(barcode)
        }
    }
}
