package br.com.kollectaops.collector.data.remote.service

import br.com.kollectaops.collector.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("v1/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>

    @POST("v1/auth/logout")
    suspend fun logout(): Response<Unit>

    // Picking
    @GET("v1/picking/caixas/{codigoPapeleta}")
    suspend fun getCaixa(@Path("codigoPapeleta") papeleta: String): Response<CaixaDetalheDto>

    @POST("v1/picking/caixas/{id}/abrir")
    suspend fun abrirCaixa(@Path("id") id: Long): Response<CaixaDetalheDto>

    @POST("v1/picking/caixas/{id}/salvar-parcial")
    suspend fun salvarParcial(@Path("id") id: Long): Response<Unit>

    @POST("v1/picking/caixas/{id}/finalizar")
    suspend fun finalizarCaixa(@Path("id") id: Long): Response<Unit>

    @POST("v1/picking/itens/{itemId}/pular")
    suspend fun pularItem(@Path("itemId") itemId: Long): Response<Unit>

    @GET("v1/picking/sku/{skuId}/posicoes")
    suspend fun getPosicoesSkU(@Path("skuId") skuId: Long): Response<List<PosicaoSkuDto>>

    @POST("v1/picking/validar-peca")
    suspend fun validarPeca(@Body request: ValidarPecaRequestDto): Response<ValidarPecaResponseDto>

    @POST("v1/sync/picking-events")
    suspend fun syncPickingEvents(@Body request: SyncPickingRequestDto): Response<SyncResponseDto>

    // Inventory
    @GET("v1/inventario/bens/{codigoPatrimonio}")
    suspend fun getBem(@Path("codigoPatrimonio") codigo: String): Response<BemDto>

    @POST("v1/inventario/bens")
    suspend fun criarBem(@Body request: BemRequestDto): Response<BemDto>

    @PUT("v1/inventario/bens/{id}")
    suspend fun atualizarBem(@Path("id") id: Long, @Body request: BemRequestDto): Response<BemDto>

    @GET("v1/inventario/inventarios")
    suspend fun getInventarios(@Query("filial_id") filialId: Long?, @Query("status") status: String?): Response<List<InventarioDto>>
}
