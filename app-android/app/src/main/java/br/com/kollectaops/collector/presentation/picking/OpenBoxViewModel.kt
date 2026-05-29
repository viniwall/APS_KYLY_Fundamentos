package br.com.kollectaops.collector.presentation.picking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.kollectaops.collector.data.remote.dto.CaixaDetalheDto
import br.com.kollectaops.collector.data.remote.service.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OpenBoxState {
    object Idle : OpenBoxState()
    object Loading : OpenBoxState()
    data class PartialCaixa(val caixa: CaixaDetalheDto) : OpenBoxState()
    data class ReadyToCollect(val caixa: CaixaDetalheDto) : OpenBoxState()
    data class AlreadyFinished(val papeleta: String) : OpenBoxState()
    data class Error(val message: String) : OpenBoxState()
}

@HiltViewModel
class OpenBoxViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _state = MutableLiveData<OpenBoxState>(OpenBoxState.Idle)
    val state: LiveData<OpenBoxState> = _state

    fun onPapeletaScanned(papeleta: String) {
        if (_state.value is OpenBoxState.Loading) return
        _state.value = OpenBoxState.Loading
        viewModelScope.launch {
            try {
                val response = apiService.getCaixa(papeleta)
                if (response.isSuccessful) {
                    val caixa = response.body() ?: run {
                        _state.value = OpenBoxState.Error("Resposta inválida do servidor")
                        return@launch
                    }
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
                if (response.isSuccessful) {
                    _state.value = OpenBoxState.ReadyToCollect(response.body() ?: caixa)
                } else {
                    _state.value = OpenBoxState.ReadyToCollect(caixa)
                }
            } catch (e: Exception) {
                _state.value = OpenBoxState.ReadyToCollect(caixa)
            }
        }
    }

    private fun openCaixa(caixa: CaixaDetalheDto) {
        viewModelScope.launch {
            try {
                val response = apiService.abrirCaixa(caixa.id)
                if (response.isSuccessful) {
                    _state.value = OpenBoxState.ReadyToCollect(response.body() ?: caixa)
                } else {
                    _state.value = OpenBoxState.ReadyToCollect(caixa)
                }
            } catch (e: Exception) {
                _state.value = OpenBoxState.ReadyToCollect(caixa)
            }
        }
    }

    fun reset() {
        _state.value = OpenBoxState.Idle
    }
}
