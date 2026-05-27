package br.com.kollectaops.api.report.controller;

import br.com.kollectaops.api.picking.repository.CaixaRepository;
import br.com.kollectaops.api.picking.repository.SessaoColetorRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin / Dashboard", description = "Indicadores do dashboard em tempo real")
public class ReportController {

    private final CaixaRepository caixaRepository;
    private final SessaoColetorRepository sessaoColetorRepository;

    @GetMapping("/dashboard")
    @Operation(summary = "Retorna indicadores do dashboard em tempo real")
    public ResponseEntity<Map<String, Object>> dashboard() {
        return ResponseEntity.ok(Map.of(
            "caixasFinalizadasHoje", caixaRepository.countFinalizadasHoje(),
            "emPickingAgora",        caixaRepository.countEmPicking(),
            "caixasParciais",        caixaRepository.countParciais(),
            "operadoresAtivos",      sessaoColetorRepository.countOperadoresAtivos()
        ));
    }
}
