package br.com.kollectaops.api.picking.service;

import br.com.kollectaops.api.common.exception.BusinessException;
import br.com.kollectaops.api.common.exception.NotFoundException;
import br.com.kollectaops.api.picking.domain.SessaoColetor;
import br.com.kollectaops.api.picking.domain.Usuario;
import br.com.kollectaops.api.picking.dto.LoginRequest;
import br.com.kollectaops.api.picking.dto.LoginResponse;
import br.com.kollectaops.api.picking.repository.SessaoColetorRepository;
import br.com.kollectaops.api.picking.repository.UsuarioRepository;
import br.com.kollectaops.api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final SessaoColetorRepository sessaoColetorRepository;
    private final JwtService jwtService;

    @Transactional
    public LoginResponse login(LoginRequest req) {
        Usuario supervisor = usuarioRepository.findByCodigoCrachaWithFiliais(req.getCodigoCrachaSupervisor())
            .orElseThrow(() -> new NotFoundException("Supervisor não encontrado: " + req.getCodigoCrachaSupervisor()));

        if (!supervisor.isAtivo()) {
            throw new BusinessException("Supervisor inativo");
        }

        if (supervisor.getPerfil() == Usuario.Perfil.OPERADOR) {
            throw new BusinessException("Crachá informado não tem perfil de supervisor");
        }

        Usuario operador = usuarioRepository.findByCodigoCrachaWithFiliais(req.getCodigoCrachaOperador())
            .orElseThrow(() -> new NotFoundException("Operador não encontrado: " + req.getCodigoCrachaOperador()));

        if (!operador.isAtivo()) {
            throw new BusinessException("Operador inativo");
        }

        SessaoColetor sessao = SessaoColetor.builder()
            .supervisor(supervisor)
            .operador(operador)
            .coletorSerial(req.getColetorSerial())
            .build();
        sessaoColetorRepository.save(sessao);

        Long filialId = operador.getFiliais().stream()
            .findFirst()
            .map(f -> f.getId())
            .orElse(null);

        String token = jwtService.generateToken(operador.getId(), operador.getCodigoCracha(),
            operador.getPerfil().name(), filialId);

        return LoginResponse.builder()
            .token(token)
            .expiresIn(480 * 60L)
            .usuario(LoginResponse.UsuarioDto.builder()
                .id(operador.getId())
                .nome(operador.getNome())
                .codigoCracha(operador.getCodigoCracha())
                .perfil(operador.getPerfil().name())
                .build())
            .filiais(operador.getFiliais().stream()
                .map(f -> LoginResponse.FilialDto.builder()
                    .id(f.getId())
                    .codigo(f.getCodigo())
                    .nome(f.getNome())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
}
