package br.com.kollectaops.api.picking.repository;

import br.com.kollectaops.api.picking.domain.Filial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FilialRepository extends JpaRepository<Filial, Long> {

    @Query("SELECT f FROM Filial f WHERE " +
           "(:q IS NULL OR LOWER(f.codigo) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "  OR LOWER(f.nome) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Filial> findByFilters(@Param("q") String q, Pageable pageable);
}
