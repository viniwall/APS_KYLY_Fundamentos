package br.com.kollectaops.api.security;

import br.com.kollectaops.api.picking.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String codigoCracha) throws UsernameNotFoundException {
        return usuarioRepository.findByCodigoCracha(codigoCracha)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + codigoCracha));
    }
}
