package br.com.kollectaops.collector.presentation.picking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.kollectaops.collector.data.local.dao.EventoPickingDao
import br.com.kollectaops.collector.data.local.dao.ItemCaixaDao
import br.com.kollectaops.collector.data.local.entity.EventoPickingLocal
import br.com.kollectaops.collector.data.local.entity.ItemCaixaLocal
import br.com.kollectaops.collector.data.remote.dto.ValidarPecaRequestDto
import br.com.kollectaops.collector.data.remote.service.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CollectViewModel @Inject constructor(
    private val apiService: ApiService,
    private val itemCaixaDao: ItemCaixaDao,
    private val eventoPickingDao: EventoPickingDao
) : ViewModel() {

    enum class ScanResultType { OK, SKU_COMPLETE, ERROR }

    data class ScanResult(val barcode: String, val type: ScanResultType, val message: String? = null)

    data class ItemState(
        val id: Long,
        val skuId: Long,
        val skuReferencia: String,
        val skuCor: String,
        val skuTamanho: String,
        val enderecoCodigo: String?,
        val qtdeSolicitada: Int,
        val qtdeColetada: Int
    )

    private val _currentItem = MutableLiveData<ItemState?>()
    val currentItem: LiveData<ItemState?> = _currentItem

    private val _progress = MutableLiveData<Pair<Int, Int>>()
    val progress: LiveData<Pair<Int, Int>> = _progress

    private val _scanResult = MutableLiveData<ScanResult?>()
    val scanResult: LiveData<ScanResult?> = _scanResult

    private val _boxFinalized = MutableLiveData<Boolean>(false)
    val boxFinalized: LiveData<Boolean> = _boxFinalized

    var papeleta: String = ""
    private var caixaId: Long = -1L
    private var items: List<ItemCaixaLocal> = emptyList()

    fun loadCaixa(id: Long, papeleta: String) {
        this.caixaId = id
        this.papeleta = papeleta
        viewModelScope.launch {
            items = itemCaixaDao.findByCaixaId(id)
            updateCurrentItem()
        }
    }

    private fun updateCurrentItem() {
        val pending = items.filter { it.status == "PENDENTE" || it.status == "EM_COLETA" }
        val total = items.size
        val done = items.count { it.status == "COMPLETO" || it.status == "EM_FALTA" }
        _progress.postValue(Pair(done, total))

        val next = pending.firstOrNull()
        _currentItem.postValue(next?.let {
            ItemState(
                id = it.id,
                skuId = it.id,
                skuReferencia = it.skuReferencia,
                skuCor = it.skuCor,
                skuTamanho = it.skuTamanho,
                enderecoCodigo = it.enderecoCodigo,
                qtdeSolicitada = it.qtdeSolicitada,
                qtdeColetada = it.qtdeColetada
            )
        })
    }

    fun validatePiece(barcode: String) {
        val item = _currentItem.value ?: return
        viewModelScope.launch {
            try {
                val response = apiService.validarPeca(
                    ValidarPecaRequestDto(
                        codigoUnico = barcode,
                        caixaId = caixaId,
                        itemId = item.id
                    )
                )
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

                    when (body.resultado) {
                        "OK" -> {
                            body.itemAtualizado?.let { dto ->
                                itemCaixaDao.updateColetado(dto.id, dto.qtdeColetada, dto.status)
                                reloadItems()
                            }
                            registrarEvento("BIPAR_OK", barcode)
                            _scanResult.postValue(ScanResult(barcode, ScanResultType.OK))
                        }
                        "SKU_COMPLETA" -> {
                            body.itemAtualizado?.let { dto ->
                                itemCaixaDao.updateColetado(dto.id, dto.qtdeColetada, dto.status)
                                reloadItems()
                            }
                            registrarEvento("BIPAR_OK_SKU_COMPLETA", barcode)
                            _scanResult.postValue(ScanResult(barcode, ScanResultType.SKU_COMPLETE))
                            checkBoxFinalization()
                        }
                        "ERRO_NAO_PERTENCE" -> {
                            registrarEvento("BIPAR_ERRO_NAO_PERTENCE", barcode)
                            _scanResult.postValue(ScanResult(barcode, ScanResultType.ERROR, "SKU não pertence à caixa"))
                        }
                        "ERRO_JA_BIPADA" -> {
                            registrarEvento("BIPAR_ERRO_SEM_SALDO", barcode)
                            _scanResult.postValue(ScanResult(barcode, ScanResultType.ERROR, "Peça já bipada"))
                        }
                    }
                } else {
                    _scanResult.postValue(ScanResult(barcode, ScanResultType.ERROR, "Erro na validação"))
                }
            } catch (e: Exception) {
                // Offline fallback: registra localmente
                registrarEvento("BIPAR_OK", barcode)
                _scanResult.postValue(ScanResult(barcode, ScanResultType.OK))
            }
        }
    }

    fun skipCurrentItem() {
        val item = _currentItem.value ?: return
        viewModelScope.launch {
            apiService.pularItem(item.id)
            itemCaixaDao.updateColetado(item.id, item.qtdeColetada, "EM_FALTA")
            registrarEvento("PULAR_ITEM", "item ${item.id}")
            reloadItems()
        }
    }

    fun savePartial() {
        viewModelScope.launch {
            try {
                apiService.salvarParcial(caixaId)
            } catch (e: Exception) {
                registrarEvento("SALVAR_PARCIAL", "offline")
            }
        }
    }

    private suspend fun reloadItems() {
        items = itemCaixaDao.findByCaixaId(caixaId)
        updateCurrentItem()
    }

    private fun checkBoxFinalization() {
        val allDone = items.all { it.status == "COMPLETO" || it.status == "EM_FALTA" }
        if (allDone) _boxFinalized.postValue(true)
    }

    private suspend fun registrarEvento(tipo: String, mensagem: String) {
        eventoPickingDao.insert(EventoPickingLocal(
            caixaId = caixaId,
            tipo = tipo,
            mensagem = mensagem,
            ocorridoEm = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            sincronizado = false
        ))
    }
}
