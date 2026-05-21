package br.com.kollectaops.api.inventory.service;

import br.com.kollectaops.api.common.exception.NotFoundException;
import br.com.kollectaops.api.inventory.domain.*;
import br.com.kollectaops.api.inventory.dto.BemRequest;
import br.com.kollectaops.api.inventory.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final BemRepository bemRepository;
    private final InventarioRepository inventarioRepository;
    private final LocalizacaoRepository localizacaoRepository;
    private final LeituraInventarioRepository leituraInventarioRepository;

    public Page<Bem> listarBens(String q, String situacao, Long filialId, Pageable pageable) {
        Bem.Situacao situacaoEnum = situacao != null ? Bem.Situacao.valueOf(situacao.toUpperCase()) : null;
        return bemRepository.findByFilters(q, situacaoEnum, filialId, pageable);
    }

    public Bem buscarBemPorCodigo(String codigo) {
        return bemRepository.findByCodigoPatrimonio(codigo)
            .orElseThrow(() -> new NotFoundException("Bem não encontrado: " + codigo));
    }

    @Transactional
    public Bem criarBem(BemRequest req) {
        LocalizacaoPatrimonial loc = req.getLocalizacaoAtualId() != null ?
            localizacaoRepository.findById(req.getLocalizacaoAtualId()).orElse(null) : null;

        Bem bem = Bem.builder()
            .codigoPatrimonio(req.getCodigoPatrimonio())
            .descricao(req.getDescricao())
            .marca(req.getMarca())
            .modelo(req.getModelo())
            .serial(req.getSerial())
            .situacao(req.getSituacao() != null ? Bem.Situacao.valueOf(req.getSituacao()) : Bem.Situacao.ATIVO)
            .localizacaoAtual(loc)
            .observacao(req.getObservacao())
            .build();

        return bemRepository.save(bem);
    }

    @Transactional
    public Bem atualizarBem(Long id, BemRequest req) {
        Bem bem = bemRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Bem não encontrado: " + id));

        LocalizacaoPatrimonial loc = req.getLocalizacaoAtualId() != null ?
            localizacaoRepository.findById(req.getLocalizacaoAtualId()).orElse(null) : bem.getLocalizacaoAtual();

        bem.setDescricao(req.getDescricao());
        bem.setMarca(req.getMarca());
        bem.setModelo(req.getModelo());
        bem.setSerial(req.getSerial());
        if (req.getSituacao() != null) bem.setSituacao(Bem.Situacao.valueOf(req.getSituacao()));
        bem.setLocalizacaoAtual(loc);
        bem.setObservacao(req.getObservacao());

        return bemRepository.save(bem);
    }

    public List<Inventario> listarInventarios(Long filialId, String status) {
        if (filialId != null && status != null) {
            return inventarioRepository.findByFilialIdAndStatus(filialId, Inventario.Status.valueOf(status));
        }
        if (filialId != null) return inventarioRepository.findByFilialId(filialId);
        return inventarioRepository.findAll();
    }

    public Page<LeituraInventario> listarDivergencias(Pageable pageable) {
        return leituraInventarioRepository.findByDivergenciaNot(LeituraInventario.Divergencia.NENHUMA, pageable);
    }
}
