package br.com.kollectaops.api.inventory.controller;

import br.com.kollectaops.api.inventory.domain.Bem;
import br.com.kollectaops.api.inventory.domain.Inventario;
import br.com.kollectaops.api.inventory.domain.LeituraInventario;
import br.com.kollectaops.api.inventory.dto.BemRequest;
import br.com.kollectaops.api.inventory.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/inventario")
@RequiredArgsConstructor
@Tag(name = "Inventário Patrimonial", description = "Gestão de bens e inventários patrimoniais")
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping("/bens")
    @Operation(summary = "Lista bens com filtros de busca, situação e filial")
    public ResponseEntity<Page<Bem>> listarBens(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) String situacao,
        @RequestParam(name = "filial_id", required = false) Long filialId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(inventarioService.listarBens(q, situacao, filialId, PageRequest.of(page, size)));
    }

    @GetMapping("/bens/{codigoPatrimonio}")
    @Operation(summary = "Retorna um bem pelo código de patrimônio")
    public ResponseEntity<Bem> buscarBem(@PathVariable String codigoPatrimonio) {
        return ResponseEntity.ok(inventarioService.buscarBemPorCodigo(codigoPatrimonio));
    }

    @PostMapping("/bens")
    @Operation(summary = "Cadastra novo bem patrimonial")
    public ResponseEntity<Bem> criarBem(@Valid @RequestBody BemRequest req) {
        return ResponseEntity.ok(inventarioService.criarBem(req));
    }

    @PutMapping("/bens/{id}")
    @Operation(summary = "Atualiza bem patrimonial")
    public ResponseEntity<Bem> atualizarBem(@PathVariable Long id, @Valid @RequestBody BemRequest req) {
        return ResponseEntity.ok(inventarioService.atualizarBem(id, req));
    }

    @GetMapping("/inventarios")
    @Operation(summary = "Lista inventários por filial e status")
    public ResponseEntity<List<Inventario>> listarInventarios(
        @RequestParam(name = "filial_id", required = false) Long filialId,
        @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(inventarioService.listarInventarios(filialId, status));
    }

    @GetMapping("/divergencias")
    @Operation(summary = "Lista leituras com divergência (para relatórios)")
    public ResponseEntity<Page<LeituraInventario>> listarDivergencias(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(inventarioService.listarDivergencias(PageRequest.of(page, size)));
    }
}
