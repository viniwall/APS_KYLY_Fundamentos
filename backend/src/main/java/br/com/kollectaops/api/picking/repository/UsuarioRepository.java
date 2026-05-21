package br.com.kollectaops.api.picking.repository;

import br.com.kollectaops.api.picking.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCodigoCracha(String codigoCracha);

    Optional<Usuario> findByEmail(String email);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.filiais WHERE u.codigoCracha = :cracha")
    Optional<Usuario> findByCodigoCrachaWithFiliais(String cracha);

    boolean existsByCodigoCracha(String codigoCracha);
}
