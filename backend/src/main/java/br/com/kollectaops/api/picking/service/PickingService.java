package br.com.kollectaops.api.picking.service;

import br.com.kollectaops.api.common.exception.BusinessException;
import br.com.kollectaops.api.common.exception.NotFoundException;
import br.com.kollectaops.api.picking.domain.*;
import br.com.kollectaops.api.picking.dto.*;
import br.com.kollectaops.api.picking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PickingService {

    private final CaixaRepository caixaRepository;
    private final ItemCaixaRepository itemCaixaRepository;
    private final PecaRepository pecaRepository;
    private final EventoPickingRepository eventoPickingRepository;
    private final EstoqueRepository estoqueRepository;

    public Page<Caixa> listarCaixas(Caixa.Status status, Long filialId, Pageable pageable) {
        return caixaRepository.findByFilters(status, filialId, pageable);
    }

    public CaixaDetalheDto detalharCaixa(String codigoPapeleta) {
        Caixa caixa = caixaRepository.findByCodigoPapeletaWithItens(codigoPapeleta)
            .orElseThrow(() -> new NotFoundException("Papeleta não encontrada: " + codigoPapeleta));
        return toCaixaDetalheDto(caixa);
    }

    @Transactional
    public CaixaDetalheDto abrirCaixa(Long caixaId) {
        Caixa caixa = caixaRepository.findById(caixaId)
            .orElseThrow(() -> new NotFoundException("Caixa não encontrada: " + caixaId));

        if (caixa.getStatus() == Caixa.Status.FINALIZADA) {
            throw new BusinessException("Caixa já finalizada em " +
                (caixa.getFinalizadaEm() != null ? caixa.getFinalizadaEm().toString() : "?"));
        }
        if (caixa.getStatus() == Caixa.Status.CANCELADA) {
            throw new BusinessException("Caixa cancelada");
        }

        if (caixa.getStatus() == Caixa.Status.AGUARDANDO) {
            caixa.setStatus(Caixa.Status.EM_PICKING);
            caixa.setAbertaEm(LocalDateTime.now());
        }
        // PARCIAL → continua sem mudar status

        caixaRepository.save(caixa);

        EventoPicking evento = EventoPicking.builder()
            .caixa(caixa)
            .tipo(EventoPicking.Tipo.ABRIR_CAIXA)
            .ocorridoEm(LocalDateTime.now())
            .sincronizado(true)
            .build();
        eventoPickingRepository.save(evento);

        return detalharCaixa(caixa.getCodigoPapeleta());
    }

    @Transactional
    public ValidarPecaResponse validarPeca(ValidarPecaRequest req) {
        Caixa caixa = caixaRepository.findById(req.getCaixaId())
            .orElseThrow(() -> new NotFoundException("Caixa não encontrada"));

        ItemCaixa item = itemCaixaRepository.findById(req.getItemId())
            .orElseThrow(() -> new NotFoundException("Item não encontrado"));

        Peca peca = pecaRepository.findByCodigoUnico(req.getCodigoUnico())
            .orElseThrow(() -> new NotFoundException("Peça não cadastrada: " + req.getCodigoUnico()));

        // Verificar se já foi bipada
        if (peca.getStatus() == Peca.Status.EM_CAIXA || peca.getStatus() == Peca.Status.BIPADA) {
            return ValidarPecaResponse.builder()
                .resultado(ValidarPecaResponse.Resultado.ERRO_JA_BIPADA)
                .itemAtualizado(toItemDto(item))
                .build();
        }

        // Verificar se pertence ao SKU do item
        if (!peca.getSku().getId().equals(item.getSku().getId())) {
            return ValidarPecaResponse.builder()
                .resultado(ValidarPecaResponse.Resultado.ERRO_NAO_PERTENCE)
                .itemAtualizado(toItemDto(item))
                .build();
        }

        // Aceitar peça
        peca.setStatus(Peca.Status.EM_CAIXA);
        peca.setBipadaEm(LocalDateTime.now());
        peca.setItemCaixa(item);
        pecaRepository.save(peca);

        item.setQtdeColetada(item.getQtdeColetada() + 1);
        item.setStatus(ItemCaixa.Status.EM_COLETA);

        boolean skuCompleta = item.getQtdeColetada() >= item.getQtdeSolicitada();
        if (skuCompleta) {
            item.setStatus(ItemCaixa.Status.COMPLETO);
        }
        itemCaixaRepository.save(item);

        EventoPicking.Tipo tipoEvento = skuCompleta ?
            EventoPicking.Tipo.BIPAR_OK_SKU_COMPLETA : EventoPicking.Tipo.BIPAR_OK;

        eventoPickingRepository.save(EventoPicking.builder()
            .caixa(caixa)
            .itemCaixa(item)
            .peca(peca)
            .tipo(tipoEvento)
            .ocorridoEm(LocalDateTime.now())
            .sincronizado(true)
            .mensagem(req.getCodigoUnico())
            .build());

        // Próximo item
        ItemCaixa proximoItem = null;
        if (skuCompleta) {
            List<ItemCaixa> pendentes = itemCaixaRepository.findPendentesOuEmColeta(caixa.getId());
            proximoItem = pendentes.stream()
                .filter(i -> !i.getId().equals(item.getId()))
                .findFirst()
                .orElse(null);

            // Verificar finalização automática
            boolean todosCompletos = itemCaixaRepository.findByCaixaIdOrdenado(caixa.getId()).stream()
                .allMatch(i -> i.getStatus() == ItemCaixa.Status.COMPLETO ||
                               i.getStatus() == ItemCaixa.Status.EM_FALTA);
            if (todosCompletos) {
                finalizarCaixaInterno(caixa);
            }
        }

        return ValidarPecaResponse.builder()
            .resultado(skuCompleta ? ValidarPecaResponse.Resultado.SKU_COMPLETA : ValidarPecaResponse.Resultado.OK)
            .itemAtualizado(toItemDto(item))
            .proximoItem(proximoItem != null ? toItemDto(proximoItem) : null)
            .build();
    }

    @Transactional
    public void pularItem(Long itemId) {
        ItemCaixa item = itemCaixaRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException("Item não encontrado"));
        item.setStatus(ItemCaixa.Status.EM_FALTA);
        itemCaixaRepository.save(item);

        eventoPickingRepository.save(EventoPicking.builder()
            .caixa(item.getCaixa())
            .itemCaixa(item)
            .tipo(EventoPicking.Tipo.PULAR_ITEM)
            .ocorridoEm(LocalDateTime.now())
            .sincronizado(true)
            .build());
    }

    @Transactional
    public void salvarParcial(Long caixaId) {
        Caixa caixa = caixaRepository.findById(caixaId)
            .orElseThrow(() -> new NotFoundException("Caixa não encontrada"));
        caixa.setStatus(Caixa.Status.PARCIAL);
        caixaRepository.save(caixa);

        eventoPickingRepository.save(EventoPicking.builder()
            .caixa(caixa)
            .tipo(EventoPicking.Tipo.SALVAR_PARCIAL)
            .ocorridoEm(LocalDateTime.now())
            .sincronizado(true)
            .build());
    }

    @Transactional
    public void finalizarCaixa(Long caixaId) {
        Caixa caixa = caixaRepository.findById(caixaId)
            .orElseThrow(() -> new NotFoundException("Caixa não encontrada"));
        finalizarCaixaInterno(caixa);
    }

    private void finalizarCaixaInterno(Caixa caixa) {
        List<ItemCaixa> itens = itemCaixaRepository.findByCaixaIdOrdenado(caixa.getId());
        boolean algumEmFalta = itens.stream().anyMatch(i -> i.getStatus() == ItemCaixa.Status.EM_FALTA);

        caixa.setStatus(algumEmFalta ? Caixa.Status.PARCIAL : Caixa.Status.FINALIZADA);
        caixa.setFinalizadaEm(LocalDateTime.now());
        caixaRepository.save(caixa);

        eventoPickingRepository.save(EventoPicking.builder()
            .caixa(caixa)
            .tipo(EventoPicking.Tipo.FINALIZAR_CAIXA)
            .ocorridoEm(LocalDateTime.now())
            .sincronizado(true)
            .build());
    }

    public List<Object[]> buscarPosicoesSkU(Long skuId) {
        return estoqueRepository.findPosicoesComEstoqueBySkuId(skuId);
    }

    @Transactional
    public EventoPickingDto.SyncResponse sincronizarEventos(EventoPickingDto.SyncRequest req) {
        int processados = 0;
        int erros = 0;

        for (EventoPickingDto dto : req.getEventos()) {
            try {
                Caixa caixa = caixaRepository.findById(dto.getCaixaId())
                    .orElse(null);
                if (caixa == null) { erros++; continue; }

                EventoPicking evento = new EventoPicking();
                evento.setCaixa(caixa);
                evento.setTipo(EventoPicking.Tipo.valueOf(dto.getTipo()));
                evento.setMensagem(dto.getMensagem());
                evento.setOcorridoEm(dto.getOcorridoEm() != null ? dto.getOcorridoEm() : LocalDateTime.now());
                evento.setSincronizado(true);
                eventoPickingRepository.save(evento);
                processados++;
            } catch (Exception e) {
                erros++;
            }
        }

        EventoPickingDto.SyncResponse resp = new EventoPickingDto.SyncResponse();
        resp.setProcessados(processados);
        resp.setErros(erros);
        resp.setMensagem(processados + " eventos processados, " + erros + " erros");
        return resp;
    }

    private CaixaDetalheDto toCaixaDetalheDto(Caixa c) {
        return CaixaDetalheDto.builder()
            .id(c.getId())
            .codigoPapeleta(c.getCodigoPapeleta())
            .numeroOp(c.getNumeroOp())
            .clienteNome(c.getPedido() != null ? c.getPedido().getClienteNome() : null)
            .status(c.getStatus().name())
            .corTarja(c.getCorTarja().name())
            .sequencia(c.getSequencia())
            .totalCaixasPedido(c.getTotalCaixasPedido())
            .abertaEm(c.getAbertaEm())
            .finalizadaEm(c.getFinalizadaEm())
            .itens(c.getItens() != null ? c.getItens().stream().map(this::toCaixaDetalheItemDto).collect(Collectors.toList()) : List.of())
            .build();
    }

    private CaixaDetalheDto.ItemDto toCaixaDetalheItemDto(ItemCaixa i) {
        return CaixaDetalheDto.ItemDto.builder()
            .id(i.getId())
            .skuReferencia(i.getSku().getReferencia())
            .skuCor(i.getSku().getCor())
            .skuTamanho(i.getSku().getTamanho())
            .enderecoCodigo(i.getEnderecoSugerido() != null ? i.getEnderecoSugerido().getCodigo() : null)
            .qtdeSolicitada(i.getQtdeSolicitada())
            .qtdeColetada(i.getQtdeColetada())
            .status(i.getStatus().name())
            .ordemPicking(i.getOrdemPicking())
            .build();
    }

    private ValidarPecaResponse.ItemCaixaDto toItemDto(ItemCaixa i) {
        return ValidarPecaResponse.ItemCaixaDto.builder()
            .id(i.getId())
            .skuReferencia(i.getSku().getReferencia())
            .skuCor(i.getSku().getCor())
            .skuTamanho(i.getSku().getTamanho())
            .enderecoCodigo(i.getEnderecoSugerido() != null ? i.getEnderecoSugerido().getCodigo() : null)
            .qtdeSolicitada(i.getQtdeSolicitada())
            .qtdeColetada(i.getQtdeColetada())
            .status(i.getStatus().name())
            .ordemPicking(i.getOrdemPicking())
            .build();
    }
}
