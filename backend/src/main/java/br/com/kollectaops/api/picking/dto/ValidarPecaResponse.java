package br.com.kollectaops.api.picking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidarPecaResponse {

    public enum Resultado { OK, SKU_COMPLETA, ERRO_NAO_PERTENCE, ERRO_JA_BIPADA }

    private Resultado resultado;
    private ItemCaixaDto itemAtualizado;
    private ItemCaixaDto proximoItem;

    @Data @Builder
    public static class ItemCaixaDto {
        private Long id;
        private String skuReferencia;
        private String skuCor;
        private String skuTamanho;
        private String enderecoCodigo;
        private int qtdeSolicitada;
        private int qtdeColetada;
        private String status;
        private int ordemPicking;
    }
}
