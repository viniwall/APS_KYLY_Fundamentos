package br.com.kollectaops.api.picking.repository;

import br.com.kollectaops.api.picking.domain.Sku;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SkuRepository extends JpaRepository<Sku, Long> {

    @Query("SELECT s FROM Sku s WHERE " +
           "(:q IS NULL OR LOWER(s.referencia) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "  OR LOWER(s.descricao) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "  OR LOWER(s.cor) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "  OR LOWER(s.codigoEan) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "AND (:ativo IS NULL OR s.ativo = :ativo)")
    Page<Sku> findByFilters(@Param("q") String q, @Param("ativo") Boolean ativo, Pageable pageable);
}
