package br.com.kollectaops.api.picking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ValidarPecaRequest {
    @NotBlank
    private String codigoUnico;
    @NotNull
    private Long caixaId;
    @NotNull
    private Long itemId;
}
