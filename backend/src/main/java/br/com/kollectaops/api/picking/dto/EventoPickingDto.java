package br.com.kollectaops.api.picking.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventoPickingDto {
    private Long sessaoId;
    private Long caixaId;
    private Long itemCaixaId;
    private String pecaCodigoUnico;
    private String tipo;
    private String mensagem;
    private LocalDateTime ocorridoEm;

    @Data
    public static class SyncRequest {
        private List<EventoPickingDto> eventos;
    }

    @Data
    public static class SyncResponse {
        private int processados;
        private int erros;
        private String mensagem;
    }
}
