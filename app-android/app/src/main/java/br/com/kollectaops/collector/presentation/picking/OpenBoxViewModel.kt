package br.com.kollectaops.collector.presentation.picking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.kollectaops.collector.data.local.dao.CaixaDao
import br.com.kollectaops.collector.data.local.dao.ItemCaixaDao
import br.com.kollectaops.collector.data.local.entity.CaixaLocal
import br.com.kollectaops.collector.data.local.entity.ItemCaixaLocal
import br.com.kollectaops.collector.data.remote.dto.CaixaDetalheDto
import br.com.kollectaops.collector.data.remote.dto.ItemCaixaDtoRemote
import br.com.kollectaops.collector.data.remote.service.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OpenBoxState {
    object Idle : OpenBoxState()
    object Loading : OpenBoxState()
    object CartLimitReached : OpenBoxState()
    data class PartialCaixa(val caixa: CaixaDetalheDto) : OpenBoxState()
    data class ReadyToCollect(val caixa: CaixaDetalheDto) : OpenBoxState()
    data class AlreadyFinished(val papeleta: String) : OpenBoxState()
    data class Error(val message: String) : OpenBoxState()
}

@HiltViewModel
class OpenBoxViewModel @Inject constructor(
    private val apiService: ApiService,
    private val caixaDao: CaixaDao,
    private val itemCaixaDao: ItemCaixaDao
) : ViewModel() {

    companion object {
        private const val MAX_CAIXAS_CARRINHO = 2
    }

    private val _state = MutableLiveData<OpenBoxState>(OpenBoxState.Idle)
    val state: LiveData<OpenBoxState> = _state

    fun onPapeletaScanned(papeleta: String) {
        if (_state.value is OpenBoxState.Loading) return
        _state.value = OpenBoxState.Loading
        viewModelScope.launch {
            try {
                val caixasAbertas = caixaDao.countEmPicking()
                if (caixasAbertas >= MAX_CAIXAS_CARRINHO) {
                    _state.value = OpenBoxState.CartLimitReached
                    return@launch
                }

                val response = apiService.getCaixa(papeleta)
                if (response.isSuccessful) {
                    val caixa = response.body()!!
                    when (caixa.status) {
                        "FINALIZADA" -> _state.value = OpenBoxState.AlreadyFinished(papeleta)
                        "PARCIAL" -> _state.value = OpenBoxState.PartialCaixa(caixa)
                        "CANCELADA" -> _state.value = OpenBoxState.Error("Caixa cancelada: $papeleta")
                        else -> openCaixa(caixa)
                    }
                } else {
                    val code = response.code()
                    val msg = if (code == 404) "Papeleta não encontrada: $papeleta"
                              else "Erro ao buscar caixa (HTTP $code)"
                    _state.value = OpenBoxState.Error(msg)
                }
            } catch (e: Exception) {
                _state.value = OpenBoxState.Error("Sem conexão com o servidor")
            }
        }
    }

    fun confirmContinuePartial(caixa: CaixaDetalheDto) {
        viewModelScope.launch {
            try {
                val response = apiService.abrirCaixa(caixa.id)
                val detalhe = if (response.isSuccessful) response.body() ?: caixa else caixa
                persistCaixaLocally(detalhe)
                _state.value = OpenBoxState.ReadyToCollect(detalhe)
            } catch (e: Exception) {
                persistCaixaLocally(caixa)
                _state.value = OpenBoxState.ReadyToCollect(caixa)
            }
        }
    }

    private fun openCaixa(caixa: CaixaDetalheDto) {
        viewModelScope.launch {
            try {
                val response = apiService.abrirCaixa(caixa.id)
                val detalhe = if (response.isSuccessful) response.body() ?: caixa else caixa
                persistCaixaLocally(detalhe)
                _state.value = OpenBoxState.ReadyToCollect(detalhe)
            } catch (e: Exception) {
                persistCaixaLocally(caixa)
                _state.value = OpenBoxState.ReadyToCollect(caixa)
            }
        }
    }

    private suspend fun persistCaixaLocally(caixa: CaixaDetalheDto) {
        caixaDao.insertOrReplace(
            CaixaLocal(
                id = caixa.id,
                codigoPapeleta = caixa.codigoPapeleta,
                numeroOp = caixa.numeroOp,
                clienteNome = caixa.clienteNome,
                status = "EM_PICKING",
                corTarja = caixa.corTarja,
                sequencia = caixa.sequencia,
                totalCaixasPedido = caixa.totalCaixasPedido,
                abertaEm = null,
                finalizadaEm = null
            )
        )
        itemCaixaDao.insertAll(caixa.itens.map { it.toLocal(caixa.id) })
    }

    fun reset() {
        _state.value = OpenBoxState.Idle
    }

    private fun ItemCaixaDtoRemote.toLocal(caixaId: Long) = ItemCaixaLocal(
        id = id,
        caixaId = caixaId,
        skuReferencia = skuReferencia,
        skuCor = skuCor,
        skuTamanho = skuTamanho,
        skuDescricao = skuDescricao,
        enderecoCodigo = enderecoCodigo,
        qtdeSolicitada = qtdeSolicitada,
        qtdeColetada = qtdeColetada,
        status = status,
        ordemPicking = ordemPicking
    )
}
