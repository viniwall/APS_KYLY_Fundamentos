package br.com.kollectaops.collector.data.remote.dto

import com.google.gson.annotations.SerializedName

// Auth
data class LoginRequestDto(
    @SerializedName("codigoCrachaSupervisor") val codigoCrachaSupervisor: String,
    @SerializedName("codigoCrachaOperador") val codigoCrachaOperador: String,
    @SerializedName("coletorSerial") val coletorSerial: String?
)

data class LoginResponseDto(
    @SerializedName("token") val token: String,
    @SerializedName("expiresIn") val expiresIn: Long,
    @SerializedName("usuario") val usuario: UsuarioDtoRemote,
    @SerializedName("filiais") val filiais: List<FilialDtoRemote>
)

data class UsuarioDtoRemote(
    @SerializedName("id") val id: Long,
    @SerializedName("nome") val nome: String,
    @SerializedName("codigoCracha") val codigoCracha: String,
    @SerializedName("perfil") val perfil: String
)

data class FilialDtoRemote(
    @SerializedName("id") val id: Long,
    @SerializedName("codigo") val codigo: String,
    @SerializedName("nome") val nome: String
)

// Caixa
data class CaixaDetalheDto(
    @SerializedName("id") val id: Long,
    @SerializedName("codigoPapeleta") val codigoPapeleta: String,
    @SerializedName("numeroOp") val numeroOp: String?,
    @SerializedName("clienteNome") val clienteNome: String?,
    @SerializedName("status") val status: String,
    @SerializedName("corTarja") val corTarja: String,
    @SerializedName("sequencia") val sequencia: Int?,
    @SerializedName("totalCaixasPedido") val totalCaixasPedido: Int?,
    @SerializedName("itens") val itens: List<ItemCaixaDtoRemote>
)

data class ItemCaixaDtoRemote(
    @SerializedName("id") val id: Long,
    @SerializedName("skuReferencia") val skuReferencia: String,
    @SerializedName("skuCor") val skuCor: String,
    @SerializedName("skuTamanho") val skuTamanho: String,
    @SerializedName("skuDescricao") val skuDescricao: String?,
    @SerializedName("enderecoCodigo") val enderecoCodigo: String?,
    @SerializedName("qtdeSolicitada") val qtdeSolicitada: Int,
    @SerializedName("qtdeColetada") val qtdeColetada: Int,
    @SerializedName("status") val status: String,
    @SerializedName("ordemPicking") val ordemPicking: Int
)

// Validar peça
data class ValidarPecaRequestDto(
    @SerializedName("codigoUnico") val codigoUnico: String,
    @SerializedName("caixaId") val caixaId: Long,
    @SerializedName("itemId") val itemId: Long
)

data class ValidarPecaResponseDto(
    @SerializedName("resultado") val resultado: String,
    @SerializedName("itemAtualizado") val itemAtualizado: ItemCaixaDtoRemote?,
    @SerializedName("proximoItem") val proximoItem: ItemCaixaDtoRemote?
)

// Posição SKU
data class PosicaoSkuDto(
    @SerializedName("codigo") val codigo: String,
    @SerializedName("quantidade") val quantidade: Int
)

// Sync
data class EventoPickingDtoLocal(
    @SerializedName("sessaoId") val sessaoId: Long?,
    @SerializedName("caixaId") val caixaId: Long,
    @SerializedName("itemCaixaId") val itemCaixaId: Long?,
    @SerializedName("pecaCodigoUnico") val pecaCodigoUnico: String?,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("mensagem") val mensagem: String?,
    @SerializedName("ocorridoEm") val ocorridoEm: String
)

data class SyncPickingRequestDto(
    @SerializedName("eventos") val eventos: List<EventoPickingDtoLocal>
)

data class SyncResponseDto(
    @SerializedName("processados") val processados: Int,
    @SerializedName("erros") val erros: Int,
    @SerializedName("mensagem") val mensagem: String
)

// Inventário
data class BemDto(
    @SerializedName("id") val id: Long,
    @SerializedName("codigoPatrimonio") val codigoPatrimonio: String,
    @SerializedName("descricao") val descricao: String?,
    @SerializedName("marca") val marca: String?,
    @SerializedName("modelo") val modelo: String?,
    @SerializedName("situacao") val situacao: String,
    @SerializedName("localizacaoAtual") val localizacaoAtual: LocalizacaoDto?
)

data class LocalizacaoDto(
    @SerializedName("id") val id: Long,
    @SerializedName("codigo") val codigo: String,
    @SerializedName("nome") val nome: String
)

data class BemRequestDto(
    @SerializedName("codigoPatrimonio") val codigoPatrimonio: String,
    @SerializedName("descricao") val descricao: String?,
    @SerializedName("marca") val marca: String?,
    @SerializedName("modelo") val modelo: String?,
    @SerializedName("serial") val serial: String?,
    @SerializedName("situacao") val situacao: String?,
    @SerializedName("localizacaoAtualId") val localizacaoAtualId: Long?,
    @SerializedName("observacao") val observacao: String?
)

data class InventarioDto(
    @SerializedName("id") val id: Long,
    @SerializedName("descricao") val descricao: String?,
    @SerializedName("status") val status: String,
    @SerializedName("iniciadoEm") val iniciadoEm: String?
)
