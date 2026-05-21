package br.com.kollectaops.api.picking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CaixaDetalheDto {
    private Long id;
    private String codigoPapeleta;
    private String numeroOp;
    private String clienteNome;
    private String status;
    private String corTarja;
    private Integer sequencia;
    private Integer totalCaixasPedido;
    private LocalDateTime abertaEm;
    private LocalDateTime finalizadaEm;
    private List<ItemDto> itens;

    @Data @Builder
    public static class ItemDto {
        private Long id;
        private String skuReferencia;
        private String skuCor;
        private String skuTamanho;
        private String skuDescricao;
        private String enderecoCodigo;
        private int qtdeSolicitada;
        private int qtdeColetada;
        private String status;
        private int ordemPicking;
    }
}
