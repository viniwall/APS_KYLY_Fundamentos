package br.com.kollectaops.api.sync.controller;

import br.com.kollectaops.api.picking.dto.EventoPickingDto;
import br.com.kollectaops.api.picking.service.PickingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/sync")
@RequiredArgsConstructor
@Tag(name = "Sincronização", description = "Endpoints de sincronização offline-first")
public class SyncController {

    private final PickingService pickingService;

    @PostMapping("/picking-events")
    @Operation(summary = "Recebe lote de EventoPicking do coletor (sync offline)")
    public ResponseEntity<EventoPickingDto.SyncResponse> syncPickingEvents(
        @RequestBody EventoPickingDto.SyncRequest req
    ) {
        return ResponseEntity.ok(pickingService.sincronizarEventos(req));
    }
}
