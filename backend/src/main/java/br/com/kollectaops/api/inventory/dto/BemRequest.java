package br.com.kollectaops.api.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BemRequest {
    @NotBlank
    private String codigoPatrimonio;
    private String descricao;
    private String marca;
    private String modelo;
    private String serial;
    private String situacao;
    private Long localizacaoAtualId;
    private String observacao;
}
