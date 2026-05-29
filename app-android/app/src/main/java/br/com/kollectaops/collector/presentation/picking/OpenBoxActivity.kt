package br.com.kollectaops.collector.presentation.picking

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.com.kollectaops.collector.R
import br.com.kollectaops.collector.data.remote.dto.CaixaDetalheDto
import br.com.kollectaops.collector.databinding.ActivityOpenBoxBinding
import br.com.kollectaops.collector.domain.service.ScannerService
import br.com.kollectaops.collector.domain.service.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OpenBoxActivity : AppCompatActivity() {

    companion object {
        private const val MAX_CAIXAS_CARRINHO = 2
    }

    private lateinit var binding: ActivityOpenBoxBinding
    private val viewModel: OpenBoxViewModel by viewModels()

    @Inject lateinit var scannerService: ScannerService
    @Inject lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenBoxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_open_box)

        setupScanCapture()
        observeState()
    }

    private fun setupScanCapture() {
        // Hidden EditText receives barcode from scanner intent
        binding.etPapeleta.setOnEditorActionListener { _, _, _ ->
            val text = binding.etPapeleta.text.toString().trim()
            if (text.isNotEmpty()) {
                scannerService.disableScanner()
                viewModel.onPapeletaScanned(text)
            }
            true
        }

        // Also observe scanner service LiveData (for hardware scanners)
        scannerService.scanResult.observe(this) { barcode ->
            if (!barcode.isNullOrBlank()) {
                binding.etPapeleta.setText(barcode)
                scannerService.disableScanner()
                viewModel.onPapeletaScanned(barcode)
            }
        }
    }

    private fun observeState() {
        viewModel.state.observe(this) { state ->
            when (state) {
                is OpenBoxState.Idle -> {
                    showIdle()
                }
                is OpenBoxState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvError.visibility = View.GONE
                }
                is OpenBoxState.ReadyToCollect -> {
                    navigateToCollect(state.caixa)
                }
                is OpenBoxState.PartialCaixa -> {
                    binding.progressBar.visibility = View.GONE
                    showPartialDialog(state.caixa)
                }
                is OpenBoxState.AlreadyFinished -> {
                    binding.progressBar.visibility = View.GONE
                    showAlreadyFinishedAlert(state.papeleta)
                }
                is OpenBoxState.CartLimitReached -> {
                    binding.progressBar.visibility = View.GONE
                    showCartLimitAlert()
                }
                is OpenBoxState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = state.message
                    binding.etPapeleta.setText("")
                    scannerService.enableScanner()
                }
            }
        }
    }

    private fun showIdle() {
        binding.progressBar.visibility = View.GONE
        binding.tvError.visibility = View.GONE
        binding.etPapeleta.setText("")
        binding.etPapeleta.requestFocus()
    }

    private fun showPartialDialog(caixa: CaixaDetalheDto) {
        val itensEmFalta = caixa.itens.count { it.status == "EM_FALTA" }
        val itensRestantes = caixa.itens.count { it.status == "AGUARDANDO" || it.status == "EM_PICKING" }
        val msg = getString(R.string.dialog_partial_message, caixa.codigoPapeleta, itensRestantes, itensEmFalta)

        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_partial_title)
            .setMessage(msg)
            .setPositiveButton(R.string.dialog_continue) { _, _ ->
                viewModel.confirmContinuePartial(caixa)
            }
            .setNegativeButton(R.string.dialog_cancel) { _, _ ->
                viewModel.reset()
                showIdle()
            }
            .setCancelable(false)
            .show()
    }

    private fun showAlreadyFinishedAlert(papeleta: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_finished_title)
            .setMessage(getString(R.string.dialog_finished_message, papeleta))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.reset()
                showIdle()
                scannerService.enableScanner()
            }
            .setCancelable(false)
            .show()
    }

    private fun showCartLimitAlert() {
        AlertDialog.Builder(this)
            .setTitle("Limite do carrinho atingido")
            .setMessage("O carrinho já possui $MAX_CAIXAS_CARRINHO caixas abertas. Finalize ou salve uma das caixas antes de abrir outra.")
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.reset()
                showIdle()
                scannerService.enableScanner()
            }
            .setCancelable(false)
            .show()
    }

    private fun navigateToCollect(caixa: CaixaDetalheDto) {
        val intent = Intent(this, CollectActivity::class.java).apply {
            putExtra(CollectActivity.EXTRA_CAIXA_ID, caixa.id)
            putExtra(CollectActivity.EXTRA_PAPELETA, caixa.codigoPapeleta)
            putExtra(CollectActivity.EXTRA_SEQUENCIA, caixa.sequencia ?: 0)
            putExtra(CollectActivity.EXTRA_TOTAL_CAIXAS, caixa.totalCaixasPedido ?: 0)
        }
        startActivity(intent)
        viewModel.reset()
    }

    override fun onResume() {
        super.onResume()
        sessionManager.updateLastActivity()
        if (viewModel.state.value is OpenBoxState.Idle ||
            viewModel.state.value is OpenBoxState.Error) {
            scannerService.enableScanner()
        }
    }

    override fun onPause() {
        super.onPause()
        scannerService.disableScanner()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
