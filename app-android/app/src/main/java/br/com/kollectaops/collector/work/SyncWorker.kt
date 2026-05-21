package br.com.kollectaops.collector.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.com.kollectaops.collector.data.local.dao.EventoPickingDao
import br.com.kollectaops.collector.data.remote.dto.EventoPickingDtoLocal
import br.com.kollectaops.collector.data.remote.dto.SyncPickingRequestDto
import br.com.kollectaops.collector.data.remote.service.ApiService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val apiService: ApiService,
    private val eventoPickingDao: EventoPickingDao
) : CoroutineWorker(appContext, params) {

    companion object {
        const val TAG = "SyncWorker"
        const val WORK_NAME = "kollecta_sync"
    }

    override suspend fun doWork(): Result {
        return try {
            val pendentes = eventoPickingDao.findPendentes()
            if (pendentes.isEmpty()) return Result.success()

            val dtos = pendentes.map { ev ->
                EventoPickingDtoLocal(
                    sessaoId = ev.sessaoId,
                    caixaId = ev.caixaId,
                    itemCaixaId = ev.itemCaixaId,
                    pecaCodigoUnico = ev.pecaCodigo,
                    tipo = ev.tipo,
                    mensagem = ev.mensagem,
                    ocorridoEm = ev.ocorridoEm
                )
            }

            val response = apiService.syncPickingEvents(SyncPickingRequestDto(dtos))
            if (response.isSuccessful) {
                val ids = pendentes.map { it.id }
                eventoPickingDao.markSynced(ids)
                Log.d(TAG, "Sync OK: ${response.body()?.processados} eventos processados")
                Result.success()
            } else {
                Log.w(TAG, "Sync falhou HTTP ${response.code()} — retry")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sync exception: ${e.message}")
            Result.retry()
        }
    }
}
