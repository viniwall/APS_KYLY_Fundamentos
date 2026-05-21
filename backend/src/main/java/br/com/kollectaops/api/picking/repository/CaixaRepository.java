package br.com.kollectaops.api.picking.repository;

import br.com.kollectaops.api.picking.domain.Caixa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaixaRepository extends JpaRepository<Caixa, Long> {

    Optional<Caixa> findByCodigoPapeleta(String codigoPapeleta);

    @Query("SELECT c FROM Caixa c WHERE (:status IS NULL OR c.status = :status) " +
           "AND (:filialId IS NULL OR c.pedido IS NOT NULL)")
    Page<Caixa> findByFilters(Caixa.Status status, Long filialId, Pageable pageable);

    @Query("SELECT c FROM Caixa c LEFT JOIN FETCH c.itens i LEFT JOIN FETCH i.sku LEFT JOIN FETCH i.enderecoSugerido " +
           "WHERE c.codigoPapeleta = :papeleta")
    Optional<Caixa> findByCodigoPapeletaWithItens(String papeleta);

    @Query("SELECT COUNT(c) FROM Caixa c WHERE c.status = 'FINALIZADA' AND DATE(c.finalizadaEm) = CURRENT_DATE")
    long countFinalizadasHoje();

    @Query("SELECT COUNT(c) FROM Caixa c WHERE c.status = 'EM_PICKING'")
    long countEmPicking();

    @Query("SELECT COUNT(c) FROM Caixa c WHERE c.status = 'PARCIAL'")
    long countParciais();
}
