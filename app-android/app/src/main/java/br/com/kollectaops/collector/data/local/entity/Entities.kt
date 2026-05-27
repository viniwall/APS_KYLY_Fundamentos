package br.com.kollectaops.collector.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "caixa_local")
data class CaixaLocal(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "codigo_papeleta") val codigoPapeleta: String,
    @ColumnInfo(name = "numero_op") val numeroOp: String?,
    @ColumnInfo(name = "cliente_nome") val clienteNome: String?,
    val status: String,
    @ColumnInfo(name = "cor_tarja") val corTarja: String,
    val sequencia: Int?,
    @ColumnInfo(name = "total_caixas_pedido") val totalCaixasPedido: Int?,
    @ColumnInfo(name = "aberta_em") val abertaEm: String?,
    @ColumnInfo(name = "finalizada_em") val finalizadaEm: String?,
    @ColumnInfo(name = "synced_at") val syncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "item_caixa_local")
data class ItemCaixaLocal(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "caixa_id") val caixaId: Long,
    @ColumnInfo(name = "sku_referencia") val skuReferencia: String,
    @ColumnInfo(name = "sku_cor") val skuCor: String,
    @ColumnInfo(name = "sku_tamanho") val skuTamanho: String,
    @ColumnInfo(name = "sku_descricao") val skuDescricao: String?,
    @ColumnInfo(name = "endereco_codigo") val enderecoCodigo: String?,
    @ColumnInfo(name = "qtde_solicitada") val qtdeSolicitada: Int,
    @ColumnInfo(name = "qtde_coletada") val qtdeColetada: Int,
    val status: String,
    @ColumnInfo(name = "ordem_picking") val ordemPicking: Int
)

@Entity(tableName = "evento_picking_local")
data class EventoPickingLocal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "sessao_id") val sessaoId: Long? = null,
    @ColumnInfo(name = "caixa_id") val caixaId: Long,
    @ColumnInfo(name = "item_caixa_id") val itemCaixaId: Long? = null,
    @ColumnInfo(name = "peca_codigo") val pecaCodigo: String? = null,
    val tipo: String,
    val mensagem: String?,
    @ColumnInfo(name = "ocorrido_em") val ocorridoEm: String,
    val sincronizado: Boolean = false
)

@Entity(tableName = "bem_local")
data class BemLocal(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "codigo_patrimonio") val codigoPatrimonio: String,
    val descricao: String?,
    val marca: String?,
    val modelo: String?,
    val situacao: String,
    @ColumnInfo(name = "localizacao_codigo") val localizacaoCodigo: String?,
    @ColumnInfo(name = "localizacao_nome") val localizacaoNome: String?
)

@Entity(tableName = "sync_log")
data class SyncLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tabela: String,
    val acao: String,
    val status: String,
    val mensagem: String?,
    @ColumnInfo(name = "criado_em") val criadoEm: Long = System.currentTimeMillis()
)
