package br.com.kollectaops.collector.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import br.com.kollectaops.collector.data.local.entity.*

@Dao
interface CaixaDao {
    @Query("SELECT * FROM caixa_local WHERE codigo_papeleta = :papeleta LIMIT 1")
    suspend fun findByPapeleta(papeleta: String): CaixaLocal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(caixa: CaixaLocal)

    @Query("UPDATE caixa_local SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)
}

@Dao
interface ItemCaixaDao {
    @Query("SELECT * FROM item_caixa_local WHERE caixa_id = :caixaId ORDER BY ordem_picking ASC")
    suspend fun findByCaixaId(caixaId: Long): List<ItemCaixaLocal>

    @Query("SELECT * FROM item_caixa_local WHERE caixa_id = :caixaId AND status IN ('PENDENTE','EM_COLETA') ORDER BY ordem_picking ASC")
    suspend fun findPendentes(caixaId: Long): List<ItemCaixaLocal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ItemCaixaLocal>)

    @Query("UPDATE item_caixa_local SET qtde_coletada = :qtde, status = :status WHERE id = :id")
    suspend fun updateColetado(id: Long, qtde: Int, status: String)
}

@Dao
interface EventoPickingDao {
    @Insert
    suspend fun insert(evento: EventoPickingLocal): Long

    @Query("SELECT * FROM evento_picking_local WHERE sincronizado = 0 ORDER BY id ASC")
    suspend fun findPendentes(): List<EventoPickingLocal>

    @Query("UPDATE evento_picking_local SET sincronizado = 1 WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<Long>)

    @Query("SELECT COUNT(*) FROM evento_picking_local WHERE sincronizado = 0")
    fun countPendentesLive(): LiveData<Int>
}

@Dao
interface BemDao {
    @Query("SELECT * FROM bem_local WHERE codigo_patrimonio = :codigo LIMIT 1")
    suspend fun findByCodigo(codigo: String): BemLocal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(bem: BemLocal)
}

@Dao
interface SyncLogDao {
    @Insert
    suspend fun insert(log: SyncLog)

    @Query("SELECT * FROM sync_log ORDER BY criado_em DESC LIMIT 100")
    suspend fun getLast100(): List<SyncLog>
}
