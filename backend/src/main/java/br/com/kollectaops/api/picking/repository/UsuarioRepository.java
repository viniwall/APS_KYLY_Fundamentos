package br.com.kollectaops.api.picking.repository;

import br.com.kollectaops.api.picking.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCodigoCracha(String codigoCracha);

    Optional<Usuario> findByEmail(String email);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.filiais WHERE u.codigoCracha = :cracha")
    Optional<Usuario> findByCodigoCrachaWithFiliais(String cracha);

    boolean existsByCodigoCracha(String codigoCracha);

    @Query("SELECT u FROM Usuario u WHERE " +
           "(:q IS NULL OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "  OR LOWER(u.codigoCracha) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "  OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "AND (:perfil IS NULL OR u.perfil = :perfil) " +
           "AND (:ativo IS NULL OR u.ativo = :ativo)")
    Page<Usuario> findByFilters(
        @Param("q") String q,
        @Param("perfil") Usuario.Perfil perfil,
        @Param("ativo") Boolean ativo,
        Pageable pageable
    );
}
