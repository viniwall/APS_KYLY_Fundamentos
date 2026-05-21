package br.com.kollectaops.collector.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.kollectaops.collector.data.local.dao.*
import br.com.kollectaops.collector.data.local.entity.*

@Database(
    entities = [
        CaixaLocal::class,
        ItemCaixaLocal::class,
        EventoPickingLocal::class,
        BemLocal::class,
        SyncLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun caixaDao(): CaixaDao
    abstract fun itemCaixaDao(): ItemCaixaDao
    abstract fun eventoPickingDao(): EventoPickingDao
    abstract fun bemDao(): BemDao
    abstract fun syncLogDao(): SyncLogDao
}
