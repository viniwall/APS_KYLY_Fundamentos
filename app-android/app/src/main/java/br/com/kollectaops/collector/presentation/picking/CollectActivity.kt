package br.com.kollectaops.collector.presentation.picking

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.kollectaops.collector.R
import br.com.kollectaops.collector.databinding.ActivityCollectBinding
import br.com.kollectaops.collector.domain.service.ScannerService
import br.com.kollectaops.collector.domain.service.SoundService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CollectActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CAIXA_ID = "caixa_id"
        const val EXTRA_PAPELETA = "papeleta"
    }

    private lateinit var binding: ActivityCollectBinding
    private val viewModel: CollectViewModel by viewModels()

    @Inject lateinit var scannerService: ScannerService
    @Inject lateinit var soundService: SoundService

    private val lastReadsAdapter = LastReadsAdapter()
    private val flashHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val caixaId = intent.getLongExtra(EXTRA_CAIXA_ID, -1)
        val papeleta = intent.getStringExtra(EXTRA_PAPELETA) ?: ""

        setupRecyclerView()
        setupButtons()
        setupScannerInput()
        setupObservers()

        viewModel.loadCaixa(caixaId, papeleta)
        scannerService.enableScanner()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        binding.rvLastReads.layoutManager = layoutManager
        binding.rvLastReads.adapter = lastReadsAdapter
        binding.rvLastReads.itemAnimator = null
    }

    private fun setupButtons() {
        binding.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.btnSkipSku.setOnClickListener {
            val item = viewModel.currentItem.value ?: return@setOnClickListener
            AlertDialog.Builder(this)
                .setTitle("Pular item")
                .setMessage("Retirar item em falta? (${item.skuReferencia} ${item.skuCor} ${item.skuTamanho})")
                .setPositiveButton("Sim") { _, _ -> viewModel.skipCurrentItem() }
                .setNegativeButton("Não", null)
                .show()
        }

        binding.btnSeePositions.setOnClickListener {
            viewModel.currentItem.value?.let { item ->
                val intent = Intent(this, OtherPositionsActivity::class.java)
                intent.putExtra(OtherPositionsActivity.EXTRA_SKU_ID, item.skuId)
                intent.putExtra(OtherPositionsActivity.EXTRA_SKU_LABEL,
                    "${item.skuReferencia} ${item.skuCor} ${item.skuTamanho}")
                startActivity(intent)
            }
        }

        binding.btnSavePartial.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Salvar parcial")
                .setMessage("Salvar e continuar depois?")
                .setPositiveButton("Sim") { _, _ ->
                    viewModel.savePartial()
                    finish()
                }
                .setNegativeButton("Não", null)
                .show()
        }
    }

    private fun setupScannerInput() {
        binding.etBarcodeCapture.requestFocus()

        // Manual input
        binding.etBarcodeCapture.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val barcode = binding.etBarcodeCapture.text.toString().trim()
                if (barcode.isNotBlank()) {
                    binding.etBarcodeCapture.text.clear()
                    onBarcodeScanned(barcode)
                }
                true
            } else false
        }

        // Scanner intent
        scannerService.scanResult.observe(this) { barcode ->
            if (!barcode.isNullOrBlank()) onBarcodeScanned(barcode)
        }
    }

    private fun setupObservers() {
        viewModel.currentItem.observe(this) { item ->
            item ?: return@observe
            binding.tvSkuRef.text = "REF: ${item.skuReferencia}"
            binding.tvSkuCor.text = item.skuCor
            binding.tvSkuTam.text = item.skuTamanho
            binding.tvAddress.text = item.enderecoCodigo ?: "—"
            binding.tvQuantity.text = "QUANTIDADE: ${item.qtdeSolicitada - item.qtdeColetada}"
        }

        viewModel.progress.observe(this) { (done, total) ->
            binding.tvBoxHeader.text = "CAIXA ${viewModel.papeleta}"
            binding.tvProgress.text = "PROGRESSO $done/$total"
        }

        viewModel.scanResult.observe(this) { result ->
            result ?: return@observe
            lastReadsAdapter.addRead(result.barcode)

            when (result.type) {
                CollectViewModel.ScanResultType.OK -> {
                    flashScreen(Color.parseColor("#2E7D32"), 200)
                    soundService.beepSuccess()
                }
                CollectViewModel.ScanResultType.SKU_COMPLETE -> {
                    flashScreen(Color.parseColor("#2E7D32"), 400)
                    showFeedback(getString(R.string.msg_sku_complete))
                    soundService.beepSuccessSkuComplete()
                }
                CollectViewModel.ScanResultType.ERROR -> {
                    flashScreen(Color.parseColor("#C62828"), 300)
                    soundService.beepError()
                    showErrorDialog(result.message ?: "Erro")
                }
            }
        }

        viewModel.boxFinalized.observe(this) { finalized ->
            if (finalized) {
                soundService.beepBoxFinished()
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.msg_box_finalized))
                    .setMessage("Todos os itens foram coletados.")
                    .setCancelable(false)
                    .setPositiveButton("OK") { _, _ -> finish() }
                    .show()
            }
        }
    }

    private fun flashScreen(color: Int, durationMs: Long) {
        binding.rootLayout.setBackgroundColor(color)
        flashHandler.postDelayed({
            binding.rootLayout.setBackgroundColor(Color.WHITE)
        }, durationMs)
    }

    private fun showFeedback(text: String) {
        binding.tvScanFeedback.text = text
        binding.tvScanFeedback.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            binding.tvScanFeedback.visibility = View.GONE
        }, 1200)
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Atenção")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> scannerService.enableScanner() }
            .show()
    }

    private fun onBarcodeScanned(barcode: String) {
        scannerService.disableScanner()
        viewModel.validatePiece(barcode)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        scannerService.processScanIntent(intent)?.let { onBarcodeScanned(it) }
    }

    override fun onResume() {
        super.onResume()
        scannerService.enableScanner()
    }

    override fun onPause() {
        super.onPause()
        scannerService.disableScanner()
    }
}
