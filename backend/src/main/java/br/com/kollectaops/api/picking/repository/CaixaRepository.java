package br.com.kollectaops.api.picking.repository;

import br.com.kollectaops.api.picking.domain.Caixa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    // Parciais report – native SQL projection
    interface ParcialRowView {
        Long getId();
        String getCodigoPapeleta();
        String getNumeroOp();
        String getClienteNome();
        LocalDateTime getAbertaEm();
        LocalDateTime getFinalizadaEm();
        Long getTotalItens();
        Long getItensCompletos();
        Long getItensEmFalta();
    }

    @Query(value = """
        SELECT c.id,
               c.codigo_papeleta   AS codigoPapeleta,
               c.numero_op         AS numeroOp,
               p.cliente_nome      AS clienteNome,
               c.aberta_em         AS abertaEm,
               c.finalizada_em     AS finalizadaEm,
               COUNT(ic.id)        AS totalItens,
               SUM(CASE WHEN ic.status = 'COMPLETO'  THEN 1 ELSE 0 END) AS itensCompletos,
               SUM(CASE WHEN ic.status = 'EM_FALTA'  THEN 1 ELSE 0 END) AS itensEmFalta
        FROM caixa c
        LEFT JOIN pedido p       ON p.id  = c.pedido_id
        LEFT JOIN item_caixa ic  ON ic.caixa_id = c.id
        WHERE c.status = 'PARCIAL'
          AND (:de  IS NULL OR DATE(COALESCE(c.finalizada_em, c.aberta_em)) >= :de)
          AND (:ate IS NULL OR DATE(COALESCE(c.finalizada_em, c.aberta_em)) <= :ate)
        GROUP BY c.id
        ORDER BY c.id DESC
        """,
        countQuery = """
        SELECT COUNT(DISTINCT c.id) FROM caixa c
        WHERE c.status = 'PARCIAL'
          AND (:de  IS NULL OR DATE(COALESCE(c.finalizada_em, c.aberta_em)) >= :de)
          AND (:ate IS NULL OR DATE(COALESCE(c.finalizada_em, c.aberta_em)) <= :ate)
        """,
        nativeQuery = true)
    Page<ParcialRowView> findParciais(
        @Param("de") String de,
        @Param("ate") String ate,
        Pageable pageable
    );

    @Query(value = """
        SELECT COALESCE(SUM(CASE WHEN ic.status = 'EM_FALTA' THEN 1 ELSE 0 END), 0)
        FROM caixa c
        LEFT JOIN item_caixa ic ON ic.caixa_id = c.id
        WHERE c.status = 'PARCIAL'
          AND (:de  IS NULL OR DATE(COALESCE(c.finalizada_em, c.aberta_em)) >= :de)
          AND (:ate IS NULL OR DATE(COALESCE(c.finalizada_em, c.aberta_em)) <= :ate)
        """, nativeQuery = true)
    long countItemsEmFaltaInParciais(@Param("de") String de, @Param("ate") String ate);
}
