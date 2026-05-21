package br.com.kollectaops.api.picking.controller;

import br.com.kollectaops.api.picking.domain.Caixa;
import br.com.kollectaops.api.picking.dto.*;
import br.com.kollectaops.api.picking.service.PickingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/picking")
@RequiredArgsConstructor
@Tag(name = "Picking", description = "Operações do módulo de picking de expedição")
public class PickingController {

    private final PickingService pickingService;

    @GetMapping("/caixas")
    @Operation(summary = "Lista caixas com filtros de status e filial")
    public ResponseEntity<Page<Caixa>> listarCaixas(
        @RequestParam(required = false) String status,
        @RequestParam(name = "filial_id", required = false) Long filialId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Caixa.Status statusEnum = status != null ? Caixa.Status.valueOf(status.toUpperCase()) : null;
        Page<Caixa> result = pickingService.listarCaixas(statusEnum, filialId,
            PageRequest.of(page, size, Sort.by("id").descending()));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/caixas/{codigoPapeleta}")
    @Operation(summary = "Retorna detalhe completo da caixa com itens e SKUs")
    public ResponseEntity<CaixaDetalheDto> detalharCaixa(@PathVariable String codigoPapeleta) {
        return ResponseEntity.ok(pickingService.detalharCaixa(codigoPapeleta));
    }

    @PostMapping("/caixas/{id}/abrir")
    @Operation(summary = "Abre a caixa para picking (AGUARDANDO ou PARCIAL)")
    public ResponseEntity<CaixaDetalheDto> abrirCaixa(@PathVariable Long id) {
        return ResponseEntity.ok(pickingService.abrirCaixa(id));
    }

    @PostMapping("/caixas/{id}/salvar-parcial")
    @Operation(summary = "Salva coleta parcial e libera o operador")
    public ResponseEntity<Void> salvarParcial(@PathVariable Long id) {
        pickingService.salvarParcial(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/caixas/{id}/finalizar")
    @Operation(summary = "Finaliza a caixa (status FINALIZADA ou PARCIAL se houver falta)")
    public ResponseEntity<Void> finalizarCaixa(@PathVariable Long id) {
        pickingService.finalizarCaixa(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/itens/{itemId}/pular")
    @Operation(summary = "Pula um item marcando-o como EM_FALTA")
    public ResponseEntity<Void> pularItem(@PathVariable Long itemId) {
        pickingService.pularItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sku/{skuId}/posicoes")
    @Operation(summary = "Lista até 5 endereços com estoque disponível da SKU")
    public ResponseEntity<?> posicoesSku(@PathVariable Long skuId) {
        return ResponseEntity.ok(pickingService.buscarPosicoesSkU(skuId));
    }

    @PostMapping("/validar-peca")
    @Operation(summary = "Valida bipagem de peça contra o item atual da caixa")
    public ResponseEntity<ValidarPecaResponse> validarPeca(@Valid @RequestBody ValidarPecaRequest req) {
        return ResponseEntity.ok(pickingService.validarPeca(req));
    }
}
