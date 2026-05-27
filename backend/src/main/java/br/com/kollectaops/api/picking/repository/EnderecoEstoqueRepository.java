package br.com.kollectaops.api.picking.repository;

import br.com.kollectaops.api.picking.domain.EnderecoEstoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EnderecoEstoqueRepository extends JpaRepository<EnderecoEstoque, Long> {

    @Query("SELECT e FROM EnderecoEstoque e WHERE " +
           "(:q IS NULL OR LOWER(e.codigo) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "  OR LOWER(e.andarRua) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "  OR LOWER(e.secao) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "AND (:ativo IS NULL OR e.ativo = :ativo)")
    Page<EnderecoEstoque> findByFilters(@Param("q") String q, @Param("ativo") Boolean ativo, Pageable pageable);
}
