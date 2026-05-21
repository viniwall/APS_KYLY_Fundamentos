package br.com.kollectaops.api.picking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Código do crachá do supervisor é obrigatório")
    private String codigoCrachaSupervisor;

    @NotBlank(message = "Código do crachá do operador é obrigatório")
    private String codigoCrachaOperador;

    private String coletorSerial;
}
