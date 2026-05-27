package br.com.kollectaops.api.admin.controller;

import br.com.kollectaops.api.picking.domain.*;
import br.com.kollectaops.api.picking.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin / Cadastros e Relatórios", description = "Endpoints administrativos de cadastros e relatórios")
public class AdminController {

    private final SkuRepository skuRepository;
    private final EnderecoEstoqueRepository enderecoEstoqueRepository;
    private final UsuarioRepository usuarioRepository;
    private final FilialRepository filialRepository;
    private final CaixaRepository caixaRepository;
    private final ItemCaixaRepository itemCaixaRepository;

    // ── Cadastros ────────────────────────────────────────────────────────────

    @GetMapping("/skus")
    @Operation(summary = "Lista SKUs com filtro de busca e status")
    public ResponseEntity<Page<Sku>> listarSkus(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(skuRepository.findByFilters(
                q, ativo, PageRequest.of(page, size, Sort.by("referencia", "cor", "tamanho"))));
    }

    @GetMapping("/enderecos")
    @Operation(summary = "Lista endereços de estoque com filtro de busca e status")
    public ResponseEntity<Page<EnderecoEstoque>> listarEnderecos(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(enderecoEstoqueRepository.findByFilters(
                q, ativo, PageRequest.of(page, size, Sort.by("codigo"))));
    }

    @GetMapping("/usuarios")
    @Operation(summary = "Lista usuários com filtro de busca, perfil e status")
    public ResponseEntity<Page<Usuario>> listarUsuarios(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String perfil,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Usuario.Perfil perfilEnum = null;
        if (perfil != null && !perfil.isBlank()) {
            perfilEnum = Usuario.Perfil.valueOf(perfil.toUpperCase());
        }
        return ResponseEntity.ok(usuarioRepository.findByFilters(
                q, perfilEnum, ativo, PageRequest.of(page, size, Sort.by("nome"))));
    }

    @GetMapping("/filiais")
    @Operation(summary = "Lista filiais com filtro de busca")
    public ResponseEntity<Page<Filial>> listarFiliais(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(filialRepository.findByFilters(
                q, PageRequest.of(page, size, Sort.by("codigo"))));
    }

    // ── Relatórios ───────────────────────────────────────────────────────────

    @GetMapping("/relatorios/parciais")
    @Operation(summary = "Relatório de caixas finalizadas parcialmente")
    public ResponseEntity<Map<String, Object>> parciais(
            @RequestParam(required = false) String de,
            @RequestParam(required = false) String ate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<CaixaRepository.ParcialRowView> result =
                caixaRepository.findParciais(de, ate, PageRequest.of(page, size));

        long totalEmFalta = caixaRepository.countItemsEmFaltaInParciais(de, ate);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("periodo",       Map.of("de", de != null ? de : "", "ate", ate != null ? ate : ""));
        response.put("resumo",        Map.of(
                "totalCaixasParciais", result.getTotalElements(),
                "totalItensEmFalta",   totalEmFalta));
        response.put("content",       result.getContent());
        response.put("totalElements", result.getTotalElements());
        response.put("totalPages",    result.getTotalPages());
        response.put("number",        result.getNumber());
        response.put("size",          result.getSize());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/relatorios/divergencias")
    @Operation(summary = "Relatório de itens com divergência no picking")
    public ResponseEntity<Map<String, Object>> divergencias(
            @RequestParam(required = false) String de,
            @RequestParam(required = false) String ate,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<ItemCaixaRepository.DivergenciaRowView> result =
                itemCaixaRepository.findDivergencias(de, ate, q, PageRequest.of(page, size));

        long totalEmFalta    = itemCaixaRepository.countByStatusInPeriodo("EM_FALTA",    de, ate);
        long totalDivergentes = itemCaixaRepository.countByStatusInPeriodo("DIVERGENTE", de, ate);
        long caixasAfetadas  = itemCaixaRepository.countCaixasAfetadasInPeriodo(de, ate);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("periodo",       Map.of("de", de != null ? de : "", "ate", ate != null ? ate : ""));
        response.put("resumo",        Map.of(
                "totalDivergencias",  result.getTotalElements(),
                "totalItensEmFalta",  totalEmFalta,
                "totalDivergentes",   totalDivergentes,
                "caixasAfetadas",     caixasAfetadas));
        response.put("content",       result.getContent());
        response.put("totalElements", result.getTotalElements());
        response.put("totalPages",    result.getTotalPages());
        response.put("number",        result.getNumber());
        response.put("size",          result.getSize());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/relatorios/produtividade")
    @Operation(summary = "Relatório de produtividade por operador")
    public ResponseEntity<Map<String, Object>> produtividade(
            @RequestParam(required = false) String de,
            @RequestParam(required = false) String ate) {

        long totalFinalizadas = caixaRepository.countFinalizadasHoje();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("periodo", Map.of("de", de != null ? de : "", "ate", ate != null ? ate : ""));
        response.put("resumo", Map.of(
                "totalCaixasFinalizadas", totalFinalizadas,
                "totalItensColetados",    0,
                "mediaMinutosPorCaixa",   0,
                "taxaDivergencia",        "N/D"));
        response.put("porOperador", List.of());
        return ResponseEntity.ok(response);
    }
}
