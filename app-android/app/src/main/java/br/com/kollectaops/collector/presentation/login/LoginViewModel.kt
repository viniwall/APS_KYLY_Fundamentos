package br.com.kollectaops.collector.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.kollectaops.collector.data.remote.dto.LoginRequestDto
import br.com.kollectaops.collector.data.remote.service.ApiService
import br.com.kollectaops.collector.domain.service.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    sealed class State {
        object WaitingInput : State()
        object WaitingOperator : State()
        object Loading : State()
        object Success : State()
        data class Error(val message: String) : State()
    }

    private val _uiState = MutableLiveData<State>(State.WaitingInput)
    val uiState: LiveData<State> = _uiState

    private var supervisorCracha: String = ""

    fun onSupervisorScanned(cracha: String) {
        supervisorCracha = cracha
        _uiState.value = State.WaitingOperator
    }

    fun onOperadorScanned(operadorCracha: String) {
        if (supervisorCracha.isBlank()) {
            _uiState.value = State.Error("Bipe o supervisor primeiro")
            return
        }
        _uiState.value = State.Loading

        viewModelScope.launch {
            try {
                val response = apiService.login(
                    LoginRequestDto(
                        codigoCrachaSupervisor = supervisorCracha,
                        codigoCrachaOperador = operadorCracha,
                        coletorSerial = android.os.Build.SERIAL
                    )
                )
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val filial = body.filiais.firstOrNull()
                    sessionManager.saveSession(
                        token = body.token,
                        userId = body.usuario.id,
                        nome = body.usuario.nome,
                        cracha = body.usuario.codigoCracha,
                        filialId = filial?.id ?: 0L,
                        filialNome = filial?.nome ?: ""
                    )
                    _uiState.value = State.Success
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Erro desconhecido"
                    _uiState.value = State.Error("Login falhou: $errorBody")
                    supervisorCracha = ""
                }
            } catch (e: Exception) {
                _uiState.value = State.Error("Sem conexão com o servidor. Verifique o Wi-Fi.")
                supervisorCracha = ""
            }
        }
    }
}
