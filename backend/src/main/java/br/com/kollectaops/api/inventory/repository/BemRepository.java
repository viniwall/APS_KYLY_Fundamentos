package br.com.kollectaops.api.inventory.repository;

import br.com.kollectaops.api.inventory.domain.Bem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BemRepository extends JpaRepository<Bem, Long> {

    Optional<Bem> findByCodigoPatrimonio(String codigoPatrimonio);

    @Query("SELECT b FROM Bem b WHERE " +
           "(:q IS NULL OR LOWER(b.codigoPatrimonio) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(b.descricao) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "AND (:situacao IS NULL OR b.situacao = :situacao) " +
           "AND (:filialId IS NULL OR b.localizacaoAtual.filial.id = :filialId)")
    Page<Bem> findByFilters(String q, Bem.Situacao situacao, Long filialId, Pageable pageable);
}
