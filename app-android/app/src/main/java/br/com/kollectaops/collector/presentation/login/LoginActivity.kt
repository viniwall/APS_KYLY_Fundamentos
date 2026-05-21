package br.com.kollectaops.collector.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.kollectaops.collector.databinding.ActivityLoginBinding
import br.com.kollectaops.collector.domain.service.ScannerService
import br.com.kollectaops.collector.presentation.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    @Inject lateinit var scannerService: ScannerService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupScannerListener()
        setupManualInput()

        scannerService.enableScanner()
        binding.etSupervisorCode.requestFocus()
    }

    private fun setupObservers() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is LoginViewModel.State.WaitingOperator -> {
                    binding.layoutSupervisor.visibility = View.GONE
                    binding.layoutOperador.visibility = View.VISIBLE
                    scannerService.enableScanner()
                    binding.etOperadorCode.requestFocus()
                }
                is LoginViewModel.State.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvError.visibility = View.GONE
                }
                is LoginViewModel.State.Success -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                is LoginViewModel.State.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = state.message
                    scannerService.enableScanner()
                }
                else -> {}
            }
        }
    }

    private fun setupScannerListener() {
        // Observa leituras do scanner via LiveData global do ScannerService
        scannerService.scanResult.observe(this) { barcode ->
            if (barcode.isNullOrBlank()) return@observe
            onBarcodeScanned(barcode)
        }
    }

    private fun setupManualInput() {
        // Permite digitação manual (modo teste sem coletor físico)
        binding.etSupervisorCode.setOnEditorActionListener { v, _, _ ->
            val text = v.text.toString().trim()
            if (text.isNotBlank()) onBarcodeScanned(text)
            true
        }
        binding.etOperadorCode.setOnEditorActionListener { v, _, _ ->
            val text = v.text.toString().trim()
            if (text.isNotBlank()) onBarcodeScanned(text)
            true
        }
    }

    private fun onBarcodeScanned(barcode: String) {
        scannerService.disableScanner()
        val state = viewModel.uiState.value
        when {
            state == null || state is LoginViewModel.State.WaitingInput -> {
                binding.etSupervisorCode.setText(barcode)
                viewModel.onSupervisorScanned(barcode)
            }
            state is LoginViewModel.State.WaitingOperator -> {
                binding.etOperadorCode.setText(barcode)
                viewModel.onOperadorScanned(barcode)
            }
        }
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
